package com.rdvdev2.timetravelmod.impl.common

import com.rdvdev2.timetravelmod.impl.Mod
import com.rdvdev2.timetravelmod.impl.ModConfig.instance
import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.loader.api.SemanticVersion
import net.fabricmc.loader.api.VersionParsingException
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.TranslatableText
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.URL

object UpdateChecker {

    @JvmStatic
    fun run(playerEntity: ServerPlayerEntity) {
        if (instance.common.enableUpdatePromos) {
            try {
                val metadata = FabricLoader.getInstance().getModContainer(Mod.MODID).get().metadata
                val currentVersion = SemanticVersion.parse(metadata.version.friendlyString)
                var newVersion: SemanticVersion? = null
                val url = metadata.getCustomValue("time_travel_mod:update_url").asString
                val updateCheckConnection = URL(url).openConnection()
                BufferedReader(InputStreamReader(updateCheckConnection.getInputStream())).use { reader ->
                    var inputLine: String
                    while (reader.readLine().also { inputLine = it } != null) {
                        val data = inputLine.split(" ").toTypedArray()
                        if (data[0] == "LATEST") newVersion = SemanticVersion.parse(data[1])
                    }
                }
                if (newVersion != null && newVersion!! > currentVersion) {
                    playerEntity.sendMessage(TranslatableText("chat.time_travel_mod.outdated", newVersion!!.friendlyString), false)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: VersionParsingException) {
                e.printStackTrace()
            }
        }
    }

    private class VersionDescriptor private constructor(val type: Type, val version: String) {

        val semver: SemanticVersion? = try {
            SemanticVersion.parse(version)
        } catch (e: VersionParsingException) {
            null
        }

        private enum class Type {
            RELEASE, BETA, ALPHA, DEV
        }
    }
}