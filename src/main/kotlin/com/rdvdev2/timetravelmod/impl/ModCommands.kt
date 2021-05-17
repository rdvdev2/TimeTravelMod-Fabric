package com.rdvdev2.timetravelmod.impl

import com.mojang.brigadier.CommandDispatcher
import com.rdvdev2.timetravelmod.impl.common.command.TimeCorruptionCommand
import com.rdvdev2.timetravelmod.impl.common.command.TimeTravelCommand
import net.minecraft.server.command.ServerCommandSource

object ModCommands {

    fun register(commandDispatcher: CommandDispatcher<ServerCommandSource>, b: Boolean) {
        TimeTravelCommand.register(commandDispatcher)
        TimeCorruptionCommand.register(commandDispatcher)
    }
}