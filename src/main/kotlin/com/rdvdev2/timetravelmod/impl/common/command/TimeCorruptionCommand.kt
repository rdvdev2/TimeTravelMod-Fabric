package com.rdvdev2.timetravelmod.impl.common.command

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType
import com.rdvdev2.timetravelmod.api.dimension.ITimeline
import com.rdvdev2.timetravelmod.api.dimension.ITimeline.Companion.getTimelineForWorld
import com.rdvdev2.timetravelmod.impl.common.command.argument.TimelineArgumentType.Companion.getTimelineArgument
import com.rdvdev2.timetravelmod.impl.common.command.argument.TimelineArgumentType.Companion.timeline
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.TranslatableText

object TimeCorruptionCommand {

    private val NO_TIMELINE_FOUND_EXCEPTION = DynamicCommandExceptionType { TranslatableText("command.time_travel_mod.time_corruption.error", it) }

    fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        val node = dispatcher.register(command())
        dispatcher.register(CommandManager.literal("tc").redirect(node))
    }

    private fun command(): LiteralArgumentBuilder<ServerCommandSource> {
        return CommandManager.literal("timecorruption")
            .requires { it.hasPermissionLevel(2) }
            .then(CommandManager.literal("get")
                .then(CommandManager.argument("timeline", timeline())
                    .executes(this::executeGet)
                )
                .executes(this::executeGet)
            )
            .then(CommandManager.literal("set")
                .then(CommandManager.argument("timeline", timeline())
                    .then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                        .executes(this::executeSet)
                    )
                )
            )
            .then(CommandManager.literal("add")
                .then(CommandManager.argument("timeline", timeline())
                    .then(CommandManager.argument("value", IntegerArgumentType.integer())
                        .executes (this::executeAdd)
                    )
                )
            )
    }

    @Throws(CommandSyntaxException::class)
    private fun getTimeline(commandContext: CommandContext<ServerCommandSource>): ITimeline {
        return try {
            getTimelineArgument(commandContext, "timeline")
        } catch (e: IllegalArgumentException) {
            val timeline = getTimelineForWorld(commandContext.source.world.registryKey)
            timeline ?: throw NO_TIMELINE_FOUND_EXCEPTION.create(commandContext.source.world.registryKey.value)
        }
    }

    @Throws(CommandSyntaxException::class)
    private fun executeGet(commandContext: CommandContext<ServerCommandSource>): Int {
        val timeline = getTimeline(commandContext)
        val value = timeline.getCorruption(commandContext.source.minecraftServer).corruptionLevel
        commandContext.source.sendFeedback(TranslatableText("command.time_travel_mod.time_corruption.result", value), false)
        return Command.SINGLE_SUCCESS
    }

    @Throws(CommandSyntaxException::class)
    private fun executeSet(commandContext: CommandContext<ServerCommandSource>): Int {
        val timeline = getTimeline(commandContext)
        var value = IntegerArgumentType.getInteger(commandContext, "value")
        value = timeline.getCorruption(commandContext.source.minecraftServer).setCorruptionLevel(value)
        commandContext.source.sendFeedback(TranslatableText("command.time_travel_mod.time_corruption.result", value), false)
        return Command.SINGLE_SUCCESS
    }

    @Throws(CommandSyntaxException::class)
    private fun executeAdd(commandContext: CommandContext<ServerCommandSource>): Int {
        val timeline = getTimeline(commandContext)
        var value = IntegerArgumentType.getInteger(commandContext, "value")
        value = timeline.getCorruption(commandContext.source.minecraftServer).increaseCorruptionLevel(value)
        commandContext.source.sendFeedback(TranslatableText("command.time_travel_mod.time_corruption.result", value), false)
        return Command.SINGLE_SUCCESS
    }

}