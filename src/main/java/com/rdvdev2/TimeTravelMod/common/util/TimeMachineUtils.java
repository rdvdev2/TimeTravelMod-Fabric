package com.rdvdev2.TimeTravelMod.common.util;

import com.rdvdev2.TimeTravelMod.ModConfig;
import com.rdvdev2.TimeTravelMod.api.timemachine.TimeMachine;
import com.rdvdev2.TimeTravelMod.api.timemachine.upgrade.TimeMachineUpgrade;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.MessageType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Arrays;

public class TimeMachineUtils {

    // Time Machine checking stuff

    public static Check check(TimeMachine tm, World world, PlayerEntity player, BlockPos pos, Direction side) {
        for(Check check: Check.values()) {
            switch (check) {
                case BUILT:
                    if (!tm.isBuilt(world, pos, side)) return check;
                    break;
                case COOLED_DOWN:
                    if (!tm.isCooledDown(world, pos, side)) return check;
                    break;
                case PALYER_INSIDE:
                    if (!tm.isPlayerInside(world, pos, side, player)) return check;
                    break;
                case OVERLOADED:
                    if (tm.isOverloaded(world, pos, side)) return check;
                    break;
            }
        }
        return null;
    }

    public static boolean serverCheck(MinecraftServer server, TimeMachine tm, World world, ServerPlayerEntity player, BlockPos pos, Direction side) {
        Check error = check(tm, world, player, pos, side);
        if (error == null) {
            return true;
        } else if (ModConfig.getInstance().getCommon().getEnableCheaterReports()){
            Arrays.stream(server.getPlayerManager().getOpList().getNames())
                    .map(op -> server.getPlayerManager().getPlayer(op))
                    .forEach(op -> {
                        if (op != null)
                        op.sendChatMessage(error.getCheaterReport(player), MessageType.GAME_INFO);
                    });
            return false;
        } else return false;
    }

    public enum Check {
        BUILT("timetravelmod.error.built.client", "timetravelmod.error.built.server"),
        COOLED_DOWN("timetravelmod.error.cooled_down.client", "timetravelmod.error.cooled_down.server"),
        PALYER_INSIDE("timetravelmod.error.player_inside.client", "timetravelmod.error.player_inside.server"),
        OVERLOADED("timetravelmod.error.overloaded.client", "timetravelmod.error.overloaded.server"),
        UNREACHABLE_DIM("", "timetravelmod.error.unreachable_dim"),
        ENTITIES_ESCAPED("timetravelmod.error.entitiesescaped", "");

        private final String clientError;
        private final String cheatError;

        Check(String clientError, String cheatError) {
            this.clientError = clientError;
            this.cheatError = cheatError;
        }

        public TranslatableText getClientError() {
            return new TranslatableText(this.clientError);
        }

        public TranslatableText getCheaterReport(ServerPlayerEntity cheater) {
            return new TranslatableText("timetravelmod.cheater_report", cheater.getDisplayName(), new TranslatableText(this.cheatError), getBanButton(cheater));
        }

        private static TranslatableText getBanButton(ServerPlayerEntity player) {
            TranslatableText textComponent = new TranslatableText("timetravelmod.ban");
            textComponent.setStyle(new Style().setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/ban "+player.getName().asString())).setColor(Formatting.RED));
            return textComponent;
        }
    }

    // Time Machine uncompatibility listing stuff

    public static TranslatableText concatUncompatibilities(ArrayList<TimeMachineUpgrade> upgrades) {
        if (upgrades.size() != 1) {
            String separator = upgrades.size() > 2 ? "timetravelmod.generic.comma" : "timetravelmod.generic.and";
            return new TranslatableText(separator, upgrades.remove(0).getName(), concatUncompatibilities(upgrades));
        } else return upgrades.remove(0).getName();
    }
}
