package com.rdvdev2.timetravelmod.impl.common.block;

import com.rdvdev2.timetravelmod.impl.ModItems;
import com.rdvdev2.timetravelmod.impl.ModTriggers;
import com.rdvdev2.timetravelmod.impl.common.block.entity.AnomalousAtemporalVoidBlockEntity;
import com.rdvdev2.timetravelmod.mixin.common.IDamageSource;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class AnomalousAtemporalVoidBlock extends Block implements BlockEntityProvider {

    private static final DamageSource DAMAGE_SOURCE = ((IDamageSource) IDamageSource.create("temporal_anomaly")).callSetUnblockable();

    public AnomalousAtemporalVoidBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (world.isClient) return;
        entity.damage(DAMAGE_SOURCE, Integer.MAX_VALUE);
        if (entity instanceof ServerPlayerEntity) ModTriggers.TEMPORAL_EXPLOSION.trigger((ServerPlayerEntity) entity);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.cuboid(VoxelShapes.fullCube().getBoundingBox().shrink(0.1, 0.1, 0.1));
    }

    @Override
    public boolean canReplace(BlockState state, ItemPlacementContext context) {
        return context.getStack().isItemEqual(ModItems.REINFORCED_HEAVY_BLOCK.getDefaultStack()) || super.canReplace(state, context);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockView world) {
        return new AnomalousAtemporalVoidBlockEntity();
    }
}
