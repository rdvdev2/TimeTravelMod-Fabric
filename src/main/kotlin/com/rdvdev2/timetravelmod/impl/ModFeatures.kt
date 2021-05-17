package com.rdvdev2.timetravelmod.impl

import com.rdvdev2.timetravelmod.impl.Mod.identifier
import com.rdvdev2.timetravelmod.impl.common.feature.GunpowderRemainsFeature
import net.fabricmc.fabric.api.biome.v1.BiomeModifications
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryKey
import net.minecraft.world.gen.GenerationStep
import net.minecraft.world.gen.feature.ConfiguredFeature
import net.minecraft.world.gen.feature.DefaultFeatureConfig
import net.minecraft.world.gen.feature.Feature

object ModFeatures {

    val GUNPOWDER_REMAINS: Feature<DefaultFeatureConfig> = GunpowderRemainsFeature()

    fun register() {
        GUNPOWDER_REMAINS.registerAs("gunpowder_remains")
        Configured.register()
    }

    object Configured {

        val ORE_TIME_CRYSTAL = getRegKey("ore_time_crystal")
        val GUNPOWDER_REMAINS = getRegKey("gunpowder_remains")

        fun register() {
            BiomeModifications.addFeature(
                BiomeSelectors.foundInOverworld(),
                GenerationStep.Feature.UNDERGROUND_ORES,
                ORE_TIME_CRYSTAL
            )
        }

        private fun getRegKey(path: String): RegistryKey<ConfiguredFeature<*, *>> = RegistryKey.of(Registry.CONFIGURED_FEATURE_WORLDGEN, identifier(path))
    }

    private fun Feature<*>.registerAs(path: String) = Registry.register(Registry.FEATURE, identifier(path), this)
}