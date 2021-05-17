package com.rdvdev2.timetravelmod.impl.client.screen

import com.rdvdev2.timetravelmod.api.dimension.ITimeline
import com.rdvdev2.timetravelmod.impl.ModNetworking
import com.rdvdev2.timetravelmod.impl.ModRegistries
import io.github.cottonmc.cotton.gui.client.CottonClientScreen
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription
import io.github.cottonmc.cotton.gui.widget.*
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment
import io.github.cottonmc.cotton.gui.widget.data.VerticalAlignment
import io.netty.buffer.Unpooled
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.client.MinecraftClient
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import net.minecraft.text.LiteralText
import net.minecraft.text.TranslatableText
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import java.util.stream.Collectors

class TimeMachineScreen private constructor(worldIdentifier: Identifier?, rootPos: BlockPos?, maxTier: Int, creative: Boolean) : CottonClientScreen(GuiDescription(worldIdentifier, rootPos, maxTier, creative)) {

    constructor(worldIdentifier: Identifier?, rootPos: BlockPos?, maxTier: Int) :
            this(worldIdentifier, rootPos, maxTier, false)

    constructor() : this(null, null, Int.MAX_VALUE, true)

    private class GuiDescription(val worldIdentifier: Identifier?, val rootPos: BlockPos?, val maxTier: Int, val creative: Boolean) : LightweightGuiDescription() {

        // Timeline widget
        private inner class WTimelinePanel : WGridPanel() {

            var icon: WItem = WItem(ItemStack.EMPTY)
            var name: WLabel
            var tier: WLabel
            var button: WButton
            var tl: ITimeline? = null

            fun onClick() {
                val passedData = PacketByteBuf(Unpooled.buffer())
                if (creative) {
                    passedData.writeIdentifier(ModRegistries.TIMELINE.getId(tl))
                    ClientPlayNetworking.send(ModNetworking.RUN_CREATIVE_TIME_MACHINE, passedData)
                } else {
                    passedData.writeIdentifier(worldIdentifier)
                    passedData.writeBlockPos(rootPos)
                    passedData.writeIdentifier(ModRegistries.TIMELINE.getId(tl))
                    ClientPlayNetworking.send(ModNetworking.RUN_TIME_MACHINE, passedData)
                }
                MinecraftClient.getInstance().openScreen(null)
            }

            init {
                // Timeline icon
                this.add(icon, 0, 0, 1, 1)

                // Timeline name
                name = WLabel(LiteralText.EMPTY)
                this.add(name, 1, 0, 10, 1)
                name.verticalAlignment = VerticalAlignment.CENTER

                // Timeline tier
                tier = WLabel(LiteralText.EMPTY)
                this.add(tier, 11, 0, 3, 1)
                tier.verticalAlignment = VerticalAlignment.CENTER

                // Timeline button
                button = WButton()
                this.add(button, 14, 0, 2, 1)
            }
        }

        private fun configure(tl: ITimeline, panel: WTimelinePanel) {
            val reachable = tl.minTier <= maxTier
            val textFormat = if (reachable) Formatting.RESET else Formatting.RED

            // Timeline icon
            panel.icon.items = listOf(tl.icon.defaultStack)

            // Timeline name
            panel.name.text = tl.name.formatted(textFormat)

            // Timeline tier
            panel.tier.text = TranslatableText("gui.time_travel_mod.time_machine.tier", tl.minTier)
                .formatted(textFormat)
                .formatted(Formatting.ITALIC)

            // Timeline button
            panel.button.label = TranslatableText("gui.time_travel_mod.time_machine.go")
            panel.button.isEnabled = reachable
            panel.button.onClick = Runnable { panel.onClick() }
            panel.tl = tl
        }

        init {

            // Root panel
            val root = WGridPanel()
            setRootPanel(root)

            // Title label
            val titleLabel = WLabel(TranslatableText("gui.time_travel_mod.time_machine.title")
                .formatted(Formatting.BOLD))
            root.add(titleLabel, 0, 0, 18, 1)
            titleLabel.horizontalAlignment = HorizontalAlignment.CENTER

            // Description text
            val descriptionText = WText(TranslatableText("gui.time_travel_mod.time_machine.description"))
            root.add(descriptionText, 0, 1, 18, 1)

            // Timeline list
            val tls = ModRegistries.TIMELINE.stream()
                .sorted(Comparator.comparingInt(ITimeline::minTier))
                .collect(Collectors.toList())
            val timelineListPanel = WListPanel(tls, this::WTimelinePanel, this::configure)
            root.add(timelineListPanel, 0, 2, 18, 7)

            root.validate(this)
        }
    }
}