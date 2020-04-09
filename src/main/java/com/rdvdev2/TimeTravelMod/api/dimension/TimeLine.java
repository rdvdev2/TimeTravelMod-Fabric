package com.rdvdev2.TimeTravelMod.api.dimension;

import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

import java.util.function.BiFunction;

/**
 * This interface defines a Time Line. It creates abd warps around the DimensionType object of the Dimension.
 * To get an instance use the provided method, never implement the interface by yourself.
 * It must be registered in the time line registry instead of the dimension manager.
 */
public interface TimeLine {

    /**
     * Gets the minimum Time Machine tier required to travel to this Time Line
     * @return The minimum tier
     */
    int getMinTier();
    
    /**
     * Gets the DimensionType associated to this Time Line
     * @return The DimensionType object
     */
    DimensionType getDimensionType();
    
    /**
     * Gets the corruption of the Time Line
     * @return The corruption object
     */
    Corruption getCorruption();

    /**
     * Creates a new Time Line
     * @param minTier The minimum Time Machine tier required to travel to this Time Line
     * @param dimensionFactory See {@link net.fabricmc.fabric.api.dimension.v1.FabricDimensionType.Builder#factory(BiFunction)}
     * @param skyLight See {@link net.fabricmc.fabric.api.dimension.v1.FabricDimensionType.Builder#skyLight(boolean)}
     * @param identifier See {@link net.fabricmc.fabric.api.dimension.v1.FabricDimensionType.Builder#buildAndRegister(Identifier)}
     * @return The new Time Line
     */
    static TimeLine getNew(int minTier, BiFunction<World, DimensionType, ? extends AbstractTimeLineDimension> dimensionFactory, boolean skyLight, Identifier identifier) {
        return new com.rdvdev2.TimeTravelMod.common.world.dimension.TimeLine(minTier, dimensionFactory, skyLight, identifier);
    }
}
