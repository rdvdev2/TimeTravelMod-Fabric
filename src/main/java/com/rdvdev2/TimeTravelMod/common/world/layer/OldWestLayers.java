package com.rdvdev2.TimeTravelMod.common.world.layer;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.layer.*;
import net.minecraft.world.biome.layer.type.ParentedLayer;
import net.minecraft.world.biome.layer.util.*;
import net.minecraft.world.biome.source.BiomeLayerSampler;
import net.minecraft.world.gen.chunk.OverworldChunkGeneratorConfig;
import net.minecraft.world.level.LevelGeneratorType;

import java.util.function.LongFunction;

public class OldWestLayers {

    protected static final int WARM_OCEAN = Registry.BIOME.getRawId(Biomes.WARM_OCEAN);
    protected static final int LUKEWARM_OCEAN = Registry.BIOME.getRawId(Biomes.LUKEWARM_OCEAN);
    protected static final int OCEAN = Registry.BIOME.getRawId(Biomes.OCEAN);
    protected static final int COLD_OCEAN = Registry.BIOME.getRawId(Biomes.COLD_OCEAN);
    protected static final int FROZEN_OCEAN = Registry.BIOME.getRawId(Biomes.FROZEN_OCEAN);
    protected static final int DEEP_WARM_OCEAN = Registry.BIOME.getRawId(Biomes.DEEP_WARM_OCEAN);
    protected static final int DEEP_LUKEWARM_OCEAN = Registry.BIOME.getRawId(Biomes.DEEP_LUKEWARM_OCEAN);
    protected static final int DEEP_OCEAN = Registry.BIOME.getRawId(Biomes.DEEP_OCEAN);
    protected static final int DEEP_COLD_OCEAN = Registry.BIOME.getRawId(Biomes.DEEP_COLD_OCEAN);
    protected static final int DEEP_FROZEN_OCEAN = Registry.BIOME.getRawId(Biomes.DEEP_FROZEN_OCEAN);
    
    private static <T extends LayerSampler, C extends LayerSampleContext<T>> LayerFactory<T> stack(long seed, ParentedLayer layer, LayerFactory<T> parent, int count, LongFunction<C> contextProvider) {
        LayerFactory<T> layerFactory = parent;
        
        for(int i = 0; i < count; ++i) {
            layerFactory = layer.create((LayerSampleContext)contextProvider.apply(seed + (long)i), layerFactory);
        }
        
        return layerFactory;
    }
    
    public static BiomeLayerSampler build(long seed, LevelGeneratorType generatorType, OverworldChunkGeneratorConfig settings) {
        LayerFactory<CachingLayerSampler> layerFactory = build(generatorType, settings, (salt) -> {
            return new CachingLayerContext(25, seed, salt);
        });
        return new BiomeLayerSampler(layerFactory);
    }

