package com.rdvdev2.timetravelmod.impl.common.dimension.timeline

import com.rdvdev2.timetravelmod.api.dimension.ICorruption
import com.rdvdev2.timetravelmod.api.dimension.ITimeline
import com.rdvdev2.timetravelmod.impl.ModRegistries
import com.rdvdev2.timetravelmod.impl.common.dimension.Corruption
import net.minecraft.item.Item
import net.minecraft.server.MinecraftServer
import net.minecraft.text.TranslatableText
import net.minecraft.util.registry.RegistryKey
import net.minecraft.world.World

class Timeline(override val minTier: Int, override val world: RegistryKey<World>, override val icon: Item) : ITimeline {

    override fun getCorruption(server: MinecraftServer): ICorruption =
        server.getWorld(world)!!.persistentStateManager.getOrCreate(
            { Corruption(this) },
            Corruption.ID.toString()
        )

    override val name: TranslatableText
        get() = TranslatableText("gui.tm.${ModRegistries.TIMELINE.getId(this)!!.namespace}.${ModRegistries.TIMELINE.getId(this)!!.path}")
}