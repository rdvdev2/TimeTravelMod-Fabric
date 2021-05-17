package com.rdvdev2.timetravelmod.impl.common.timemachine

import com.rdvdev2.timetravelmod.api.timemachine.ITimeMachine
import net.minecraft.block.BlockState
import net.minecraft.item.Item

class TimeMachine(
    private val _icon: Item?,
    override val cooldownTime: Int,
    override val tier: Int,
    override val structureLayers: Array<Array<String>>,
    override val controllerStates: Array<BlockState>,
    override val coreStates: Array<BlockState>,
    override val basicStates: Array<BlockState>,
    override val entityMaxLoad: Int,
    override val corruptionMultiplier: Int)
    : ITimeMachine {

    override val icon: Item?
        get() = _icon ?: super.icon
}