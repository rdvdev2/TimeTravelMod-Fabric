package com.rdvdev2.timetravelmod.impl.client;

import com.rdvdev2.timetravelmod.impl.ModNetworking;
import com.rdvdev2.timetravelmod.impl.client.screen.EngineerBookScreen;
import com.rdvdev2.timetravelmod.impl.client.screen.TimeMachineScreen;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class ModClientNetworking {

    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(ModNetworking.OPEN_TIME_MACHINE_GUI,
                (client, handler, buf, responseSender) -> {
                    Identifier worldIdentifier = buf.readIdentifier();
                    BlockPos rootPos = buf.readBlockPos();
                    int maxTier = buf.readInt();
                    client.execute(() ->
                            openScreen(client, new TimeMachineScreen(worldIdentifier, rootPos, maxTier)));
                });

        ClientPlayNetworking.registerGlobalReceiver(ModNetworking.OPEN_CREATIVE_TIME_MACHINE_GUI,
                (client, handler, buf, responseSender) -> client.execute(() ->
                        openScreen(MinecraftClient.getInstance(), new TimeMachineScreen())));

        ClientPlayNetworking.registerGlobalReceiver(ModNetworking.OPEN_ENGINEER_BOOK_GUI,
                (client, handler, buf, responseSender) -> client.execute(() ->
                        openScreen(MinecraftClient.getInstance(), new EngineerBookScreen())));
    }

    private static void openScreen(MinecraftClient client, Screen screen) {
        client.openScreen(screen);
    }
}
