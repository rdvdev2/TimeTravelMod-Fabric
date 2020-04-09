package com.rdvdev2.TimeTravelMod.mixin;

import com.rdvdev2.TimeTravelMod.ModTimeLines;
import com.rdvdev2.TimeTravelMod.api.dimension.TimeLine;
import com.rdvdev2.TimeTravelMod.common.world.dimension.Corruption;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.World;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.OverworldDimension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OverworldDimension.class)
public abstract class MixinOverworldDimension extends Dimension {
    
    private TimeLine timeLine = ModTimeLines.PRESENT;
    
    private MixinOverworldDimension(World world, DimensionType type, float f) {
        super(world, type, f);
    }
    
    @Inject(method = "<init>", at = @At("RETURN"))
    private void timetravelmod_onConstructor(World world, DimensionType type, CallbackInfo ci) {
        CompoundTag tag = this.world.getLevelProperties().getWorldData(this.getType());
        if (tag.contains("corruption")) ((Corruption) timeLine.getCorruption()).setCorruptionLevel(tag.getInt("corruption"));
    }
    
    @Override
    public void saveWorldData() {
        CompoundTag tag = this.world.getLevelProperties().getWorldData(this.getType());
        if (tag.contains("corruption")) tag.remove("corruption");
        tag.putInt("corruption", timeLine.getCorruption().getCorruptionLevel());
    }
}
