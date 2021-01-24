package com.rdvdev2.timetravelmod.impl.common.block;

import com.rdvdev2.timetravelmod.api.dimension.ITimeline;
import com.rdvdev2.timetravelmod.api.timemachine.block.TimeMachineControllerBlock;
import com.rdvdev2.timetravelmod.impl.ModBlocks;
import com.rdvdev2.timetravelmod.impl.common.block.entity.TimeMachineRecallerBlockEntity;
import com.rdvdev2.timetravelmod.impl.common.timemachine.TimeMachineExecutor;
import com.rdvdev2.timetravelmod.impl.common.timemachine.TimeMachineManager;
import com.rdvdev2.timetravelmod.impl.common.timemachine.exception.TimeMachineExecutionException;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class TimeMachineRecallerBlock extends Block implements BlockEntityProvider {

    public static final BooleanProperty CONFIGURED = BooleanProperty.of("configured");
    public static final BooleanProperty TRIGGERED = BooleanProperty.of("triggered");

    public TimeMachineRecallerBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(CONFIGURED, false).with(TRIGGERED, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(CONFIGURED).add(TRIGGERED);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockView world) {
        return new TimeMachineRecallerBlockEntity();
    }

    @Override
    public boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        if (!world.isClient) {
            if (world.isReceivingRedstonePower(pos)) {
                if (!state.get(TRIGGERED)) {
                    if (state.get(CONFIGURED)) {
                        world.setBlockState(pos, state.with(TRIGGERED, true));
                        TimeMachineRecallerBlockEntity entity = (TimeMachineRecallerBlockEntity) world.getBlockEntity(pos);
                        Optional<TimeMachineExecutor> _tme = findTimeMachine(world.getServer().getWorld(entity.getTmWorld()), entity.getRootPos(), entity.getTrackerPos(), pos);
                        if (_tme.isPresent()) {
                            try {
                                _tme.get().checkAndRun(ITimeline.getTimelineForWorld(world.getRegistryKey()), null, world.getServer());
                                world.setBlockState(pos, state.with(CONFIGURED, false));
                                entity.clear();
                            } catch (TimeMachineExecutionException ignored) {
                                // TODO: Notify player somehow
                            }
                        } else {
                            world.setBlockState(pos, state.with(CONFIGURED, false));
                            entity.clear();
                        }
                    }
                }
            } else {
                if (state.get(TRIGGERED)) {
                    world.setBlockState(pos, state.with(TRIGGERED, false));
                }
            }
        }
    }

    private Optional<TimeMachineExecutor> findTimeMachine(World world, BlockPos rootPos, BlockPos trackerPos, BlockPos recallerPos) {
        if (world.getBlockState(trackerPos).isOf(ModBlocks.TIME_MACHINE_TRACKER) && world.getBlockState(rootPos).getBlock() instanceof TimeMachineControllerBlock) {
            return TimeMachineManager.getInstance().generateExecutor(world, rootPos);
        } else if (world.getBlockState(recallerPos).isOf(ModBlocks.TIME_MACHINE_RECALLER)) {
            TimeMachineRecallerBlockEntity entity = (TimeMachineRecallerBlockEntity) world.getBlockEntity(recallerPos);
            if (entity.getTmWorld().equals(world.getRegistryKey()) && entity.getRootPos().equals(rootPos) && entity.getTrackerPos().equals(trackerPos)) {
                return findTimeMachine(world.getServer().getWorld(entity.getTmWorld()), rootPos, trackerPos, recallerPos);
            }
        }
        return Optional.empty();
    }
}
