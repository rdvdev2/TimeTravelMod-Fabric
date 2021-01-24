package com.rdvdev2.timetravelmod.impl.common;

import com.rdvdev2.timetravelmod.impl.Mod;
import com.rdvdev2.timetravelmod.impl.ModConfig;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.SemanticVersion;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.SharedConstants;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class UpdateChecker {

    public static void run(ServerPlayerEntity playerEntity) {
        if (ModConfig.getInstance().getCommon().getEnableUpdatePromos()) {
            try {
                ModMetadata metadata = FabricLoader.getInstance().getModContainer(Mod.MODID).get().getMetadata();
                SemanticVersion currentVersion = SemanticVersion.parse(metadata.getVersion().getFriendlyString());

                SemanticVersion newVersion = null;
                String url = metadata.getCustomValue("time_travel_mod:update_url").getAsString();
                URLConnection updateCheckConnection = new URL(url).openConnection();
                try (BufferedReader in = new BufferedReader(new InputStreamReader(updateCheckConnection.getInputStream()))) {
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        String[] data = inputLine.split(" ");
                        if (data[0].equals("LATEST")) newVersion = SemanticVersion.parse(data[1]);
                    }
                }

                if (newVersion != null && newVersion.compareTo(currentVersion) > 0) {
                    playerEntity.sendMessage(new TranslatableText("chat.time_travel_mod.outdated", newVersion.getFriendlyString()), false);
                }

            } catch (IOException | VersionParsingException e) {
                e.printStackTrace();
            }
        }
    }

    private static class VersionDescriptor {

        private final Type type;
        private final String version;
        private final SemanticVersion semver;

        private VersionDescriptor(Type type, String version) {
            this.type = type;
            this.version = version;

            SemanticVersion semver;
            try {
                semver = SemanticVersion.parse(version);
            } catch (VersionParsingException e) {
                semver = null;
            }
            this.semver = semver;
        }

        public String getVersion() {
            return version;
        }

        public Type getType() {
            return type;
        }

        public SemanticVersion getSemver() {
            return semver;
        }

        private enum Type {
            RELEASE, BETA, ALPHA, DEV
        }
    }
}
