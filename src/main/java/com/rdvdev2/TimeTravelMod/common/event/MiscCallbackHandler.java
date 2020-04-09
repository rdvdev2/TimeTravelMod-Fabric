package com.rdvdev2.TimeTravelMod.common.event;

import com.rdvdev2.TimeTravelMod.Mod;
import com.rdvdev2.TimeTravelMod.ModConfig;
import net.fabricmc.loader.FabricLoader;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.SharedConstants;
import net.minecraft.network.MessageType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class MiscCallbackHandler {
    
    public static void playerJoin(ServerPlayerEntity player) {
        if (ModConfig.getInstance().getCommon().getEnableUpdatePromos()) {
            try {
                ModMetadata metadata = FabricLoader.INSTANCE.getModContainer(Mod.MODID).get().getMetadata();
                String currentVersion = metadata.getVersion().getFriendlyString();
                String targetVersion = currentVersion;
                String url = metadata.getCustomValue("timetravelmod_updateurl").getAsString();
                URLConnection updateCheckConnection = new URL(url).openConnection();
                try (BufferedReader in = new BufferedReader(new InputStreamReader(updateCheckConnection.getInputStream()))) {
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        String[] data = inputLine.split(",");
                        if (data[0].equals("R") && data[1].equals(SharedConstants.getGameVersion().getId())) targetVersion = data[2];
                    }
                }
                if (!currentVersion.equals(targetVersion)) {
                    player.sendChatMessage(new TranslatableText("chat.ttm.outdated"), MessageType.SYSTEM);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
}
