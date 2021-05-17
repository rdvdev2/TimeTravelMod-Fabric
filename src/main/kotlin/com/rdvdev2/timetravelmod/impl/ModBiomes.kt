package com.rdvdev2.timetravelmod.impl

import com.rdvdev2.timetravelmod.impl.Mod.identifier
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryKey
import net.minecraft.world.biome.Biome

object ModBiomes {

    @JvmField
    val OLD_WEST: RegistryKey<Biome> = RegistryKey.of(Registry.BIOME_KEY, identifier("old_west"))
}