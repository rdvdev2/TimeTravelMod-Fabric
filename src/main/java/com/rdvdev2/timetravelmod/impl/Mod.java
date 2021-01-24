package com.rdvdev2.timetravelmod.impl;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Mod implements ModInitializer {

    public static final String MODID = "time_travel_mod";
    public static final Logger LOGGER = LogManager.getLogger();
    public static final ItemGroup ITEM_GROUP = FabricItemGroupBuilder.build(identifier("main"), () -> new ItemStack(ModItems.TIME_CRYSTAL));

    public static Identifier identifier(String path) {
        return new Identifier(MODID, path);
    }

    @Override
    public void onInitialize() {
        ModBiomeSources.register();
        ModBlocks.register();
        CommandRegistrationCallback.EVENT.register(ModCommands::register);
        ModConfig.register();
        ModFeatures.register();
        ModItems.register();
        ModNetworking.register();
        ModSoundEvents.register();
        ModTimelines.register();
        ModTimeMachines.register();
        ModTriggers.register();
    }
}
