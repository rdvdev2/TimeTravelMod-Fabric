package com.rdvdev2.timetravelmod.impl.common.item;

import com.rdvdev2.timetravelmod.impl.ModNetworking;
import com.rdvdev2.timetravelmod.impl.ModTriggers;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class CreativeTimeMachineItem extends Item {

    public CreativeTimeMachineItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        if (!world.isClient) {
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
            ModTriggers.ACCESS_TIME_MACHINE.trigger(serverPlayer);
            ServerPlayNetworking.send(serverPlayer, ModNetworking.OPEN_CREATIVE_TIME_MACHINE_GUI, new PacketByteBuf(Unpooled.buffer()));
        }
        return TypedActionResult.success(player.getStackInHand(hand));
    }
}
