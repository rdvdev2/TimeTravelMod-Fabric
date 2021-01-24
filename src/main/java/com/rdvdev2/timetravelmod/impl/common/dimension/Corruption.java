package com.rdvdev2.timetravelmod.impl.common.dimension;

import com.rdvdev2.timetravelmod.api.dimension.ICorruption;
import com.rdvdev2.timetravelmod.api.dimension.ITimeline;
import com.rdvdev2.timetravelmod.api.event.WorldCorruptionChangedEvent;
import com.rdvdev2.timetravelmod.impl.Mod;
import com.rdvdev2.timetravelmod.impl.ModRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.world.PersistentState;

public class Corruption extends PersistentState implements ICorruption {

    public static final Identifier ID = Mod.identifier("corruption");

    private int corruption;
    private ITimeline timeline;

    public Corruption(int corruption, ITimeline timeline) {
        super(ID.toString());
        this.corruption = corruption;
        this.timeline = timeline;
    }

    public Corruption(ITimeline timeline) {
        this(0, timeline);
    }

    @Override
    public int increaseCorruptionLevel(int amount) {
        setCorruptionLevel(corruption + amount);
        return getCorruptionLevel();
    }

    @Override
    public int setCorruptionLevel(int value) {
        int prevCorruption = corruption;
        corruption = Math.max(value, 0);
        WorldCorruptionChangedEvent.EVENT.invoker().onWorldCorruptionChanged(timeline, corruption, prevCorruption);
        markDirty();
        return getCorruptionLevel();
    }

    @Override
    public int getCorruptionLevel() {
        return corruption;
    }

    @Override
    public void fromTag(CompoundTag tag) {
        if (tag.contains("timeline")) {
            this.corruption = tag.getInt("value");
            this.timeline = ModRegistries.TIMELINE.get(Identifier.tryParse(tag.getString("timeline")));
        }
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        tag.putInt("value", this.corruption);
        tag.putString("timeline", ModRegistries.TIMELINE.getId(timeline).toString());
        return tag;
    }
}
