package com.rdvdev2.timetravelmod.api.timemachine.block;

import com.rdvdev2.timetravelmod.impl.ModNetworking;
import com.rdvdev2.timetravelmod.impl.ModTriggers;
import com.rdvdev2.timetravelmod.impl.common.timemachine.TimeMachineExecutor;
import com.rdvdev2.timetravelmod.impl.common.timemachine.TimeMachineManager;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Optional;

public class TimeMachineControllerBlock extends Block {

    public TimeMachineControllerBlock(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        Optional<TimeMachineExecutor> _tme = TimeMachineManager.getInstance().generateExecutor(world, pos);

        if (_tme.isPresent()) {
            Optional<TimeMachineExecutor.TimeMachineError> error = _tme.get().runChecks(player);
            if (error.isPresent()) {
                if (world.isClient) {
                    player.sendMessage(error.get().getClientError(), true);
                }
                return ActionResult.FAIL;
            } else {
                if (!world.isClient) {
                    ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
                    ModTriggers.ACCESS_TIME_MACHINE.trigger(serverPlayer);
                    PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer());
                    passedData.writeIdentifier(world.getRegistryKey().getValue());
                    passedData.writeBlockPos(pos);
                    passedData.writeInt(_tme.get().getMaxTier());
                    ServerPlayNetworking.send(serverPlayer, ModNetworking.OPEN_TIME_MACHINE_GUI, passedData);
                }
                return ActionResult.SUCCESS;
            }
        } else {
            player.sendMessage(TimeMachineExecutor.TimeMachineError.NOT_BUILT.getClientError(), true);
            return ActionResult.FAIL;
        }
    }
}
