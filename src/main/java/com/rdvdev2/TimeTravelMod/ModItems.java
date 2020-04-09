package com.rdvdev2.TimeTravelMod;

import com.rdvdev2.TimeTravelMod.common.item.CreativeTimeMachineItem;
import com.rdvdev2.TimeTravelMod.common.item.ItemEngineerBook;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModItems {
    
    public static final Item TIME_CRYSTAL = new Item(new Item.Settings().group(Mod.TAB_TTM));
    public static final Item CONTROLLER_CIRCUIT = new Item(new Item.Settings().group(Mod.TAB_TTM));
    public static final Item HEAVY_INGOT = new Item(new Item.Settings().group(Mod.TAB_TTM));
    public static final Item CREATIVE_TIME_MACHINE = new CreativeTimeMachineItem();
    public static final Item ENGINEER_BOOK = new ItemEngineerBook();
    public static final Item COMMUNICATIONS_CIRCUIT = new Item(new Item.Settings().group(Mod.TAB_TTM));

    public static final Item TIME_CRYSTAL_ORE = createBlockItem(ModBlocks.TIME_CRYSTAL_ORE);
    public static final Item TIME_MACHINE_BASIC_BLOCK = createBlockItem(ModBlocks.TIME_MACHINE_BASIC_BLOCK);
    public static final Item TIME_MACHINE_CORE = createBlockItem(ModBlocks.TIME_MACHINE_CORE);
    public static final Item TIME_MACHINE_CONTROL_PANEL = createBlockItem(ModBlocks.TIME_MACHINE_CONTROL_PANEL);
    public static final Item HEAVY_BLOCK = createBlockItem(ModBlocks.HEAVY_BLOCK);
    public static final Item REINFORCED_HEAVY_BLOCK = createBlockItem(ModBlocks.REINFORCED_HEAVY_BLOCK);
    public static final Item TEMPORAL_EXPLOSION = createBlockItem(ModBlocks.TEMPORAL_EXPLOSION);
    public static final Item TEMPORAL_CAULDRON = createBlockItem(ModBlocks.TEMPORAL_CAULDRON);
    public static final Item GUNPOWDER_WIRE = createBlockItem(ModBlocks.GUNPOWDER_WIRE);
    public static final Item TIME_MACHINE_TRACKER = createBlockItem(ModBlocks.TIME_MACHINE_TRACKER);
    public static final Item TIME_MACHINE_RECALLER = createBlockItem(ModBlocks.TIME_MACHINE_RECALLER);

    private static Item createBlockItem(Block block) {
        return new BlockItem(block, new Item.Settings().group(Mod.TAB_TTM));
    }

    public static void register() {
        Registry.register(Registry.ITEM, new Identifier(Mod.MODID, "timecrystal"), TIME_CRYSTAL);
        Registry.register(Registry.ITEM, new Identifier(Mod.MODID, "controllercircuit"), CONTROLLER_CIRCUIT);
        Registry.register(Registry.ITEM, new Identifier(Mod.MODID, "heavyingot"), HEAVY_INGOT);
        Registry.register(Registry.ITEM, new Identifier(Mod.MODID, "creativetimemachine"), CREATIVE_TIME_MACHINE);
        Registry.register(Registry.ITEM, new Identifier(Mod.MODID, "engineerbook"), ENGINEER_BOOK);
        Registry.register(Registry.ITEM, new Identifier(Mod.MODID, "communicationscircuit"), COMMUNICATIONS_CIRCUIT);
    
        Registry.register(Registry.ITEM, new Identifier(Mod.MODID, "timecrystalore"), TIME_CRYSTAL_ORE);
        Registry.register(Registry.ITEM, new Identifier(Mod.MODID, "timemachinebasicblock"), TIME_MACHINE_BASIC_BLOCK);
        Registry.register(Registry.ITEM, new Identifier(Mod.MODID, "timemachinecore"), TIME_MACHINE_CORE);
        Registry.register(Registry.ITEM, new Identifier(Mod.MODID, "timemachinecontrolpanel"), TIME_MACHINE_CONTROL_PANEL);
        Registry.register(Registry.ITEM, new Identifier(Mod.MODID, "heavyblock"), HEAVY_BLOCK);
        Registry.register(Registry.ITEM, new Identifier(Mod.MODID, "reinforcedheavyblock"), REINFORCED_HEAVY_BLOCK);
        Registry.register(Registry.ITEM, new Identifier(Mod.MODID, "temporalexplosion"), TEMPORAL_EXPLOSION);
        Registry.register(Registry.ITEM, new Identifier(Mod.MODID, "temporalcauldron"), TEMPORAL_CAULDRON);
        Registry.register(Registry.ITEM, new Identifier(Mod.MODID, "gunpowderwire"), GUNPOWDER_WIRE);
        Registry.register(Registry.ITEM, new Identifier(Mod.MODID, "timemachinetracker"), TIME_MACHINE_TRACKER);
        Registry.register(Registry.ITEM, new Identifier(Mod.MODID, "timemachinerecaller"), TIME_MACHINE_RECALLER);
    }
}
