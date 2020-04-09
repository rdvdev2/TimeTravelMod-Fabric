package com.rdvdev2.TimeTravelMod;

import com.rdvdev2.TimeTravelMod.common.world.dimension.oldwest.biome.OldWestBiome;
import com.rdvdev2.TimeTravelMod.common.world.dimension.oldwest.biome.OldWestBiomeSource;
import com.rdvdev2.TimeTravelMod.mixin.IBiomeSourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSourceType;
import net.minecraft.world.biome.source.VanillaLayeredBiomeSourceConfig;

public class ModBiomes {

    public static final Biome OLDWEST = new OldWestBiome();

    public static void register() {
        Registry.register(Registry.BIOME, new Identifier(Mod.MODID, "oldwest"), OLDWEST);
        
        ProviderTypes.register();
    }

    public static class ProviderTypes {

        public static final BiomeSourceType<VanillaLayeredBiomeSourceConfig, OldWestBiomeSource> OLDWEST_LAYERED = IBiomeSourceType.create(OldWestBiomeSource::new, VanillaLayeredBiomeSourceConfig::new);
    
        public static void register() {
            Registry.register(Registry.BIOME_SOURCE_TYPE, new Identifier(Mod.MODID, "oldwest_layered"), OLDWEST_LAYERED);
        }
    }
}
