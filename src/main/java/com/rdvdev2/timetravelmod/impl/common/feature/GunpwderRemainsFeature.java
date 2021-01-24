package com.rdvdev2.timetravelmod.impl.common.feature;

import com.rdvdev2.timetravelmod.impl.ModBlocks;
import com.rdvdev2.timetravelmod.impl.common.block.GunpowderWireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GunpwderRemainsFeature extends Feature<DefaultFeatureConfig> {

    public GunpwderRemainsFeature() {
        super(DefaultFeatureConfig.CODEC);
    }

    @Override
    public boolean generate(StructureWorldAccess world, ChunkGenerator chunkGenerator, Random random, BlockPos pos, DefaultFeatureConfig config) {
        if (world.isWater(pos)) return false;
        List<BlockPos> postUpdate = new ArrayList<>();
        for (int i = random.nextInt(8) + 2; i > 0; i--) {
            BlockPos genPos = pos.add(random.nextInt(5) - 2, 0, random.nextInt(5) - 2);
            genPos = world.getTopPosition(Heightmap.Type.WORLD_SURFACE_WG, genPos);
            if (world.isWater(genPos.down())) continue;
            if (random.nextFloat() > 0.2) {
                setBlockState(world, genPos, ModBlocks.GUNPOWDER_WIRE.getDefaultState());
                postUpdate.add(genPos);
            } else {
                setBlockState(world, genPos, Blocks.TNT.getDefaultState());
            }
        }
        for (BlockPos updatePos: postUpdate) {
            BlockState bs = ((GunpowderWireBlock) ModBlocks.GUNPOWDER_WIRE).getSideState(world, ModBlocks.GUNPOWDER_WIRE.getDefaultState(), updatePos);
            setBlockState(world, updatePos, bs);
        }
        return true;
    }
}
