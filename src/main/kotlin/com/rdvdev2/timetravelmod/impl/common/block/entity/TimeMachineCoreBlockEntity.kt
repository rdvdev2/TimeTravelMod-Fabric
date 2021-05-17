package com.rdvdev2.timetravelmod.impl.common.block.entity

import com.rdvdev2.timetravelmod.impl.ModBlocks
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.Tickable

class TimeMachineCoreBlockEntity : BlockEntity(ModBlocks.Entities.TIME_MACHINE_CORE), Tickable, BlockEntityClientSerializable {

    private var remainingTicks = 0

    override fun tick() {
        if (remainingTicks == 0) return
        remainingTicks--
        markDirty()
    }

    // TODO: Reserved for visuals
    fun getRemainingTicks(): Int {
        return remainingTicks
    }

    fun setRemainingTicks(remainingTicks: Int) {
        this.remainingTicks = remainingTicks
        markDirty()
    }

    val ready: Boolean
        get() = remainingTicks <= 0

    override fun fromTag(state: BlockState, tag: CompoundTag) {
        fromClientTag(tag)
        super.fromTag(state, tag)
    }

    override fun toTag(tag: CompoundTag): CompoundTag {
        val tag = toClientTag(tag)
        return super.toTag(tag)
    }

    override fun fromClientTag(tag: CompoundTag) {
        remainingTicks = tag.getInt("remaining_ticks")
    }

    override fun toClientTag(tag: CompoundTag): CompoundTag {
        tag.putInt("remaining_ticks", remainingTicks)
        return tag
    }
}