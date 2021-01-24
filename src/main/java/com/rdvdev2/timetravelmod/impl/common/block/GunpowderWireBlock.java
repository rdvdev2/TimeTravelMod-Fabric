package com.rdvdev2.timetravelmod.impl.common.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.rdvdev2.timetravelmod.impl.ModBlocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.*;
import net.minecraft.block.enums.WireConnection;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.Items;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

import java.util.*;

public class GunpowderWireBlock extends Block {

    public static final EnumProperty<WireConnection> WIRE_CONNECTION_NORTH;
    public static final EnumProperty<WireConnection> WIRE_CONNECTION_EAST;
    public static final EnumProperty<WireConnection> WIRE_CONNECTION_SOUTH;
    public static final EnumProperty<WireConnection> WIRE_CONNECTION_WEST;
    public static final BooleanProperty BURNED;
    public static final Map<Direction, EnumProperty<WireConnection>> DIRECTION_TO_WIRE_CONNECTION_PROPERTY;
    private static final VoxelShape DOT_SHAPE;
    private static final Map<Direction, VoxelShape> SIDE_SHAPE;
    private static final Map<Direction, VoxelShape> UP_SHAPE;
    private final Map<BlockState, VoxelShape> stateShapeMap = Maps.newHashMap();
    private static final Vector3f[] COLORS;

