package com.rdvdev2.timetravelmod.api.event;

import com.rdvdev2.timetravelmod.api.dimension.ITimeline;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface WorldCorruptionChangedEvent {

    Event<WorldCorruptionChangedEvent> EVENT = EventFactory.createArrayBacked(WorldCorruptionChangedEvent.class,
            listeners -> (timeline, value, previousValue) -> {
                for (WorldCorruptionChangedEvent listener: listeners) {
                    listener.onWorldCorruptionChanged(timeline, value, previousValue);
                }
            });

    void onWorldCorruptionChanged(ITimeline timeline, int value, int previousValue);
}
