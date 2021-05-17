package com.rdvdev2.timetravelmod.api.timemachine

import com.rdvdev2.timetravelmod.impl.ModRegistries
import net.minecraft.block.BlockState
import net.minecraft.item.Item
import net.minecraft.text.TranslatableText

interface ITimeMachine {

    val name: TranslatableText
        get() {
            val id = ModRegistries.TIME_MACHINE.getId(this)
            return TranslatableText(String.format("tm.%s.%s.name", id!!.namespace, id.path))
        }

    val description: TranslatableText
        get() {
            val id = ModRegistries.TIME_MACHINE.getId(this)
            return TranslatableText(String.format("tm.%s.%s.description", id!!.namespace, id.path))
        }

    // Must be overridden by TMs that aren't physically built
    val icon: Item?
        get() = controllerStates[0].block.asItem()

    val cooldownTime: Int
        get() = 400

    val tier: Int
        get() = 1

    /*
    Z -> Controller
    C -> Core
    B -> Basic / Upgrade
      -> Air
    * -> Exclude
     */
    val structureLayers: Array<Array<String>>

    val controllerStates: Array<BlockState>

    val coreStates: Array<BlockState>

    val basicStates: Array<BlockState>

    val entityMaxLoad: Int
        get() = 1

    val corruptionMultiplier: Int
        get() = 1
}