package com.rdvdev2.timetravelmod.impl.client

import com.rdvdev2.timetravelmod.impl.ModBlocks
import com.rdvdev2.timetravelmod.impl.client.renderer.AnomalousAtemporalVoidBlockEntityRenderer
import com.rdvdev2.timetravelmod.impl.client.renderer.TemporalCauldronBlockEntityRenderer
import com.rdvdev2.timetravelmod.impl.common.block.GunpowderWireBlock
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry
import net.minecraft.block.BlockState
import net.minecraft.client.color.block.BlockColorProvider
import net.minecraft.client.render.RenderLayer
import net.minecraft.util.math.BlockPos
import net.minecraft.world.BlockRenderView

object ModClient : ClientModInitializer {

    override fun onInitializeClient() {
        ModClientNetworking.register()
        registerRenderers()
        registerBlockColorProviders()
        setBlockRenderLayers()
    }

    private fun registerRenderers() {
        BlockEntityRendererRegistry.INSTANCE.register(ModBlocks.Entities.ANOMALOUS_ATEMPORAL_VOID, ::AnomalousAtemporalVoidBlockEntityRenderer)
        BlockEntityRendererRegistry.INSTANCE.register(ModBlocks.Entities.TEMPORAL_CAULDRON, ::TemporalCauldronBlockEntityRenderer)
    }

    private fun registerBlockColorProviders() {
        ColorProviderRegistry.BLOCK.register(
            BlockColorProvider { state: BlockState, _: BlockRenderView?, _: BlockPos?, _: Int ->
                GunpowderWireBlock.getWireColor(
                    state.get(GunpowderWireBlock.BURNED)
                )
            },
            ModBlocks.GUNPOWDER_WIRE
        )
    }

    private fun setBlockRenderLayers() {
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.GUNPOWDER_WIRE, RenderLayer.getCutoutMipped())
    }
}