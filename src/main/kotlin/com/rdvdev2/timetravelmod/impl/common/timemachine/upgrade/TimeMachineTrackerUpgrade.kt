package com.rdvdev2.timetravelmod.impl.common.timemachine.upgrade

import com.rdvdev2.timetravelmod.api.dimension.ITimeline
import com.rdvdev2.timetravelmod.api.timemachine.ITimeMachineUpgrade
import com.rdvdev2.timetravelmod.impl.ModBlocks
import com.rdvdev2.timetravelmod.impl.ModItems
import com.rdvdev2.timetravelmod.impl.common.block.TimeMachineRecallerBlock
import com.rdvdev2.timetravelmod.impl.common.block.entity.TimeMachineRecallerBlockEntity
import com.rdvdev2.timetravelmod.impl.common.timemachine.TimeMachineStructure
import net.minecraft.item.Item
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World

class TimeMachineTrackerUpgrade : ITimeMachineUpgrade {

    override val icon: Item
        get() = ModItems.TIME_MACHINE_TRACKER

    override fun beforeTeleporting(structure: TimeMachineStructure?, root: BlockPos?, upgrade: BlockPos?, origWorld: World?, destWorld: World?, origTimeline: ITimeline?, destTimeline: ITimeline?) {
        for (dir in Direction.values()) {
            val recallerPos = upgrade!!.offset(dir)
            if (origWorld!!.getBlockState(recallerPos).isOf(ModBlocks.TIME_MACHINE_RECALLER)) {
                origWorld.setBlockState(recallerPos, origWorld.getBlockState(recallerPos).with(TimeMachineRecallerBlock.CONFIGURED, true))
                (origWorld.getBlockEntity(recallerPos) as TimeMachineRecallerBlockEntity?)!!.configure(destWorld!!.registryKey, root!!, upgrade)
            }
        }
    }
}