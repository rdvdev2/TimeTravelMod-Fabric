package com.rdvdev2.timetravelmod.impl

import me.sargunvohra.mcmods.autoconfig1u.AutoConfig
import me.sargunvohra.mcmods.autoconfig1u.ConfigData
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry.Gui.TransitiveObject
import me.sargunvohra.mcmods.autoconfig1u.serializer.PartitioningSerializer
import me.sargunvohra.mcmods.autoconfig1u.serializer.PartitioningSerializer.GlobalData
import me.sargunvohra.mcmods.autoconfig1u.serializer.Toml4jConfigSerializer
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment

object ModConfig : ConfigData {

    fun register() {
        AutoConfig.register(DisplayConfig::class.java, PartitioningSerializer.wrap(::Toml4jConfigSerializer))
    }

    @JvmStatic
    val instance: DisplayConfig
        get() = AutoConfig.getConfigHolder(DisplayConfig::class.java).config

    @Config(name = Mod.MODID)
    class DisplayConfig : GlobalData() {

        @Environment(EnvType.CLIENT)
        @ConfigEntry.Category(Mod.MODID + "_client")
        @TransitiveObject
        val client = ClientConfig()

        @ConfigEntry.Category(Mod.MODID + "_common")
        @TransitiveObject
        val common = CommonConfig()
    }

    @Config(name = "client")
    class ClientConfig : ConfigData {

        @ConfigEntry.Gui.Tooltip
        val enableTimelineMusic = true
    }

    @Config(name = "common")
    class CommonConfig : ConfigData {

        @ConfigEntry.Gui.Tooltip
        val enableUpdatePromos = true

        @ConfigEntry.Gui.Tooltip
        val enableCheaterReports = true
    }
}