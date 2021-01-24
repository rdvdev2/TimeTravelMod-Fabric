package com.rdvdev2.timetravelmod.impl.common.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.rdvdev2.timetravelmod.api.dimension.ITimeline;
import com.rdvdev2.timetravelmod.impl.common.command.argument.TimelineArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

public class TimeCorruptionCommand {

    private static final DynamicCommandExceptionType NO_TIMELINE_FOUND_EXCEPTION;

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralCommandNode<ServerCommandSource> node = dispatcher.register(command());
        dispatcher.register(CommandManager.literal("tc").redirect(node));
    }

    private static LiteralArgumentBuilder<ServerCommandSource> command() {
        return CommandManager.literal("timecorruption")
                .requires(c -> c.hasPermissionLevel(2))
                .then(CommandManager.literal("get")
                        .then(CommandManager.argument("timeline", TimelineArgumentType.timeline())
                                .executes(TimeCorruptionCommand::executeGet))
                        .executes(TimeCorruptionCommand::executeGet))
                .then(CommandManager.literal("set")
                        .then(CommandManager.argument("timeline", TimelineArgumentType.timeline())
                                .then(CommandManager.argument("value", IntegerArgumentType.integer(0))
                                    .executes(TimeCorruptionCommand::executeSet))))
                .then(CommandManager.literal("add")
                        .then(CommandManager.argument("timeline", TimelineArgumentType.timeline())
                                .then(CommandManager.argument("value", IntegerArgumentType.integer())
                                        .executes(TimeCorruptionCommand::executeAdd))));

    }

    private static ITimeline getTimeline(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException {
        try {
            return TimelineArgumentType.getTimelineArgument(commandContext, "timeline");
        } catch (IllegalArgumentException e) {
            ITimeline timeline = ITimeline.getTimelineForWorld(commandContext.getSource().getWorld().getRegistryKey());
            if (timeline != null) return timeline;
            else throw NO_TIMELINE_FOUND_EXCEPTION.create(commandContext.getSource().getWorld().getRegistryKey().getValue());
        }
    }

    private static int executeGet(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException {
        ITimeline timeline = getTimeline(commandContext);
        int value = timeline.getCorruption(commandContext.getSource().getMinecraftServer()).getCorruptionLevel();
        commandContext.getSource().sendFeedback(new TranslatableText("command.time_travel_mod.time_corruption.result", value), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int executeSet(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException {
        ITimeline timeline = getTimeline(commandContext);
        int value = IntegerArgumentType.getInteger(commandContext, "value");
        value = timeline.getCorruption(commandContext.getSource().getMinecraftServer()).setCorruptionLevel(value);
        commandContext.getSource().sendFeedback(new TranslatableText("command.time_travel_mod.time_corruption.result", value), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int executeAdd(CommandContext<ServerCommandSource> commandContext) throws CommandSyntaxException {
        ITimeline timeline = getTimeline(commandContext);
        int value = IntegerArgumentType.getInteger(commandContext, "value");
        value = timeline.getCorruption(commandContext.getSource().getMinecraftServer()).increaseCorruptionLevel(value);
        commandContext.getSource().sendFeedback(new TranslatableText("command.time_travel_mod.time_corruption.result", value), false);
        return Command.SINGLE_SUCCESS;
    }

    static {
        NO_TIMELINE_FOUND_EXCEPTION = new DynamicCommandExceptionType( o ->
                new TranslatableText("command.time_travel_mod.time_corruption.error", o));
    }
}
