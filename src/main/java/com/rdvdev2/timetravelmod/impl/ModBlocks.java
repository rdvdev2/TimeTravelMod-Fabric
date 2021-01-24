package com.rdvdev2.timetravelmod.impl;

import com.rdvdev2.timetravelmod.api.timemachine.block.TimeMachineUpgradeBlock;
import com.rdvdev2.timetravelmod.impl.common.block.AnomalousAtemporalVoidBlock;
import com.rdvdev2.timetravelmod.api.timemachine.block.TimeMachineControllerBlock;
import com.rdvdev2.timetravelmod.api.timemachine.block.TimeMachineCoreBlock;
import com.rdvdev2.timetravelmod.impl.common.block.GunpowderWireBlock;
import com.rdvdev2.timetravelmod.impl.common.block.TemporalCauldronBlock;
import com.rdvdev2.timetravelmod.impl.common.block.TimeMachineRecallerBlock;
import com.rdvdev2.timetravelmod.impl.common.block.entity.AnomalousAtemporalVoidBlockEntity;
import com.rdvdev2.timetravelmod.impl.common.block.entity.TemporalCauldronBlockEntity;
import com.rdvdev2.timetravelmod.impl.common.block.entity.TimeMachineCoreBlockEntity;
import com.rdvdev2.timetravelmod.impl.common.block.entity.TimeMachineRecallerBlockEntity;
import com.rdvdev2.timetravelmod.mixin.common.IFluidBlock;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.registry.Registry;

import java.util.function.Supplier;

public class ModBlocks {

