package com.rdvdev2.timetravelmod.impl.common.block

import com.google.common.collect.ImmutableMap
import com.google.common.collect.Maps
import com.rdvdev2.timetravelmod.impl.ModBlocks
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.block.*
import net.minecraft.block.enums.WireConnection
import net.minecraft.client.util.math.Vector3f
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemPlacementContext
import net.minecraft.item.Items
import net.minecraft.particle.DustParticleEffect
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.state.property.EnumProperty
import net.minecraft.state.property.Properties
import net.minecraft.util.ActionResult
import net.minecraft.util.BlockMirror
import net.minecraft.util.BlockRotation
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.MathHelper
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.World
import net.minecraft.world.WorldAccess
import net.minecraft.world.WorldView
import java.util.*

class GunpowderWireBlock(settings: Settings) : Block(settings) {

    private val stateShapeMap: MutableMap<BlockState, VoxelShape?> = Maps.newHashMap()

    init {
        defaultState = stateManager.defaultState
            .with(WIRE_CONNECTION_NORTH, WireConnection.NONE)
            .with(WIRE_CONNECTION_EAST, WireConnection.NONE)
            .with(WIRE_CONNECTION_SOUTH, WireConnection.NONE)
            .with(WIRE_CONNECTION_WEST, WireConnection.NONE)
            .with(BURNED, false)
        for (blockState in getStateManager().states) {
            if (!blockState.get(BURNED)) {
                stateShapeMap[blockState] = getShape(blockState)
            }
        }
    }

    private fun getShape(state: BlockState): VoxelShape {
        var voxelShape = DOT_SHAPE
        for (direction in Direction.Type.HORIZONTAL) {
            val wireConnection = state.get(DIRECTION_TO_WIRE_CONNECTION_PROPERTY[direction])
            if (wireConnection == WireConnection.SIDE) {
                voxelShape = VoxelShapes.union(voxelShape, SIDE_SHAPE[direction])
            } else if (wireConnection == WireConnection.UP) {
                voxelShape = VoxelShapes.union(voxelShape, UP_SHAPE[direction])
            }
        }
        return voxelShape
    }

    override fun getOutlineShape(state: BlockState, world: BlockView, pos: BlockPos, context: ShapeContext): VoxelShape {
        return stateShapeMap[state.with(BURNED, false)]!!
    }

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState {
        return getSideState(ctx.world, defaultState, ctx.blockPos)
    }

    fun getSideState(world: BlockView, state: BlockState, pos: BlockPos): BlockState {
        var state = state
        val notConnected = isNotConnected(state)
        state = method_27843(world, defaultState.with(BURNED, state.get(BURNED)), pos)
        if (notConnected && isNotConnected(state)) {
            return state
        } else {
            val northConnected = state.get(WIRE_CONNECTION_NORTH).isConnected
            val southConnected = state.get(WIRE_CONNECTION_SOUTH).isConnected
            val eastConnected = state.get(WIRE_CONNECTION_EAST).isConnected
            val westConnected = state.get(WIRE_CONNECTION_WEST).isConnected
            val horizontal = !northConnected && !southConnected
            val vertical = !eastConnected && !westConnected
            if (!westConnected && horizontal) {
                return state.with(WIRE_CONNECTION_WEST, WireConnection.SIDE)
            }
            if (!eastConnected && horizontal) {
                return state.with(WIRE_CONNECTION_EAST, WireConnection.SIDE)
            }
            if (!northConnected && vertical) {
                return state.with(WIRE_CONNECTION_NORTH, WireConnection.SIDE)
            }
            if (!southConnected && vertical) {
                return state.with(WIRE_CONNECTION_SOUTH, WireConnection.SIDE)
            }
            return state
        }
    }

    private fun method_27843(world: BlockView, state: BlockState, pos: BlockPos): BlockState {
        var state = state
        val nonSolidCeiling = !world.getBlockState(pos.up()).isSolidBlock(world, pos)
        for (direction in Direction.Type.HORIZONTAL) {
            if (!state.get(DIRECTION_TO_WIRE_CONNECTION_PROPERTY[direction]).isConnected) {
                val wireConnection = method_27841(world, pos, direction, nonSolidCeiling)
                state = state.with(DIRECTION_TO_WIRE_CONNECTION_PROPERTY[direction], wireConnection)
            }
        }
        return state
    }

