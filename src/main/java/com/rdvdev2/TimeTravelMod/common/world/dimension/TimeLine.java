package com.rdvdev2.TimeTravelMod.common.world.dimension;

import com.rdvdev2.TimeTravelMod.ModRegistries;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

public class TimeLine implements com.rdvdev2.TimeTravelMod.api.dimension.TimeLine {
    
    private final int minTier;
    private RegistryKey<World> worldKey;
    private com.rdvdev2.TimeTravelMod.api.dimension.Corruption corruption = new Corruption(this);

    @Override
    public int getMinTier() {
        return minTier;
    }

    public RegistryKey<World> getWorldKey() {
        return this.worldKey;
    }
    
    @Override
    public com.rdvdev2.TimeTravelMod.api.dimension.Corruption getCorruption() {
        return corruption;
    }
    
    public TimeLine(int minTier, RegistryKey<World> worldKey) {
        this.minTier = minTier;
        this.worldKey = worldKey;
    }

    public static boolean isValidTimeLine(World world) {
        for (com.rdvdev2.TimeTravelMod.api.dimension.TimeLine tl: ModRegistries.TIME_LINES) {
            if (tl.getWorldKey() == world.getRegistryKey()) return true;
        }
        return false;
    }
}
