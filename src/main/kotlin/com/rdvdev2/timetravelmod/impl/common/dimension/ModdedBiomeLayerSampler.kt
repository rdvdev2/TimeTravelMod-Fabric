package com.rdvdev2.timetravelmod.impl.common.dimension

import net.minecraft.SharedConstants
import net.minecraft.util.Util
import net.minecraft.util.registry.Registry
import net.minecraft.world.biome.Biome
import net.minecraft.world.biome.BuiltinBiomes
import net.minecraft.world.biome.layer.util.CachingLayerSampler
import net.minecraft.world.biome.layer.util.LayerFactory
import org.apache.logging.log4j.LogManager

class ModdedBiomeLayerSampler(layerFactory: LayerFactory<CachingLayerSampler>) {

    private val sampler: CachingLayerSampler = layerFactory.make()

    fun sample(registry: Registry<Biome>, i: Int, j: Int): Biome? {
        val k = sampler.sample(i, j)
        val registryKey = registry.getKey(registry[k])
        if (!registryKey.isPresent) {
            throw IllegalStateException("Unknown biome id emitted by layers: $k")
        } else {
            val biome = registry[registryKey.get()]
            return biome ?: if (SharedConstants.isDevelopment) {
                    throw Util.throwOrPause(IllegalStateException("Unknown biome id: $k"))
                } else {
                    LOGGER.warn("Unknown biome id: {}", k)
                    registry[BuiltinBiomes.fromRawId(0)]
                }
        }
    }

    companion object {
        private val LOGGER = LogManager.getLogger()
    }

}