    override fun getStateForNeighborUpdate(state: BlockState, direction: Direction, newState: BlockState, world: WorldAccess, pos: BlockPos, posFrom: BlockPos): BlockState {
        if (direction == Direction.DOWN || world.getBlockState(pos).isOf(this) && world.getBlockState(pos).get(BURNED)) {
            return state
        } else if (direction == Direction.UP) {
            return getSideState(world, state, pos)
        } else {
            val wireConnection = getRenderConnectionType(world, pos, direction)
            if (wireConnection.isConnected == state.get(DIRECTION_TO_WIRE_CONNECTION_PROPERTY[direction]).isConnected && !isFullyConnected(state))
                return state.with(DIRECTION_TO_WIRE_CONNECTION_PROPERTY[direction], wireConnection)
            else
                return getSideState(world, defaultState.with(BURNED, state.get(BURNED)).with(DIRECTION_TO_WIRE_CONNECTION_PROPERTY[direction], wireConnection), pos)
        }
    }

    override fun prepare(state: BlockState, world: WorldAccess, pos: BlockPos, flags: Int, maxUpdateDepth: Int) {
        val mutable = BlockPos.Mutable()
        for (direction in Direction.Type.HORIZONTAL) {
            val wireConnection = state.get(DIRECTION_TO_WIRE_CONNECTION_PROPERTY[direction])
            if (wireConnection != WireConnection.NONE && !world.getBlockState(mutable.set(pos, direction)).isOf(this)) {
                mutable.move(Direction.DOWN)
                val blockState = world.getBlockState(mutable)
                val blockPos = mutable.offset(direction.opposite)
                val blockState2 = blockState.getStateForNeighborUpdate(direction.opposite, world.getBlockState(blockPos), world, mutable, blockPos)
                replace(blockState, blockState2, world, mutable, flags, maxUpdateDepth)
                mutable.set(pos, direction).move(Direction.UP)
                val blockState3 = world.getBlockState(mutable)
                val blockPos2 = mutable.offset(direction.opposite)
                val blockState4 = blockState3.getStateForNeighborUpdate(direction.opposite, world.getBlockState(blockPos2), world, mutable, blockPos2)
                replace(blockState3, blockState4, world, mutable, flags, maxUpdateDepth)
            }
        }
    }

    private fun getRenderConnectionType(blockView: BlockView, blockPos: BlockPos, direction: Direction): WireConnection {
        return method_27841(blockView, blockPos, direction, !blockView.getBlockState(blockPos.up()).isSolidBlock(blockView, blockPos))
    }

    private fun method_27841(blockView: BlockView, blockPos: BlockPos, direction: Direction, nonSolidCeiling: Boolean): WireConnection {
        val neighbourPos = blockPos.offset(direction)
        val neighbourState = blockView.getBlockState(neighbourPos)
        if (nonSolidCeiling) {
            val neighbourCanHold = canRunOnTop(blockView, neighbourPos, neighbourState)
            if (neighbourCanHold && connectsTo(blockView.getBlockState(neighbourPos.up()))) {
                if (neighbourState.isSideSolidFullSquare(blockView, neighbourPos, direction.opposite))
                    return WireConnection.UP
                else return WireConnection.SIDE
            }
        }
        if (!connectsTo(neighbourState) && (neighbourState.isSolidBlock(blockView, neighbourPos) || !connectsTo(blockView.getBlockState(neighbourPos.down()))))
            return WireConnection.NONE
        else return WireConnection.SIDE
    }

    override fun canPlaceAt(state: BlockState, world: WorldView, pos: BlockPos): Boolean {
        val floorPos = pos.down()
        val floorState = world.getBlockState(floorPos)
        return canRunOnTop(world, floorPos, floorState)
    }

    private fun canRunOnTop(world: BlockView, pos: BlockPos, floor: BlockState): Boolean {
        return floor.isSideSolidFullSquare(world, pos, Direction.UP) || floor.isOf(Blocks.HOPPER)
    }

    private fun updateNeighbors(world: World, pos: BlockPos) {
        if (world.getBlockState(pos).isOf(this)) {
            world.updateNeighborsAlways(pos, this)
            for (direction in Direction.values()) {
                world.updateNeighborsAlways(pos.offset(direction), this)
            }
        }
    }

