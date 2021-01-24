package com.rdvdev2.timetravelmod.impl.common.dimension.oldwest;

import com.rdvdev2.timetravelmod.impl.common.dimension.ModdedBiomeLayerSampler;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.layer.*;
import net.minecraft.world.biome.layer.type.ParentedLayer;
import net.minecraft.world.biome.layer.util.*;
import net.minecraft.world.biome.source.BiomeLayerSampler;

import java.util.function.LongFunction;

public class OldWestLayers {

    private static <T extends LayerSampler, C extends LayerSampleContext<T>> LayerFactory<T> stack(long seed, ParentedLayer layer, LayerFactory<T> parent, int count, LongFunction<C> contextProvider) {
        LayerFactory<T> layerFactory = parent;

        for(int i = 0; i < count; ++i) {
            layerFactory = layer.create(contextProvider.apply(seed + (long)i), layerFactory);
        }

        return layerFactory;
    }

    private static <T extends LayerSampler, C extends LayerSampleContext<T>> LayerFactory<T> build(int biomeSize, int riverSize, LongFunction<C> contextProvider, Registry<Biome> biomeRegistry) {
        LayerFactory<T> layerFactory = ContinentLayer.INSTANCE.create(contextProvider.apply(1L));
        layerFactory = ScaleLayer.FUZZY.create(contextProvider.apply(2000L), layerFactory);
        layerFactory = IncreaseEdgeCurvatureLayer.INSTANCE.create(contextProvider.apply(1L), layerFactory);
        layerFactory = ScaleLayer.NORMAL.create(contextProvider.apply(2001L), layerFactory);
        layerFactory = IncreaseEdgeCurvatureLayer.INSTANCE.create(contextProvider.apply(2L), layerFactory);
        layerFactory = IncreaseEdgeCurvatureLayer.INSTANCE.create(contextProvider.apply(50L), layerFactory);
        layerFactory = IncreaseEdgeCurvatureLayer.INSTANCE.create(contextProvider.apply(70L), layerFactory);
        layerFactory = AddIslandLayer.INSTANCE.create(contextProvider.apply(2L), layerFactory);
        LayerFactory<T> layerFactory2 = OceanTemperatureLayer.INSTANCE.create(contextProvider.apply(2L));
        layerFactory2 = stack(2001L, ScaleLayer.NORMAL, layerFactory2, 6, contextProvider);
        layerFactory = AddColdClimatesLayer.INSTANCE.create(contextProvider.apply(2L), layerFactory);
        layerFactory = IncreaseEdgeCurvatureLayer.INSTANCE.create(contextProvider.apply(3L), layerFactory);
        layerFactory = AddClimateLayers.AddTemperateBiomesLayer.INSTANCE.create(contextProvider.apply(2L), layerFactory);
        layerFactory = AddClimateLayers.AddCoolBiomesLayer.INSTANCE.create(contextProvider.apply(2L), layerFactory);
        layerFactory = AddClimateLayers.AddSpecialBiomesLayer.INSTANCE.create(contextProvider.apply(3L), layerFactory);
        layerFactory = ScaleLayer.NORMAL.create(contextProvider.apply(2002L), layerFactory);
        layerFactory = ScaleLayer.NORMAL.create(contextProvider.apply(2003L), layerFactory);
        layerFactory = IncreaseEdgeCurvatureLayer.INSTANCE.create(contextProvider.apply(4L), layerFactory);
        layerFactory = AddMushroomIslandLayer.INSTANCE.create(contextProvider.apply(5L), layerFactory);
        layerFactory = AddDeepOceanLayer.INSTANCE.create(contextProvider.apply(4L), layerFactory);
        layerFactory = stack(1000L, ScaleLayer.NORMAL, layerFactory, 0, contextProvider);
        LayerFactory<T> layerFactory3 = stack(1000L, ScaleLayer.NORMAL, layerFactory, 0, contextProvider);
        layerFactory3 = SimpleLandNoiseLayer.INSTANCE.create(contextProvider.apply(100L), layerFactory3);
        LayerFactory<T> layerFactory4 = new OldWestSetBaseBiomesLayer(biomeRegistry).create(contextProvider.apply(200L), layerFactory);
        layerFactory4 = AddBambooJungleLayer.INSTANCE.create(contextProvider.apply(1001L), layerFactory4);
        layerFactory4 = stack(1000L, ScaleLayer.NORMAL, layerFactory4, 2, contextProvider);
        layerFactory4 = EaseBiomeEdgeLayer.INSTANCE.create(contextProvider.apply(1000L), layerFactory4);
        LayerFactory<T> layerFactory5 = stack(1000L, ScaleLayer.NORMAL, layerFactory3, 2, contextProvider);
        layerFactory4 = AddHillsLayer.INSTANCE.create(contextProvider.apply(1000L), layerFactory4, layerFactory5);
        layerFactory3 = stack(1000L, ScaleLayer.NORMAL, layerFactory3, 2, contextProvider);
        layerFactory3 = stack(1000L, ScaleLayer.NORMAL, layerFactory3, riverSize, contextProvider);
        layerFactory3 = NoiseToRiverLayer.INSTANCE.create(contextProvider.apply(1L), layerFactory3);
        layerFactory3 = SmoothLayer.INSTANCE.create(contextProvider.apply(1000L), layerFactory3);
        layerFactory4 = AddSunflowerPlainsLayer.INSTANCE.create(contextProvider.apply(1001L), layerFactory4);

        for(int i = 0; i < biomeSize; ++i) {
            layerFactory4 = ScaleLayer.NORMAL.create(contextProvider.apply(1000 + i), layerFactory4);
            if (i == 0) {
                layerFactory4 = IncreaseEdgeCurvatureLayer.INSTANCE.create(contextProvider.apply(3L), layerFactory4);
            }

            if (i == 1 || biomeSize == 1) {
                layerFactory4 = AddEdgeBiomesLayer.INSTANCE.create(contextProvider.apply(1000L), layerFactory4);
            }
        }

        layerFactory4 = SmoothLayer.INSTANCE.create(contextProvider.apply(1000L), layerFactory4);
        layerFactory4 = AddRiversLayer.INSTANCE.create(contextProvider.apply(100L), layerFactory4, layerFactory3);
        layerFactory4 = ApplyOceanTemperatureLayer.INSTANCE.create(contextProvider.apply(100L), layerFactory4, layerFactory2);
        return layerFactory4;
    }

    public static ModdedBiomeLayerSampler build(long seed, int biomeSize, int riverSize, Registry<Biome> biomeRegistry) {
        LayerFactory<CachingLayerSampler> layerFactory = build(biomeSize, riverSize, (salt) ->
                new CachingLayerContext(25, seed, salt), biomeRegistry);
        return new ModdedBiomeLayerSampler(layerFactory);
    }
}
