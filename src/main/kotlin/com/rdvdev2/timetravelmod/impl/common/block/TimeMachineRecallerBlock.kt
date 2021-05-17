package com.rdvdev2.timetravelmod.impl.common.block

import com.rdvdev2.timetravelmod.api.dimension.ITimeline.Companion.getTimelineForWorld
import com.rdvdev2.timetravelmod.api.timemachine.block.TimeMachineControllerBlock
import com.rdvdev2.timetravelmod.impl.ModBlocks
import com.rdvdev2.timetravelmod.impl.common.block.entity.TimeMachineRecallerBlockEntity
import com.rdvdev2.timetravelmod.impl.common.timemachine.TimeMachineExecutor
import com.rdvdev2.timetravelmod.impl.common.timemachine.TimeMachineManager
import com.rdvdev2.timetravelmod.impl.common.timemachine.exception.TimeMachineExecutionException
import net.minecraft.block.Block
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.BlockState
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.util.math.BlockPos
import net.minecraft.world.BlockView
import net.minecraft.world.World
import java.util.*

class TimeMachineRecallerBlock(settings: Settings) : Block(settings), BlockEntityProvider {

    init {
        defaultState = getStateManager().defaultState
            .with(CONFIGURED, false)
            .with(TRIGGERED, false)
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(CONFIGURED).add(TRIGGERED)
    }

    override fun createBlockEntity(world: BlockView) = TimeMachineRecallerBlockEntity()

    override fun emitsRedstonePower(state: BlockState) = true

    override fun neighborUpdate(state: BlockState, world: World, pos: BlockPos, block: Block, fromPos: BlockPos, notify: Boolean) {
        if (!world.isClient) {
            if (world.isReceivingRedstonePower(pos)) {
                if (!state.get(TRIGGERED)) {
                    if (state.get(CONFIGURED)) {
                        world.setBlockState(pos, state.with(TRIGGERED, true))
                        val entity = world.getBlockEntity(pos) as TimeMachineRecallerBlockEntity?
                        val _tme = if (entity!!.configured) findTimeMachine(world.server!!.getWorld(entity.tmWorld), entity.rootPos!!, entity.trackerPos!!, pos) else Optional.empty()
                        if (_tme.isPresent) {
                            try {
                                _tme.get().checkAndRun(getTimelineForWorld(world.registryKey)!!, null, world.server!!)
                                world.setBlockState(pos, state.with(CONFIGURED, false))
                                entity.clear()
                            } catch (ignored: TimeMachineExecutionException) {
                                // TODO: Notify player somehow
                            }
                        } else {
                            world.setBlockState(pos, state.with(CONFIGURED, false))
                            entity.clear()
                        }
                    }
                }
            } else {
                if (state.get(TRIGGERED)) {
                    world.setBlockState(pos, state.with(TRIGGERED, false))
                }
            }
        }
    }

    private fun findTimeMachine(world: World?, rootPos: BlockPos, trackerPos: BlockPos, recallerPos: BlockPos): Optional<TimeMachineExecutor> {
        if (world!!.getBlockState(trackerPos).isOf(ModBlocks.TIME_MACHINE_TRACKER) && world.getBlockState(rootPos).block is TimeMachineControllerBlock) {
            return TimeMachineManager.generateExecutor(world, rootPos)
        } else if (world.getBlockState(recallerPos).isOf(ModBlocks.TIME_MACHINE_RECALLER)) {
            val entity = world.getBlockEntity(recallerPos) as TimeMachineRecallerBlockEntity?
            if (entity!!.tmWorld == world.registryKey && entity.rootPos == rootPos && entity.trackerPos == trackerPos) {
                return findTimeMachine(world.server!!.getWorld(entity.tmWorld), rootPos, trackerPos, recallerPos)
            }
        }
        return Optional.empty()
    }

    companion object {
        @JvmField
        val CONFIGURED: BooleanProperty = BooleanProperty.of("configured")
        val TRIGGERED: BooleanProperty = BooleanProperty.of("triggered")
    }
}