    override fun onBlockAdded(state: BlockState, world: World, pos: BlockPos, oldState: BlockState, notify: Boolean) {
        if (!oldState.isOf(state.block) && !world.isClient) {
            for (direction in Direction.Type.VERTICAL) {
                world.updateNeighborsAlways(pos.offset(direction), this)
            }
            updateNecessaryNeighbors(world, pos)
        }
    }

    override fun onStateReplaced(state: BlockState, world: World, pos: BlockPos, newState: BlockState, moved: Boolean) {
        if (!moved && !state.isOf(newState.block)) {
            super.onStateReplaced(state, world, pos, newState, moved)
            if (!world.isClient) {
                for (direction in Direction.values()) {
                    world.updateNeighborsAlways(pos.offset(direction), this)
                }
                updateNecessaryNeighbors(world, pos)
            }
        }
    }

    private fun updateNecessaryNeighbors(world: World, pos: BlockPos) {
        var directionIterator: Iterator<Direction?> = Direction.Type.HORIZONTAL.iterator()
        var direction: Direction?
        while (directionIterator.hasNext()) {
            direction = directionIterator.next()
            updateNeighbors(world, pos.offset(direction))
        }
        directionIterator = Direction.Type.HORIZONTAL.iterator()
        while (directionIterator.hasNext()) {
            direction = directionIterator.next()
            val neighbourPos = pos.offset(direction)
            if (world.getBlockState(neighbourPos).isSolidBlock(world, neighbourPos)) {
                updateNeighbors(world, neighbourPos.up())
            } else {
                updateNeighbors(world, neighbourPos.down())
            }
        }
    }

    override fun neighborUpdate(state: BlockState, world: World, pos: BlockPos, block: Block, fromPos: BlockPos, notify: Boolean) {
        if (!world.isClient && !state.canPlaceAt(world, pos)) {
            dropStacks(state, world, pos)
            world.removeBlock(pos, false)
        }
    }

    @Environment(EnvType.CLIENT)
    private fun addParticles(world: World, random: Random, pos: BlockPos, color: Vector3f?, direction: Direction, direction2: Direction, f: Float, g: Float) {
        val h = g - f
        if (random.nextFloat() < 0.2f * h) {
            val j = f + h * random.nextFloat()
            val xOffset = 0.5 + (0.4375f * direction.offsetX.toFloat()).toDouble() + (j * direction2.offsetX.toFloat()).toDouble()
            val yOffset = 0.5 + (0.4375f * direction.offsetY.toFloat()).toDouble() + (j * direction2.offsetY.toFloat()).toDouble()
            val zOffset = 0.5 + (0.4375f * direction.offsetZ.toFloat()).toDouble() + (j * direction2.offsetZ.toFloat()).toDouble()
            world.addParticle(
                DustParticleEffect(color!!.x, color.y, color.z, 1.0f),
                pos.x.toDouble() + xOffset, pos.y.toDouble() + yOffset, pos.z.toDouble() + zOffset,
                0.0, 0.0, 0.0
            )
        }
    }

    @Environment(EnvType.CLIENT)
    override fun randomDisplayTick(state: BlockState, world: World, pos: BlockPos, random: Random) {
        val burned = state.get(BURNED)
        if (!burned) {
            for (direction in Direction.Type.HORIZONTAL) {
                when (state.get(DIRECTION_TO_WIRE_CONNECTION_PROPERTY[direction])) {
                    WireConnection.UP -> {
                        addParticles(world, random, pos, COLORS[0], direction, Direction.UP,  -0.5f, 0.5f)
                        addParticles(world, random, pos, COLORS[0], Direction.DOWN, direction, 0.0f, 0.5f)
                    }
                    WireConnection.SIDE ->
                        addParticles(world, random, pos, COLORS[0], Direction.DOWN, direction, 0.0f, 0.5f)
                    WireConnection.NONE ->
                        addParticles(world, random, pos, COLORS[0], Direction.DOWN, direction, 0.0f, 0.3f)
                    else ->
                        addParticles(world, random, pos, COLORS[0], Direction.DOWN, direction, 0.0f, 0.3f)
                }
            }
        }
    }

