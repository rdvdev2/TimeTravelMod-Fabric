package com.rdvdev2.TimeTravelMod.client.networking;

import com.rdvdev2.TimeTravelMod.ModRegistries;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Direction;

import java.util.UUID;

@Environment(EnvType.CLIENT)
public class OpenTmGuiPKT {
    
    public static void decode(PacketContext ctx, PacketByteBuf buf) {
        com.rdvdev2.TimeTravelMod.common.networking.OpenTmGuiPKT pkt = new com.rdvdev2.TimeTravelMod.common.networking.OpenTmGuiPKT();
        pkt.tm = ModRegistries.TIME_MACHINES.get(buf.readIdentifier());
        pkt.pos = buf.readBlockPos();
        pkt.side = buf.readEnumConstant(Direction.class);
        int size = buf.readInt();
        for (int i = 0; i < size; i++) {
            UUID uuid = buf.readUuid();
            pkt.additionalEntities.add(uuid);
        }
        
        handle(pkt, ctx);
    }
    
    public static void handle(com.rdvdev2.TimeTravelMod.common.networking.OpenTmGuiPKT pkt, PacketContext ctx) {
        ctx.getTaskQueue().execute(() -> {
            PlayerEntity player = net.minecraft.client.MinecraftClient.getInstance().player;
            net.minecraft.client.MinecraftClient.getInstance().openScreen(new com.rdvdev2.TimeTravelMod.client.gui.TimeMachineScreen(player, pkt.tm, pkt.pos, pkt.side, pkt.additionalEntities.toArray(new UUID[]{})));
        });
    }
}
