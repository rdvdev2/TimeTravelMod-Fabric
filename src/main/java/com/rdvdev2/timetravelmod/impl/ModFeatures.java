package com.rdvdev2.timetravelmod.impl;

import com.rdvdev2.timetravelmod.impl.common.feature.GunpwderRemainsFeature;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.decorator.ChanceDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

public class ModFeatures {

    public static final Feature<DefaultFeatureConfig> GUNPOWDER_REMAINS = new GunpwderRemainsFeature();

    public static void register() {
        registerFeature("gunpowder_remains", GUNPOWDER_REMAINS);

        Configured.register();
    }

    public static void registerFeature(String path, Feature<?> feature) {
        Registry.register(Registry.FEATURE, Mod.identifier(path), feature);
    }

    public static class Configured {

        public static final RegistryKey<ConfiguredFeature<?, ?>> ORE_TIME_CRYSTAL = getRegKey("ore_time_crystal");
        public static final RegistryKey<ConfiguredFeature<?, ?>> GUNPOWDER_REMAINS = getRegKey("gunpowder_remains");

        public static void register() {
            BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES, ORE_TIME_CRYSTAL);
        }

        private static RegistryKey<ConfiguredFeature<?, ?>> getRegKey(String path) {
            return RegistryKey.of(Registry.CONFIGURED_FEATURE_WORLDGEN, Mod.identifier(path));
        }
    }
}