    public static <T extends LayerSampler, C extends LayerSampleContext<T>> LayerFactory<T> build(LevelGeneratorType generatorType, OverworldChunkGeneratorConfig settings, LongFunction<C> contextFactory) {
        LayerFactory<T> factory = ContinentLayer.INSTANCE.create(contextFactory.apply(1L));
        factory = ScaleLayer.FUZZY.create(contextFactory.apply(2000L), factory);
        factory = IncreaseEdgeCurvatureLayer.INSTANCE.create(contextFactory.apply(1L), factory);
        factory = ScaleLayer.NORMAL.create(contextFactory.apply(2001L), factory);
        factory = IncreaseEdgeCurvatureLayer.INSTANCE.create(contextFactory.apply(2L), factory);
        factory = IncreaseEdgeCurvatureLayer.INSTANCE.create(contextFactory.apply(50L), factory);
        factory = IncreaseEdgeCurvatureLayer.INSTANCE.create(contextFactory.apply(70L), factory);
        factory = AddIslandLayer.INSTANCE.create(contextFactory.apply(2L), factory);
        LayerFactory<T> factory1 = OceanTemperatureLayer.INSTANCE.create(contextFactory.apply(2L));
        factory1 = stack(2001L, ScaleLayer.NORMAL, factory1, 6, contextFactory);
        factory = IncreaseEdgeCurvatureLayer.INSTANCE.create(contextFactory.apply(3L), factory);
        factory = AddClimateLayers.AddCoolBiomesLayer.INSTANCE.create(contextFactory.apply(2L), factory);
        factory = AddClimateLayers.AddSpecialBiomesLayer.INSTANCE.create(contextFactory.apply(3L), factory);
        factory = ScaleLayer.NORMAL.create(contextFactory.apply(2002L), factory);
        factory = ScaleLayer.NORMAL.create(contextFactory.apply(2003L), factory);
        factory = IncreaseEdgeCurvatureLayer.INSTANCE.create(contextFactory.apply(4L), factory);
        factory = AddDeepOceanLayer.INSTANCE.create(contextFactory.apply(4L), factory);
        factory = stack(1000L, ScaleLayer.NORMAL, factory, 0, contextFactory);
        int i = generatorType == LevelGeneratorType.LARGE_BIOMES ? 6 : settings.getBiomeSize();
        int j = settings.getRiverSize();

        LayerFactory<T> factory2 = stack(1000L, ScaleLayer.NORMAL, factory, 0, contextFactory);
        factory2 = SimpleLandNoiseLayer.INSTANCE.create((LayerSampleContext)contextFactory.apply(100L), factory2);
        LayerFactory<T> factory3 = getOldWestBiomeLayer(factory, contextFactory); // Custom biomes
        LayerFactory<T> factory4 = stack(1000L, ScaleLayer.NORMAL, factory2, 2, contextFactory);
        factory3 = AddHillsLayer.INSTANCE.create((LayerSampleContext) contextFactory.apply(1000L), factory3, factory4);
        factory2 = stack(1000L, ScaleLayer.NORMAL, factory2, 2, contextFactory);
        factory2 = stack(1000L, ScaleLayer.NORMAL, factory2, j, contextFactory);
        factory2 = NoiseToRiverLayer.INSTANCE.create((LayerSampleContext) contextFactory.apply(1L), factory2); // Custom rivers
        factory2 = SmoothenShorelineLayer.INSTANCE.create((LayerSampleContext) contextFactory.apply(1000L), factory2);

        for(int k = 0; k < i; ++k) {
            factory3 = ScaleLayer.NORMAL.create((LayerSampleContext) contextFactory.apply((long)(1000 + k)), factory3);
            if (k == 0) {
                factory3 = IncreaseEdgeCurvatureLayer.INSTANCE.create((LayerSampleContext) contextFactory.apply(3L), factory3);
            }

            if (k == 1 || i == 1) {
                factory3 = AddEdgeBiomesLayer.INSTANCE.create((LayerSampleContext)contextFactory.apply(1000L), factory3);
            }
        }

        factory3 = SmoothenShorelineLayer.INSTANCE.create((LayerSampleContext) contextFactory.apply(1000L), factory3);
        factory3 = AddRiversLayer.INSTANCE.create((LayerSampleContext) contextFactory.apply(100L), factory3, factory2);
        factory3 = ApplyOceanTemperatureLayer.INSTANCE.create(contextFactory.apply(100L), factory3, factory1);
        return factory3;
    }

    static <T extends LayerSampler, C extends LayerSampleContext<T>> LayerFactory<T> getOldWestBiomeLayer(LayerFactory<T> parentLayer, LongFunction<C> contextFactory) {
        parentLayer = (new OldWestBiomeLayer()).create(contextFactory.apply(200L), parentLayer);
        parentLayer = stack(1000L, ScaleLayer.NORMAL, parentLayer, 2, contextFactory);
        parentLayer = AddEdgeBiomesLayer.INSTANCE.create(contextFactory.apply(1000L), parentLayer);
        return parentLayer;
    }

    public static boolean isOcean(int biomeIn) {
        return biomeIn == WARM_OCEAN || biomeIn == LUKEWARM_OCEAN || biomeIn == OCEAN || biomeIn == COLD_OCEAN || biomeIn == FROZEN_OCEAN || biomeIn == DEEP_WARM_OCEAN || biomeIn == DEEP_LUKEWARM_OCEAN || biomeIn == DEEP_OCEAN || biomeIn == DEEP_COLD_OCEAN || biomeIn == DEEP_FROZEN_OCEAN;
    }
}
