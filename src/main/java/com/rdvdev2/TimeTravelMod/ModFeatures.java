package com.rdvdev2.TimeTravelMod;

import com.rdvdev2.TimeTravelMod.common.world.generator.GunpowderFeature;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

public class ModFeatures {
    public static final Feature<DefaultFeatureConfig> GUNPOWDER = new GunpowderFeature(DefaultFeatureConfig.CODEC);
    
    public static void register() {
        Registry.register(Registry.FEATURE, new Identifier(Mod.MODID, "gunpowder"), GUNPOWDER);
    }
}
