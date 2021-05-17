package com.rdvdev2.timetravelmod.api.event

import com.rdvdev2.timetravelmod.api.dimension.ITimeline
import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory

interface WorldCorruptionChangedEvent {
    fun onWorldCorruptionChanged(timeline: ITimeline, value: Int, previousValue: Int)

    companion object {
        @JvmField
        val EVENT: Event<WorldCorruptionChangedEvent> = EventFactory.createArrayBacked(WorldCorruptionChangedEvent::class.java) { listeners ->
            object : WorldCorruptionChangedEvent {
                override fun onWorldCorruptionChanged(timeline: ITimeline, value: Int, previousValue: Int) {
                    for (listener in listeners) {
                        listener.onWorldCorruptionChanged(timeline, value, previousValue)
                    }
                }
            }
        }
    }
}