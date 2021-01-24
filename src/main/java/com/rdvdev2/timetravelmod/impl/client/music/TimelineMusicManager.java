package com.rdvdev2.timetravelmod.impl.client.music;

import com.rdvdev2.timetravelmod.impl.ModConfig;
import com.rdvdev2.timetravelmod.impl.ModDimensions;
import com.rdvdev2.timetravelmod.impl.ModSoundEvents;
import net.minecraft.client.sound.MusicType;
import net.minecraft.sound.MusicSound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TimelineMusicManager {

    public static final TimelineMusicManager INSTANCE = new TimelineMusicManager();

    private final Map<RegistryKey<World>, MusicSound> musicMap = new HashMap<>();

    private TimelineMusicManager() {
        addMusic(ModDimensions.OLD_WEST, ModSoundEvents.OLD_WEST_MUSIC);
    }

    private void addMusic(RegistryKey<World> dimension, SoundEvent soundEvent) {
        musicMap.put(dimension, MusicType.createIngameMusic(soundEvent));
    }

    public Optional<MusicSound> get(RegistryKey<World> dimension) {
        if (!ModConfig.getInstance().getClient().getEnableTimelineMusic()) return Optional.empty();
        return Optional.ofNullable(musicMap.get(dimension));
    }
}
