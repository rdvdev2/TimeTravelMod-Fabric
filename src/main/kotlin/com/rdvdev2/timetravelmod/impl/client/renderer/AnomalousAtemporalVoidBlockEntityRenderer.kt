package com.rdvdev2.timetravelmod.impl.client.renderer

import com.rdvdev2.timetravelmod.impl.common.block.entity.AnomalousAtemporalVoidBlockEntity
import net.minecraft.block.Block
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Matrix4f
import java.util.*
import java.util.stream.Collectors
import java.util.stream.IntStream

class AnomalousAtemporalVoidBlockEntityRenderer(dispatcher: BlockEntityRenderDispatcher?) : BlockEntityRenderer<AnomalousAtemporalVoidBlockEntity>(dispatcher) {

    override fun render(entity: AnomalousAtemporalVoidBlockEntity, f: Float, matrixStack: MatrixStack, vertexConsumerProvider: VertexConsumerProvider, i: Int, j: Int) {
        RANDOM.setSeed(31100L)
        val distance = entity.pos.getSquaredDistance(dispatcher.camera.pos, true)
        val iterations = calculateIterations(distance)
        val matrix4f = matrixStack.peek().model
        drawCube(entity, 0.15f, matrix4f, vertexConsumerProvider.getBuffer(RENDER_LAYERS[0]))
        for (l in 1 until iterations) {
            drawCube(entity, 2.0f / (18 - l).toFloat(), matrix4f, vertexConsumerProvider.getBuffer(RENDER_LAYERS[l]))
        }
    }

    private fun drawCube(entity: AnomalousAtemporalVoidBlockEntity, colorMultiplier: Float, matrix4f: Matrix4f, vertexConsumer: VertexConsumer) {
        val r = (RANDOM.nextFloat() * 0.5f + 0.1f) * colorMultiplier
        val g = (RANDOM.nextFloat() * 0.5f + 0.4f) * colorMultiplier
        val b = (RANDOM.nextFloat() * 0.5f + 0.5f) * colorMultiplier
        drawFace(entity, matrix4f, vertexConsumer, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, r, g, b, Direction.SOUTH)
        drawFace(entity, matrix4f, vertexConsumer, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, r, g, b, Direction.NORTH)
        drawFace(entity, matrix4f, vertexConsumer, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, r, g, b, Direction.EAST)
        drawFace(entity, matrix4f, vertexConsumer, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, r, g, b, Direction.WEST)
        drawFace(entity, matrix4f, vertexConsumer, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, r, g, b, Direction.DOWN)
        drawFace(entity, matrix4f, vertexConsumer, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, r, g, b, Direction.UP)
    }

    private fun drawFace(entity: AnomalousAtemporalVoidBlockEntity, matrix4f: Matrix4f, vertexConsumer: VertexConsumer, x1: Float, x2: Float, y1: Float, y2: Float, z1: Float, z2: Float, z3: Float, z4: Float, r: Float, g: Float, b: Float, direction: Direction) {
        if (Block.shouldDrawSide(entity.cachedState, entity.world, entity.pos, direction)) {
            vertexConsumer.vertex(matrix4f, x1, y1, z1).color(r, g, b, 1.0f).next()
            vertexConsumer.vertex(matrix4f, x2, y1, z2).color(r, g, b, 1.0f).next()
            vertexConsumer.vertex(matrix4f, x2, y2, z3).color(r, g, b, 1.0f).next()
            vertexConsumer.vertex(matrix4f, x1, y2, z4).color(r, g, b, 1.0f).next()
        }
    }

    private fun calculateIterations(d: Double): Int {
        return if (d > 36864.0) 1
        else if (d > 25600.0) 3
        else if (d > 16384.0) 5
        else if (d > 9216.0) 7
        else if (d > 4096.0) 9
        else if (d > 1024.0) 11
        else if (d > 576.0) 13
        else if (d > 256.0) 14
        else 15
    }

    companion object {
        private val RANDOM = Random(31100L)
        private val RENDER_LAYERS: List<RenderLayer> = IntStream.range(0, 16)
            .mapToObj { i: Int -> RenderLayer.getEndPortal(i + 1) }
            .collect(Collectors.toUnmodifiableList())
    }
}