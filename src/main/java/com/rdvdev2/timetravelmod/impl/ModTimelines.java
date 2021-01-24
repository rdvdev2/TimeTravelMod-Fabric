package com.rdvdev2.timetravelmod.impl;

import com.rdvdev2.timetravelmod.api.dimension.ITimeline;
import com.rdvdev2.timetravelmod.api.dimension.TimelineBuilder;
import net.minecraft.item.Items;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class ModTimelines {

    public static final ITimeline PRESENT = new TimelineBuilder()
            .setWorld(World.OVERWORLD)
            .setMinTier(0)
            .setIcon(Items.GRASS_BLOCK)
            .build();

    public static final ITimeline OLD_WEST = new TimelineBuilder()
            .setWorld(ModDimensions.OLD_WEST)
            .setMinTier(1)
            .setIcon(Items.SAND)
            .build();

    public static void register() {
        registerTimeline("present", PRESENT);
        registerTimeline("old_west", OLD_WEST);
    }

    private static void registerTimeline(String path, ITimeline timeline) {
        Registry.register(ModRegistries.TIMELINE, Mod.identifier(path), timeline);
    }
}
