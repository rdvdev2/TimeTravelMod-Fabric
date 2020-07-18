package com.rdvdev2.TimeTravelMod.common.block;

import com.rdvdev2.TimeTravelMod.ModBlocks;
import com.rdvdev2.TimeTravelMod.ModTriggers;
import com.rdvdev2.TimeTravelMod.mixin.IDamageSource;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class TemporalExplosionBlock extends Block {

    public static DamageSource damage = ((IDamageSource) IDamageSource.create("temporalerror")).callSetUnblockable();

    public TemporalExplosionBlock() {
        super(FabricBlockSettings.of(Material.PORTAL).sounds(BlockSoundGroup.METAL).strength(-1, -1).lightLevel(0).nonOpaque().build());
    }

    @Override
    public void onEntityCollision(BlockState p_196262_1_, World p_196262_2_, BlockPos p_196262_3_, Entity entity) {
        entity.damage(damage, 1000000);
        if (entity instanceof ServerPlayerEntity) ModTriggers.TEMPORAL_EXPLOSION.trigger((ServerPlayerEntity) entity);
    }
    
    
    
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
        return VoxelShapes.cuboid(VoxelShapes.fullCube().getBoundingBox().shrink(0.1, 0.1, 0.1));
    }
    
    
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (ItemStack.areItemsEqual(player.inventory.getMainHandStack(), new ItemStack(ModBlocks.REINFORCED_HEAVY_BLOCK, player.inventory.getMainHandStack().getCount()))) {
            if(!player.isCreative()) player.inventory.getMainHandStack().increment(-1);
            world.setBlockState(pos, ModBlocks.REINFORCED_HEAVY_BLOCK.getDefaultState());
            world.playSound(null, pos, SoundEvents.BLOCK_METAL_PLACE, SoundCategory.BLOCKS, 3.0F, 1);
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }
}
