package com.rdvdev2.timetravelmod.impl.common.block;

import com.rdvdev2.timetravelmod.impl.ModItems;
import com.rdvdev2.timetravelmod.impl.common.block.entity.TemporalCauldronBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import javax.naming.directory.ModificationItem;

import static net.minecraft.block.CauldronBlock.LEVEL;

public class TemporalCauldronBlock extends Block implements BlockEntityProvider {
    private static final VoxelShape RAY_TRACE_SHAPE;
    protected static final VoxelShape OUTLINE_SHAPE;

    public TemporalCauldronBlock(Settings settings) {
        super(settings);
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return OUTLINE_SHAPE;
    }

    public VoxelShape getRaycastShape(BlockState state, BlockView world, BlockPos pos) {
        return RAY_TRACE_SHAPE;
    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack itemStack = player.getStackInHand(hand);
        TemporalCauldronBlockEntity entity = (TemporalCauldronBlockEntity) world.getBlockEntity(pos);
        if (itemStack.getItem() == ModItems.TIME_CRYSTAL) {
            if (entity.doesFullBucketFit()) {
                if (!player.isCreative()) itemStack.decrement(1);
                entity.addFullBucket();
                return ActionResult.SUCCESS;
            }
        } else if (itemStack.isDamageable()) { // TODO: Exceptions with a tag
            if (entity.getItemInside().isEmpty()) {
                entity.setItemInside(itemStack);
                player.setStackInHand(hand, ItemStack.EMPTY);
                return ActionResult.SUCCESS;
            }
        } else if (itemStack.isEmpty()) {
            if (!entity.getItemInside().isEmpty()) {
                player.setStackInHand(hand, entity.consumeItemInside());
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }



    @Override
    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack stack) {
        super.afterBreak(world, player, pos, state, blockEntity, stack);
        dropStack(world, pos, ((TemporalCauldronBlockEntity) world.getBlockEntity(pos)).getItemInside());
    }

    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return 0; // TODO: Clamp mbs
    }

    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return false;
    }

    static {
        RAY_TRACE_SHAPE = createCuboidShape(2.0D, 4.0D, 2.0D, 14.0D, 16.0D, 14.0D);
        OUTLINE_SHAPE = VoxelShapes.combineAndSimplify(VoxelShapes.fullCube(), VoxelShapes.union(createCuboidShape(0.0D, 0.0D, 4.0D, 16.0D, 3.0D, 12.0D), createCuboidShape(4.0D, 0.0D, 0.0D, 12.0D, 3.0D, 16.0D), createCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 3.0D, 14.0D), RAY_TRACE_SHAPE), BooleanBiFunction.ONLY_FIRST);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockView world) {
        return new TemporalCauldronBlockEntity();
    }
}
