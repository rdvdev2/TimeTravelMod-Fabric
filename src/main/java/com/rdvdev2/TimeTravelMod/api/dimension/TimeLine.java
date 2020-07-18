package com.rdvdev2.TimeTravelMod.api.dimension;

import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

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
     * Gets the {@link RegistryKey} of the {@link World} associated to this Time Line
     * @return The DimensionType object
     */
    RegistryKey<World> getWorldKey();
    
    /**
     * Gets the corruption of the Time Line
     * @return The corruption object
     */
    Corruption getCorruption();

    /**
     * Creates a new Time Line
     * @param minTier The minimum Time Machine tier required to travel to this Time Line
     * @param worldKey The {@link RegistryKey} of the {@link World} associated to this Time Line
     * @return The new Time Line
     */
    static TimeLine getNew(int minTier, RegistryKey<World> worldKey) {
        return new com.rdvdev2.TimeTravelMod.common.world.dimension.TimeLine(minTier, worldKey);
    }
}
