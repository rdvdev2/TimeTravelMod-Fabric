package com.rdvdev2.timetravelmod.impl;

import com.mojang.serialization.Codec;
import com.rdvdev2.timetravelmod.impl.common.dimension.oldwest.OldWestBiomeSource;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.source.BiomeSource;

public class ModBiomeSources {

    public static final Codec<? extends BiomeSource> OLD_WEST = OldWestBiomeSource.CODEC;

    public static void register() {
        registerBiomeSource("old_west", OLD_WEST);
    }

    public static void registerBiomeSource(String path, Codec<? extends BiomeSource> biomeSource) {
        Registry.register(Registry.BIOME_SOURCE, Mod.identifier(path), biomeSource);
    }
}
