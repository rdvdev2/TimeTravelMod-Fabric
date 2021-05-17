package com.rdvdev2.timetravelmod.impl.common.command.argument

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import com.rdvdev2.timetravelmod.api.dimension.ITimeline
import com.rdvdev2.timetravelmod.impl.ModRegistries
import com.rdvdev2.timetravelmod.impl.ModTimelines
import net.minecraft.command.CommandSource
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.LiteralText
import net.minecraft.util.Identifier
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.stream.Collectors
import java.util.stream.Stream

class TimelineArgumentType : ArgumentType<Identifier> {

    @Throws(CommandSyntaxException::class)
    override fun parse(stringReader: StringReader): Identifier = Identifier.fromCommandInput(stringReader)

    override fun <S> listSuggestions(context: CommandContext<S>, builder: SuggestionsBuilder): CompletableFuture<Suggestions> = CommandSource.suggestIdentifiers(ModRegistries.TIMELINE.ids, builder)

    override fun getExamples(): Collection<String> = EXAMPLES

    companion object {

        private val EXAMPLES = Stream.of(ModTimelines.PRESENT, ModTimelines.OLD_WEST)
            .map(ModRegistries.TIMELINE::getId)
            .filter(Objects::nonNull)
            .map(Identifier?::toString)
            .collect(Collectors.toList())

        private val INVALID_TIMELINE_EXCEPTION = DynamicCommandExceptionType { LiteralText("Invalid Timeline: $it") }

        fun timeline(): TimelineArgumentType {
            return TimelineArgumentType()
        }

        @Throws(CommandSyntaxException::class)
        fun getTimelineArgument(context: CommandContext<ServerCommandSource>, name: String): ITimeline {
            val identifier = context.getArgument(name, Identifier::class.java)
            val timeline = ModRegistries.TIMELINE[identifier]
            return timeline ?: throw INVALID_TIMELINE_EXCEPTION.create(identifier)
        }
    }
}