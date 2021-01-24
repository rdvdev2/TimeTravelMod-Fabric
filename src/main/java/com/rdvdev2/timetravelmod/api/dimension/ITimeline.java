package com.rdvdev2.timetravelmod.api.dimension;

import com.rdvdev2.timetravelmod.impl.ModRegistries;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

public interface ITimeline {

    int getMinTier();

    RegistryKey<World> getWorld();

    ICorruption getCorruption(MinecraftServer server);

    TranslatableText getName();

    Item getIcon();

    static ITimeline getTimelineForWorld(RegistryKey<World> world) {
        return ModRegistries.TIMELINE.stream()
                .filter(t -> t.getWorld() == world)
                .findFirst()
                .orElse(null);
    }
}
