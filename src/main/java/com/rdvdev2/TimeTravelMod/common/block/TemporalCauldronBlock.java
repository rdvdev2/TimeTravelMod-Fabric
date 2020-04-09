package com.rdvdev2.TimeTravelMod.common.block;

import com.rdvdev2.TimeTravelMod.ModBlocks;
import com.rdvdev2.TimeTravelMod.ModItems;
import com.rdvdev2.TimeTravelMod.ModTriggers;
import com.rdvdev2.TimeTravelMod.common.block.blockentity.TemporalCauldronTileEntity;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tools.FabricToolTags;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BooleanBiFunction;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class TemporalCauldronBlock extends Block implements BlockEntityProvider {

    public static final IntProperty LEVEL = IntProperty.of("level", 0, 3);
    protected static final VoxelShape INSIDE;
    protected static final VoxelShape WALLS;

    public TemporalCauldronBlock() {
        super(FabricBlockSettings.of(Material.METAL, MaterialColor.STONE).breakByTool(FabricToolTags.PICKAXES, 1).hardness(2.0F).nonOpaque().build());
        this.setDefaultState(this.getStateManager().getDefaultState().with(LEVEL, Integer.valueOf(0)));
    }

    @Override
    public ActionResult onUse(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockHitResult blockRayTraceResult) {
        super.onUse(state, worldIn, pos, playerIn, hand, blockRayTraceResult);
        TemporalCauldronTileEntity te = (TemporalCauldronTileEntity) worldIn.getBlockEntity(pos);
        ItemStack playerItemStack = playerIn.getStackInHand(hand);
        if (te == null) {
            return ActionResult.FAIL;
        }
        if (!playerItemStack.isEmpty() && !playerItemStack.isItemEqual(new ItemStack(ModItems.TIME_CRYSTAL)) && playerItemStack.isDamaged() && !te.containsItem()) {
            if (!worldIn.isClient) {
                ItemStack copy = playerItemStack.copy();
                playerItemStack.increment(-1);
                playerIn.setStackInHand(hand, playerItemStack);
                te.putItem(copy);
            }
        } else if (playerItemStack.isItemEqual(new ItemStack(ModItems.TIME_CRYSTAL)) && !te.containsCrystal()) {
            if(!worldIn.isClient) {
                if (!playerIn.isCreative())
                    playerIn.setStackInHand(hand, new ItemStack(playerItemStack.getItem(), playerItemStack.getCount() - 1));
                te.putCrystal(new ItemStack(ModItems.TIME_CRYSTAL, 1));
            }
        } else if (te.containsItem()) {
            if(!worldIn.isClient) {
                ItemStack item = te.removeItem();
                if (!item.isDamaged() && playerIn instanceof ServerPlayerEntity) ModTriggers.BETTER_THAN_MENDING.trigger((ServerPlayerEntity) playerIn);
                worldIn.spawnEntity(new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), item));
            }
        } else return ActionResult.PASS; return ActionResult.SUCCESS;
    }
    
    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBreak(world, pos, state, player);
        if (!world.isClient) {
            ItemStack item = ((TemporalCauldronTileEntity)(world.getBlockEntity(pos))).removeItem();
            if (!item.isEmpty() && !item.isDamaged() && player instanceof ServerPlayerEntity) ModTriggers.BETTER_THAN_MENDING.trigger((ServerPlayerEntity) player);
            world.spawnEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), item));
        }
    }
    
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext context) {
        return WALLS;
    }
    
    @Override
    public VoxelShape getRayTraceShape(BlockState p_199600_1_, BlockView p_199600_2_, BlockPos p_199600_3_) {
        return INSIDE;
    }
    
    public void setTimeFluidLevel(World worldIn, BlockPos pos, BlockState state, int level)
    {
        worldIn.setBlockState(pos, state.with(LEVEL, Integer.valueOf(MathHelper.clamp(level, 0, 3))), 2);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(LEVEL);
    }
    
    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        return new ItemStack(Item.fromBlock(ModBlocks.TEMPORAL_CAULDRON));
    }
    
    @Override
    public BlockEntity createBlockEntity(BlockView view) {
        return new TemporalCauldronTileEntity();
    }
    
    @Override
    public void onBlockRemoved(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            super.onBlockRemoved(state, world, pos, newState, moved);
            world.removeBlockEntity(pos);
        }
    }
    
    static {
        INSIDE = Block.createCuboidShape(2.0D, 4.0D, 2.0D, 14.0D, 16.0D, 14.0D);
        WALLS = VoxelShapes.combineAndSimplify(VoxelShapes.fullCube(), VoxelShapes.union(createCuboidShape(0.0D, 0.0D, 4.0D, 16.0D, 3.0D, 12.0D), createCuboidShape(4.0D, 0.0D, 0.0D, 12.0D, 3.0D, 16.0D), createCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 3.0D, 14.0D), INSIDE), BooleanBiFunction.ONLY_FIRST);
    }
}
