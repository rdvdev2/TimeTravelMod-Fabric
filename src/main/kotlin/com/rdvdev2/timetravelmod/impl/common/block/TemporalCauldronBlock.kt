package com.rdvdev2.timetravelmod.impl.common.block

import com.rdvdev2.timetravelmod.impl.ModItems
import com.rdvdev2.timetravelmod.impl.common.block.entity.TemporalCauldronBlockEntity
import net.minecraft.block.Block
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.BlockState
import net.minecraft.block.ShapeContext
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.ai.pathing.NavigationType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.function.BooleanBiFunction
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.World

class TemporalCauldronBlock(settings: Settings) : Block(settings), BlockEntityProvider {

    override fun getOutlineShape(state: BlockState, world: BlockView, pos: BlockPos, context: ShapeContext) = OUTLINE_SHAPE

    override fun getRaycastShape(state: BlockState, world: BlockView, pos: BlockPos) = RAY_TRACE_SHAPE

    override fun onUse(state: BlockState, world: World, pos: BlockPos, player: PlayerEntity, hand: Hand, hit: BlockHitResult): ActionResult {
        val itemStack = player.getStackInHand(hand)
        val entity = world.getBlockEntity(pos) as TemporalCauldronBlockEntity?
        if (itemStack.item === ModItems.TIME_CRYSTAL) {
            if (entity!!.doesFullBucketFit()) {
                if (!player.isCreative) itemStack.decrement(1)
                entity.addFullBucket()
                return ActionResult.SUCCESS
            }
        } else if (itemStack.isDamageable) { // TODO: Exceptions with a tag
            if (entity!!.itemInside.isEmpty) {
                entity.itemInside = itemStack
                player.setStackInHand(hand, ItemStack.EMPTY)
                return ActionResult.SUCCESS
            }
        } else if (itemStack.isEmpty) {
            if (!entity!!.itemInside.isEmpty) {
                player.setStackInHand(hand, entity.consumeItemInside())
                return ActionResult.SUCCESS
            }
        }
        return ActionResult.PASS
    }

    override fun afterBreak(world: World, player: PlayerEntity, pos: BlockPos, state: BlockState, blockEntity: BlockEntity?, stack: ItemStack) {
        super.afterBreak(world, player, pos, state, blockEntity, stack)
        dropStack(world, pos, (world.getBlockEntity(pos) as TemporalCauldronBlockEntity?)!!.itemInside)
    }

    override fun hasComparatorOutput(state: BlockState) = true

    override fun getComparatorOutput(state: BlockState, world: World, pos: BlockPos) = 0 // TODO: Clamp mbs

    override fun canPathfindThrough(state: BlockState, world: BlockView, pos: BlockPos, type: NavigationType) = false

    companion object {

        private val RAY_TRACE_SHAPE: VoxelShape = createCuboidShape(2.0, 4.0, 2.0, 14.0, 16.0, 14.0)
        private val OUTLINE_SHAPE: VoxelShape = VoxelShapes.combineAndSimplify(
            VoxelShapes.fullCube(),
            VoxelShapes.union(
                createCuboidShape(0.0, 0.0, 4.0, 16.0, 3.0, 12.0),
                createCuboidShape(4.0, 0.0, 0.0, 12.0, 3.0, 16.0),
                createCuboidShape(2.0, 0.0, 2.0, 14.0, 3.0, 14.0),
                RAY_TRACE_SHAPE
            ),
            BooleanBiFunction.ONLY_FIRST
        )
    }

    override fun createBlockEntity(world: BlockView) = TemporalCauldronBlockEntity()
}