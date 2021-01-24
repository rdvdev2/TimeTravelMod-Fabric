package com.rdvdev2.timetravelmod.impl.common.dimension.oldwest;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.rdvdev2.timetravelmod.impl.Mod;
import com.rdvdev2.timetravelmod.impl.ModBiomes;
import com.rdvdev2.timetravelmod.impl.common.dimension.ModdedBiomeLayerSampler;
import com.rdvdev2.timetravelmod.mixin.common.IVanillaLayeredBiomeSource;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.RegistryLookupCodec;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeLayerSampler;
import net.minecraft.world.biome.source.BiomeSource;

import java.util.ArrayList;
import java.util.List;

public class OldWestBiomeSource extends BiomeSource {

    public static final Codec<OldWestBiomeSource> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.LONG.fieldOf("seed").stable().forGetter(OldWestBiomeSource::getSeed),
                    RegistryLookupCodec.of(Registry.BIOME_KEY).forGetter(OldWestBiomeSource::getBiomeRegistry)
            ).apply(instance, OldWestBiomeSource::new));

    private final ModdedBiomeLayerSampler biomeSampler;
    private static final List<RegistryKey<Biome>> BIOMES;
    private final long seed;
    private final Registry<Biome> biomeRegistry;

    public OldWestBiomeSource(long seed, Registry<Biome> biomeRegistry) {
        super(BIOMES.stream().map(key -> () -> biomeRegistry.getOrThrow(key)));
        this.seed = seed;
        this.biomeRegistry = biomeRegistry;
        this.biomeSampler = OldWestLayers.build(seed, 4, 4, biomeRegistry);
    }

    @Override
    protected Codec<? extends BiomeSource> getCodec() {
        return CODEC;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public BiomeSource withSeed(long seed) {
        return new OldWestBiomeSource(seed, biomeRegistry);
    }

    @Override
    public Biome getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
        return this.biomeSampler.sample(this.biomeRegistry, biomeX, biomeZ);
    }

    public long getSeed() {
        return seed;
    }

    public Registry<Biome> getBiomeRegistry() {
        return biomeRegistry;
    }

    static {
        List<RegistryKey<Biome>> biomes = new ArrayList(IVanillaLayeredBiomeSource.getBiomes());
        biomes.add(ModBiomes.OLD_WEST);
        BIOMES = ImmutableList.copyOf(biomes);
    }
}
