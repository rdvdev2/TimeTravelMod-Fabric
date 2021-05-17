package com.rdvdev2.timetravelmod.integration

import com.rdvdev2.timetravelmod.impl.ModConfig
import io.github.prospector.modmenu.api.ConfigScreenFactory
import io.github.prospector.modmenu.api.ModMenuApi
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.screen.Screen

@Environment(EnvType.CLIENT)
object ModMenu : ModMenuApi {

    override fun getModConfigScreenFactory(): ConfigScreenFactory<*> = ConfigScreenFactory { parent: Screen? ->
        AutoConfig.getConfigScreen(
            ModConfig.DisplayConfig::class.java, parent
        ).get()
    }
}