package com.rdvdev2.TimeTravelMod.common.world.generator;

import com.rdvdev2.TimeTravelMod.ModBlocks;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

import java.util.Random;

public class GunpowderFeature extends Feature<DefaultFeatureConfig> {

    public GunpowderFeature() {
        super(DefaultFeatureConfig::deserialize);
    }

    @Override
    public boolean generate(IWorld world, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos pos, DefaultFeatureConfig noFeatureConfig) {
        if (world.isWater(pos)) return false;
        for (int i = random.nextInt(8) + 2; i > 0; i--) {
            BlockPos thisPos = pos.add(random.nextInt(5)-2, 0, random.nextInt(5)-2);
            thisPos = world.getTopPosition(Heightmap.Type.WORLD_SURFACE_WG, thisPos);
            if (random.nextFloat() > 0.2) {
                setBlockState(world, thisPos, ModBlocks.GUNPOWDER_WIRE.getDefaultState());
            } else {
                setBlockState(world, thisPos, Blocks.TNT.getDefaultState());
            }
        }
        return true;
    }
}
