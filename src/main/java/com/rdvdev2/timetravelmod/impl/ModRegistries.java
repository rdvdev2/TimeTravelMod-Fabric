package com.rdvdev2.timetravelmod.impl;

import com.mojang.serialization.Lifecycle;
import com.rdvdev2.timetravelmod.api.dimension.ITimeline;
import com.rdvdev2.timetravelmod.api.timemachine.ITimeMachine;
import com.rdvdev2.timetravelmod.api.timemachine.ITimeMachineUpgrade;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;

public class ModRegistries {

    private static final RegistryKey<Registry<ITimeMachine>> TIME_MACHINE_KEY = RegistryKey.ofRegistry(Mod.identifier("time_machine"));
    public static final Registry<ITimeMachine> TIME_MACHINE = new SimpleRegistry<>(TIME_MACHINE_KEY, Lifecycle.stable());

    private static final RegistryKey<Registry<ITimeline>> TIMELINE_KEY = RegistryKey.ofRegistry(Mod.identifier("timeline"));
    public static final Registry<ITimeline> TIMELINE = new SimpleRegistry<>(TIMELINE_KEY, Lifecycle.stable());

    private static final RegistryKey<Registry<ITimeMachineUpgrade>> TIME_MACHINE_UPGRADE_KEY = RegistryKey.ofRegistry(Mod.identifier("time_machine_upgrade"));
    public static final Registry<ITimeMachineUpgrade> TIME_MACHINE_UPGRADE = new SimpleRegistry<>(TIME_MACHINE_UPGRADE_KEY, Lifecycle.stable());
}
