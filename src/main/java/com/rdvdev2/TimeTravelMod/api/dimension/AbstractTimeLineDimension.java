package com.rdvdev2.TimeTravelMod.api.dimension;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.World;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;

/**
 * An extended version of {@link Dimension}. It provides extra functionality to handle {@link Corruption}.
 */
public abstract class AbstractTimeLineDimension extends Dimension {
    
    private TimeLine timeLine;
    
    /**
     * @see Dimension#Dimension(World, DimensionType, float)
     */
    public AbstractTimeLineDimension(World world, DimensionType type, float f) {
        super(world, type, f);
        CompoundTag tag = this.world.getLevelProperties().getWorldData(this.getType());
        if (tag.contains("corruption")) ((com.rdvdev2.TimeTravelMod.common.world.dimension.Corruption) timeLine.getCorruption()).setCorruptionLevel(tag.getInt("corruption"));
    }
    
    /**
     * Gets the {@link TimeLine} associated to this Dimension
     * @return The associated {@link TimeLine}
     */
    public TimeLine getTimeLine() {
        return timeLine;
    }
    
    @Deprecated
    public void setTimeLine(TimeLine timeLine) {
        if (this.timeLine == null) this.timeLine = timeLine;
        else throw new RuntimeException("The TimeLine of an AbstractTimeLineDimension is set automatically by the TimeLine constructor");
    }
    
    @Override
    public void saveWorldData() {
        CompoundTag tag = this.world.getLevelProperties().getWorldData(this.getType());
        if (tag.contains("corruption")) tag.remove("corruption");
        tag.putInt("corruption", timeLine.getCorruption().getCorruptionLevel());
    }
}
