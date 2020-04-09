package com.rdvdev2.TimeTravelMod;

import com.rdvdev2.TimeTravelMod.api.dimension.TimeLine;
import com.rdvdev2.TimeTravelMod.api.timemachine.TimeMachine;
import com.rdvdev2.TimeTravelMod.api.timemachine.upgrade.TimeMachineUpgrade;
import com.rdvdev2.TimeTravelMod.common.timemachine.TimeMachineManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.SimpleRegistry;

public class ModRegistries {

    public static final MutableRegistry<TimeMachine> TIME_MACHINES = new SimpleRegistry<>();
    public static final MutableRegistry<TimeLine> TIME_LINES = new SimpleRegistry<>();
    public static final MutableRegistry<TimeMachineUpgrade> UPGRADES = new SimpleRegistry<>();
    
    public static void register() {
        Registry.register(Registry.REGISTRIES, new Identifier(Mod.MODID, "timelines"), TIME_LINES);
        Registry.register(Registry.REGISTRIES, new Identifier(Mod.MODID, "timemachines"), TIME_MACHINES);
        Registry.register(Registry.REGISTRIES, new Identifier(Mod.MODID, "tmupgrades"), UPGRADES);
    
        TimeMachineManager.registerEvents();
    }
}
