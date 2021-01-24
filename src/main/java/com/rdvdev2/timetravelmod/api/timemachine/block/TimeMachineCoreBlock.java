package com.rdvdev2.timetravelmod.api.timemachine.block;

import com.rdvdev2.timetravelmod.impl.ModBlocks;
import com.rdvdev2.timetravelmod.impl.common.block.entity.TimeMachineCoreBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;

public class TimeMachineCoreBlock extends Block implements BlockEntityProvider {

    public TimeMachineCoreBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBreak(world, pos, state, player);
        if (!isReady(world, pos)) {
            Explosion explosion = world.createExplosion(null, pos.getX(), pos.getY(), pos.getZ(), 5, Explosion.DestructionType.DESTROY);
            for (BlockPos blockPos : explosion.getAffectedBlocks())
                world.setBlockState(blockPos, ModBlocks.ANOMALOUS_ATEMPORAL_VOID.getDefaultState());
        }
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockView world) {
        return new TimeMachineCoreBlockEntity();
    }

    private boolean isReady(World world, BlockPos pos) {
        return ((TimeMachineCoreBlockEntity) world.getBlockEntity(pos)).isReady();
    }
}