    override fun rotate(state: BlockState, rotation: BlockRotation): BlockState {
        return when (rotation) {
            BlockRotation.CLOCKWISE_180 -> state
                .with(WIRE_CONNECTION_NORTH, state.get(WIRE_CONNECTION_SOUTH))
                .with(WIRE_CONNECTION_EAST, state.get(WIRE_CONNECTION_WEST))
                .with(WIRE_CONNECTION_SOUTH, state.get(WIRE_CONNECTION_NORTH))
                .with(WIRE_CONNECTION_WEST, state.get(WIRE_CONNECTION_EAST))
            BlockRotation.COUNTERCLOCKWISE_90 -> state
                .with(WIRE_CONNECTION_NORTH, state.get(WIRE_CONNECTION_EAST))
                .with(WIRE_CONNECTION_EAST, state.get(WIRE_CONNECTION_SOUTH))
                .with(WIRE_CONNECTION_SOUTH, state.get(WIRE_CONNECTION_WEST))
                .with(WIRE_CONNECTION_WEST, state.get(WIRE_CONNECTION_NORTH))
            BlockRotation.CLOCKWISE_90 -> state
                .with(WIRE_CONNECTION_NORTH, state.get(WIRE_CONNECTION_WEST))
                .with(WIRE_CONNECTION_EAST, state.get(WIRE_CONNECTION_NORTH))
                .with(WIRE_CONNECTION_SOUTH, state.get(WIRE_CONNECTION_EAST))
                .with(WIRE_CONNECTION_WEST, state.get(WIRE_CONNECTION_SOUTH))
            else -> state
        }
    }

    override fun mirror(state: BlockState, mirror: BlockMirror): BlockState {
        return when (mirror) {
            BlockMirror.LEFT_RIGHT -> state
                .with(WIRE_CONNECTION_NORTH, state.get(WIRE_CONNECTION_SOUTH))
                .with(WIRE_CONNECTION_SOUTH, state.get(WIRE_CONNECTION_NORTH))
            BlockMirror.FRONT_BACK -> state
                .with(WIRE_CONNECTION_EAST, state.get(WIRE_CONNECTION_WEST))
                .with(WIRE_CONNECTION_WEST, state.get(WIRE_CONNECTION_EAST))
            else -> super.mirror(state, mirror)
        }
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(WIRE_CONNECTION_NORTH, WIRE_CONNECTION_EAST, WIRE_CONNECTION_SOUTH, WIRE_CONNECTION_WEST, BURNED)
    }

    override fun onUse(state: BlockState, world: World, pos: BlockPos, player: PlayerEntity, hand: Hand, hit: BlockHitResult): ActionResult {
        if (!(player.getStackInHand(hand).item === Items.FLINT_AND_STEEL || player.getStackInHand(hand).item === Items.FIRE_CHARGE)) {
            return super.onUse(state, world, pos, player, hand, hit)
        }
        for (tntPos in burn(world, pos, HashSet())) {
            world.setBlockState(tntPos, Blocks.AIR.defaultState)
            TntBlock.primeTnt(world, tntPos)
        }
        return ActionResult.SUCCESS
    }

