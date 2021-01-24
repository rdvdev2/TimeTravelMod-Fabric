package com.rdvdev2.timetravelmod.impl.common.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.rdvdev2.timetravelmod.api.dimension.ITimeline;
import com.rdvdev2.timetravelmod.impl.ModRegistries;
import com.rdvdev2.timetravelmod.impl.ModTimelines;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TimelineArgumentType implements ArgumentType<Identifier> {
    private static final Collection<String> EXAMPLES;
    private static final DynamicCommandExceptionType INVALID_TIMELINE_EXCEPTION;

    public Identifier parse(StringReader stringReader) throws CommandSyntaxException {
        return Identifier.fromCommandInput(stringReader);
    }

    public <S>CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return CommandSource.suggestIdentifiers(ModRegistries.TIMELINE.getIds(), builder);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public static TimelineArgumentType timeline() {
        return new TimelineArgumentType();
    }

    public static ITimeline getTimelineArgument(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
        Identifier identifier = context.getArgument(name, Identifier.class);
        ITimeline timeline = ModRegistries.TIMELINE.get(identifier);
        if (timeline == null) throw INVALID_TIMELINE_EXCEPTION.create(identifier);
        else return timeline;
    }

    static {
        EXAMPLES = Stream.of(ModTimelines.PRESENT, ModTimelines.OLD_WEST)
                .map(ModRegistries.TIMELINE::getId)
                .filter(Objects::nonNull)
                .map(Identifier::toString)
                .collect(Collectors.toList());
        INVALID_TIMELINE_EXCEPTION = new DynamicCommandExceptionType( o ->
                new LiteralText("Invalid Timeline: " + o));
    }
}
