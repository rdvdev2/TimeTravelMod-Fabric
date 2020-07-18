package com.rdvdev2.TimeTravelMod.common.world.dimension;

import com.rdvdev2.TimeTravelMod.api.dimension.TimeLine;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

public class PresentTimeLine implements TimeLine {
    
    private com.rdvdev2.TimeTravelMod.api.dimension.Corruption corruption = new Corruption(this);
    
    @Override
    public int getMinTier() {
        return 0;
    }
    
    @Override
    public RegistryKey<World> getWorldKey() {
        return World.OVERWORLD;
    }
    
    @Override
    public com.rdvdev2.TimeTravelMod.api.dimension.Corruption getCorruption() {
        return corruption;
    }
}
