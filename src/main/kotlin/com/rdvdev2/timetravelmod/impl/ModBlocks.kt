package com.rdvdev2.timetravelmod.impl

import com.rdvdev2.timetravelmod.api.timemachine.block.TimeMachineControllerBlock
import com.rdvdev2.timetravelmod.api.timemachine.block.TimeMachineCoreBlock
import com.rdvdev2.timetravelmod.api.timemachine.block.TimeMachineUpgradeBlock
import com.rdvdev2.timetravelmod.impl.Mod.identifier
import com.rdvdev2.timetravelmod.impl.common.block.AnomalousAtemporalVoidBlock
import com.rdvdev2.timetravelmod.impl.common.block.GunpowderWireBlock
import com.rdvdev2.timetravelmod.impl.common.block.TemporalCauldronBlock
import com.rdvdev2.timetravelmod.impl.common.block.TimeMachineRecallerBlock
import com.rdvdev2.timetravelmod.impl.common.block.entity.AnomalousAtemporalVoidBlockEntity
import com.rdvdev2.timetravelmod.impl.common.block.entity.TemporalCauldronBlockEntity
import com.rdvdev2.timetravelmod.impl.common.block.entity.TimeMachineCoreBlockEntity
import com.rdvdev2.timetravelmod.impl.common.block.entity.TimeMachineRecallerBlockEntity
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.block.Material
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.util.registry.Registry
import java.util.function.Supplier

object ModBlocks {

    val TIME_CRYSTAL_ORE = Block(FabricBlockSettings.of(Material.STONE).sounds(BlockSoundGroup.STONE).strength(5f, 5f).luminance(5 / 16).breakByTool(FabricToolTags.PICKAXES, 3))
    @JvmField
    val TIME_MACHINE_BASIC_BLOCK = Block(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).strength(3f, 3f).luminance(0 / 16).breakByTool(FabricToolTags.PICKAXES, 2))
    @JvmField
    val TIME_MACHINE_CABLE_PANEL = Block(FabricBlockSettings.copyOf(TIME_MACHINE_BASIC_BLOCK))
    @JvmField
    val TIME_MACHINE_PILLAR = Block(FabricBlockSettings.copyOf(TIME_MACHINE_BASIC_BLOCK))
    @JvmField
    val TIME_MACHINE_HORIZONTAL_VENTILATION = Block(FabricBlockSettings.copyOf(TIME_MACHINE_BASIC_BLOCK))
    @JvmField
    val TIME_MACHINE_VERTICAL_VENTILATION = Block(FabricBlockSettings.copyOf(TIME_MACHINE_BASIC_BLOCK))
    @JvmField
    val TIME_MACHINE_CORE: Block = TimeMachineCoreBlock(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).strength(4f, 4f).luminance(5 / 16).breakByTool(FabricToolTags.PICKAXES, 3))
    @JvmField
    val TIME_MACHINE_CONTROL_PANEL: Block = TimeMachineControllerBlock(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).strength(3f, 3f).luminance(0 / 16).breakByTool(FabricToolTags.PICKAXES, 2))
    val HEAVY_BLOCK = Block(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).strength(7f, 7f).luminance(0 / 16).breakByTool(FabricToolTags.PICKAXES, 3))
    val REINFORCED_HEAVY_BLOCK = Block(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).strength(10f, 10f).luminance(0).breakByTool(FabricToolTags.PICKAXES, 3))
    @JvmField
    val ANOMALOUS_ATEMPORAL_VOID: Block = AnomalousAtemporalVoidBlock(FabricBlockSettings.of(Material.PORTAL).sounds(BlockSoundGroup.METAL).strength(-1f, Float.MAX_VALUE).luminance(0).nonOpaque())
    val TEMPORAL_CAULDRON: Block = TemporalCauldronBlock(FabricBlockSettings.of(Material.METAL).breakByTool(FabricToolTags.PICKAXES, 1).hardness(2.0f).nonOpaque())
    @JvmField
    val GUNPOWDER_WIRE: Block = GunpowderWireBlock(FabricBlockSettings.copyOf(Blocks.REDSTONE_WIRE))
    @JvmField
    val TIME_MACHINE_TRACKER: Block = TimeMachineUpgradeBlock(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).strength(3f, 3f).luminance(0 / 16).breakByTool(FabricToolTags.PICKAXES, 2), ModTimeMachines.Upgrades.TRACKER)
    @JvmField
    val TIME_MACHINE_RECALLER: Block = TimeMachineRecallerBlock(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).hardness(3f).luminance(0 / 16).nonOpaque().breakByTool(FabricToolTags.PICKAXES, 2))

    fun register() {
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
        Entities.register()
    }

    object Entities {
        @JvmField
        val TIME_MACHINE_CORE = createBEType(::TimeMachineCoreBlockEntity, ModBlocks.TIME_MACHINE_CORE)
        @JvmField
        val ANOMALOUS_ATEMPORAL_VOID = createBEType(::AnomalousAtemporalVoidBlockEntity, ModBlocks.ANOMALOUS_ATEMPORAL_VOID)
        @JvmField
        val TIME_MACHINE_RECALLER = createBEType(::TimeMachineRecallerBlockEntity, ModBlocks.TIME_MACHINE_RECALLER)
        @JvmField
        val TEMPORAL_CAULDRON = createBEType(::TemporalCauldronBlockEntity, ModBlocks.TEMPORAL_CAULDRON)

        fun register() {
            TIME_MACHINE_CORE.registerAs("time_machine_core")
            ANOMALOUS_ATEMPORAL_VOID.registerAs("anomalous_atemporal_void")
            TIME_MACHINE_RECALLER.registerAs("time_machine_recaller")
            TEMPORAL_CAULDRON.registerAs("temporal_cauldron")
        }

        private fun <T : BlockEntity?> createBEType(blockEntitySupplier: Supplier<T>, vararg blocks: Block) = BlockEntityType.Builder.create(blockEntitySupplier, *blocks).build(null)
        private fun BlockEntityType<*>.registerAs(path: String) = Registry.register(Registry.BLOCK_ENTITY_TYPE, identifier(path), this)
    }

    private fun Block.registerAs(path: String) = Registry.register(Registry.BLOCK, identifier(path), this)
}