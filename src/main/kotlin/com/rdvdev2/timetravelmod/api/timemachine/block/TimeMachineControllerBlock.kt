package com.rdvdev2.timetravelmod.api.timemachine.block

import com.rdvdev2.timetravelmod.impl.ModNetworking
import com.rdvdev2.timetravelmod.impl.ModTriggers
import com.rdvdev2.timetravelmod.impl.common.timemachine.TimeMachineExecutor.TimeMachineError
import com.rdvdev2.timetravelmod.impl.common.timemachine.TimeMachineManager
import io.netty.buffer.Unpooled
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class TimeMachineControllerBlock(settings: Settings?) : Block(settings) {

    override fun onUse(state: BlockState, world: World, pos: BlockPos, player: PlayerEntity, hand: Hand, hit: BlockHitResult): ActionResult {
        val _tme = TimeMachineManager.generateExecutor(world, pos)
        if (_tme.isPresent) {
            val error = _tme.get().runChecks(player)
            if (error.isPresent) {
                if (world.isClient) {
                    player.sendMessage(error.get().clientError, true)
                }
                return ActionResult.FAIL
            } else {
                if (!world.isClient) {
                    val serverPlayer = player as ServerPlayerEntity
                    ModTriggers.ACCESS_TIME_MACHINE.trigger(serverPlayer)
                    val passedData = PacketByteBuf(Unpooled.buffer())
                    passedData.writeIdentifier(world.registryKey.value)
                    passedData.writeBlockPos(pos)
                    passedData.writeInt(_tme.get().timeMachine.tier)
                    ServerPlayNetworking.send(serverPlayer, ModNetworking.OPEN_TIME_MACHINE_GUI, passedData)
                }
                return ActionResult.SUCCESS
            }
        } else {
            player.sendMessage(TimeMachineError.NOT_BUILT.clientError, true)
            return ActionResult.FAIL
        }
    }
}