package com.rdvdev2.TimeTravelMod;

import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModSounds {

    public static final SoundEvent OLDWEST_MUSIC = new SoundEvent(new Identifier(Mod.MODID, "oldwest_music"));

    public static void register() {
        Registry.register(Registry.SOUND_EVENT, new Identifier(Mod.MODID, "oldwest_music"), OLDWEST_MUSIC);
    }
}
