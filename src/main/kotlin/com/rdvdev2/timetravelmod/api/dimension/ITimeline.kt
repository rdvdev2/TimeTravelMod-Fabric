package com.rdvdev2.timetravelmod.api.dimension

import com.rdvdev2.timetravelmod.impl.ModRegistries
import net.minecraft.item.Item
import net.minecraft.server.MinecraftServer
import net.minecraft.text.TranslatableText
import net.minecraft.util.registry.RegistryKey
import net.minecraft.world.World

interface ITimeline {

    val minTier: Int

    val world: RegistryKey<World>

    fun getCorruption(server: MinecraftServer): ICorruption

    val name: TranslatableText

    val icon: Item

    companion object {
        @JvmStatic
        fun getTimelineForWorld(world: RegistryKey<World>): ITimeline? {
            return ModRegistries.TIMELINE.stream()
                .filter { t: ITimeline -> t.world === world }
                .findFirst()
                .orElse(null)
        }
    }
}