    companion object {

        val WIRE_CONNECTION_NORTH: EnumProperty<WireConnection> = Properties.NORTH_WIRE_CONNECTION
        val WIRE_CONNECTION_EAST: EnumProperty<WireConnection> = Properties.EAST_WIRE_CONNECTION
        val WIRE_CONNECTION_SOUTH: EnumProperty<WireConnection> = Properties.SOUTH_WIRE_CONNECTION
        val WIRE_CONNECTION_WEST: EnumProperty<WireConnection> = Properties.WEST_WIRE_CONNECTION
        val BURNED: BooleanProperty = BooleanProperty.of("burned")
        val DIRECTION_TO_WIRE_CONNECTION_PROPERTY: Map<Direction, EnumProperty<WireConnection>> = Maps.newEnumMap(
            ImmutableMap.of(
                Direction.NORTH, WIRE_CONNECTION_NORTH,
                Direction.EAST, WIRE_CONNECTION_EAST,
                Direction.SOUTH, WIRE_CONNECTION_SOUTH,
                Direction.WEST, WIRE_CONNECTION_WEST
            ))
        private val DOT_SHAPE: VoxelShape = createCuboidShape(3.0, 0.0, 3.0, 13.0, 1.0, 13.0)
        private val SIDE_SHAPE: Map<Direction, VoxelShape> = Maps.newEnumMap(
            ImmutableMap.of(
                Direction.NORTH, createCuboidShape(3.0, 0.0, 0.0, 13.0, 1.0, 13.0),
                Direction.SOUTH, createCuboidShape(3.0, 0.0, 3.0, 13.0, 1.0, 16.0),
                Direction.EAST, createCuboidShape(3.0, 0.0, 3.0, 16.0, 1.0, 13.0),
                Direction.WEST, createCuboidShape(0.0, 0.0, 3.0, 13.0, 1.0, 13.0)
            ))
        private val UP_SHAPE: Map<Direction, VoxelShape> = Maps.newEnumMap(
            ImmutableMap.of(
                Direction.NORTH, VoxelShapes.union(SIDE_SHAPE[Direction.NORTH], createCuboidShape(3.0, 0.0, 0.0, 13.0, 16.0, 1.0)),
                Direction.SOUTH, VoxelShapes.union(SIDE_SHAPE[Direction.SOUTH], createCuboidShape(3.0, 0.0, 15.0, 13.0, 16.0, 16.0)),
                Direction.EAST, VoxelShapes.union(SIDE_SHAPE[Direction.EAST], createCuboidShape(15.0, 0.0, 3.0, 16.0, 16.0, 13.0)),
                Direction.WEST, VoxelShapes.union(SIDE_SHAPE[Direction.WEST], createCuboidShape(0.0, 0.0, 3.0, 1.0, 16.0, 13.0))
            ))
        private val COLORS: Array<Vector3f> = arrayOf(
            Vector3f(0.596f, 0.596f, 0.596f), // Not burned
            Vector3f(0.149f, 0.149f, 0.149f) // Burned
        )

        private fun isFullyConnected(state: BlockState) =
            state.get(WIRE_CONNECTION_NORTH).isConnected
                    && state.get(WIRE_CONNECTION_SOUTH).isConnected
                    && state.get(WIRE_CONNECTION_EAST).isConnected
                    && state.get(WIRE_CONNECTION_WEST).isConnected

        private fun isNotConnected(state: BlockState) =
            !state.get(WIRE_CONNECTION_NORTH).isConnected
                    && !state.get(WIRE_CONNECTION_SOUTH).isConnected
                    && !state.get(WIRE_CONNECTION_EAST).isConnected
                    && !state.get(WIRE_CONNECTION_WEST).isConnected

        private fun connectsTo(state: BlockState) =
            state.isOf(ModBlocks.GUNPOWDER_WIRE) && !state.get(BURNED) || state.isOf(Blocks.TNT)

        @Environment(EnvType.CLIENT)
        fun getWireColor(burned: Boolean): Int {
            val vector3f = COLORS[if (burned) 1 else 0]
            return MathHelper.packRgb(vector3f.x, vector3f.y, vector3f.z)
        }

        private fun burn(world: World, pos: BlockPos, tntToIgnite: MutableSet<BlockPos>): Set<BlockPos> {
            if (world.getBlockState(pos).isOf(ModBlocks.GUNPOWDER_WIRE) && !world.getBlockState(pos).get(BURNED)) {
                world.setBlockState(pos, world.getBlockState(pos).with(BURNED, true), 3, 0)
                for ((direction, wireConnectionProperty) in DIRECTION_TO_WIRE_CONNECTION_PROPERTY) {
                    val neighborPos = pos.offset(direction)
                    val connection = world.getBlockState(pos).get(wireConnectionProperty)
                    if (connection == WireConnection.UP)
                        burn(world, neighborPos.up(), tntToIgnite)
                    else if (connection == WireConnection.SIDE && connectsTo(world.getBlockState(neighborPos)))
                        burn(world, neighborPos, tntToIgnite)
                    else if (connection == WireConnection.SIDE && !world.getBlockState(neighborPos).isSideSolidFullSquare(world, pos, direction.opposite) && world.getBlockState(neighborPos.down()).isOf(ModBlocks.GUNPOWDER_WIRE))
                        burn(world, neighborPos.down(), tntToIgnite)
                }
                burn(world, pos.down(), tntToIgnite)
            } else if (world.getBlockState(pos).isOf(Blocks.TNT)) {
                tntToIgnite.add(pos)
            }
            return tntToIgnite
        }
    }
}