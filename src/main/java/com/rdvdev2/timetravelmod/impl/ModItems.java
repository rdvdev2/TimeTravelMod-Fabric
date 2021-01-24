package com.rdvdev2.timetravelmod.impl;

import com.rdvdev2.timetravelmod.impl.common.item.CreativeTimeMachineItem;
import com.rdvdev2.timetravelmod.impl.common.item.EngineerBookItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.registry.Registry;

public class ModItems {

    public static final Item TIME_CRYSTAL = new Item(new FabricItemSettings().group(Mod.ITEM_GROUP));
    public static final Item CONTROLLER_CIRCUIT = new Item(new FabricItemSettings().group(Mod.ITEM_GROUP));
    public static final Item HEAVY_INGOT = new Item(new FabricItemSettings().group(Mod.ITEM_GROUP));
    public static final Item CREATIVE_TIME_MACHINE = new CreativeTimeMachineItem(new FabricItemSettings().group(Mod.ITEM_GROUP).maxCount(1));
    public static final Item ENGINEER_BOOK = new EngineerBookItem(new FabricItemSettings().group(Mod.ITEM_GROUP).maxCount(1));
    public static final Item COMMUNICATIONS_CIRCUIT = new Item(new FabricItemSettings().group(Mod.ITEM_GROUP));

    public static final Item TIME_CRYSTAL_ORE = createBlockItem(ModBlocks.TIME_CRYSTAL_ORE);
    public static final Item TIME_MACHINE_BASIC_BLOCK = createBlockItem(ModBlocks.TIME_MACHINE_BASIC_BLOCK);
    public static final Item TIME_MACHINE_CABLE_PANEL = createBlockItem(ModBlocks.TIME_MACHINE_CABLE_PANEL);
    public static final Item TIME_MACHINE_PILLAR = createBlockItem(ModBlocks.TIME_MACHINE_PILLAR);
    public static final Item TIME_MACHINE_HORIZONTAL_VENTILATION = createBlockItem(ModBlocks.TIME_MACHINE_HORIZONTAL_VENTILATION);
    public static final Item TIME_MACHINE_VERTICAL_VENTILATION = createBlockItem(ModBlocks.TIME_MACHINE_VERTICAL_VENTILATION);
    public static final Item TIME_MACHINE_CORE = createBlockItem(ModBlocks.TIME_MACHINE_CORE);
    public static final Item TIME_MACHINE_CONTROL_PANEL = createBlockItem(ModBlocks.TIME_MACHINE_CONTROL_PANEL);
    public static final Item HEAVY_BLOCK = createBlockItem(ModBlocks.HEAVY_BLOCK);
    public static final Item REINFORCED_HEAVY_BLOCK = createBlockItem(ModBlocks.REINFORCED_HEAVY_BLOCK);
    public static final Item ANOMALOUS_ATEMPORAL_VOID = createBlockItem(ModBlocks.ANOMALOUS_ATEMPORAL_VOID);
    public static final Item TEMPORAL_CAULDRON = createBlockItem(ModBlocks.TEMPORAL_CAULDRON);
    public static final Item GUNPOWDER_WIRE = createBlockItem(ModBlocks.GUNPOWDER_WIRE);
    public static final Item TIME_MACHINE_TRACKER = createBlockItem(ModBlocks.TIME_MACHINE_TRACKER);
    public static final Item TIME_MACHINE_RECALLER = createBlockItem(ModBlocks.TIME_MACHINE_RECALLER);

    private static Item createBlockItem(Block block) {
        return new BlockItem(block, new FabricItemSettings().group(Mod.ITEM_GROUP));
    }

    public static void register() {
        registerItem("time_crystal", TIME_CRYSTAL);
        registerItem("controller_circuit", CONTROLLER_CIRCUIT);
        registerItem("heavy_ingot", HEAVY_INGOT);
        registerItem("creative_time_machine", CREATIVE_TIME_MACHINE);
        registerItem("engineer_book", ENGINEER_BOOK);
        registerItem("communications_circuit", COMMUNICATIONS_CIRCUIT);

        registerItem("time_crystal_ore", TIME_CRYSTAL_ORE);
        registerItem("time_machine_basic_block", TIME_MACHINE_BASIC_BLOCK);
        registerItem("time_machine_cable_panel", TIME_MACHINE_CABLE_PANEL);
        registerItem("time_machine_pillar", TIME_MACHINE_PILLAR);
        registerItem("time_machine_horizontal_ventilation", TIME_MACHINE_HORIZONTAL_VENTILATION);
        registerItem("time_machine_vertical_ventilation", TIME_MACHINE_VERTICAL_VENTILATION);
        registerItem("time_machine_control_panel", TIME_MACHINE_CONTROL_PANEL);
        registerItem("time_machine_core", TIME_MACHINE_CORE);
        registerItem("heavy_block", HEAVY_BLOCK);
        registerItem("reinforced_heavy_block", REINFORCED_HEAVY_BLOCK);
        registerItem("anomalous_atemporal_void", ANOMALOUS_ATEMPORAL_VOID);
        registerItem("temporal_cauldron", TEMPORAL_CAULDRON);
        registerItem("gunpowder_wire", GUNPOWDER_WIRE);
        registerItem("time_machine_tracker", TIME_MACHINE_TRACKER);
        registerItem("time_machine_recaller", TIME_MACHINE_RECALLER);
    }

    private static void registerItem(String path, Item item) {
        Registry.register(Registry.ITEM, Mod.identifier(path), item);
    }
}
