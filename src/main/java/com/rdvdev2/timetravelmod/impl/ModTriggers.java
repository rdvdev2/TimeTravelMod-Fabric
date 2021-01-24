package com.rdvdev2.timetravelmod.impl;

import com.rdvdev2.timetravelmod.impl.common.CustomTrigger;
import com.rdvdev2.timetravelmod.mixin.common.ICriteria;

public class ModTriggers {

    public static final CustomTrigger ACCESS_TIME_MACHINE = new CustomTrigger(Mod.identifier("access_time_machine"));
    public static final CustomTrigger BETTER_THAN_MENDING = new CustomTrigger(Mod.identifier("better_than_mending"));
    public static final CustomTrigger TEMPORAL_EXPLOSION = new CustomTrigger(Mod.identifier("temporal_explosion"));

    private static final CustomTrigger[] TRIGGERS = new CustomTrigger[]{
            ACCESS_TIME_MACHINE,
            BETTER_THAN_MENDING,
            TEMPORAL_EXPLOSION
    };

    public static void register() {
        for (CustomTrigger trigger: TRIGGERS) ICriteria.callRegister(trigger);
    }
}
