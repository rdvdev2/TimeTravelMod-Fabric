package com.rdvdev2.timetravelmod.impl.client.music

import com.rdvdev2.timetravelmod.impl.ModConfig.instance
import com.rdvdev2.timetravelmod.impl.ModDimensions
import com.rdvdev2.timetravelmod.impl.ModSoundEvents
import net.minecraft.client.sound.MusicType
import net.minecraft.sound.MusicSound
import net.minecraft.sound.SoundEvent
import net.minecraft.util.registry.RegistryKey
import net.minecraft.world.World
import java.util.*

object TimelineMusicManager {

    private val musicMap = mutableMapOf<RegistryKey<World>, MusicSound>()

    private fun addMusic(dimension: RegistryKey<World>, soundEvent: SoundEvent) {
        musicMap[dimension] = MusicType.createIngameMusic(soundEvent)
    }

    fun getMusic(dimension: RegistryKey<World>): Optional<MusicSound> {
        return if (!instance.client.enableTimelineMusic) Optional.empty()
        else Optional.ofNullable(musicMap[dimension])
    }

    init {
        addMusic(ModDimensions.OLD_WEST, ModSoundEvents.OLD_WEST_MUSIC)
    }
}