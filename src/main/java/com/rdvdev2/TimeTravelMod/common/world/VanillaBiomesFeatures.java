package com.rdvdev2.TimeTravelMod.common.world;

import com.rdvdev2.TimeTravelMod.ModBlocks;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.RangeDecoratorConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;

public class VanillaBiomesFeatures {

    public static void register() {
        // Time Crystal Ores
        registerGenerator(GenerationStep.Feature.UNDERGROUND_ORES, Feature.ORE.configure(
                new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, ModBlocks.TIME_CRYSTAL_ORE.getDefaultState(), 4)).createDecoratedFeature(
                Decorator.COUNT_RANGE.configure(
                new RangeDecoratorConfig(1, 0, 0, 16)))
        );
    }

    private static void registerGenerator(GenerationStep.Feature stage, ConfiguredFeature<?, ?> feature) {
        for (Biome biome : Registry.BIOME) {
            if (biome.getCategory() != Biome.Category.NETHER && biome.getCategory() != Biome.Category.THEEND) {
                biome.addFeature(stage, feature);
            }
        }
    }
}
