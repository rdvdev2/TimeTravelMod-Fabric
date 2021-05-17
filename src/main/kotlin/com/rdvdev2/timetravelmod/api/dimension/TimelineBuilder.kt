package com.rdvdev2.timetravelmod.api.dimension

import com.rdvdev2.timetravelmod.impl.common.dimension.timeline.Timeline
import net.minecraft.item.Item
import net.minecraft.util.registry.RegistryKey
import net.minecraft.world.World

class TimelineBuilder {

    private var minTier = 1
    private var world: RegistryKey<World>? = null
    private var icon: Item? = null

    fun setMinTier(minTier: Int): TimelineBuilder {
        this.minTier = minTier
        return this
    }

    fun setWorld(world: RegistryKey<World>?): TimelineBuilder {
        this.world = world
        return this
    }

    fun setIcon(icon: Item?): TimelineBuilder {
        this.icon = icon
        return this
    }

    fun build(): ITimeline {
        if (world == null || icon == null) throw RuntimeException("You must define all properties")
        return Timeline(minTier, world!!, icon!!)
    }
}