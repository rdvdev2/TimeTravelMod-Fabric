package com.rdvdev2.TimeTravelMod;

import com.mojang.serialization.Lifecycle;
import com.rdvdev2.TimeTravelMod.api.dimension.TimeLine;
import com.rdvdev2.TimeTravelMod.api.timemachine.TimeMachine;
import com.rdvdev2.TimeTravelMod.api.timemachine.upgrade.TimeMachineUpgrade;
import com.rdvdev2.TimeTravelMod.common.timemachine.TimeMachineManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;

public class ModRegistries {

    public static final MutableRegistry<TimeMachine> TIME_MACHINES = new SimpleRegistry<>(RegistryKey.ofRegistry(new Identifier(Mod.MODID, "timemachines")), Lifecycle.stable());
    public static final MutableRegistry<TimeLine> TIME_LINES = new SimpleRegistry<>(RegistryKey.ofRegistry(new Identifier(Mod.MODID, "timelines")), Lifecycle.stable());
    public static final MutableRegistry<TimeMachineUpgrade> UPGRADES = new SimpleRegistry<>(RegistryKey.ofRegistry(new Identifier(Mod.MODID, "upgrades")), Lifecycle.stable());
    
    public static void register() {
        /*Registry.register(Registry.REGISTRIES, new Identifier(Mod.MODID, "timelines"), TIME_LINES); TODO: Does this break anything?
        Registry.register(Registry.REGISTRIES, new Identifier(Mod.MODID, "timemachines"), TIME_MACHINES);
        Registry.register(Registry.REGISTRIES, new Identifier(Mod.MODID, "tmupgrades"), UPGRADES);*/
    
        TimeMachineManager.registerEvents();
    }
}
