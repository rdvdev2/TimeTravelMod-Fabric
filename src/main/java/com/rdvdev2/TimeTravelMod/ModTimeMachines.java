package com.rdvdev2.TimeTravelMod;

import com.rdvdev2.TimeTravelMod.api.timemachine.TimeMachine;
import com.rdvdev2.TimeTravelMod.api.timemachine.upgrade.TimeMachineUpgrade;
import com.rdvdev2.TimeTravelMod.common.timemachine.CreativeTimeMachine;
import com.rdvdev2.TimeTravelMod.common.timemachine.Tier1TimeMachine;
import com.rdvdev2.TimeTravelMod.common.timemachine.hook.TrackerHooks;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static com.rdvdev2.TimeTravelMod.Mod.MODID;


public class ModTimeMachines {

    public static final TimeMachine TIER_1 = TimeMachine.fromTemplate(new Tier1TimeMachine());
    public static final TimeMachine CREATIVE = new CreativeTimeMachine();
    
    public static void register() {
        Registry.register(ModRegistries.TIME_MACHINES, new Identifier(MODID, "tier1"), TIER_1);
        Registry.register(ModRegistries.TIME_MACHINES, new Identifier(MODID, "creative"), CREATIVE);
        
        Upgrades.register();
    }

    public static class Upgrades {

        public static final TimeMachineUpgrade TRACKER = TimeMachineUpgrade.getNew().addHook(TrackerHooks.HOOKS[0], true).setCompatibleTMs(TIER_1);
        
        public static void register() {
            Registry.register(ModRegistries.UPGRADES, new Identifier(MODID, "tracker"), TRACKER);
        }
    }
}
