package com.rdvdev2.TimeTravelMod.mixin;

import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.BiomeSourceConfig;
import net.minecraft.world.biome.source.BiomeSourceType;
import net.minecraft.world.level.LevelProperties;
import org.apache.commons.lang3.NotImplementedException;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.function.Function;

@Mixin(BiomeSourceType.class)
public interface IBiomeSourceType<C extends BiomeSourceConfig, T extends BiomeSource> {
    
    @Invoker("<init>")
    static <C extends BiomeSourceConfig, T extends BiomeSource> BiomeSourceType<C, T> create(Function<C, T> biomeSource, Function<LevelProperties, C> function) {
        throw new NotImplementedException("IBiomeSourceType mixin failed to apply");
    }
}
