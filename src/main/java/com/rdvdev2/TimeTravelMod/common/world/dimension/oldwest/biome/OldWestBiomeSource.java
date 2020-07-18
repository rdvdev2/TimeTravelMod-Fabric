package com.rdvdev2.TimeTravelMod.common.world.dimension.oldwest.biome;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.rdvdev2.TimeTravelMod.ModBiomes;
import com.rdvdev2.TimeTravelMod.common.world.layer.OldWestLayers;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.source.BiomeLayerSampler;
import net.minecraft.world.biome.source.BiomeSource;

import java.util.List;

public class OldWestBiomeSource extends BiomeSource {

    public static final Codec<OldWestBiomeSource> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.LONG.fieldOf("seed").stable().forGetter((oldWestBiomeSource) -> {
            return oldWestBiomeSource.seed;
        }), Codec.BOOL.optionalFieldOf("legacy_biome_init_layer", false, Lifecycle.stable()).forGetter((oldWestBiomeSource) -> {
            return oldWestBiomeSource.legacyBiomeInitLayer;
        }), Codec.BOOL.fieldOf("large_biomes").withDefault(false).stable().forGetter((oldWestBiomeSource) -> {
            return oldWestBiomeSource.largeBiomes;
        })).apply(instance, instance.stable(OldWestBiomeSource::new));
    });
    private final BiomeLayerSampler biomeSampler;
    private static final List<Biome> BIOMES = ImmutableList.of(ModBiomes.OLDWEST, Biomes.BADLANDS, Biomes.BADLANDS_PLATEAU, Biomes.WOODED_BADLANDS_PLATEAU, Biomes.ERODED_BADLANDS, Biomes.MODIFIED_BADLANDS_PLATEAU, Biomes.MODIFIED_WOODED_BADLANDS_PLATEAU, Biomes.OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.WARM_OCEAN, Biomes.DEEP_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.DEEP_WARM_OCEAN, Biomes.RIVER, Biomes.BEACH);
    private final long seed;
    private final boolean legacyBiomeInitLayer;
    private final boolean largeBiomes;

    public OldWestBiomeSource(long seed, boolean legacyBiomeInitLayer, boolean largeBiomes) {
        super(BIOMES);
        this.seed = seed;
        this.legacyBiomeInitLayer = legacyBiomeInitLayer;
        this.largeBiomes = largeBiomes;
        this.biomeSampler = OldWestLayers.build(seed, legacyBiomeInitLayer, largeBiomes ? 6 : 4, 4);
    }

    @Override
    protected Codec<? extends BiomeSource> method_28442() {
        return CODEC;
    }

    @Environment(EnvType.CLIENT)
    @Override
    public BiomeSource withSeed(long seed) {
        return new OldWestBiomeSource(seed, this.legacyBiomeInitLayer, this.largeBiomes);
    }

    @Override
    public Biome getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
        return this.biomeSampler.sample(biomeX, biomeZ);
    }
}
