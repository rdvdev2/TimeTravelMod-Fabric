package com.rdvdev2.timetravelmod.impl

import com.rdvdev2.timetravelmod.api.dimension.ITimeline
import com.rdvdev2.timetravelmod.api.dimension.TimelineBuilder
import com.rdvdev2.timetravelmod.impl.Mod.identifier
import net.minecraft.item.Items
import net.minecraft.util.registry.Registry
import net.minecraft.world.World

object ModTimelines {

    @JvmField
    val PRESENT = TimelineBuilder()
        .setWorld(World.OVERWORLD)
        .setMinTier(0)
        .setIcon(Items.GRASS_BLOCK)
        .build()

    @JvmField
    val OLD_WEST = TimelineBuilder()
        .setWorld(ModDimensions.OLD_WEST)
        .setMinTier(1)
        .setIcon(Items.SAND)
        .build()

    fun register() {
        PRESENT.registerAs("present")
        OLD_WEST.registerAs("old_west")
    }

    private fun ITimeline.registerAs(path: String) = Registry.register(ModRegistries.TIMELINE, identifier(path), this)
}