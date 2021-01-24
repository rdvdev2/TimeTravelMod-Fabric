package com.rdvdev2.timetravelmod.impl.common.dimension;

import net.minecraft.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BuiltinBiomes;
import net.minecraft.world.biome.layer.util.CachingLayerSampler;
import net.minecraft.world.biome.layer.util.LayerFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

public class ModdedBiomeLayerSampler {
    private static final Logger LOGGER = LogManager.getLogger();
    private final CachingLayerSampler sampler;

    public ModdedBiomeLayerSampler(LayerFactory<CachingLayerSampler> layerFactory) {
        this.sampler = layerFactory.make();
    }

    public Biome sample(Registry<Biome> registry, int i, int j) {
        int k = this.sampler.sample(i, j);
        Optional<RegistryKey<Biome>> registryKey = registry.getKey(registry.get(k));
        if (!registryKey.isPresent()) {
            throw new IllegalStateException("Unknown biome id emitted by layers: " + k);
        } else {
            Biome biome = registry.get(registryKey.get());
            if (biome == null) {
                if (SharedConstants.isDevelopment) {
                    throw Util.throwOrPause(new IllegalStateException("Unknown biome id: " + k));
                } else {
                    LOGGER.warn("Unknown biome id: {}", k);
                    return registry.get(BuiltinBiomes.fromRawId(0));
                }
            } else {
                return biome;
            }
        }
    }
}
