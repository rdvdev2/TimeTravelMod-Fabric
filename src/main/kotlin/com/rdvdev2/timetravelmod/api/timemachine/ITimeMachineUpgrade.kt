package com.rdvdev2.timetravelmod.api.timemachine

import com.rdvdev2.timetravelmod.api.dimension.ITimeline
import com.rdvdev2.timetravelmod.impl.ModRegistries
import com.rdvdev2.timetravelmod.impl.common.timemachine.TimeMachineStructure
import net.minecraft.item.Item
import net.minecraft.text.TranslatableText
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

interface ITimeMachineUpgrade {

    val name: TranslatableText?
        get() {
            val id = ModRegistries.TIME_MACHINE_UPGRADE.getId(this)
            return TranslatableText(String.format("tm_upgrade.%s.%s.name", id!!.namespace, id.path))
        }

    val description: TranslatableText?
        get() {
            val id = ModRegistries.TIME_MACHINE_UPGRADE.getId(this)
            return TranslatableText(String.format("tm_upgrade.%s.%s.description", id!!.namespace, id.path))
        }

    val icon: Item?

    fun isTimeMachineCompatible(timeMachine: ITimeMachine?): Boolean {
        return true
    }

    fun beforeTeleporting(
        structure: TimeMachineStructure?,
        root: BlockPos?,
        upgrade: BlockPos?,
        origWorld: World?,
        destWorld: World?,
        origTimeline: ITimeline?,
        destTimeline: ITimeline?
    ) {
    }

    fun afterTeleporting(
        structure: TimeMachineStructure?,
        root: BlockPos?,
        upgrade: BlockPos?,
        origWorld: World?,
        destWorld: World?,
        origTimeline: ITimeline?,
        destTimeline: ITimeline?
    ) {
    }
}