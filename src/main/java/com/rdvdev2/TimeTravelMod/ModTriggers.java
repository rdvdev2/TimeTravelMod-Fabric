package com.rdvdev2.TimeTravelMod;

import com.rdvdev2.TimeTravelMod.common.triggers.CustomTrigger;
import com.rdvdev2.TimeTravelMod.mixin.ICriteria;
import net.minecraft.util.Identifier;

import static com.rdvdev2.TimeTravelMod.Mod.MODID;

public class ModTriggers {
    public static final CustomTrigger ACCESS_TIME_MACHINE = new CustomTrigger(new Identifier(MODID, "access_time_machine"));
    public static final CustomTrigger BETTER_THAN_MENDING = new CustomTrigger(new Identifier(MODID, "better_than_mending"));
    public static final CustomTrigger TEMPORAL_EXPLOSION = new CustomTrigger(new Identifier(MODID, "temporal_explosion"));

    public static final CustomTrigger[] TRIGGERS = new CustomTrigger[]{
            ACCESS_TIME_MACHINE,
            BETTER_THAN_MENDING,
            TEMPORAL_EXPLOSION
    };

    public static void register() {
        for (CustomTrigger trigger : TRIGGERS) ICriteria.doRegister(trigger);
    }
}
