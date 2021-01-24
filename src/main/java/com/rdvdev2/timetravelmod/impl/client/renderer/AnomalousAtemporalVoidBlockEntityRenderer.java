package com.rdvdev2.timetravelmod.impl.client.renderer;

import com.google.common.collect.ImmutableList;
import com.rdvdev2.timetravelmod.impl.common.block.entity.AnomalousAtemporalVoidBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Matrix4f;

import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

public class AnomalousAtemporalVoidBlockEntityRenderer extends BlockEntityRenderer<AnomalousAtemporalVoidBlockEntity> {

    public AnomalousAtemporalVoidBlockEntityRenderer(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    private static final Random RANDOM = new Random(31100L);
    private static final List<RenderLayer> RENDER_LAYERS = IntStream.range(0, 16)
            .mapToObj(i -> RenderLayer.getEndPortal(i + 1))
            .collect(ImmutableList.toImmutableList());

    public void render(AnomalousAtemporalVoidBlockEntity entity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
        RANDOM.setSeed(31100L);
        double distance = entity.getPos().getSquaredDistance(this.dispatcher.camera.getPos(), true);
        int iterations = this.calculateIterations(distance);
        Matrix4f matrix4f = matrixStack.peek().getModel();
        this.drawCube(entity, 0.15F, matrix4f, vertexConsumerProvider.getBuffer(RENDER_LAYERS.get(0)));

        for(int l = 1; l < iterations; ++l) {
            this.drawCube(entity, 2.0F / (float)(18 - l), matrix4f, vertexConsumerProvider.getBuffer(RENDER_LAYERS.get(l)));
        }

    }

    private void drawCube(AnomalousAtemporalVoidBlockEntity entity, float colorMultiplier, Matrix4f matrix4f, VertexConsumer vertexConsumer) {
        float r = (RANDOM.nextFloat() * 0.5F + 0.1F) * colorMultiplier;
        float g = (RANDOM.nextFloat() * 0.5F + 0.4F) * colorMultiplier;
        float b = (RANDOM.nextFloat() * 0.5F + 0.5F) * colorMultiplier;
        this.drawFace(entity, matrix4f, vertexConsumer, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, r, g, b, Direction.SOUTH);
        this.drawFace(entity, matrix4f, vertexConsumer, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, r, g, b, Direction.NORTH);
        this.drawFace(entity, matrix4f, vertexConsumer, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, r, g, b, Direction.EAST);
        this.drawFace(entity, matrix4f, vertexConsumer, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F, r, g, b, Direction.WEST);
        this.drawFace(entity, matrix4f, vertexConsumer, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, r, g, b, Direction.DOWN);
        this.drawFace(entity, matrix4f, vertexConsumer, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, r, g, b, Direction.UP);
    }

    private void drawFace(AnomalousAtemporalVoidBlockEntity entity, Matrix4f matrix4f, VertexConsumer vertexConsumer, float x1, float x2, float y1, float y2, float z1, float z2, float z3, float z4, float r, float g, float b, Direction direction) {
        if (Block.shouldDrawSide(entity.getCachedState(), entity.getWorld(), entity.getPos(), direction)) {
            vertexConsumer.vertex(matrix4f, x1, y1, z1).color(r, g, b, 1.0F).next();
            vertexConsumer.vertex(matrix4f, x2, y1, z2).color(r, g, b, 1.0F).next();
            vertexConsumer.vertex(matrix4f, x2, y2, z3).color(r, g, b, 1.0F).next();
            vertexConsumer.vertex(matrix4f, x1, y2, z4).color(r, g, b, 1.0F).next();
        }
    }

    protected int calculateIterations(double d) {
        if (d > 36864.0D) {
            return 1;
        } else if (d > 25600.0D) {
            return 3;
        } else if (d > 16384.0D) {
            return 5;
        } else if (d > 9216.0D) {
            return 7;
        } else if (d > 4096.0D) {
            return 9;
        } else if (d > 1024.0D) {
            return 11;
        } else if (d > 576.0D) {
            return 13;
        } else {
            return d > 256.0D ? 14 : 15;
        }
    }
}