    public static final Block TIME_CRYSTAL_ORE = new Block(FabricBlockSettings.of(Material.STONE).sounds(BlockSoundGroup.STONE).strength(5f, 5f).luminance(5/16).breakByTool(FabricToolTags.PICKAXES, 3));
    public static final Block TIME_MACHINE_BASIC_BLOCK = new Block(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).strength(3f, 3f).luminance(0 / 16).breakByTool(FabricToolTags.PICKAXES, 2));
    public static final Block TIME_MACHINE_CABLE_PANEL = new Block(FabricBlockSettings.copyOf(TIME_MACHINE_BASIC_BLOCK));
    public static final Block TIME_MACHINE_PILLAR = new Block(FabricBlockSettings.copyOf(TIME_MACHINE_BASIC_BLOCK));
    public static final Block TIME_MACHINE_HORIZONTAL_VENTILATION = new Block(FabricBlockSettings.copyOf(TIME_MACHINE_BASIC_BLOCK));
    public static final Block TIME_MACHINE_VERTICAL_VENTILATION = new Block(FabricBlockSettings.copyOf(TIME_MACHINE_BASIC_BLOCK));
    public static final Block TIME_MACHINE_CORE = new TimeMachineCoreBlock(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).strength(4f, 4f).luminance(5 / 16).breakByTool(FabricToolTags.PICKAXES, 3));
    public static final Block TIME_MACHINE_CONTROL_PANEL = new TimeMachineControllerBlock(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).strength(3f, 3f).luminance(0 / 16).breakByTool(FabricToolTags.PICKAXES, 2));
    public static final Block HEAVY_BLOCK = new Block(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).strength(7f, 7f).luminance(0/16).breakByTool(FabricToolTags.PICKAXES, 3));
    public static final Block REINFORCED_HEAVY_BLOCK = new Block(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).strength(10f, 10f).luminance(0).breakByTool(FabricToolTags.PICKAXES, 3));
    public static final Block ANOMALOUS_ATEMPORAL_VOID = new AnomalousAtemporalVoidBlock(FabricBlockSettings.of(Material.PORTAL).sounds(BlockSoundGroup.METAL).strength(-1, Float.MAX_VALUE).luminance(0).nonOpaque());
    public static final Block TEMPORAL_CAULDRON = new TemporalCauldronBlock(FabricBlockSettings.of(Material.METAL).breakByTool(FabricToolTags.PICKAXES, 1).hardness(2.0F).nonOpaque());
    public static final Block GUNPOWDER_WIRE = new GunpowderWireBlock(FabricBlockSettings.copyOf(Blocks.REDSTONE_WIRE));
    public static final Block TIME_MACHINE_TRACKER = new TimeMachineUpgradeBlock(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).strength(3f, 3f).luminance(0 / 16).breakByTool(FabricToolTags.PICKAXES, 2), ModTimeMachines.Upgrades.TRACKER);
    public static final Block TIME_MACHINE_RECALLER = new TimeMachineRecallerBlock(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).hardness(3f).luminance(0 / 16).nonOpaque().breakByTool(FabricToolTags.PICKAXES, 2));

    public static void register() {
        registerBlock("time_crystal_ore", TIME_CRYSTAL_ORE);
        registerBlock("time_machine_basic_block", TIME_MACHINE_BASIC_BLOCK);
        registerBlock("time_machine_cable_panel", TIME_MACHINE_CABLE_PANEL);
        registerBlock("time_machine_pillar", TIME_MACHINE_PILLAR);
        registerBlock("time_machine_horizontal_ventilation", TIME_MACHINE_HORIZONTAL_VENTILATION);
        registerBlock("time_machine_vertical_ventilation", TIME_MACHINE_VERTICAL_VENTILATION);
        registerBlock("time_machine_control_panel", TIME_MACHINE_CONTROL_PANEL);
        registerBlock("time_machine_core", TIME_MACHINE_CORE);
        registerBlock("heavy_block", HEAVY_BLOCK);
        registerBlock("reinforced_heavy_block", REINFORCED_HEAVY_BLOCK);
        registerBlock("anomalous_atemporal_void", ANOMALOUS_ATEMPORAL_VOID);
        registerBlock("temporal_cauldron", TEMPORAL_CAULDRON);
        registerBlock("gunpowder_wire", GUNPOWDER_WIRE);
        registerBlock("time_machine_tracker", TIME_MACHINE_TRACKER);
        registerBlock("time_machine_recaller", TIME_MACHINE_RECALLER);
        Entities.register();
    }

    private static void registerBlock(String path, Block block) {
        Registry.register(Registry.BLOCK, Mod.identifier(path), block);
    }

    public static class Entities {

        public static final BlockEntityType<TimeMachineCoreBlockEntity> TIME_MACHINE_CORE = createType(TimeMachineCoreBlockEntity::new, ModBlocks.TIME_MACHINE_CORE);
        public static final BlockEntityType<AnomalousAtemporalVoidBlockEntity> ANOMALOUS_ATEMPORAL_VOID = createType(AnomalousAtemporalVoidBlockEntity::new, ModBlocks.ANOMALOUS_ATEMPORAL_VOID);
        public static final BlockEntityType<TimeMachineRecallerBlockEntity> TIME_MACHINE_RECALLER = createType(TimeMachineRecallerBlockEntity::new, ModBlocks.TIME_MACHINE_RECALLER);
        public static final BlockEntityType<TemporalCauldronBlockEntity> TEMPORAL_CAULDRON = createType(TemporalCauldronBlockEntity::new, ModBlocks.TEMPORAL_CAULDRON);

        private static <T extends BlockEntity> BlockEntityType<T> createType(Supplier<T> blockEntitySupplier, Block... blocks) {
            return BlockEntityType.Builder.create(blockEntitySupplier, blocks).build(null);
        }

        public static void register() {
            registerBlockEntity("time_machine_core", TIME_MACHINE_CORE);
            registerBlockEntity("anomalous_atemporal_void", ANOMALOUS_ATEMPORAL_VOID);
            registerBlockEntity("time_machine_recaller", TIME_MACHINE_RECALLER);
            registerBlockEntity("temporal_cauldron", TEMPORAL_CAULDRON);
        }

        private static void registerBlockEntity(String path, BlockEntityType<?> entityType) {
            Registry.register(Registry.BLOCK_ENTITY_TYPE, Mod.identifier(path), entityType);
        }
    }
}
