package com.rdvdev2.timetravelmod.impl.common.command

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.rdvdev2.timetravelmod.impl.ModNetworking
import com.rdvdev2.timetravelmod.impl.ModTriggers
import io.netty.buffer.Unpooled
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource

object TimeTravelCommand {

    fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        val node = dispatcher.register(command())
        dispatcher.register(CommandManager.literal("tt").redirect(node))
    }

    private fun command(): LiteralArgumentBuilder<ServerCommandSource> {
        return CommandManager.literal("timetravel")
            .requires { it.hasPermissionLevel(2) }
            .executes(this::execute)
    }

    @Throws(CommandSyntaxException::class)
    private fun execute(commandContext: CommandContext<ServerCommandSource>): Int {
        ModTriggers.ACCESS_TIME_MACHINE.trigger(commandContext.source.player)
        ServerPlayNetworking.send(commandContext.source.player, ModNetworking.OPEN_CREATIVE_TIME_MACHINE_GUI, PacketByteBuf(Unpooled.buffer()))
        return Command.SINGLE_SUCCESS
    }
}