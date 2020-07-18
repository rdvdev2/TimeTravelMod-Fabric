package com.rdvdev2.TimeTravelMod;

import com.rdvdev2.TimeTravelMod.common.world.dimension.oldwest.biome.OldWestBiome;
import com.rdvdev2.TimeTravelMod.common.world.dimension.oldwest.biome.OldWestBiomeSource;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;

public class ModBiomes {

    public static final Biome OLDWEST = new OldWestBiome();

    public static void register() {
        Registry.register(Registry.BIOME, new Identifier(Mod.MODID, "oldwest"), OLDWEST);
        
        BiomeSources.register();
    }

    public static class BiomeSources {

        public static void register() {
            Registry.register(Registry.BIOME_SOURCE, new Identifier(Mod.MODID, "oldwest"), OldWestBiomeSource.CODEC);
        }
    }
}
