package com.rdvdev2.timetravelmod.impl.client.screen

import com.rdvdev2.timetravelmod.api.timemachine.ITimeMachine
import com.rdvdev2.timetravelmod.api.timemachine.ITimeMachineUpgrade
import com.rdvdev2.timetravelmod.impl.ModRegistries
import io.github.cottonmc.cotton.gui.client.CottonClientScreen
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription
import io.github.cottonmc.cotton.gui.widget.*
import io.github.cottonmc.cotton.gui.widget.data.Axis
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment
import io.github.cottonmc.cotton.gui.widget.data.VerticalAlignment
import io.github.cottonmc.cotton.gui.widget.icon.ItemIcon
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.item.Item
import net.minecraft.text.TranslatableText
import net.minecraft.util.Formatting
import java.util.*
import java.util.function.IntConsumer
import java.util.stream.Collectors
import kotlin.math.max

class EngineerBookScreen : CottonClientScreen(GuiDescription()) {

    private class GuiDescription : LightweightGuiDescription() {

        companion object {
            private fun generateItemList(vararg blockStates: BlockState) = Arrays.stream(blockStates)
                .map(BlockState::getBlock)
                .distinct()
                .map(Block::asItem)
                .map(Item::getDefaultStack)
                .collect(Collectors.toList())

            private fun configureCompatibleTMPanel(tm: ITimeMachine, panel: WGridPanel) {
                val icon = WItem(tm.icon!!.defaultStack)
                panel.add(icon, 0, 0, 1, 1)

                val name = WLabel(tm.name.formatted(Formatting.ITALIC))
                panel.add(name, 1, 0, 8, 1)
                name.verticalAlignment = VerticalAlignment.CENTER
            }
        }

