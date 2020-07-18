package com.rdvdev2.TimeTravelMod.client;

import com.rdvdev2.TimeTravelMod.ModConfig;
import com.rdvdev2.TimeTravelMod.ModSounds;
import com.rdvdev2.TimeTravelMod.ModTimeLines;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.sound.SoundCategory;

import java.util.Optional;

@Environment(EnvType.CLIENT)
public class MusicManager {

    private static final SoundInstance OLDWEST_MUSIC = PositionedSoundInstance.music(ModSounds.OLDWEST_MUSIC);

    public static Optional<SoundInstance> onPlaySound(SoundInstance originalSound) {
        if (ModConfig.getInstance().getClient().getEnableTimeLineMusic()) {
            if (originalSound != null &&
                    MinecraftClient.getInstance().player != null &&
                    MinecraftClient.getInstance().player.world.getRegistryKey() == ModTimeLines.OLDWEST.getWorldKey() &&
                    originalSound.getCategory() == SoundCategory.MUSIC) {
                if (MinecraftClient.getInstance().getSoundManager().isPlaying(OLDWEST_MUSIC)) {
                    return Optional.empty();
                } else {
                    return Optional.of(OLDWEST_MUSIC);
                }
            }
        }
        return Optional.ofNullable(originalSound);
    }
}
