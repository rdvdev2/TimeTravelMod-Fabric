package com.rdvdev2.timetravelmod.impl;

import net.minecraft.sound.SoundEvent;
import net.minecraft.util.registry.Registry;

public class ModSoundEvents {

    public static final SoundEvent OLD_WEST_MUSIC = new SoundEvent(Mod.identifier("old_west_music"));

    public static void register() {
        registerSoundEvent("old_west_music", OLD_WEST_MUSIC);
    }

    public static void registerSoundEvent(String path, SoundEvent soundEvent) {
        Registry.register(Registry.SOUND_EVENT, Mod.identifier(path), soundEvent);
    }
}
