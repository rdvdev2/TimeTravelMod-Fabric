package com.rdvdev2.timetravelmod.impl.client;

import com.rdvdev2.timetravelmod.impl.ModBlocks;
import com.rdvdev2.timetravelmod.impl.client.renderer.AnomalousAtemporalVoidBlockEntityRenderer;
import com.rdvdev2.timetravelmod.impl.client.renderer.TemporalCauldronBlockEntityRenderer;
import com.rdvdev2.timetravelmod.impl.common.block.GunpowderWireBlock;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.render.RenderLayer;

public class ModClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ModClientNetworking.register();
        registerRenderers();
        registerBlockColorProviders();
        setBlockRenderLayers();
    }

    private void registerRenderers() {
        BlockEntityRendererRegistry.INSTANCE.register(ModBlocks.Entities.ANOMALOUS_ATEMPORAL_VOID, AnomalousAtemporalVoidBlockEntityRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(ModBlocks.Entities.TEMPORAL_CAULDRON, TemporalCauldronBlockEntityRenderer::new);
    }

    private void registerBlockColorProviders() {
        ColorProviderRegistry.BLOCK.register(
                (state, world, pos, tintIndex) -> GunpowderWireBlock.getWireColor(state.get(GunpowderWireBlock.BURNED)),
                ModBlocks.GUNPOWDER_WIRE);
    }

    private void setBlockRenderLayers() {
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.GUNPOWDER_WIRE, RenderLayer.getCutoutMipped());
    }
}
