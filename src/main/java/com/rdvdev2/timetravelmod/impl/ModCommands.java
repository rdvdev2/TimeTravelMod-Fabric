package com.rdvdev2.timetravelmod.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.rdvdev2.timetravelmod.impl.common.command.TimeCorruptionCommand;
import com.rdvdev2.timetravelmod.impl.common.command.TimeTravelCommand;
import net.minecraft.server.command.ServerCommandSource;

public class ModCommands {

    public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher, boolean b) {
        TimeTravelCommand.register(commandDispatcher);
        TimeCorruptionCommand.register(commandDispatcher);
    }
}
