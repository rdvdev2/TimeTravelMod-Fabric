package com.rdvdev2.timetravelmod.impl

import com.rdvdev2.timetravelmod.impl.Mod.identifier
import com.rdvdev2.timetravelmod.impl.common.CustomTrigger
import com.rdvdev2.timetravelmod.mixin.common.ICriteria

object ModTriggers {

    @JvmField
    val ACCESS_TIME_MACHINE = CustomTrigger(identifier("access_time_machine"))
    val BETTER_THAN_MENDING = CustomTrigger(identifier("better_than_mending"))
    @JvmField
    val TEMPORAL_EXPLOSION = CustomTrigger(identifier("temporal_explosion"))

    private val TRIGGERS = arrayOf(
        ACCESS_TIME_MACHINE,
        BETTER_THAN_MENDING,
        TEMPORAL_EXPLOSION
    )

    fun register() {
        for (trigger in TRIGGERS) ICriteria.callRegister(trigger)
    }
}