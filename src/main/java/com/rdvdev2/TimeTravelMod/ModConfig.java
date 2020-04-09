package com.rdvdev2.TimeTravelMod;

import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry;
import me.sargunvohra.mcmods.autoconfig1u.serializer.PartitioningSerializer;
import me.sargunvohra.mcmods.autoconfig1u.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class ModConfig implements ConfigData {
    
    @Config(name = Mod.MODID)
    public static class DisplayConfig extends PartitioningSerializer.GlobalData {
        
        @Environment(EnvType.CLIENT)
        @ConfigEntry.Category(Mod.MODID + "_client")
        @ConfigEntry.Gui.TransitiveObject
        private ClientConfig clientConfig = new ClientConfig();
        
        @ConfigEntry.Category(Mod.MODID + "_common")
        @ConfigEntry.Gui.TransitiveObject
        private CommonConfig commonConfig = new CommonConfig();
    
        @Environment(EnvType.CLIENT)
        public ClientConfig getClient() {
            return clientConfig;
        }
    
        public CommonConfig getCommon() {
            return commonConfig;
        }
    }
    
    @Config(name = "client")
    public static class ClientConfig implements ConfigData {
        
        @ConfigEntry.Gui.Tooltip
        private boolean enableTimeLineMusic = true;
    
        public boolean getEnableTimeLineMusic() {
            return enableTimeLineMusic;
        }
    }
    
    @Config(name = "common")
    public static class CommonConfig implements ConfigData {
        
        @ConfigEntry.Gui.Tooltip
        private boolean enableExperimentalFeatures = false;
        
        @ConfigEntry.Gui.Tooltip
        private boolean enableUpdatePromos = true;
        
        @ConfigEntry.Gui.Tooltip
        private boolean enableCheaterReports = true;
    
        public boolean getEnableExperimentalFeatures() {
            return enableExperimentalFeatures;
        }
    
        public boolean getEnableUpdatePromos() {
            return enableUpdatePromos;
        }
    
        public boolean getEnableCheaterReports() {
            return enableCheaterReports;
        }
    }
    
    public static void register() {
        AutoConfig.register(DisplayConfig.class, PartitioningSerializer.wrap(Toml4jConfigSerializer::new));
    }
    
    public static DisplayConfig getInstance() {
        return AutoConfig.getConfigHolder(DisplayConfig.class).getConfig();
    }
}