    public GunpowderWireBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(WIRE_CONNECTION_NORTH, WireConnection.NONE).with(WIRE_CONNECTION_EAST, WireConnection.NONE).with(WIRE_CONNECTION_SOUTH, WireConnection.NONE).with(WIRE_CONNECTION_WEST, WireConnection.NONE).with(BURNED, false));

        for (BlockState blockState: this.getStateManager().getStates()) {
            if (!blockState.get(BURNED)) {
                this.stateShapeMap.put(blockState, this.getShape(blockState));
            }
        }

    }

    private VoxelShape getShape(BlockState state) {
        VoxelShape voxelShape = DOT_SHAPE;

        for (Direction direction : Direction.Type.HORIZONTAL) {
            WireConnection wireConnection = state.get(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction));
            if (wireConnection == WireConnection.SIDE) {
                voxelShape = VoxelShapes.union(voxelShape, SIDE_SHAPE.get(direction));
            } else if (wireConnection == WireConnection.UP) {
                voxelShape = VoxelShapes.union(voxelShape, UP_SHAPE.get(direction));
            }
        }

        return voxelShape;
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return this.stateShapeMap.get(state.with(BURNED, false));
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getSideState(ctx.getWorld(), this.getDefaultState(), ctx.getBlockPos());
    }

    public BlockState getSideState(BlockView world, BlockState state, BlockPos pos) {
        boolean notConnected = isNotConnected(state);
        state = this.method_27843(world, this.getDefaultState().with(BURNED, state.get(BURNED)), pos);
        if (notConnected && isNotConnected(state)) {
            return state;
        } else {
            boolean northConnected = state.get(WIRE_CONNECTION_NORTH).isConnected();
            boolean southConnected = state.get(WIRE_CONNECTION_SOUTH).isConnected();
            boolean eastConnected = state.get(WIRE_CONNECTION_EAST).isConnected();
            boolean westConnected = state.get(WIRE_CONNECTION_WEST).isConnected();
            boolean horizontal = !northConnected && !southConnected;
            boolean vertical = !eastConnected && !westConnected;
            if (!westConnected && horizontal) {
                state = state.with(WIRE_CONNECTION_WEST, WireConnection.SIDE);
            }

            if (!eastConnected && horizontal) {
                state = state.with(WIRE_CONNECTION_EAST, WireConnection.SIDE);
            }

            if (!northConnected && vertical) {
                state = state.with(WIRE_CONNECTION_NORTH, WireConnection.SIDE);
            }

            if (!southConnected && vertical) {
                state = state.with(WIRE_CONNECTION_SOUTH, WireConnection.SIDE);
            }

            return state;
        }
    }

    private BlockState method_27843(BlockView world, BlockState state, BlockPos pos) {
        boolean nonSolidCeiling = !world.getBlockState(pos.up()).isSolidBlock(world, pos);

        for (Direction direction : Direction.Type.HORIZONTAL) {
            if (!state.get(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction)).isConnected()) {
                WireConnection wireConnection = this.method_27841(world, pos, direction, nonSolidCeiling);
                state = state.with(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction), wireConnection);
            }
        }

        return state;
    }

    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
        if (direction == Direction.DOWN ||
                (world.getBlockState(pos).isOf(this) && world.getBlockState(pos).get(BURNED))) {
            return state;
        } else if (direction == Direction.UP) {
            return this.getSideState(world, state, pos);
        } else {
            WireConnection wireConnection = this.getRenderConnectionType(world, pos, direction);
            return wireConnection.isConnected() == state.get(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction)).isConnected() && !isFullyConnected(state) ? state.with(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction), wireConnection) : this.getSideState(world, this.getDefaultState().with(BURNED, state.get(BURNED)).with(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction), wireConnection), pos);
        }
    }

    private static boolean isFullyConnected(BlockState state) {
        return state.get(WIRE_CONNECTION_NORTH).isConnected() && state.get(WIRE_CONNECTION_SOUTH).isConnected() && state.get(WIRE_CONNECTION_EAST).isConnected() && state.get(WIRE_CONNECTION_WEST).isConnected();
    }

    private static boolean isNotConnected(BlockState state) {
        return !state.get(WIRE_CONNECTION_NORTH).isConnected() && !state.get(WIRE_CONNECTION_SOUTH).isConnected() && !state.get(WIRE_CONNECTION_EAST).isConnected() && !state.get(WIRE_CONNECTION_WEST).isConnected();
    }

    public void prepare(BlockState state, WorldAccess world, BlockPos pos, int flags, int maxUpdateDepth) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();

        for (Direction direction : Direction.Type.HORIZONTAL) {
            WireConnection wireConnection = state.get(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction));
            if (wireConnection != WireConnection.NONE && !world.getBlockState(mutable.set(pos, direction)).isOf(this)) {
                mutable.move(Direction.DOWN);
                BlockState blockState = world.getBlockState(mutable);
                BlockPos blockPos = mutable.offset(direction.getOpposite());
                BlockState blockState2 = blockState.getStateForNeighborUpdate(direction.getOpposite(), world.getBlockState(blockPos), world, mutable, blockPos);
                replace(blockState, blockState2, world, mutable, flags, maxUpdateDepth);

                mutable.set(pos, direction).move(Direction.UP);
                BlockState blockState3 = world.getBlockState(mutable);
                BlockPos blockPos2 = mutable.offset(direction.getOpposite());
                BlockState blockState4 = blockState3.getStateForNeighborUpdate(direction.getOpposite(), world.getBlockState(blockPos2), world, mutable, blockPos2);
                replace(blockState3, blockState4, world, mutable, flags, maxUpdateDepth);
            }
        }

    }

    private WireConnection getRenderConnectionType(BlockView blockView, BlockPos blockPos, Direction direction) {
        return this.method_27841(blockView, blockPos, direction, !blockView.getBlockState(blockPos.up()).isSolidBlock(blockView, blockPos));
    }

    private WireConnection method_27841(BlockView blockView, BlockPos blockPos, Direction direction, boolean nonSolidCeiling) {
        BlockPos neighbourPos = blockPos.offset(direction);
        BlockState neighbourState = blockView.getBlockState(neighbourPos);
        if (nonSolidCeiling) {
            boolean neighbourCanHold = this.canRunOnTop(blockView, neighbourPos, neighbourState);
            if (neighbourCanHold && connectsTo(blockView.getBlockState(neighbourPos.up()))) {
                if (neighbourState.isSideSolidFullSquare(blockView, neighbourPos, direction.getOpposite())) {
                    return WireConnection.UP;
                }

                return WireConnection.SIDE;
            }
        }

        return !connectsTo(neighbourState) && (neighbourState.isSolidBlock(blockView, neighbourPos) || !connectsTo(blockView.getBlockState(neighbourPos.down()))) ? WireConnection.NONE : WireConnection.SIDE;
    }

    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockPos floorPos = pos.down();
        BlockState floorState = world.getBlockState(floorPos);
        return this.canRunOnTop(world, floorPos, floorState);
    }

    private boolean canRunOnTop(BlockView world, BlockPos pos, BlockState floor) {
        return floor.isSideSolidFullSquare(world, pos, Direction.UP) || floor.isOf(Blocks.HOPPER);
    }

    private void updateNeighbors(World world, BlockPos pos) {
        if (world.getBlockState(pos).isOf(this)) {
            world.updateNeighborsAlways(pos, this);

            for (Direction direction : Direction.values()) {
                world.updateNeighborsAlways(pos.offset(direction), this);
            }

        }
    }

    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!oldState.isOf(state.getBlock()) && !world.isClient) {

            for (Direction direction : Direction.Type.VERTICAL) {
                world.updateNeighborsAlways(pos.offset(direction), this);
            }

            this.updateNecessaryNeighbors(world, pos);
        }
    }

    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!moved && !state.isOf(newState.getBlock())) {
            super.onStateReplaced(state, world, pos, newState, moved);
            if (!world.isClient) {

                for (Direction direction : Direction.values()) {
                    world.updateNeighborsAlways(pos.offset(direction), this);
                }

                this.updateNecessaryNeighbors(world, pos);
            }
        }
    }

    private void updateNecessaryNeighbors(World world, BlockPos pos) {
        Iterator<Direction> directionIterator = Direction.Type.HORIZONTAL.iterator();

        Direction direction;
        while(directionIterator.hasNext()) {
            direction = directionIterator.next();
            this.updateNeighbors(world, pos.offset(direction));
        }

        directionIterator = Direction.Type.HORIZONTAL.iterator();

        while(directionIterator.hasNext()) {
            direction = directionIterator.next();
            BlockPos neighbourPos = pos.offset(direction);
            if (world.getBlockState(neighbourPos).isSolidBlock(world, neighbourPos)) {
                this.updateNeighbors(world, neighbourPos.up());
            } else {
                this.updateNeighbors(world, neighbourPos.down());
            }
        }

    }

    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        if (!world.isClient && !state.canPlaceAt(world, pos)) {
            dropStacks(state, world, pos);
            world.removeBlock(pos, false);
        }
    }

    private static boolean connectsTo(BlockState state) {
        return (state.isOf(ModBlocks.GUNPOWDER_WIRE) && !state.get(BURNED)) || state.isOf(Blocks.TNT);
    }

    @Environment(EnvType.CLIENT)
    public static int getWireColor(boolean burned) {
        Vector3f vector3f = COLORS[burned ? 1 : 0];
        return MathHelper.packRgb(vector3f.getX(), vector3f.getY(), vector3f.getZ());
    }

    @Environment(EnvType.CLIENT)
    private void addParticles(World world, Random random, BlockPos pos, Vector3f color, Direction direction, Direction direction2, float f, float g) {
        float h = g - f;
        if (random.nextFloat() < 0.2F * h) {
            float j = f + h * random.nextFloat();
            double xOffset = 0.5D + (double)(0.4375F * (float)direction.getOffsetX()) + (double)(j * (float)direction2.getOffsetX());
            double yOffset = 0.5D + (double)(0.4375F * (float)direction.getOffsetY()) + (double)(j * (float)direction2.getOffsetY());
            double zOffset = 0.5D + (double)(0.4375F * (float)direction.getOffsetZ()) + (double)(j * (float)direction2.getOffsetZ());
            world.addParticle(new DustParticleEffect(color.getX(), color.getY(), color.getZ(), 1.0F), (double)pos.getX() + xOffset, (double)pos.getY() + yOffset, (double)pos.getZ() + zOffset, 0.0D, 0.0D, 0.0D);
        }
    }

    @Environment(EnvType.CLIENT)
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        boolean burned = state.get(BURNED);
        if (!burned) {

            for (Direction direction : Direction.Type.HORIZONTAL) {
                WireConnection wireConnection = state.get(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction));
                switch (wireConnection) {
                    case UP:
                        this.addParticles(world, random, pos, COLORS[0], direction, Direction.UP, -0.5F, 0.5F);
                    case SIDE:
                        this.addParticles(world, random, pos, COLORS[0], Direction.DOWN, direction, 0.0F, 0.5F);
                        break;
                    case NONE:
                    default:
                        this.addParticles(world, random, pos, COLORS[0], Direction.DOWN, direction, 0.0F, 0.3F);
                }
            }

        }
    }

    public BlockState rotate(BlockState state, BlockRotation rotation) {
        switch(rotation) {
            case CLOCKWISE_180:
                return state.with(WIRE_CONNECTION_NORTH, state.get(WIRE_CONNECTION_SOUTH)).with(WIRE_CONNECTION_EAST, state.get(WIRE_CONNECTION_WEST)).with(WIRE_CONNECTION_SOUTH, state.get(WIRE_CONNECTION_NORTH)).with(WIRE_CONNECTION_WEST, state.get(WIRE_CONNECTION_EAST));
            case COUNTERCLOCKWISE_90:
                return state.with(WIRE_CONNECTION_NORTH, state.get(WIRE_CONNECTION_EAST)).with(WIRE_CONNECTION_EAST, state.get(WIRE_CONNECTION_SOUTH)).with(WIRE_CONNECTION_SOUTH, state.get(WIRE_CONNECTION_WEST)).with(WIRE_CONNECTION_WEST, state.get(WIRE_CONNECTION_NORTH));
            case CLOCKWISE_90:
                return state.with(WIRE_CONNECTION_NORTH, state.get(WIRE_CONNECTION_WEST)).with(WIRE_CONNECTION_EAST, state.get(WIRE_CONNECTION_NORTH)).with(WIRE_CONNECTION_SOUTH, state.get(WIRE_CONNECTION_EAST)).with(WIRE_CONNECTION_WEST, state.get(WIRE_CONNECTION_SOUTH));
            default:
                return state;
        }
    }

    public BlockState mirror(BlockState state, BlockMirror mirror) {
        switch(mirror) {
            case LEFT_RIGHT:
                return state.with(WIRE_CONNECTION_NORTH, state.get(WIRE_CONNECTION_SOUTH)).with(WIRE_CONNECTION_SOUTH, state.get(WIRE_CONNECTION_NORTH));
            case FRONT_BACK:
                return state.with(WIRE_CONNECTION_EAST, state.get(WIRE_CONNECTION_WEST)).with(WIRE_CONNECTION_WEST, state.get(WIRE_CONNECTION_EAST));
            default:
                return super.mirror(state, mirror);
        }
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(WIRE_CONNECTION_NORTH, WIRE_CONNECTION_EAST, WIRE_CONNECTION_SOUTH, WIRE_CONNECTION_WEST, BURNED);
    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!(player.getStackInHand(hand).getItem() == Items.FLINT_AND_STEEL || player.getStackInHand(hand).getItem() == Items.FIRE_CHARGE)) {
            return super.onUse(state, world, pos, player, hand, hit);
        }
        for (BlockPos tntPos: burn(world, pos, new HashSet<>())) {
            world.setBlockState(tntPos, Blocks.AIR.getDefaultState());
            TntBlock.primeTnt(world, tntPos);
        }
        return ActionResult.SUCCESS;
    }

    private static Set<BlockPos> burn(World world, BlockPos pos, Set<BlockPos> tntToIgnite) {
        if (world.getBlockState(pos).isOf(ModBlocks.GUNPOWDER_WIRE) && !world.getBlockState(pos).get(BURNED)) {
            world.setBlockState(pos, world.getBlockState(pos).with(BURNED, true), 3, 0);
            for (Map.Entry<Direction, EnumProperty<WireConnection>> entry : DIRECTION_TO_WIRE_CONNECTION_PROPERTY.entrySet()) {
                BlockPos neighborPos = pos.offset(entry.getKey());
                WireConnection connection = world.getBlockState(pos).get(entry.getValue());
                if (connection == WireConnection.UP)
                    burn(world, neighborPos.up(), tntToIgnite);
                else if (connection == WireConnection.SIDE && connectsTo(world.getBlockState(neighborPos)))
                    burn(world, neighborPos, tntToIgnite);
                else if (connection == WireConnection.SIDE && !world.getBlockState(neighborPos).isSideSolidFullSquare(world, pos, entry.getKey().getOpposite()) && world.getBlockState(neighborPos.down()).isOf(ModBlocks.GUNPOWDER_WIRE))
                    burn(world, neighborPos.down(), tntToIgnite);
            }
            burn(world, pos.down(), tntToIgnite);
        } else if (world.getBlockState(pos).isOf(Blocks.TNT)) {
            tntToIgnite.add(pos);
        }
        return tntToIgnite;
    }

    static {
        WIRE_CONNECTION_NORTH = Properties.NORTH_WIRE_CONNECTION;
        WIRE_CONNECTION_EAST = Properties.EAST_WIRE_CONNECTION;
        WIRE_CONNECTION_SOUTH = Properties.SOUTH_WIRE_CONNECTION;
        WIRE_CONNECTION_WEST = Properties.WEST_WIRE_CONNECTION;
        BURNED = BooleanProperty.of("burned");
        DIRECTION_TO_WIRE_CONNECTION_PROPERTY = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, WIRE_CONNECTION_NORTH, Direction.EAST, WIRE_CONNECTION_EAST, Direction.SOUTH, WIRE_CONNECTION_SOUTH, Direction.WEST, WIRE_CONNECTION_WEST));
        DOT_SHAPE = Block.createCuboidShape(3.0D, 0.0D, 3.0D, 13.0D, 1.0D, 13.0D);
        SIDE_SHAPE = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, Block.createCuboidShape(3.0D, 0.0D, 0.0D, 13.0D, 1.0D, 13.0D), Direction.SOUTH, Block.createCuboidShape(3.0D, 0.0D, 3.0D, 13.0D, 1.0D, 16.0D), Direction.EAST, Block.createCuboidShape(3.0D, 0.0D, 3.0D, 16.0D, 1.0D, 13.0D), Direction.WEST, Block.createCuboidShape(0.0D, 0.0D, 3.0D, 13.0D, 1.0D, 13.0D)));
        UP_SHAPE = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, VoxelShapes.union(SIDE_SHAPE.get(Direction.NORTH), Block.createCuboidShape(3.0D, 0.0D, 0.0D, 13.0D, 16.0D, 1.0D)), Direction.SOUTH, VoxelShapes.union(SIDE_SHAPE.get(Direction.SOUTH), Block.createCuboidShape(3.0D, 0.0D, 15.0D, 13.0D, 16.0D, 16.0D)), Direction.EAST, VoxelShapes.union(SIDE_SHAPE.get(Direction.EAST), Block.createCuboidShape(15.0D, 0.0D, 3.0D, 16.0D, 16.0D, 13.0D)), Direction.WEST, VoxelShapes.union(SIDE_SHAPE.get(Direction.WEST), Block.createCuboidShape(0.0D, 0.0D, 3.0D, 1.0D, 16.0D, 13.0D))));
        COLORS = new Vector3f[2];

        COLORS[0] = new Vector3f(0.596F, 0.596F, 0.596F); // Not burned
        COLORS[1] = new Vector3f(0.149F, 0.149F, 0.149F); // Burned
    }
}
