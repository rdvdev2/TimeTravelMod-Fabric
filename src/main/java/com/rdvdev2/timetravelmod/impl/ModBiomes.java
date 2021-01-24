package com.rdvdev2.timetravelmod.impl;

import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;

public class ModBiomes {

    public static final RegistryKey<Biome> OLD_WEST = RegistryKey.of(Registry.BIOME_KEY, Mod.identifier("old_west"));
}
