package com.rdvdev2.timetravelmod.impl.common.timemachine

import com.rdvdev2.timetravelmod.api.dimension.ITimeline
import com.rdvdev2.timetravelmod.api.dimension.ITimeline.Companion.getTimelineForWorld
import com.rdvdev2.timetravelmod.api.timemachine.ITimeMachine
import com.rdvdev2.timetravelmod.api.timemachine.ITimeMachineUpgrade
import com.rdvdev2.timetravelmod.api.timemachine.block.TimeMachineCoreBlock
import com.rdvdev2.timetravelmod.impl.common.block.entity.TimeMachineCoreBlockEntity
import com.rdvdev2.timetravelmod.impl.common.timemachine.exception.TimeMachineExecutionException
import net.fabricmc.fabric.api.dimension.v1.FabricDimensions
import net.minecraft.block.Blocks
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.ClickEvent
import net.minecraft.text.Style
import net.minecraft.text.TranslatableText
import net.minecraft.util.Formatting
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.world.TeleportTarget
import net.minecraft.world.World
import java.util.*
import kotlin.math.abs

class TimeMachineExecutor(
    private val structure: TimeMachineStructure,
    private val root: BlockPos,
    private val world: World
    ) {

    private val structurePos: Array<BlockPos> = structure.getStructurePos(root)
    val timeMachine: ITimeMachine = structure.timeMachine
    private val upgradeMap: Map<ITimeMachineUpgrade, BlockPos> = structure.getUpgrades(world, root)

    private fun checkIfBuilt() = structure.checkIfBuilt(world, root)

    private fun checkIfCooledDown(): Boolean {
        for (pos in structurePos) {
            val be = world.getBlockEntity(pos)
            if (be is TimeMachineCoreBlockEntity && !be.ready) return false
        }
        return true
    }

    private fun checkIfPlayerIsInside(player: PlayerEntity): Boolean {
        val detectedEntities: MutableList<Entity> = ArrayList()
        for (pos in structurePos) {
            detectedEntities += world.getEntitiesByClass(PlayerEntity::class.java, Box(pos)) { it == player }
        }
        return detectedEntities.isNotEmpty()
    }

    private fun checkIfOverloaded(): Boolean {
        val detectedEntities: MutableSet<Entity> = HashSet()
        for (pos in structurePos) {
            detectedEntities += world.getNonSpectatingEntities(Entity::class.java, Box(pos))
        }
        return detectedEntities.size <= timeMachine.entityMaxLoad
    }

    fun isTimeMachineReady(player: PlayerEntity?) = runChecks(player).isEmpty

    fun runChecks(player: PlayerEntity?): Optional<TimeMachineError> {
        if (!checkIfBuilt()) return Optional.of(TimeMachineError.NOT_BUILT)
        if (!checkIfCooledDown()) return Optional.of(TimeMachineError.HOT_CORES)
        if (!checkIfOverloaded()) return Optional.of(TimeMachineError.OVERLOADED)
        return if (player != null && !checkIfPlayerIsInside(player))
            Optional.of(TimeMachineError.PLAYER_OUTSIDE)
        else
            Optional.empty()
    }

    @Throws(TimeMachineExecutionException::class)
    fun checkAndRun(destTl: ITimeline, player: ServerPlayerEntity?, server: MinecraftServer) {

        // Run checks and throw errors
        val error = runChecks(player)
        if (error.isPresent) throw error.get().exception
        if (timeMachine.tier < destTl.minTier) throw TimeMachineError.UNREACHABLE_TIMELINE.exception
        val destWorld = server.getWorld(destTl.world)
        if (destWorld === world) throw TimeMachineError.SAME_TIMELINE.exception
        val origTl = getTimelineForWorld(world.registryKey)

        upgradeMap.forEach { (upgrade, pos) -> upgrade.beforeTeleporting(structure, root, pos, world, destWorld, origTl, destTl) }

        // Copy the structure on the new dimension with hot cores
        structurePos.forEach { pos ->
            val state = world.getBlockState(pos)
            destWorld!!.setBlockState(pos, state)
            if (state.block is TimeMachineCoreBlock) {
                (destWorld.getBlockEntity(pos) as TimeMachineCoreBlockEntity).setRemainingTicks(timeMachine.cooldownTime)
            }
        }

        // Teleport all entities
        val detectedEntities: MutableSet<Entity> = HashSet()
        for (pos in structurePos) {
            detectedEntities += world.getNonSpectatingEntities(Entity::class.java, Box(pos))
        }
        for (entity in detectedEntities) {
            if (entity is ServerPlayerEntity) {
                entity.teleport(destWorld, entity.getX(), entity.getY(), entity.getZ(), entity.yaw, entity.pitch)
            } else {
                FabricDimensions.teleport(entity, destWorld, TeleportTarget(entity.pos, entity.velocity, entity.yaw, entity.pitch))
            }
        }

        // Remove the original structure
        structurePos.forEach { pos -> world.setBlockState(pos, Blocks.AIR.defaultState) }

        // Apply corruption
        val corruption = abs(origTl!!.minTier - destTl.minTier) * timeMachine.corruptionMultiplier
        origTl.getCorruption(server)!!.increaseCorruptionLevel(corruption)
        destTl.getCorruption(server)!!.increaseCorruptionLevel(corruption)

        upgradeMap.forEach { (upgrade, pos) -> upgrade.afterTeleporting(structure, root, pos, world, destWorld, origTl, destTl) }
    }

    enum class TimeMachineError {
        NOT_BUILT, HOT_CORES, PLAYER_OUTSIDE, OVERLOADED, UNREACHABLE_TIMELINE, SAME_TIMELINE;

        val clientError: TranslatableText
            get() = TranslatableText(String.format("time_travel_mod.error.%s.client", name.toLowerCase()))

        fun getCheaterReport(player: ServerPlayerEntity): TranslatableText =
            TranslatableText("time_travel_mod.cheater_report", player.displayName, String.format("time_travel_mod.error.%s.server", name.toLowerCase()), getBanButton(player))

        val exception: TimeMachineExecutionException
            get() = TimeMachineExecutionException(this)

        companion object {
            private fun getBanButton(player: ServerPlayerEntity): TranslatableText {
                val text = TranslatableText("time_travel_mod.ban")
                text.style = Style.EMPTY
                    .withClickEvent(
                        ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, String.format("/ban %s", player.name.asString()))
                    ).withColor(Formatting.RED)
                return text
            }
        }
    }
}