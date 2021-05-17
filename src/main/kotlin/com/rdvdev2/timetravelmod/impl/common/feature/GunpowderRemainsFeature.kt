package com.rdvdev2.timetravelmod.impl.common.feature

import com.rdvdev2.timetravelmod.impl.ModBlocks
import com.rdvdev2.timetravelmod.impl.common.block.GunpowderWireBlock
import net.minecraft.block.Blocks
import net.minecraft.util.math.BlockPos
import net.minecraft.world.Heightmap
import net.minecraft.world.StructureWorldAccess
import net.minecraft.world.gen.chunk.ChunkGenerator
import net.minecraft.world.gen.feature.DefaultFeatureConfig
import net.minecraft.world.gen.feature.Feature
import java.util.*

class GunpowderRemainsFeature : Feature<DefaultFeatureConfig>(DefaultFeatureConfig.CODEC) {

    override fun generate(world: StructureWorldAccess, chunkGenerator: ChunkGenerator, random: Random, pos: BlockPos, config: DefaultFeatureConfig?): Boolean {
        if (world.isWater(pos)) return false
        val postUpdate: MutableList<BlockPos> = ArrayList()
        for (i in random.nextInt(8) + 2 downTo 1) {
            var genPos = pos.add(random.nextInt(5) - 2, 0, random.nextInt(5) - 2)
            genPos = world.getTopPosition(Heightmap.Type.WORLD_SURFACE_WG, genPos)
            if (world.isWater(genPos.down())) continue
            if (random.nextFloat() > 0.2) {
                setBlockState(world, genPos, ModBlocks.GUNPOWDER_WIRE.defaultState)
                postUpdate.add(genPos)
            } else {
                setBlockState(world, genPos, Blocks.TNT.defaultState)
            }
        }
        for (updatePos in postUpdate) {
            val bs = (ModBlocks.GUNPOWDER_WIRE as GunpowderWireBlock).getSideState(world, ModBlocks.GUNPOWDER_WIRE.getDefaultState(), updatePos)
            setBlockState(world, updatePos, bs)
        }
        return true
    }
}