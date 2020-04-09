package com.rdvdev2.TimeTravelMod.common.timemachine;

import com.google.common.collect.ImmutableMap;
import com.rdvdev2.TimeTravelMod.ModRegistries;
import com.rdvdev2.TimeTravelMod.api.timemachine.TimeMachine;
import com.rdvdev2.TimeTravelMod.api.timemachine.block.TimeMachineUpgradeBlock;
import com.rdvdev2.TimeTravelMod.api.timemachine.upgrade.TimeMachineUpgrade;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.minecraft.block.BlockState;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class TimeMachineManager {
    
    private static final Map<BlockState, Identifier> STATE_TO_TM = new HashMap<>();
    private static final Map<TimeMachineUpgrade, TimeMachineUpgradeBlock[]> UPGRADE_TO_BLOCKS = new HashMap<>();
    
    public static ImmutableMap<BlockState, Identifier> getStateToTm() {
        return ImmutableMap.copyOf(STATE_TO_TM);
    }
    
    public static void addUpgradeToBlockEntry(TimeMachineUpgrade upgrade, TimeMachineUpgradeBlock block) {
        if (UPGRADE_TO_BLOCKS.containsKey(upgrade)) {
            TimeMachineUpgradeBlock[] blocks = UPGRADE_TO_BLOCKS.get(upgrade);
            int index = blocks.length;
            blocks = Arrays.copyOf(blocks, index+1);
            blocks[index] = block;
            UPGRADE_TO_BLOCKS.put(upgrade, blocks);
        } else {
            UPGRADE_TO_BLOCKS.put(upgrade, new TimeMachineUpgradeBlock[]{block});
        }
    }
    
    public static ImmutableMap<TimeMachineUpgrade, TimeMachineUpgradeBlock[]> getUpgradeToBlocks() {
        return ImmutableMap.copyOf(UPGRADE_TO_BLOCKS);
    }
    
    public static void registerEvents() {
        RegistryEntryAddedCallback.event(ModRegistries.TIME_MACHINES).register(TimeMachineManager::onTimeMachineAdded);
    }
    
    private static void onTimeMachineAdded(int rawId, Identifier id, TimeMachine tm) {
        if (tm instanceof CreativeTimeMachine) return; // Special rule for the creative Time Machine
        if (!STATE_TO_TM.containsValue(ModRegistries.TIME_MACHINES.getId(tm))) {
            for(BlockState block:tm.getControllerBlocks()) {
                if (!STATE_TO_TM.containsKey(block)) {
                    STATE_TO_TM.put(block, ModRegistries.TIME_MACHINES.getId(tm));
                } else {
                    throw new RuntimeException(ModRegistries.TIME_MACHINES.getId(tm)+" tryed to register with controller block "+block.toString()+", but it is already registered to "+ STATE_TO_TM.get(block).toString());
                }
            }
        }
    }
}
