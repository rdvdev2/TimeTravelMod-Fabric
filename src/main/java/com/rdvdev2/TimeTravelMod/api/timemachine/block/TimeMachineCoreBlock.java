package com.rdvdev2.TimeTravelMod.api.timemachine.block;

import com.rdvdev2.TimeTravelMod.common.block.blockentity.TMCooldownBlockEntity;
import com.rdvdev2.TimeTravelMod.common.world.TemporalExplosion;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.Random;

/**
 * Blocks that pretend to act as a Time Machine Core must extend from this class.
 * Subclasses of this will have a cooldown and a random explosion chance integrated without needing to overwrite nothing on the class.
 */
public class TimeMachineCoreBlock extends Block implements BlockEntityProvider {

    /**
     * This property represents whether the core is ready (isn't cooling down)
     */
    public static final BooleanProperty TM_READY = BooleanProperty.of("ready");
    
    /**
     * @see Block#Block(Settings)
     */
    public TimeMachineCoreBlock(Settings properties) {
        super(properties);
        setDefaultState(getStateManager().getDefaultState().with(TM_READY, true));
    }

    /**
     * Gets the chance of the Time Machine Core to explode
     * @return The chance (x/1)
     */
    public float getRandomExplosionChance() {
        return 0.001F;
    }
    
    /**
     * @see Block#appendProperties(StateManager.Builder)
     */
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(TM_READY);
    }
    
    /**
     * @see BlockEntityProvider#createBlockEntity(BlockView)
     */
    @Override
    public BlockEntity createBlockEntity(BlockView view) {
        return new TMCooldownBlockEntity();
    }
    
    /**
     * Triggers a Time Machine Core random explosion
     * @param world The {@link World} where the Time Machine Core is
     * @param pos The {@link BlockPos} of the Time Machine Core
     * @param aportation The extra chance of the Time Machine Core to explode (It is summed to the base one)
     * @return True if the Time Machine Core exploded
     */
    public final boolean randomExplosion(World world, BlockPos pos, float aportation) {
        Random r = new Random();
        if (r.nextFloat() < getRandomExplosionChance()+aportation) {
            world.setBlockState(pos, Blocks.AIR.getDefaultState());
            new TemporalExplosion(world, null, pos, 4.0F).explode();
            return true;
        }
        return false;
    }

    /**
     * Triggers a Time Machine Core random explosion with it's default explosion chance
     * @param world The {@link World} where the Time Machine Core is
     * @param pos The {@link BlockPos} of the Time Machine Core
     * @return True if the Time Machine Core exploded
     */
    public final boolean randomExplosion(World world, BlockPos pos) {
        return randomExplosion(world, pos, 0);
    }

    /**
     * Trigger a Time Machine Core explosion
     * @param world The {@link World} where the Time Machine Core is
     * @param pos The {@link BlockPos} of the Time Machine Core
     * @return True if the Time Machine Core exploded (This should be always true)
     */
    public final boolean forceExplosion(World world, BlockPos pos) {
        return randomExplosion(world, pos, 1);
    }
    
    /**
     * @see Block#onBreak(World, BlockPos, BlockState, PlayerEntity)
     */
    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBreak(world, pos, state, player);
        if (!state.get(TM_READY)) {
            forceExplosion(world.getWorld(), pos);
        }
    }
}
