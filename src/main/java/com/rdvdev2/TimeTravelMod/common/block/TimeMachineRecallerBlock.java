package com.rdvdev2.TimeTravelMod.common.block;

import com.rdvdev2.TimeTravelMod.ModRegistries;
import com.rdvdev2.TimeTravelMod.api.dimension.TimeLine;
import com.rdvdev2.TimeTravelMod.api.timemachine.TimeMachine;
import com.rdvdev2.TimeTravelMod.api.timemachine.block.TimeMachineControlPanelBlock;
import com.rdvdev2.TimeTravelMod.api.timemachine.exception.IncompatibleTimeMachineHooksException;
import com.rdvdev2.TimeTravelMod.common.block.blockentity.TimeMachineRecallerBlockEntity;
import com.rdvdev2.TimeTravelMod.common.timemachine.TimeMachineEntityPlacer;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.fabricmc.fabric.api.tools.FabricToolTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.List;

import static com.rdvdev2.TimeTravelMod.common.networking.DimensionTpPKT.applyCorruption;

public class TimeMachineRecallerBlock extends Block implements BlockEntityProvider {

    public static final BooleanProperty CONFIGURED = BooleanProperty.of("configured");
    public static final BooleanProperty TRIGGERED = BooleanProperty.of("triggered");

    public TimeMachineRecallerBlock() {
        super(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).hardness(3f).lightLevel(0 / 16).nonOpaque().breakByTool(FabricToolTags.PICKAXES, 2).build());
        setDefaultState(getStateManager().getDefaultState().with(CONFIGURED, false).with(TRIGGERED, false));
    }
    
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(CONFIGURED).add(TRIGGERED);
    }
    
    
    @Override
    public BlockEntity createBlockEntity(BlockView view) {
        return new TimeMachineRecallerBlockEntity();
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (newState.getBlock() != this) {
            world.removeBlockEntity(pos);
        }
    }
    
    @Override
    public boolean emitsRedstonePower(BlockState state) {
        return true;
    }
    
    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block neighbourBlock, BlockPos neighbourPos, boolean bool) {
        super.neighborUpdate(state, world, pos, neighbourBlock, neighbourPos, bool);
        if (!world.isClient) {
            if (world.isReceivingRedstonePower(pos)) {
                if (!(Boolean) state.get(TRIGGERED)) {
                    world.setBlockState(pos, state.with(TRIGGERED, true));
                    if (state.get(CONFIGURED)) {
                        BlockEntity tile = world.getBlockEntity(pos);
                        if (world instanceof ServerWorld && tile instanceof TimeMachineRecallerBlockEntity) {
                            BlockPos controllerPos = ((TimeMachineRecallerBlockEntity) tile).getControllerPos();
                            Direction side = ((TimeMachineRecallerBlockEntity) tile).getSide();
                            RegistryKey<World> searchDim = ((TimeMachineRecallerBlockEntity) tile).getDest();
                            boolean ret = searchRecall((ServerWorld) world, world.getServer().getWorld(searchDim), controllerPos, side, pos);
                            world.setBlockState(pos, state.with(CONFIGURED, ret).with(TRIGGERED, true));
                        }
                    }
                }
            } else {
                world.setBlockState(pos, state.with(TRIGGERED, false));
            }
        }
    }

    private boolean searchRecall(ServerWorld origin, ServerWorld searchWorld, BlockPos controllerPos, Direction side, BlockPos recallerPos) {
        Block controllerBlock = searchWorld.getBlockState(controllerPos).getBlock();
        if (controllerBlock instanceof TimeMachineControlPanelBlock) {
            TimeMachine tm = ((TimeMachineControlPanelBlock) controllerBlock).getTimeMachine(searchWorld.getBlockState(controllerPos));
            searchWorld.setChunkForced(controllerPos.getX() >> 4, controllerPos.getZ() >> 4, true);
            boolean ret = tryRecall(tm, searchWorld, origin, controllerPos, side);
            searchWorld.setChunkForced(controllerPos.getX() >> 4, controllerPos.getZ() >> 4, false);
            return !ret;
        } else {
            BlockEntity tile = searchWorld.getBlockEntity(recallerPos);
            if (tile instanceof TimeMachineRecallerBlockEntity) {
                BlockPos _controllerPos = ((TimeMachineRecallerBlockEntity) tile).getControllerPos();
                Direction _side = ((TimeMachineRecallerBlockEntity) tile).getSide();
                RegistryKey<World> _searchDim = ((TimeMachineRecallerBlockEntity) tile).getDest();
                if (controllerPos.equals(_controllerPos) && side.equals(_side)) {
                    return searchRecall(origin, origin.getServer().getWorld(_searchDim), controllerPos, side, recallerPos);
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
    }

    private boolean tryRecall(TimeMachine tm, ServerWorld foundWorld, ServerWorld recallWorld, BlockPos controllerPos, Direction side) {
        RegistryKey<World> foundDim = foundWorld.getRegistryKey();
        TimeLine tl = null;
        for (TimeLine _tl : ModRegistries.TIME_LINES) {
            if (_tl.getWorldKey() == foundDim) {
                tl = _tl;
                break;
            }
        }
        if (tl == null) return false;
        try {
            tm = tm.hook(foundWorld, controllerPos, side);
        } catch (IncompatibleTimeMachineHooksException e) {
            return false;
        }
        List<Entity> entities = tm.getEntitiesInside(foundWorld, controllerPos, side);
        if (foundWorld.isChunkLoaded(controllerPos) &&
            tm.isBuilt(foundWorld, controllerPos, side) &&
            tm.isCooledDown(foundWorld, controllerPos, side) &&
            !tm.isOverloaded(foundWorld, controllerPos, side)) {
                if (tm.getTier() >= tl.getMinTier()) {
                    applyCorruption(tm, foundWorld, recallWorld, foundWorld.getServer());
                    tm.teleporterTasks(null, recallWorld, foundWorld, controllerPos, side, true);
                    final TimeMachine finalTm = tm;
                    entities.forEach(entity -> FabricDimensions.teleport(entity, recallWorld, new TimeMachineEntityPlacer(finalTm, foundWorld, controllerPos, side, false)));
                    return true;
                } else {
                    return false;
                }
        } else {
            return false;
        }
    }
}
