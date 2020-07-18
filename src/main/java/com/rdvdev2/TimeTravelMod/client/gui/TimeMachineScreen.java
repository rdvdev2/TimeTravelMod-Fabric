package com.rdvdev2.TimeTravelMod.client.gui;

import com.rdvdev2.TimeTravelMod.ModRegistries;
import com.rdvdev2.TimeTravelMod.api.dimension.TimeLine;
import com.rdvdev2.TimeTravelMod.api.timemachine.TimeMachine;
import com.rdvdev2.TimeTravelMod.api.timemachine.exception.IncompatibleTimeMachineHooksException;
import com.rdvdev2.TimeTravelMod.common.networking.DimensionTpPKT;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Environment(EnvType.CLIENT)
public class TimeMachineScreen extends Screen {

    private final UUID[] additionalEntities;
    private PlayerEntity player;
    private TimeMachine tm;
    private BlockPos pos;
    private Direction side;

    public TimeMachineScreen(PlayerEntity player, TimeMachine tm, BlockPos pos, Direction side, UUID... additionalEntities) {
        super(new LiteralText("TITLE PLACEHOLDER"));
        this.player = player;
        try {
            this.tm = tm.hook(player.world, pos, side);
        } catch (IncompatibleTimeMachineHooksException e) {
            throw new RuntimeException("Time Machine GUI opened with invalid upgrade configuration");
        }
        this.pos = pos;
        this.side = side;
        this.additionalEntities = additionalEntities;
    }

    @Override
    public void init() {
        List<com.rdvdev2.TimeTravelMod.api.dimension.TimeLine> tls = ModRegistries.TIME_LINES.stream().collect(Collectors.toCollection(ArrayList::new));
        tls.sort(Comparator.comparingInt(com.rdvdev2.TimeTravelMod.api.dimension.TimeLine::getMinTier));
        int buttoncount = tls.size();
        for(int id = 0; id < tls.size(); id++) {
            addButton(new TimeLineButton(this.width / 2 -100, (this.height / (buttoncount+1))*(id+1), tls.get(id), this));
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }


    protected class TimeLineButton extends ButtonWidget {

        TimeLine tl;
        TimeMachineScreen screen;

        TimeLineButton(int x, int y, TimeLine tl, TimeMachineScreen screen) {
            super(x, y, 200, 20, new TranslatableText(String.format("gui.tm.%s.%s", ModRegistries.TIME_LINES.getId(tl).getNamespace(), ModRegistries.TIME_LINES.getId(tl).getPath())), TimeMachineScreen::clickHandler);
            this.screen = screen;
            this.tl = tl;
            this.active = tl.getMinTier() <= tm.getTier();
        }
    }

    private static void clickHandler(ButtonWidget button) {
        TimeLineButton b = (TimeLineButton) button;
        MinecraftClient.getInstance().openScreen(null);
        if (b.tl.getWorldKey() != b.screen.player.world.getRegistryKey() && com.rdvdev2.TimeTravelMod.common.world.dimension.TimeLine.isValidTimeLine(b.screen.player.world)) {
            DimensionTpPKT pkt = new DimensionTpPKT(b.tl, b.screen.tm, b.screen.pos, b.screen.side, b.screen.additionalEntities);
            ClientSidePacketRegistry.INSTANCE.sendToServer(DimensionTpPKT.ID, pkt.encode());
        } else {
            b.screen.player.sendMessage(new TranslatableText("gui.tm.error"), false);
        }
    }
}
