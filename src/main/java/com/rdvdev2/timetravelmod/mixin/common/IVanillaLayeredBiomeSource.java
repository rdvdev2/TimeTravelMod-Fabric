package com.rdvdev2.timetravelmod.mixin.common;

import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.VanillaLayeredBiomeSource;
import org.apache.commons.lang3.NotImplementedException;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(VanillaLayeredBiomeSource.class)
public interface IVanillaLayeredBiomeSource {

    @Accessor("BIOMES")
    static List<RegistryKey<Biome>> getBiomes() {
        throw new NotImplementedException("IVanillaLayeredBiomeSource failed to apply");
    }
}
