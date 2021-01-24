package com.rdvdev2.timetravelmod.impl.client.screen;

import com.rdvdev2.timetravelmod.api.dimension.ITimeline;
import com.rdvdev2.timetravelmod.impl.ModNetworking;
import com.rdvdev2.timetravelmod.impl.ModRegistries;
import io.github.cottonmc.cotton.gui.client.CottonClientScreen;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.*;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
import io.github.cottonmc.cotton.gui.widget.data.VerticalAlignment;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class TimeMachineScreen extends CottonClientScreen {

    private TimeMachineScreen(Identifier worldIdentifier, BlockPos rootPos, int maxTier, boolean creative) {
        super(new GuiDescription(worldIdentifier, rootPos, maxTier, creative));
    }

    public TimeMachineScreen(Identifier worldIdentifier, BlockPos rootPos, int maxTier) {
        this(worldIdentifier, rootPos, maxTier, false);
    }

    public TimeMachineScreen() {
        this(null, null, Integer.MAX_VALUE, true);
    }

    private static class GuiDescription extends LightweightGuiDescription {

        final Identifier worldIdentifier;
        final BlockPos rootPos;
        final int maxTier;
        final boolean creative;

        public GuiDescription(Identifier worldIdentifier, BlockPos rootPos, int maxTier, boolean creative) {
            this.worldIdentifier = worldIdentifier;
            this.rootPos = rootPos;
            this.maxTier = maxTier;
            this.creative = creative;

            // Root panel
            WGridPanel root = new WGridPanel();
            this.setRootPanel(root);

            // Title label
            WLabel titleLabel = new WLabel(new TranslatableText("gui.time_travel_mod.time_machine.title").formatted(Formatting.BOLD));
            root.add(titleLabel, 0, 0, 18, 1);
            titleLabel.setHorizontalAlignment(HorizontalAlignment.CENTER);

            // Description text
            WText descriptionText = new WText(new TranslatableText("gui.time_travel_mod.time_machine.description"));
            root.add(descriptionText, 0, 1, 18, 1);

            // Timeline list
            List<ITimeline> tls = ModRegistries.TIMELINE.stream()
                    .sorted(Comparator.comparingInt(ITimeline::getMinTier))
                    .collect(Collectors.toList());

            WListPanel<ITimeline, WTimelinePanel> timelineListPanel = new WListPanel<>(tls, WTimelinePanel::new, this::configure);
            root.add(timelineListPanel, 0, 2, 18, 7);

            root.validate(this);
        }

        // Timeline widget
        private class WTimelinePanel extends WGridPanel {

            WItem icon;
            WLabel name;
            WLabel tier;
            WButton button;
            ITimeline tl;

            WTimelinePanel() {
                // Timeline icon
                this.icon = new WItem(ItemStack.EMPTY);
                this.add(icon, 0, 0, 1, 1);

                // Timeline name
                this.name = new WLabel(LiteralText.EMPTY);
                this.add(name, 1, 0, 10, 1);
                this.name.setVerticalAlignment(VerticalAlignment.CENTER);

                // Timeline tier
                this.tier = new WLabel(LiteralText.EMPTY);
                this.add(tier, 11, 0, 3, 1);
                this.tier.setVerticalAlignment(VerticalAlignment.CENTER);

                // Timeline button
                this.button = new WButton();
                this.add(button, 14, 0, 2, 1);
            }

            void onClick() {
                PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer());

                if (creative) {
                    passedData.writeIdentifier(ModRegistries.TIMELINE.getId(tl));
                    ClientPlayNetworking.send(ModNetworking.RUN_CREATIVE_TIME_MACHINE, passedData);
                } else {
                    passedData.writeIdentifier(worldIdentifier);
                    passedData.writeBlockPos(rootPos);
                    passedData.writeIdentifier(ModRegistries.TIMELINE.getId(tl));
                    ClientPlayNetworking.send(ModNetworking.RUN_TIME_MACHINE, passedData);
                }
                MinecraftClient.getInstance().openScreen(null);
            }
        }

        private void configure(ITimeline tl, WTimelinePanel panel) {
            boolean reachable = tl.getMinTier() <= maxTier;
            Formatting textFormat = reachable ? Formatting.RESET : Formatting.RED;

            // Timeline icon
            panel.icon.setItems(Collections.singletonList(tl.getIcon().getDefaultStack()));

            // Timeline name
            panel.name.setText(tl.getName().formatted(textFormat));

            // Timeline tier
            panel.tier.setText(new TranslatableText("gui.time_travel_mod.time_machine.tier", tl.getMinTier()).formatted(textFormat).formatted(Formatting.ITALIC));

            // Timeline button
            panel.button.setLabel(new TranslatableText("gui.time_travel_mod.time_machine.go"));
            panel.button.setEnabled(reachable);
            panel.button.setOnClick(panel::onClick);

            panel.tl = tl;
        }
    }

}
