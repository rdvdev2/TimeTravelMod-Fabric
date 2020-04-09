package com.rdvdev2.TimeTravelMod.common.event;

import com.rdvdev2.TimeTravelMod.api.dimension.TimeLine;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface WorldCorruptionChangedCallback {
    
    Event<WorldCorruptionChangedCallback> EVENT = EventFactory.createArrayBacked(WorldCorruptionChangedCallback.class,
            listeners -> (timeLine, corruptionLevel) -> {
                for (WorldCorruptionChangedCallback listener: listeners) {
                    listener.onWorldCorruptionChanged(timeLine, corruptionLevel);
                }
            });
    
    void onWorldCorruptionChanged(TimeLine timeLine, int corruptionLevel);
}
