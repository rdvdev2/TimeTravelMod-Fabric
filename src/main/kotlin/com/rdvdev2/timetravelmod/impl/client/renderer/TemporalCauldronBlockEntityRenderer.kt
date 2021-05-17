package com.rdvdev2.timetravelmod.impl.client.renderer

import com.rdvdev2.timetravelmod.impl.Mod.identifier
import com.rdvdev2.timetravelmod.impl.common.block.entity.TemporalCauldronBlockEntity
import net.minecraft.client.MinecraftClient
import net.minecraft.client.model.ModelPart
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.render.model.json.ModelTransformation
import net.minecraft.client.texture.SpriteAtlasTexture
import net.minecraft.client.util.SpriteIdentifier
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.client.util.math.Vector3f
import kotlin.math.sin

class TemporalCauldronBlockEntityRenderer(dispatcher: BlockEntityRenderDispatcher?) : BlockEntityRenderer<TemporalCauldronBlockEntity>(dispatcher) {

    override fun render(entity: TemporalCauldronBlockEntity, tickDelta: Float, matrices: MatrixStack, vertexConsumers: VertexConsumerProvider, light: Int, overlay: Int) {
        if (!entity.itemInside.isEmpty) {
            renderItemInside(entity, tickDelta, matrices, vertexConsumers, light, overlay)
        }
        val mbs = entity.timeCrystalMbs
        if (mbs > 0) renderFluidLayer(mbs, matrices, vertexConsumers, light, overlay)
    }

    private fun renderItemInside(entity: TemporalCauldronBlockEntity, tickDelta: Float, matrices: MatrixStack, vertexConsumers: VertexConsumerProvider, light: Int, overlay: Int) {
        matrices.push()
        val yOffset = sin((entity.world!!.time + tickDelta) / 8.0) / 6.0
        matrices.translate(0.5, 0.5 + yOffset, 0.5)
        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion((entity.world!!.time + tickDelta) * 4))
        MinecraftClient.getInstance().itemRenderer.renderItem(entity.itemInside, ModelTransformation.Mode.GROUND, light, overlay, matrices, vertexConsumers)
        matrices.pop()
    }

    private fun renderFluidLayer(mbs: Int, matrices: MatrixStack, vertexConsumers: VertexConsumerProvider, light: Int, overlay: Int) {
        matrices.push()
        val normalizedMbs = mbs.toFloat() / TemporalCauldronBlockEntity.MAX_TIME_CRYSTAL_MBS
        val fluidLayerHeight = 12 * normalizedMbs + 3
        val normalizedFluidHeight = fluidLayerHeight / 16
        matrices.translate(0.0, normalizedFluidHeight.toDouble(), 0.0)
        val vc = FLUID_LAYER_TEXTURE.getVertexConsumer(vertexConsumers, RenderLayer::getEntitySolid)
        FLUID_LAYER.render(matrices, vc, light, overlay)
        matrices.pop()
    }

    companion object {
        private val FLUID_LAYER = ModelPart(16, 16, 0, 0).addCuboid(2f, 0f, 2f, 12f, 0f, 12f)
        private val FLUID_LAYER_TEXTURE = SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, identifier("block/time_fluid_still")) // FIXME: Black texture
    }
}