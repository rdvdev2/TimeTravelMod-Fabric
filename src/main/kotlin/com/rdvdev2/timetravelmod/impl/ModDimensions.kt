package com.rdvdev2.timetravelmod.impl

import com.rdvdev2.timetravelmod.impl.Mod.identifier
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryKey

object ModDimensions {

    @JvmField
    val OLD_WEST = RegistryKey.of(Registry.DIMENSION, identifier("old_west"))
}