package com.rdvdev2.timetravelmod.api.dimension;

import com.rdvdev2.timetravelmod.impl.common.dimension.timeline.Timeline;
import net.minecraft.item.Item;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

public class TimelineBuilder {

    private int minTier = 1;
    private RegistryKey<World> world = null;
    private Item icon = null;

    public TimelineBuilder setMinTier(int minTier) {
        this.minTier = minTier;
        return this;
    }

    public TimelineBuilder setWorld(RegistryKey<World> world) {
        this.world = world;
        return this;
    }

    public TimelineBuilder setIcon(Item icon) {
        this.icon = icon;
        return this;
    }

    public ITimeline build() {
        if (world == null || icon == null)
            throw new RuntimeException("You must define all properties");
        return new Timeline(minTier, world, icon);
    }
}
