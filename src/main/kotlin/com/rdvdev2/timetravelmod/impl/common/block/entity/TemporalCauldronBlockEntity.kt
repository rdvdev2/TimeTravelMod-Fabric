package com.rdvdev2.timetravelmod.impl.common.block.entity

import com.rdvdev2.timetravelmod.impl.ModBlocks
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.Tickable

class TemporalCauldronBlockEntity : BlockEntity(ModBlocks.Entities.TEMPORAL_CAULDRON), BlockEntityClientSerializable, Tickable {

    var itemInside: ItemStack = ItemStack.EMPTY
    var timeCrystalMbs = 0
        private set
    private var ticksForNextConsumption = 0

    fun consumeItemInside(): ItemStack {
        val ret = itemInside
        itemInside = ItemStack.EMPTY
        return ret
    }

    fun doesFullBucketFit() = timeCrystalMbs <= MAX_TIME_CRYSTAL_MBS - 1000

    fun addFullBucket() {
        timeCrystalMbs = (timeCrystalMbs + 1000).coerceAtMost(MAX_TIME_CRYSTAL_MBS)
    }

    override fun tick() {
        if (world!!.isClient || !itemInside.isDamaged || timeCrystalMbs == 0) return
        if (ticksForNextConsumption == 0) {
            ticksForNextConsumption = REPAIR_INTERVAL
            timeCrystalMbs--
            val prevDmg = itemInside.damage
            itemInside.damage = prevDmg - 1
        }
        ticksForNextConsumption--
    }

    override fun toTag(tag: CompoundTag): CompoundTag {
        val tag = toClientTag(tag)
        tag.putInt("ticksForNextConsumption", ticksForNextConsumption)
        return super.toTag(tag)
    }

    override fun toClientTag(tag: CompoundTag): CompoundTag {
        tag.put("itemstack", itemInside.toTag(CompoundTag()))
        tag.putInt("timeCrystalMbs", timeCrystalMbs)
        return tag
    }

    override fun fromTag(state: BlockState, tag: CompoundTag) {
        fromClientTag(tag)
        ticksForNextConsumption = tag.getInt("ticksForNextConsumption")
        super.fromTag(state, tag)
    }

    override fun fromClientTag(tag: CompoundTag) {
        if (tag.contains("itemstack")) {
            itemInside = ItemStack.fromTag(tag.getCompound("itemstack"))
            timeCrystalMbs = tag.getInt("timeCrystalMbs")
        }
    }

    companion object {
        const val MAX_TIME_CRYSTAL_MBS = 3000
        private const val REPAIR_INTERVAL = 20
    }
}