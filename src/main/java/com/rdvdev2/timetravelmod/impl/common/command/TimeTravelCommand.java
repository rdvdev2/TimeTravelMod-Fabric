package com.rdvdev2.timetravelmod.impl.common.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.rdvdev2.timetravelmod.impl.ModNetworking;
import com.rdvdev2.timetravelmod.impl.ModTriggers;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class TimeTravelCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralCommandNode<ServerCommandSource> node = dispatcher.register(command());
        dispatcher.register(CommandManager.literal("tt").redirect(node));
    }

    private static LiteralArgumentBuilder<ServerCommandSource> command() {
        return CommandManager.literal("timetravel")
                .requires(c -> c.hasPermissionLevel(2))
                .executes(TimeTravelCommand::execute);
    }

    private static int execute(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException {
        ModTriggers.ACCESS_TIME_MACHINE.trigger(commandContext.getSource().getPlayer());
        ServerPlayNetworking.send(commandContext.getSource().getPlayer(),
                ModNetworking.OPEN_CREATIVE_TIME_MACHINE_GUI, new PacketByteBuf(Unpooled.buffer()));
        return Command.SINGLE_SUCCESS;
    }
}
