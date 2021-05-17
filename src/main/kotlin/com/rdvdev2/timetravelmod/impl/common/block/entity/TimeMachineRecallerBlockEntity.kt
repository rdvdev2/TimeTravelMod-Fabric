package com.rdvdev2.timetravelmod.impl.common.block.entity

import com.rdvdev2.timetravelmod.impl.ModBlocks
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryKey
import net.minecraft.world.World

class TimeMachineRecallerBlockEntity : BlockEntity(ModBlocks.Entities.TIME_MACHINE_RECALLER) {

    var tmWorld: RegistryKey<World>? = null
        private set
    var rootPos: BlockPos? = null
        private set
    var trackerPos: BlockPos? = null
        private set

    fun configure(world: RegistryKey<World>, rootPos: BlockPos, trackerPos: BlockPos) {
        tmWorld = world
        this.rootPos = rootPos
        this.trackerPos = trackerPos
        markDirty()
    }

    fun clear() {
        tmWorld = null
        rootPos = null
        trackerPos = null
        markDirty()
    }

    val configured: Boolean
        get() = tmWorld != null

    override fun fromTag(state: BlockState, tag: CompoundTag) {
        if (tag.contains("time_machine_world")) {
            tmWorld = RegistryKey.of(Registry.DIMENSION, Identifier.tryParse(tag.getString("time_machine_world")))
            val rootPosTag = tag.getCompound("root_pos")
            rootPos = BlockPos(rootPosTag.getInt("x"), rootPosTag.getInt("y"), rootPosTag.getInt("z"))
            val trackerPosTag = tag.getCompound("tracker_pos")
            trackerPos = BlockPos(trackerPosTag.getInt("x"), trackerPosTag.getInt("y"), trackerPosTag.getInt("z"))
        }
        super.fromTag(state, tag)
    }

    override fun toTag(tag: CompoundTag): CompoundTag {
        if (configured) {
            tag.putString("time_machine_world", tmWorld!!.value.toString())
            val rootPosTag = CompoundTag()
            rootPosTag.putInt("x", rootPos!!.x)
            rootPosTag.putInt("y", rootPos!!.y)
            rootPosTag.putInt("z", rootPos!!.z)
            tag.put("root_pos", rootPosTag)
            val trackerPosTag = CompoundTag()
            trackerPosTag.putInt("x", trackerPos!!.x)
            trackerPosTag.putInt("y", trackerPos!!.y)
            trackerPosTag.putInt("z", trackerPos!!.z)
            tag.put("tracker_pos", trackerPosTag)
        }
        return super.toTag(tag)
    }
}