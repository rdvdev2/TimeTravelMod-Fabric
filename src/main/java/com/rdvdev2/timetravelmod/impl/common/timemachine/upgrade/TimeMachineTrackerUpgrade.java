package com.rdvdev2.timetravelmod.impl.common.timemachine.upgrade;

import com.rdvdev2.timetravelmod.api.dimension.ITimeline;
import com.rdvdev2.timetravelmod.api.timemachine.ITimeMachineUpgrade;
import com.rdvdev2.timetravelmod.impl.ModBlocks;
import com.rdvdev2.timetravelmod.impl.ModItems;
import com.rdvdev2.timetravelmod.impl.common.block.TimeMachineRecallerBlock;
import com.rdvdev2.timetravelmod.impl.common.block.entity.TimeMachineRecallerBlockEntity;
import com.rdvdev2.timetravelmod.impl.common.timemachine.TimeMachineStructure;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class TimeMachineTrackerUpgrade implements ITimeMachineUpgrade {

    @Override
    public Item getIcon() {
        return ModItems.TIME_MACHINE_TRACKER;
    }

    @Override
    public void beforeTeleporting(TimeMachineStructure structure, BlockPos root, BlockPos upgrade, World origWorld, World destWorld, ITimeline origTimeline, ITimeline destTimeline) {
        for (Direction dir: Direction.values()) {
            BlockPos recallerPos = upgrade.offset(dir);
            if (origWorld.getBlockState(recallerPos).isOf(ModBlocks.TIME_MACHINE_RECALLER)) {
                origWorld.setBlockState(recallerPos, origWorld.getBlockState(recallerPos).with(TimeMachineRecallerBlock.CONFIGURED, true));
                ((TimeMachineRecallerBlockEntity) origWorld.getBlockEntity(recallerPos)).configure(destWorld.getRegistryKey(), root, upgrade);
            }
        }
    }
}
