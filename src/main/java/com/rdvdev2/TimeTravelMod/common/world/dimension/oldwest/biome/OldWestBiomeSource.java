package com.rdvdev2.TimeTravelMod.common.world.dimension.oldwest.biome;

import com.google.common.collect.ImmutableSet;
import com.rdvdev2.TimeTravelMod.ModBiomes;
import com.rdvdev2.TimeTravelMod.common.world.layer.OldWestLayers;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.source.BiomeLayerSampler;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.VanillaLayeredBiomeSourceConfig;

import java.util.Set;

public class OldWestBiomeSource extends BiomeSource {
    private final BiomeLayerSampler biomeSampler;
    private static final Set<Biome> BIOMES = ImmutableSet.of(ModBiomes.OLDWEST, Biomes.BADLANDS, Biomes.BADLANDS_PLATEAU, Biomes.WOODED_BADLANDS_PLATEAU, Biomes.ERODED_BADLANDS, Biomes.MODIFIED_BADLANDS_PLATEAU, Biomes.MODIFIED_WOODED_BADLANDS_PLATEAU, Biomes.OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.WARM_OCEAN, Biomes.DEEP_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.DEEP_WARM_OCEAN, Biomes.RIVER, Biomes.BEACH);

    public OldWestBiomeSource(VanillaLayeredBiomeSourceConfig config) {
        super(BIOMES);
        this.biomeSampler = OldWestLayers.build(config.getSeed(), config.getGeneratorType(), config.getGeneratorSettings());
    }

    @Override
    public Biome getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
        return this.biomeSampler.sample(biomeX, biomeZ);
    }
}
