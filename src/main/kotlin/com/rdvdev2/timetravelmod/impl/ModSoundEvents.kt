package com.rdvdev2.timetravelmod.impl

import com.rdvdev2.timetravelmod.impl.Mod.identifier
import net.minecraft.sound.SoundEvent
import net.minecraft.util.registry.Registry

object ModSoundEvents {

    @JvmField
    val OLD_WEST_MUSIC = SoundEvent(identifier("old_west_music"))

    fun register() {
        OLD_WEST_MUSIC.registerAs("old_west_music")
    }

    private fun SoundEvent.registerAs(path: String) = Registry.register(Registry.SOUND_EVENT, identifier(path), this)
}