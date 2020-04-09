package com.rdvdev2.TimeTravelMod;

import com.rdvdev2.TimeTravelMod.common.world.VanillaBiomesFeatures;
import com.rdvdev2.TimeTravelMod.common.world.dimension.oldwest.village.OldWestVillagePools;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Mod implements ModInitializer {

    public static final String MODID = "timetravelmod";

    public static final Logger LOGGER = LogManager.getLogger("TIMETRAVELMOD");

    public static final ItemGroup TAB_TTM = FabricItemGroupBuilder.build(new Identifier(MODID, "tab"), () -> new ItemStack(ModItems.TIME_CRYSTAL));

    public void onInitialize() {
        Mod.LOGGER.info("Time Travel Mod is in commonInitialize stage");
        ModConfig.register();
        ModRegistries.register();
        ModBlocks.register();
        ModItems.register();
        ModFeatures.register();
        ModBiomes.register();
        ModSounds.register();
        ModPacketHandler.registerServer();
        ModTimeLines.register();
        ModTimeMachines.register();
        VanillaBiomesFeatures.register();
        OldWestVillagePools.init();
        ModTriggers.register();
    }
}
