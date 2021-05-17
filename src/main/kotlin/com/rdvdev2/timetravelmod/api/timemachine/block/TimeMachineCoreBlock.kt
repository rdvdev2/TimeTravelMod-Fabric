package com.rdvdev2.timetravelmod.api.timemachine.block

import com.rdvdev2.timetravelmod.impl.ModBlocks
import com.rdvdev2.timetravelmod.impl.common.block.entity.TimeMachineCoreBlockEntity
import net.minecraft.block.Block
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.BlockView
import net.minecraft.world.World
import net.minecraft.world.explosion.Explosion

class TimeMachineCoreBlock(settings: Settings?) : Block(settings), BlockEntityProvider {

    override fun onBreak(world: World, pos: BlockPos, state: BlockState, player: PlayerEntity) {
        super.onBreak(world, pos, state, player)
        if (!isReady(world, pos)) {
            val explosion = world.createExplosion(null, pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble(), 5f, Explosion.DestructionType.DESTROY)
            for (blockPos in explosion.affectedBlocks) world.setBlockState(blockPos, ModBlocks.ANOMALOUS_ATEMPORAL_VOID.defaultState)
        }
    }

    override fun createBlockEntity(world: BlockView): BlockEntity {
        return TimeMachineCoreBlockEntity()
    }

    private fun isReady(world: World, pos: BlockPos): Boolean {
        return (world.getBlockEntity(pos) as TimeMachineCoreBlockEntity?)!!.ready
    }
}