package com.rdvdev2.timetravelmod.impl

import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback
import net.minecraft.item.ItemGroup

object Mod : net.fabricmc.api.ModInitializer {

    const val MODID = "time_travel_mod"
    val LOGGER = org.apache.logging.log4j.LogManager.getLogger()

    @JvmField
    val ITEM_GROUP: ItemGroup = net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder.build(identifier("main")) {
        net.minecraft.item.ItemStack(ModItems.TIME_CRYSTAL)
    }

    @JvmStatic
    fun identifier(path: String?): net.minecraft.util.Identifier {
        return net.minecraft.util.Identifier(MODID, path)
    }

    override fun onInitialize() {
        ModBiomeSources.register()
        ModBlocks.register()
        CommandRegistrationCallback.EVENT.register(ModCommands::register)
        ModConfig.register()
        ModFeatures.register()
        ModItems.register()
        ModNetworking.register()
        ModSoundEvents.register()
        ModTimelines.register()
        ModTimeMachines.register()
        ModTriggers.register()
    }
}