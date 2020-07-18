package com.rdvdev2.TimeTravelMod.api.timemachine.block;

import com.google.common.collect.Lists;
import com.rdvdev2.TimeTravelMod.ModRegistries;
import com.rdvdev2.TimeTravelMod.api.timemachine.TimeMachine;
import com.rdvdev2.TimeTravelMod.api.timemachine.exception.IncompatibleTimeMachineHooksException;
import com.rdvdev2.TimeTravelMod.common.timemachine.TimeMachineManager;
import com.rdvdev2.TimeTravelMod.common.util.TimeMachineUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

/**
 * This subclass of block is meant to be used on blocks that will act as a Time Machine Control Panel.
 * This block will provide a GUI to control the Time Machine on right click without needing to overwrite nothing on the class.
 */
public class TimeMachineControlPanelBlock extends Block {
    
    /**
     * @see Block#Block(Settings)
     */
    public TimeMachineControlPanelBlock(Settings properties) {
        super(properties);
    }

    /**
     * Returns the {@link TimeMachine} that belongs to this block
     * @return The compatible {@link TimeMachine}
     */
    public final TimeMachine getTimeMachine(BlockState state) {
        return ModRegistries.TIME_MACHINES.get(TimeMachineManager.getStateToTm().get(state));
    }
    
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        Direction side = hit.getSide();
        if (!world.isClient && !(side == Direction.UP || side == Direction.DOWN)) {
            TimeMachine hookRunner;
            try {
                hookRunner = getTimeMachine(state).hook(world, pos, side);
                hookRunner.run(world, player, pos, side);
                return ActionResult.SUCCESS;
            } catch (IncompatibleTimeMachineHooksException e) {
                TranslatableText message = new TranslatableText("timetravelmod.error.uncompatible_upgrades", TimeMachineUtils.concatUncompatibilities(Lists.newArrayList(e.getIncompatibilities())));
                player.sendMessage(message, false);
                return ActionResult.FAIL;
            }
        } else return ActionResult.FAIL;
    }
}
