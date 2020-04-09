package com.rdvdev2.TimeTravelMod.common.timemachine.hook;

import com.rdvdev2.TimeTravelMod.ModBlocks;
import com.rdvdev2.TimeTravelMod.ModTimeMachines;
import com.rdvdev2.TimeTravelMod.api.timemachine.TimeMachine;
import com.rdvdev2.TimeTravelMod.api.timemachine.upgrade.TimeMachineHook;
import com.rdvdev2.TimeTravelMod.common.block.TimeMachineRecallerBlock;
import com.rdvdev2.TimeTravelMod.common.block.blockentity.TimeMachineRecallerBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.Set;

public class TrackerHooks {

    public static final TimeMachineHook<?>[] HOOKS = {new TeleporterHook()};

    public static class TeleporterHook implements TimeMachineHook.TeleporterTasks {

        @Override
        public void run(TimeMachine tm, Entity entity, World worldIn, World worldOut, BlockPos controllerPos, Direction side, boolean shouldBuild) {
            tm.removeHooks().teleporterTasks(entity, worldIn, worldOut, controllerPos, side, shouldBuild);
            if (shouldBuild) {
                Set<BlockPos> upgrades = tm.getUpgradePos(ModTimeMachines.Upgrades.TRACKER);
                for (BlockPos pos: upgrades) {
                    for (Direction direction: Direction.values()) {
                        BlockPos recaller = pos.offset(direction);
                        if (worldOut.getBlockState(recaller).getBlock() == ModBlocks.TIME_MACHINE_RECALLER) {
                            worldOut.setBlockState(recaller, worldOut.getBlockState(recaller).with(TimeMachineRecallerBlock.CONFIGURED, true));
                            BlockEntity tile = worldOut.getBlockEntity(recaller);
                            if (tile instanceof TimeMachineRecallerBlockEntity) {
                                ((TimeMachineRecallerBlockEntity) tile).setControllerPos(controllerPos);
                                ((TimeMachineRecallerBlockEntity) tile).setSide(side);
                                ((TimeMachineRecallerBlockEntity) tile).setDest(worldIn.getDimension().getType());
                            }
                        }
                    }
                }
            }
        }
    }
}
