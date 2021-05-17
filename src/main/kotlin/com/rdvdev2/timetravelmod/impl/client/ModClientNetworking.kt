package com.rdvdev2.timetravelmod.impl.client

import com.rdvdev2.timetravelmod.impl.ModNetworking
import com.rdvdev2.timetravelmod.impl.client.screen.EngineerBookScreen
import com.rdvdev2.timetravelmod.impl.client.screen.TimeMachineScreen
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen

object ModClientNetworking {

    fun register() {
        ClientPlayNetworking.registerGlobalReceiver(ModNetworking.OPEN_TIME_MACHINE_GUI) { client, _, buf, _ ->
            val worldIdentifier = buf.readIdentifier()
            val rootPos = buf.readBlockPos()
            val maxTier = buf.readInt()
            client.execute { openScreen(client, TimeMachineScreen(worldIdentifier, rootPos, maxTier)) }
        }

        ClientPlayNetworking.registerGlobalReceiver(ModNetworking.OPEN_CREATIVE_TIME_MACHINE_GUI) { client, _, _, _ ->
            client.execute { openScreen(client, TimeMachineScreen()) }
        }

        ClientPlayNetworking.registerGlobalReceiver(ModNetworking.OPEN_ENGINEER_BOOK_GUI) { client, _, _, _ ->
            client.execute { openScreen(client, EngineerBookScreen()) }
        }
    }

    private fun openScreen(client: MinecraftClient, screen: Screen) = client.openScreen(screen)
}