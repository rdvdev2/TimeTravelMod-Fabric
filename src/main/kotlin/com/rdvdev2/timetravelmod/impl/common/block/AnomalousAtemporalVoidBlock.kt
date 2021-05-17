package com.rdvdev2.timetravelmod.impl.common.block

import com.rdvdev2.timetravelmod.impl.ModItems
import com.rdvdev2.timetravelmod.impl.ModTriggers
import com.rdvdev2.timetravelmod.impl.common.block.entity.AnomalousAtemporalVoidBlockEntity
import com.rdvdev2.timetravelmod.mixin.common.IDamageSource
import net.minecraft.block.Block
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.BlockState
import net.minecraft.block.ShapeContext
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.Entity
import net.minecraft.item.ItemPlacementContext
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.World

class AnomalousAtemporalVoidBlock(settings: Settings) : Block(settings), BlockEntityProvider {

    override fun onEntityCollision(state: BlockState, world: World, pos: BlockPos, entity: Entity) {
        if (world.isClient) return
        entity.damage(DAMAGE_SOURCE, Int.MAX_VALUE.toFloat())
        if (entity is ServerPlayerEntity) ModTriggers.TEMPORAL_EXPLOSION.trigger(entity)
    }

    override fun getCollisionShape(state: BlockState, world: BlockView, pos: BlockPos, context: ShapeContext): VoxelShape {
        return VoxelShapes.cuboid(VoxelShapes.fullCube().boundingBox.shrink(0.1, 0.1, 0.1))
    }

    override fun canReplace(state: BlockState, context: ItemPlacementContext): Boolean {
        return context.stack.isItemEqual(ModItems.REINFORCED_HEAVY_BLOCK.defaultStack) || super.canReplace(state, context)
    }

    override fun createBlockEntity(world: BlockView): BlockEntity {
        return AnomalousAtemporalVoidBlockEntity()
    }

    companion object {
        private val DAMAGE_SOURCE = (IDamageSource.create("temporal_anomaly") as IDamageSource).callSetUnblockable()
    }
}