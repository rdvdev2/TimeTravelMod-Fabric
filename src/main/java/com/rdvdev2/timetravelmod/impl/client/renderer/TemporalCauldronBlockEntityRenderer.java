package com.rdvdev2.timetravelmod.impl.client.renderer;

import com.rdvdev2.timetravelmod.impl.Mod;
import com.rdvdev2.timetravelmod.impl.common.block.entity.TemporalCauldronBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class TemporalCauldronBlockEntityRenderer extends BlockEntityRenderer<TemporalCauldronBlockEntity> {

    public TemporalCauldronBlockEntityRenderer(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    private final static ModelPart FLUID_LAYER = new ModelPart(16, 16, 0, 0)
            .addCuboid(2, 0, 2, 12, 0, 12);
    private final static SpriteIdentifier FLUID_LAYER_TEXTURE = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, Mod.identifier("block/time_fluid_still")); // FIXME: Black texture

    @Override
    public void render(TemporalCauldronBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (!entity.getItemInside().isEmpty()) {
            renderItemInside(entity, tickDelta, matrices, vertexConsumers, light, overlay);
        }

        int mbs = entity.getTimeCrystalMbs();
        if (mbs > 0) {
            renderFluidLayer(mbs, matrices, vertexConsumers, light, overlay);
        }
    }

    private void renderItemInside(TemporalCauldronBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();

        double yOffset = Math.sin((entity.getWorld().getTime() + tickDelta) / 8.0) / 6.0;

        matrices.translate(0.5, 0.5 + yOffset, 0.5);
        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion((entity.getWorld().getTime() + tickDelta) * 4));
        MinecraftClient.getInstance().getItemRenderer().renderItem(entity.getItemInside(), ModelTransformation.Mode.GROUND, light, overlay, matrices, vertexConsumers);

        matrices.pop();
    }

    private void renderFluidLayer(int mbs, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();

        float normalizedMbs = (float) mbs / TemporalCauldronBlockEntity.MAX_TIME_CRYSTAL_MBS;
        float fluidLayerHeight = (12 * normalizedMbs) + 3;
        float normalizedFluidHeight = fluidLayerHeight / 16;

        matrices.translate(0, normalizedFluidHeight, 0);
        VertexConsumer vc = FLUID_LAYER_TEXTURE.getVertexConsumer(vertexConsumers, RenderLayer::getEntitySolid);
        FLUID_LAYER.render(matrices, vc, light, overlay);

        matrices.pop();
    }
}
