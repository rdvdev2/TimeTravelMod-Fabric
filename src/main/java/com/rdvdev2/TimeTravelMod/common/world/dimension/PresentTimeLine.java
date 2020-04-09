package com.rdvdev2.TimeTravelMod.common.world.dimension;

import com.rdvdev2.TimeTravelMod.api.dimension.TimeLine;
import net.minecraft.world.dimension.DimensionType;

public class PresentTimeLine implements TimeLine {
    
    private com.rdvdev2.TimeTravelMod.api.dimension.Corruption corruption = new Corruption(this);
    
    @Override
    public int getMinTier() {
        return 0;
    }
    
    @Override
    public DimensionType getDimensionType() {
        return DimensionType.OVERWORLD;
    }
    
    @Override
    public com.rdvdev2.TimeTravelMod.api.dimension.Corruption getCorruption() {
        return corruption;
    }
}
