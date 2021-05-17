package com.rdvdev2.timetravelmod.impl

import com.mojang.serialization.Lifecycle
import com.rdvdev2.timetravelmod.api.dimension.ITimeline
import com.rdvdev2.timetravelmod.api.timemachine.ITimeMachine
import com.rdvdev2.timetravelmod.api.timemachine.ITimeMachineUpgrade
import com.rdvdev2.timetravelmod.impl.Mod.identifier
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryKey
import net.minecraft.util.registry.SimpleRegistry

object ModRegistries {

    private val TIME_MACHINE_KEY = RegistryKey.ofRegistry<ITimeMachine>(identifier("time_machine"))

    @JvmField
    val TIME_MACHINE: Registry<ITimeMachine> = SimpleRegistry(TIME_MACHINE_KEY, Lifecycle.stable())

    private val TIMELINE_KEY = RegistryKey.ofRegistry<ITimeline>(identifier("timeline"))

    @JvmField
    val TIMELINE: Registry<ITimeline> = SimpleRegistry(TIMELINE_KEY, Lifecycle.stable())

    private val TIME_MACHINE_UPGRADE_KEY = RegistryKey.ofRegistry<ITimeMachineUpgrade>(identifier("time_machine_upgrade"))

    @JvmField
    val TIME_MACHINE_UPGRADE: Registry<ITimeMachineUpgrade> = SimpleRegistry(TIME_MACHINE_UPGRADE_KEY, Lifecycle.stable())
}