        init {

            // Root panel
            val root = WGridPanel()
            setRootPanel(root)

            val titleLabel = WLabel(TranslatableText("item.time_travel_mod.engineer_book").formatted(Formatting.BOLD))
            root.add(titleLabel, 0, 0, 18, 1)
            titleLabel.horizontalAlignment = HorizontalAlignment.CENTER

            val introductionText = WText(TranslatableText("gui.tm_engineer_book.introduction"))
            root.add(introductionText, 0, 1, 18, 2)

            val tabCardPanel = WCardPanel()
            root.add(tabCardPanel, 0, 3)

            val switchTabButton = WButton(TranslatableText("gui.tm_engineer_book.upgrades.symbol"))
            root.add(switchTabButton, 18, 3, 1, 1)
            switchTabButton.onClick = Runnable {
                val newIndex: Int
                val newLabel: TranslatableText
                if (tabCardPanel.selectedIndex == 0) {
                    newIndex = 1
                    newLabel = TranslatableText("gui.tm_engineer_book.tms.symbol")
                } else {
                    newIndex = 0
                    newLabel = TranslatableText("gui.tm_engineer_book.upgrades.symbol")
                }
                tabCardPanel.selectedIndex = newIndex
                switchTabButton.label = newLabel
            }

            // TM Tabs
            val timeMachineTabs = WTabPanel()
            tabCardPanel.add(timeMachineTabs)

            // Time machines (general)
            val timeMachinesPanel = WGridPanel()
            timeMachineTabs.add(timeMachinesPanel) { it.title(TranslatableText("gui.tm_engineer_book.tms")) }

            val timeMachinesText = WText(TranslatableText("gui.tm_engineer_book.tms_introduction"))
            timeMachinesPanel.add(timeMachinesText, 0, 0, 18, 7)

            // Time machines (each)
            ModRegistries.TIME_MACHINE.stream().sorted(Comparator.comparingInt(ITimeMachine::tier))
                .forEach { tm ->
                    val includeBuildGuide: Boolean = tm.controllerStates.isNotEmpty()

                    val tmPanel = WGridPanel()
                    timeMachineTabs.add(tmPanel) { it.icon(ItemIcon(tm.icon)).tooltip(tm.name) }

                    // TM Name
                    val tmNameLabel = WLabel(tm.name.formatted(Formatting.BOLD))
                    tmPanel.add(tmNameLabel, 0, 0, 18, 1)
                    tmNameLabel.horizontalAlignment = HorizontalAlignment.CENTER

                    // TM Stats
                    val tmStatsLabel = WText(
                        TranslatableText("gui.tm_engineer_book.tm_stats", tm.tier, tm.cooldownTime / 20)
                            .formatted(Formatting.ITALIC))
                    tmPanel.add(tmStatsLabel, 0, 1, 9, 2)
                    tmStatsLabel.verticalAlignment = VerticalAlignment.CENTER

                    // TM Description
                    val tmDescriptionText = WText(tm.description)
                    tmPanel.add(tmDescriptionText, 0, 3, if (includeBuildGuide) 9 else 18, 4)

                    // TM Build Guide
                    if (includeBuildGuide) {

                        // TM Build Guide Title
                        val buildingGuideLabel = WLabel(TranslatableText("gui.tm_engineer_book.how_to").formatted(Formatting.BOLD))
                        tmPanel.add(buildingGuideLabel, 9, 1, 9, 1)
                        buildingGuideLabel.horizontalAlignment = HorizontalAlignment.CENTER

                        // TM Build Guide Icons
                        val layerPanels = arrayOfNulls<WGridPanel>(tm.structureLayers.size)
                        var maxWidth = 0
                        var maxHeight = 0
                        val controllerStacks = generateItemList(*tm.controllerStates)
                        val coreStacks = generateItemList(*tm.coreStates)
                        val basicStacks = generateItemList(*tm.basicStates)
                        for (y in 0 until tm.structureLayers.size) {
                            val layerPanel = WGridPanel()
                            maxHeight = max(maxHeight, tm.structureLayers[y].size)
                            for (z in 0 until tm.structureLayers[y].size) {
                                maxWidth = max(maxWidth, tm.structureLayers[y][z].length)
                                for (x in 0 until tm.structureLayers[y][z].length) {
                                    val stacks = when (tm.structureLayers[y][z][x]) {
                                        'Z' -> controllerStacks
                                        'C' -> coreStacks
                                        'B' -> basicStacks
                                        else -> continue
                                    }
                                    val element = WItem(stacks)
                                    layerPanel.add(element, x, z, 1, 1)
                                }
                            }
                            layerPanels[tm.structureLayers.size - 1 - y] = layerPanel
                        }

                        val structurePanel = WCardPanel()
                        tmPanel.add(structurePanel, 9 + (9 - maxWidth) / 2, 2 + (5 - maxHeight) / 2)
                        layerPanels.forEach(structurePanel::add)

                        // TM Build Guide Y Slider
                        val layerSlider = WSlider(0, structurePanel.cardCount - 1, Axis.VERTICAL)
                        tmPanel.add(layerSlider, 17, 2, 1, 5)
                        layerSlider.value = 0
                        layerSlider.valueChangeListener = IntConsumer { structurePanel.selectedIndex = it }
                    }
                }

            // TM Upgrades Tabs
            val timeMachineUpgradesTabs = WTabPanel()
            tabCardPanel.add(timeMachineUpgradesTabs)

            val upgradesPanel = WGridPanel()
            timeMachineUpgradesTabs.add(upgradesPanel) { it.title(TranslatableText("gui.tm_engineer_book.upgrades")) }

            val upgradesDescriptionText = WText(TranslatableText("gui.tm_engineer_book.upgrades_introduction"))
            upgradesPanel.add(upgradesDescriptionText, 0, 0, 18, 7)
            ModRegistries.TIME_MACHINE_UPGRADE.forEach { tmu: ITimeMachineUpgrade ->
                val compatibleTms = ModRegistries.TIME_MACHINE.stream()
                    .filter(tmu::isTimeMachineCompatible)
                    .filter { it.controllerStates.isNotEmpty() }
                    .collect(Collectors.toList())
                if (compatibleTms.isEmpty()) return@forEach   // Skip unused upgrades to avoid confusion

                // TM Upgrade Panel
                val tmuPanel = WGridPanel()
                timeMachineUpgradesTabs.add(tmuPanel) { it.icon(ItemIcon(tmu.icon)).tooltip(tmu.name) }

                // TM Upgrade Name
                val tmuNameLabel = WLabel(tmu.name!!.formatted(Formatting.BOLD))
                tmuPanel.add(tmuNameLabel, 0, 0, 18, 1)
                tmuNameLabel.horizontalAlignment = HorizontalAlignment.CENTER

                // TM Upgrade Description
                val tmuDescriptionText = WText(tmu.description)
                tmuPanel.add(tmuDescriptionText, 0, 1, 9, 6)

                // Compatible TMs Title
                val compatibleTMsLabel = WLabel(TranslatableText("gui.tm_engineer_book.compatible_tms").formatted(Formatting.BOLD))
                tmuPanel.add(compatibleTMsLabel, 9, 1, 9, 1)
                compatibleTMsLabel.horizontalAlignment = HorizontalAlignment.CENTER

                // Compatible TMs List
                val compatibleTMsList = WListPanel(compatibleTms, ::WGridPanel, ::configureCompatibleTMPanel)
                tmuPanel.add(compatibleTMsList, 9, 2, 9, 5)
            }

            root.validate(this)
        }
    }
}