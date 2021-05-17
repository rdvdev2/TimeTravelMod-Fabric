package com.rdvdev2.timetravelmod.impl

import com.mojang.serialization.Codec
import com.rdvdev2.timetravelmod.impl.Mod.identifier
import com.rdvdev2.timetravelmod.impl.common.dimension.oldwest.OldWestBiomeSource
import net.minecraft.util.registry.Registry
import net.minecraft.world.biome.source.BiomeSource

object ModBiomeSources {

    val OLD_WEST: Codec<out BiomeSource?> = OldWestBiomeSource.CODEC

    fun register() {
        OLD_WEST.registerAs("old_west")
    }

    private fun Codec<out BiomeSource>.registerAs(path: String) = Registry.register(Registry.BIOME_SOURCE, identifier(path), this)
}

