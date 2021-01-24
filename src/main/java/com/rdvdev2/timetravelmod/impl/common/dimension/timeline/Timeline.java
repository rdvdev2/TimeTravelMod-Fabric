package com.rdvdev2.timetravelmod.impl.common.dimension.timeline;

import com.rdvdev2.timetravelmod.api.dimension.ICorruption;
import com.rdvdev2.timetravelmod.api.dimension.ITimeline;
import com.rdvdev2.timetravelmod.impl.ModRegistries;
import com.rdvdev2.timetravelmod.impl.common.dimension.Corruption;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

public class Timeline implements ITimeline {

    private final int minTier;
    private final RegistryKey<World> world;
    private final Item icon;

    public Timeline(int minTier, RegistryKey<World> world, Item icon) {
        this.minTier = minTier;
        this.world = world;
        this.icon = icon;
    }

    @Override
    public int getMinTier() {
        return minTier;
    }

    @Override
    public RegistryKey<World> getWorld() {
        return world;
    }

    @Override
    public ICorruption getCorruption(MinecraftServer server) {
        return server.getWorld(world).getPersistentStateManager().getOrCreate(() -> new Corruption(this), Corruption.ID.toString());
    }

    @Override
    public TranslatableText getName() {
        return new TranslatableText(String.format("gui.tm.%s.%s",
                ModRegistries.TIMELINE.getId(this).getNamespace(),
                ModRegistries.TIMELINE.getId(this).getPath()));
    }

    @Override
    public Item getIcon() {
        return icon;
    }
}
