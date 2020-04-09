package com.rdvdev2.TimeTravelMod.common.world.dimension;

import com.rdvdev2.TimeTravelMod.api.dimension.TimeLine;
import com.rdvdev2.TimeTravelMod.common.event.WorldCorruptionChangedCallback;

public class Corruption implements com.rdvdev2.TimeTravelMod.api.dimension.Corruption {
    
    private int corruptionLevel;
    private final TimeLine timeLine;
    
    public Corruption(TimeLine timeLine) {
        this.timeLine = timeLine;
    }
    
    @Override
    public final int increaseCorruptionLevel(int amount) {
        corruptionLevel += amount;
        WorldCorruptionChangedCallback.EVENT.invoker().onWorldCorruptionChanged(timeLine, corruptionLevel);
        return corruptionLevel;
    }
    
    @Override
    public final int decreaseCorruptionLevel(int amount) {
        corruptionLevel -= amount;
        WorldCorruptionChangedCallback.EVENT.invoker().onWorldCorruptionChanged(timeLine, corruptionLevel);
        return corruptionLevel;
    }
    
    @Override
    public final int getCorruptionLevel() {
        return corruptionLevel;
    }
    
    public void setCorruptionLevel(int level) {
        corruptionLevel = level;
    }
}
