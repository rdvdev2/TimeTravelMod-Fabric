package com.rdvdev2.timetravelmod.impl

import com.rdvdev2.timetravelmod.api.timemachine.ITimeMachine
import com.rdvdev2.timetravelmod.api.timemachine.ITimeMachineUpgrade
import com.rdvdev2.timetravelmod.api.timemachine.TimeMachineBuilder
import com.rdvdev2.timetravelmod.impl.Mod.identifier
import com.rdvdev2.timetravelmod.impl.common.timemachine.upgrade.TimeMachineTrackerUpgrade
import net.minecraft.util.registry.Registry

object ModTimeMachines {

    private val BASIC_TIME_MACHINE_BLOCKS = arrayOf(
        ModBlocks.TIME_MACHINE_BASIC_BLOCK.defaultState,
        ModBlocks.TIME_MACHINE_CABLE_PANEL.defaultState,
        ModBlocks.TIME_MACHINE_PILLAR.defaultState,
        ModBlocks.TIME_MACHINE_HORIZONTAL_VENTILATION.defaultState,
        ModBlocks.TIME_MACHINE_VERTICAL_VENTILATION.defaultState
    )

    val CREATIVE = TimeMachineBuilder()
        .setIcon(ModItems.CREATIVE_TIME_MACHINE)
        .setCooldownTime(0)
        .setStructureLayers(arrayOf(arrayOf("*")))
        .setControllerStates()
        .setCoreStates()
        .setBasicStates()
        .build()

    val TIER_1 = TimeMachineBuilder()
        .setStructureLayers(
            arrayOf(
                arrayOf(
                    "BBB",
                    "BBB",
                    "BBB"
                ), arrayOf(
                    "BZB",
                    "B B",
                    "B B"
                ), arrayOf(
                    "BBB",
                    "B B",
                    "B B"
                ), arrayOf(
                    "BBB",
                    "BCB",
                    "BBB"
                )
            )
        )
        .setControllerStates(ModBlocks.TIME_MACHINE_CONTROL_PANEL.defaultState)
        .setCoreStates(ModBlocks.TIME_MACHINE_CORE.defaultState)
        .setBasicStates(*BASIC_TIME_MACHINE_BLOCKS)
        .build()

    val BIG_TIER_1 = TimeMachineBuilder()
        .setStructureLayers(
            arrayOf(
                arrayOf(
                    "BBBBB",
                    "BBBBB",
                    "BBBBB",
                    "BBBBB",
                    "BBBBB"
                ), arrayOf(
                    "BBBBB",
                    "B   B",
                    "B   B",
                    "B   B",
                    "BBBBB"
                ), arrayOf(
                    "BBZBB",
                    "B   B",
                    "B   B",
                    "B   B",
                    "BB BB"
                ), arrayOf(
                    "BBBBB",
                    "B   B",
                    "B   B",
                    "B   B",
                    "BB BB"
                ), arrayOf(
                    "BBBBB",
                    "BBBBB",
                    "BBCBB",
                    "BBBBB",
                    "BBBBB"
                )
            )
        )
        .setControllerStates(ModBlocks.TIME_MACHINE_CONTROL_PANEL.defaultState)
        .setCoreStates(ModBlocks.TIME_MACHINE_CORE.defaultState)
        .setBasicStates(*BASIC_TIME_MACHINE_BLOCKS)
        .setCorruptionMultiplier(3)
        .setEntityMaxLoad(3)
        .setCooldownTime(60 * 20)
        .build()

    fun register() {
        CREATIVE.registerAs("creative")
        TIER_1.registerAs("tier_1")
        BIG_TIER_1.registerAs("big_tier_1")
        Upgrades.register()
    }

    object Upgrades {

        val TRACKER: ITimeMachineUpgrade = TimeMachineTrackerUpgrade()

        fun register() {
            TRACKER.registerAs("tracker")
        }

        private fun ITimeMachineUpgrade.registerAs(path: String) = Registry.register(ModRegistries.TIME_MACHINE_UPGRADE, identifier(path), this)
    }

    private fun ITimeMachine.registerAs(path: String) = Registry.register(ModRegistries.TIME_MACHINE, identifier(path), this)
}