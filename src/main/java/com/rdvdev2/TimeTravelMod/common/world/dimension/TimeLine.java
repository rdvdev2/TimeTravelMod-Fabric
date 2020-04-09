package com.rdvdev2.TimeTravelMod.common.world.dimension;

import com.rdvdev2.TimeTravelMod.ModRegistries;
import com.rdvdev2.TimeTravelMod.api.dimension.AbstractTimeLineDimension;
import net.fabricmc.fabric.api.dimension.v1.FabricDimensionType;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

import java.util.function.BiFunction;

public class TimeLine implements com.rdvdev2.TimeTravelMod.api.dimension.TimeLine {
    
    private final int minTier;
    private DimensionType dimensionType;
    private com.rdvdev2.TimeTravelMod.api.dimension.Corruption corruption = new Corruption(this);

    @Override
    public int getMinTier() {
        return minTier;
    }

    public DimensionType getDimensionType() {
        return this.dimensionType;
    }
    
    @Override
    public com.rdvdev2.TimeTravelMod.api.dimension.Corruption getCorruption() {
        return corruption;
    }
    
    public TimeLine(int minTier, BiFunction<World, DimensionType, ? extends AbstractTimeLineDimension> dimensionFactory, boolean skyLight, Identifier identifier) {
        this.minTier = minTier;
        this.dimensionType = FabricDimensionType.builder()
                .factory(dimensionFactory.andThen(d -> {d.setTimeLine(this); return d;}))
                .defaultPlacer((entity, serverWorld, direction, v, v1) -> new BlockPattern.TeleportTarget(entity.getPos(), entity.getVelocity(), (int) entity.getHeadYaw())) // Placeholder, never used
                .skyLight(skyLight)
                .buildAndRegister(identifier);
    }

    public static boolean isValidTimeLine(World world) {
        for (com.rdvdev2.TimeTravelMod.api.dimension.TimeLine tl: ModRegistries.TIME_LINES) {
            if (tl.getDimensionType() == world.getDimension().getType()) return true;
        }
        return false;
    }
}
