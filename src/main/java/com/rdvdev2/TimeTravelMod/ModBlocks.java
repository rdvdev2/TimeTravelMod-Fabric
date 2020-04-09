package com.rdvdev2.TimeTravelMod;

import com.rdvdev2.TimeTravelMod.api.timemachine.block.TimeMachineControlPanelBlock;
import com.rdvdev2.TimeTravelMod.api.timemachine.block.TimeMachineCoreBlock;
import com.rdvdev2.TimeTravelMod.api.timemachine.block.TimeMachineUpgradeBlock;
import com.rdvdev2.TimeTravelMod.common.block.GunpowderWireBlock;
import com.rdvdev2.TimeTravelMod.common.block.TemporalCauldronBlock;
import com.rdvdev2.TimeTravelMod.common.block.TemporalExplosionBlock;
import com.rdvdev2.TimeTravelMod.common.block.TimeMachineRecallerBlock;
import com.rdvdev2.TimeTravelMod.common.block.blockentity.TMCooldownBlockEntity;
import com.rdvdev2.TimeTravelMod.common.block.blockentity.TemporalCauldronTileEntity;
import com.rdvdev2.TimeTravelMod.common.block.blockentity.TimeMachineRecallerBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.tools.FabricToolTags;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.HashSet;

public class ModBlocks {

    public static final Block TIME_CRYSTAL_ORE = new Block(FabricBlockSettings.of(Material.STONE).sounds(BlockSoundGroup.STONE).strength(5f, 5f).lightLevel(5/16).breakByTool(FabricToolTags.PICKAXES, 3).build());
    public static final Block TIME_MACHINE_BASIC_BLOCK = new Block(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).strength(3f, 3f).lightLevel(0 / 16).breakByTool(FabricToolTags.PICKAXES, 2).build());
    public static final Block TIME_MACHINE_CORE = new TimeMachineCoreBlock(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).strength(4f, 4f).lightLevel(5 / 16).breakByTool(FabricToolTags.PICKAXES, 3).build());
    public static final Block TIME_MACHINE_CONTROL_PANEL = new TimeMachineControlPanelBlock(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).strength(3f, 3f).lightLevel(0 / 16).breakByTool(FabricToolTags.PICKAXES, 2).build());
    public static final Block HEAVY_BLOCK = new Block(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).strength(7f, 7f).lightLevel(0/16).breakByTool(FabricToolTags.PICKAXES, 3).build());
    public static final Block REINFORCED_HEAVY_BLOCK = new Block(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).strength(10f, 10f).lightLevel(10).breakByTool(FabricToolTags.PICKAXES, 3).build());
    public static final Block TEMPORAL_EXPLOSION = new TemporalExplosionBlock();
    public static final Block TEMPORAL_CAULDRON = new TemporalCauldronBlock();
    public static final Block GUNPOWDER_WIRE = new GunpowderWireBlock();
    public static final Block TIME_MACHINE_TRACKER = new TimeMachineUpgradeBlock(FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.METAL).strength(3f, 3f).lightLevel(0 / 16).breakByTool(FabricToolTags.PICKAXES, 2).build(), ModTimeMachines.Upgrades.TRACKER);
    public static final Block TIME_MACHINE_RECALLER = new TimeMachineRecallerBlock();

    @Environment(EnvType.CLIENT)
    public static void registerBlockColor() {
        ColorProviderRegistry.BLOCK.register((state, world, pos, num) -> GunpowderWireBlock.colorMultiplier(state.get(GunpowderWireBlock.BURNED)), ModBlocks.GUNPOWDER_WIRE);
    }

    public static void register() {
        Registry.register(Registry.BLOCK, new Identifier(Mod.MODID, "timecrystalore"), TIME_CRYSTAL_ORE);
        Registry.register(Registry.BLOCK, new Identifier(Mod.MODID, "timemachinebasicblock"), TIME_MACHINE_BASIC_BLOCK);
        Registry.register(Registry.BLOCK, new Identifier(Mod.MODID, "timemachinecore"), TIME_MACHINE_CORE);
        Registry.register(Registry.BLOCK, new Identifier(Mod.MODID, "timemachinecontrolpanel"), TIME_MACHINE_CONTROL_PANEL);
        Registry.register(Registry.BLOCK, new Identifier(Mod.MODID, "heavyblock"), HEAVY_BLOCK);
        Registry.register(Registry.BLOCK, new Identifier(Mod.MODID, "reinforcedheavyblock"), REINFORCED_HEAVY_BLOCK);
        Registry.register(Registry.BLOCK, new Identifier(Mod.MODID, "temporalexplosion"), TEMPORAL_EXPLOSION);
        Registry.register(Registry.BLOCK, new Identifier(Mod.MODID, "temporalcauldron"), TEMPORAL_CAULDRON);
        Registry.register(Registry.BLOCK, new Identifier(Mod.MODID, "gunpowderwire"), GUNPOWDER_WIRE);
        Registry.register(Registry.BLOCK, new Identifier(Mod.MODID, "timemachinetracker"), TIME_MACHINE_TRACKER);
        Registry.register(Registry.BLOCK, new Identifier(Mod.MODID, "timemachinerecaller"), TIME_MACHINE_RECALLER);
        
        TileEntities.register();
    }

    public static class TileEntities {

        public static BlockEntityType<TemporalCauldronTileEntity> TEMPORAL_CAULDRON;
        public static BlockEntityType<TMCooldownBlockEntity> TM_COOLDOWN;
        public static BlockEntityType<TimeMachineRecallerBlockEntity> TM_RECALLER;

        private static Block[] getAllCoreBlocks() {
            HashSet<Block> blocks = new HashSet<Block>();
            for (Block block: Registry.BLOCK) {
                if (block instanceof TimeMachineCoreBlock) blocks.add(block);
            }
            return blocks.toArray(new Block[]{});
        }
        
        public static void register() {
            TEMPORAL_CAULDRON = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Mod.MODID, "temporalcauldron"), BlockEntityType.Builder.create(TemporalCauldronTileEntity::new, ModBlocks.TEMPORAL_CAULDRON).build(null));
            TM_COOLDOWN = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Mod.MODID, "tmcooldown"), BlockEntityType.Builder.create(TMCooldownBlockEntity::new, getAllCoreBlocks()).build(null));
            TM_RECALLER = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Mod.MODID, "tmrecaller"), BlockEntityType.Builder.create(TimeMachineRecallerBlockEntity::new, ModBlocks.TIME_MACHINE_RECALLER).build(null));
        }
    }
}
