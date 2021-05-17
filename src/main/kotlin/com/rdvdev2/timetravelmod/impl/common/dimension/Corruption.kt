package com.rdvdev2.timetravelmod.impl.common.dimension

import com.rdvdev2.timetravelmod.api.dimension.ICorruption
import com.rdvdev2.timetravelmod.api.dimension.ITimeline
import com.rdvdev2.timetravelmod.api.event.WorldCorruptionChangedEvent
import com.rdvdev2.timetravelmod.impl.Mod.identifier
import com.rdvdev2.timetravelmod.impl.ModRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.Identifier
import net.minecraft.world.PersistentState

class Corruption(override var corruptionLevel: Int, private var timeline: ITimeline) : PersistentState(ID.toString()), ICorruption {

    constructor(timeline: ITimeline) : this(0, timeline)

    override fun increaseCorruptionLevel(amount: Int): Int {
        setCorruptionLevel(corruptionLevel + amount)
        return corruptionLevel
    }

    override fun setCorruptionLevel(value: Int): Int {
        val prevCorruption = corruptionLevel
        corruptionLevel = value.coerceAtLeast(0)
        WorldCorruptionChangedEvent.EVENT.invoker().onWorldCorruptionChanged(timeline, corruptionLevel, prevCorruption)
        markDirty()
        return corruptionLevel
    }

    override fun fromTag(tag: CompoundTag) {
        if (tag.contains("timeline")) {
            corruptionLevel = tag.getInt("value")
            timeline = ModRegistries.TIMELINE[Identifier.tryParse(tag.getString("timeline"))]!!
        }
    }

    override fun toTag(tag: CompoundTag): CompoundTag {
        tag.putInt("value", corruptionLevel)
        tag.putString("timeline", ModRegistries.TIMELINE.getId(timeline).toString())
        return tag
    }

    companion object {
        val ID = identifier("corruption")
    }
}