package com.rdvdev2.timetravelmod.impl

import com.rdvdev2.timetravelmod.impl.Mod.identifier
import com.rdvdev2.timetravelmod.impl.ModConfig.instance
import com.rdvdev2.timetravelmod.impl.common.timemachine.TimeMachineExecutor
import com.rdvdev2.timetravelmod.impl.common.timemachine.TimeMachineManager
import com.rdvdev2.timetravelmod.impl.common.timemachine.exception.TimeMachineExecutionException
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryKey
import net.minecraft.world.Heightmap
import java.util.*

// TODO: Separate packets
object ModNetworking {

    @JvmField
    val OPEN_TIME_MACHINE_GUI = identifier("open_time_machine_gui")

    @JvmField
    val OPEN_CREATIVE_TIME_MACHINE_GUI = identifier("open_creative_time_machine_gui")

    @JvmField
    val OPEN_ENGINEER_BOOK_GUI = identifier("open_engineer_book_gui")

    @JvmField
    val RUN_TIME_MACHINE = identifier("run_time_machine")

    @JvmField
    val RUN_CREATIVE_TIME_MACHINE = identifier("run_creative_time_machine")

    fun register() {
        ServerPlayNetworking.registerGlobalReceiver(RUN_TIME_MACHINE) { server, player, _, buf, _ ->
            val worldIdentifier = buf.readIdentifier()
            val rootPos = buf.readBlockPos()
            val timelineIdentifier = buf.readIdentifier()
            server.execute {
                val world = player.getServer()!!.getWorld(RegistryKey.of(Registry.DIMENSION, worldIdentifier)) ?: return@execute
                val timeline = ModRegistries.TIMELINE[timelineIdentifier] ?: return@execute
                val _tme = TimeMachineManager.generateExecutor(world, rootPos)
                _tme.ifPresent { tme: TimeMachineExecutor ->
                    try {
                        tme.checkAndRun(timeline, player, server)
                    } catch (e: TimeMachineExecutionException) {
                        player.sendMessage(e.error.clientError, true)
                        if (instance.common.enableCheaterReports) {
                            Arrays.stream(server.playerManager.opNames)
                                .map { name: String? -> server.playerManager.getPlayer(name) }
                                .filter { p -> p != player }
                                .forEach { p: ServerPlayerEntity? ->
                                    p?.sendMessage(
                                        e.error.getCheaterReport(
                                            player
                                        ), false
                                    )
                                }
                        }
                    }
                }
            }
        }

        ServerPlayNetworking.registerGlobalReceiver(RUN_CREATIVE_TIME_MACHINE) { server, player, _, buf, _ ->
            val timelineIdentifier = buf.readIdentifier()
            server.execute {
                val timeline = ModRegistries.TIMELINE[timelineIdentifier] ?: return@execute
                val world = server.getWorld(timeline.world)
                if (world == null || world == player.world) return@execute
                player.itemsHand.forEach { itemStack ->
                    if (itemStack.item === ModItems.CREATIVE_TIME_MACHINE) {
                        var pos = world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, player.blockPos)
                        if (pos.y == 0) pos = pos.up(world.seaLevel + 2)
                        while (world.getBlockState(pos).isSolidBlock(world, pos) || world.getBlockState(pos.up()).isSolidBlock(world, pos.up())) pos = pos.up() // Air space
                        while (!world.getBlockState(pos.down()).isSolidBlock(world, pos)) pos = pos.down() // Touching ground
                        // TODO: Special logic for flying players
                        player.teleport(world, player.x, pos.y.toDouble(), player.z, player.yaw, player.pitch)
                    }
                }
            }
        }
    }
}