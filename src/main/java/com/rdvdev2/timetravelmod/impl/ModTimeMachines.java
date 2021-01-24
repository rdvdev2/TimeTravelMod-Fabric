package com.rdvdev2.timetravelmod.impl;

import com.rdvdev2.timetravelmod.api.timemachine.ITimeMachine;
import com.rdvdev2.timetravelmod.api.timemachine.ITimeMachineUpgrade;
import com.rdvdev2.timetravelmod.api.timemachine.TimeMachineBuilder;
import com.rdvdev2.timetravelmod.impl.common.timemachine.upgrade.TimeMachineTrackerUpgrade;
import net.minecraft.block.BlockState;
import net.minecraft.util.registry.Registry;

public class ModTimeMachines {

    private static final BlockState[] BASIC_TIME_MACHINE_BLOCKS = new BlockState[]{
            ModBlocks.TIME_MACHINE_BASIC_BLOCK.getDefaultState(),
            ModBlocks.TIME_MACHINE_CABLE_PANEL.getDefaultState(),
            ModBlocks.TIME_MACHINE_PILLAR.getDefaultState(),
            ModBlocks.TIME_MACHINE_HORIZONTAL_VENTILATION.getDefaultState(),
            ModBlocks.TIME_MACHINE_VERTICAL_VENTILATION.getDefaultState()
    };

    public static final ITimeMachine CREATIVE = new TimeMachineBuilder()
            .setIcon(ModItems.CREATIVE_TIME_MACHINE)
            .setCooldownTime(0)
            .setStructureLayers(new String[][]{{"*"}})
            .setControllerStates()
            .setCoreStates()
            .setBasicStates()
            .build();

    public static final ITimeMachine TIER_1 = new TimeMachineBuilder()
            .setStructureLayers(new String[][]{
                    {
                            "BBB",
                            "BBB",
                            "BBB"
                    }, {
                            "BZB",
                            "B B",
                            "B B"
                    }, {
                            "BBB",
                            "B B",
                            "B B"
                    }, {
                            "BBB",
                            "BCB",
                            "BBB"
                    }})
            .setControllerStates(ModBlocks.TIME_MACHINE_CONTROL_PANEL.getDefaultState())
            .setCoreStates(ModBlocks.TIME_MACHINE_CORE.getDefaultState())
            .setBasicStates(BASIC_TIME_MACHINE_BLOCKS)
            .build();

    public static final ITimeMachine BIG_TIER_1 = new TimeMachineBuilder()
            .setStructureLayers(new String[][]{
                    {
                            "BBBBB",
                            "BBBBB",
                            "BBBBB",
                            "BBBBB",
                            "BBBBB"
                    }, {
                            "BBBBB",
                            "B   B",
                            "B   B",
                            "B   B",
                            "BBBBB"
                    }, {
                            "BBZBB",
                            "B   B",
                            "B   B",
                            "B   B",
                            "BB BB"
                    }, {
                            "BBBBB",
                            "B   B",
                            "B   B",
                            "B   B",
                            "BB BB"
                    }, {
                            "BBBBB",
                            "BBBBB",
                            "BBCBB",
                            "BBBBB",
                            "BBBBB"
                    }})
            .setControllerStates(ModBlocks.TIME_MACHINE_CONTROL_PANEL.getDefaultState())
            .setCoreStates(ModBlocks.TIME_MACHINE_CORE.getDefaultState())
            .setBasicStates(BASIC_TIME_MACHINE_BLOCKS)
            .setCorruptionMultiplier(3)
            .setEntityMaxLoad(3)
            .setCooldownTime(60 * 20)
            .build();

    public static void register() {
        registerTimeMachine("creative", CREATIVE);
        registerTimeMachine("tier_1", TIER_1);
        registerTimeMachine("big_tier_1", BIG_TIER_1);
        Upgrades.register();
    }

    public static void registerTimeMachine(String path, ITimeMachine timeMachine) {
        Registry.register(ModRegistries.TIME_MACHINE, Mod.identifier(path), timeMachine);
    }

    public static class Upgrades {

        public static final ITimeMachineUpgrade TRACKER = new TimeMachineTrackerUpgrade();

        public static void register() {
            registerTimeMachineUpgrade("tracker", TRACKER);
        }

        public static void registerTimeMachineUpgrade(String path, ITimeMachineUpgrade timeMachineUpgrade) {
            Registry.register(ModRegistries.TIME_MACHINE_UPGRADE, Mod.identifier(path), timeMachineUpgrade);
        }
    }
}
