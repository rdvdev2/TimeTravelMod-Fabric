package com.rdvdev2.timetravelmod.api.timemachine

import com.rdvdev2.timetravelmod.impl.common.timemachine.TimeMachine
import net.minecraft.block.BlockState
import net.minecraft.item.Item

class TimeMachineBuilder {

    private var icon: Item? = null
    private var cooldownTime = 400
    private var tier = 1
    private var structureLayers: Array<Array<String>> = emptyArray()
    private var controllerStates: Array<BlockState> = emptyArray()
    private var coreStates: Array<BlockState> = emptyArray()
    private var basicStates: Array<BlockState> = emptyArray()
    private var entityMaxLoad = 1
    private var corruptionMultiplier = 1

    fun setIcon(icon: Item?): TimeMachineBuilder {
        this.icon = icon
        return this
    }

    fun setCooldownTime(cooldownTime: Int): TimeMachineBuilder {
        this.cooldownTime = cooldownTime
        return this
    }

    fun setTier(tier: Int): TimeMachineBuilder {
        this.tier = tier
        return this
    }

    fun setStructureLayers(structureLayers: Array<Array<String>>): TimeMachineBuilder {
        this.structureLayers = structureLayers
        return this
    }

    fun setControllerStates(vararg controllerStates: BlockState): TimeMachineBuilder {
        this.controllerStates = arrayOf(*controllerStates)
        return this
    }

    fun setCoreStates(vararg coreStates: BlockState): TimeMachineBuilder {
        this.coreStates = arrayOf(*coreStates)
        return this
    }

    fun setBasicStates(vararg basicStates: BlockState): TimeMachineBuilder {
        this.basicStates = arrayOf(*basicStates)
        return this
    }

    fun setEntityMaxLoad(entityMaxLoad: Int): TimeMachineBuilder {
        this.entityMaxLoad = entityMaxLoad
        return this
    }

    fun setCorruptionMultiplier(corruptionMultiplier: Int): TimeMachineBuilder {
        this.corruptionMultiplier = corruptionMultiplier
        return this
    }

    fun build(): ITimeMachine {
        return TimeMachine(icon, cooldownTime, tier, structureLayers, controllerStates, coreStates, basicStates, entityMaxLoad, corruptionMultiplier)
    }
}