package com.rdvdev2.timetravelmod.impl

import com.rdvdev2.timetravelmod.impl.Mod.identifier
import com.rdvdev2.timetravelmod.impl.common.item.CreativeTimeMachineItem
import com.rdvdev2.timetravelmod.impl.common.item.EngineerBookItem
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.block.Block
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.util.registry.Registry

object ModItems {

    @JvmField
    val TIME_CRYSTAL = Item(FabricItemSettings().group(Mod.ITEM_GROUP))
    val CONTROLLER_CIRCUIT = Item(FabricItemSettings().group(Mod.ITEM_GROUP))
    val HEAVY_INGOT = Item(FabricItemSettings().group(Mod.ITEM_GROUP))
    @JvmField
    val CREATIVE_TIME_MACHINE: Item = CreativeTimeMachineItem(FabricItemSettings().group(Mod.ITEM_GROUP).maxCount(1))
    val ENGINEER_BOOK: Item = EngineerBookItem(FabricItemSettings().group(Mod.ITEM_GROUP).maxCount(1))
    val COMMUNICATIONS_CIRCUIT = Item(FabricItemSettings().group(Mod.ITEM_GROUP))
    val TIME_CRYSTAL_ORE = ModBlocks.TIME_CRYSTAL_ORE.createBlockItem()
    val TIME_MACHINE_BASIC_BLOCK = ModBlocks.TIME_MACHINE_BASIC_BLOCK.createBlockItem()
    val TIME_MACHINE_CABLE_PANEL = ModBlocks.TIME_MACHINE_CABLE_PANEL.createBlockItem()
    val TIME_MACHINE_PILLAR = ModBlocks.TIME_MACHINE_PILLAR.createBlockItem()
    val TIME_MACHINE_HORIZONTAL_VENTILATION = ModBlocks.TIME_MACHINE_HORIZONTAL_VENTILATION.createBlockItem()
    val TIME_MACHINE_VERTICAL_VENTILATION = ModBlocks.TIME_MACHINE_VERTICAL_VENTILATION.createBlockItem()
    val TIME_MACHINE_CORE = ModBlocks.TIME_MACHINE_CORE.createBlockItem()
    val TIME_MACHINE_CONTROL_PANEL = ModBlocks.TIME_MACHINE_CONTROL_PANEL.createBlockItem()
    val HEAVY_BLOCK = ModBlocks.HEAVY_BLOCK.createBlockItem()
    @JvmField
    val REINFORCED_HEAVY_BLOCK = ModBlocks.REINFORCED_HEAVY_BLOCK.createBlockItem()
    val ANOMALOUS_ATEMPORAL_VOID = ModBlocks.ANOMALOUS_ATEMPORAL_VOID.createBlockItem()
    val TEMPORAL_CAULDRON = ModBlocks.TEMPORAL_CAULDRON.createBlockItem()
    val GUNPOWDER_WIRE = ModBlocks.GUNPOWDER_WIRE.createBlockItem()
    @JvmField
    val TIME_MACHINE_TRACKER = ModBlocks.TIME_MACHINE_TRACKER.createBlockItem()
    val TIME_MACHINE_RECALLER = ModBlocks.TIME_MACHINE_RECALLER.createBlockItem()

    fun register() {
        TIME_CRYSTAL.registerAs("time_crystal")
        CONTROLLER_CIRCUIT.registerAs("controller_circuit")
        HEAVY_INGOT.registerAs("heavy_ingot")
        CREATIVE_TIME_MACHINE.registerAs("creative_time_machine")
        ENGINEER_BOOK.registerAs("engineer_book")
        COMMUNICATIONS_CIRCUIT.registerAs("communications_circuit")
        TIME_CRYSTAL_ORE.registerAs("time_crystal_ore")
        TIME_MACHINE_BASIC_BLOCK.registerAs("time_machine_basic_block")
        TIME_MACHINE_CABLE_PANEL.registerAs("time_machine_cable_panel")
        TIME_MACHINE_PILLAR.registerAs("time_machine_pillar")
        TIME_MACHINE_HORIZONTAL_VENTILATION.registerAs("time_machine_horizontal_ventilation")
        TIME_MACHINE_VERTICAL_VENTILATION.registerAs("time_machine_vertical_ventilation")
        TIME_MACHINE_CONTROL_PANEL.registerAs("time_machine_control_panel")
        TIME_MACHINE_CORE.registerAs("time_machine_core")
        HEAVY_BLOCK.registerAs("heavy_block")
        REINFORCED_HEAVY_BLOCK.registerAs("reinforced_heavy_block")
        ANOMALOUS_ATEMPORAL_VOID.registerAs("anomalous_atemporal_void")
        TEMPORAL_CAULDRON.registerAs("temporal_cauldron")
        GUNPOWDER_WIRE.registerAs("gunpowder_wire")
        TIME_MACHINE_TRACKER.registerAs("time_machine_tracker")
        TIME_MACHINE_RECALLER.registerAs("time_machine_recaller")
    }

    private fun Item.registerAs(path: String) = Registry.register(Registry.ITEM, identifier(path), this)
    private fun Block.createBlockItem() = BlockItem(this, FabricItemSettings().group(Mod.ITEM_GROUP))
}

