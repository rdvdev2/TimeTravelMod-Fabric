package com.rdvdev2.TimeTravelMod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;

@Environment(EnvType.CLIENT)
public class ModClient implements ClientModInitializer {
    
    @Override
    public void onInitializeClient() {
        ModBlocks.registerBlockColor();
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.GUNPOWDER_WIRE, RenderLayer.getCutout());
        ModPacketHandler.registerClient();
    }
}
