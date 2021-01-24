package com.rdvdev2.timetravelmod.impl.client.screen;

import com.rdvdev2.timetravelmod.api.timemachine.ITimeMachine;
import com.rdvdev2.timetravelmod.impl.ModRegistries;
import io.github.cottonmc.cotton.gui.client.CottonClientScreen;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.*;
import io.github.cottonmc.cotton.gui.widget.data.Axis;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
import io.github.cottonmc.cotton.gui.widget.data.VerticalAlignment;
import io.github.cottonmc.cotton.gui.widget.icon.ItemIcon;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class EngineerBookScreen extends CottonClientScreen {

    public EngineerBookScreen() {
        super(new GuiDescription());
    }

    private static class GuiDescription extends LightweightGuiDescription {

        public GuiDescription() {

            // Root panel
            WGridPanel root = new WGridPanel();
            setRootPanel(root);

            WLabel titleLabel = new WLabel(new TranslatableText("item.time_travel_mod.engineer_book").formatted(Formatting.BOLD));
            root.add(titleLabel, 0, 0, 18, 1);
            titleLabel.setHorizontalAlignment(HorizontalAlignment.CENTER);

            WText introductionText = new WText(new TranslatableText("gui.tm_engineer_book.introduction"));
            root.add(introductionText, 0, 1, 18, 2);

            WCardPanel tabCardPanel = new WCardPanel();
            root.add(tabCardPanel, 0, 3);

            WButton switchTabButton = new WButton(new TranslatableText("gui.tm_engineer_book.upgrades.symbol"));
            root.add(switchTabButton, 18, 3, 1, 1);
            switchTabButton.setOnClick(() -> {
                int newIndex;
                TranslatableText newLabel;
                if (tabCardPanel.getSelectedIndex() == 0) {
                    newIndex = 1;
                    newLabel = new TranslatableText("gui.tm_engineer_book.tms.symbol");
                } else {
                    newIndex = 0;
                    newLabel = new TranslatableText("gui.tm_engineer_book.upgrades.symbol");
                }
                tabCardPanel.setSelectedIndex(newIndex);
                switchTabButton.setLabel(newLabel);
            });

            // TM Tabs
            WTabPanel timeMachineTabs = new WTabPanel();
            tabCardPanel.add(timeMachineTabs);

            // Time machines (general)
            WGridPanel timeMachinesPanel = new WGridPanel();
            timeMachineTabs.add(timeMachinesPanel, tab -> tab.title(new TranslatableText("gui.tm_engineer_book.tms")));

            WText timeMachinesText = new WText(new TranslatableText("gui.tm_engineer_book.tms_introduction"));
            timeMachinesPanel.add(timeMachinesText, 0, 0, 18, 7);

            // Time machines (each)
            ModRegistries.TIME_MACHINE.stream().sorted(Comparator.comparingInt(ITimeMachine::getTier)).forEach(tm -> {
                boolean includeBuildGuide = tm.getControllerStates().length > 0;

                WGridPanel tmPanel = new WGridPanel();
                timeMachineTabs.add(tmPanel, tab -> tab.icon(new ItemIcon(tm.getIcon())).tooltip(tm.getName()));

                // TM Name
                WLabel tmNameLabel = new WLabel(tm.getName().formatted(Formatting.BOLD));
                tmPanel.add(tmNameLabel, 0, 0, 18, 1);
                tmNameLabel.setHorizontalAlignment(HorizontalAlignment.CENTER);

                // TM Stats
                WText tmStatsLabel = new WText(new TranslatableText("gui.tm_engineer_book.tm_stats", tm.getTier(), tm.getCooldownTime() / 20).formatted(Formatting.ITALIC));
                tmPanel.add(tmStatsLabel, 0, 1, 9, 2);
                tmStatsLabel.setVerticalAlignment(VerticalAlignment.CENTER);

                // TM Description
                WText tmDescriptionText = new WText(tm.getDescription());
                tmPanel.add(tmDescriptionText, 0, 3, includeBuildGuide ? 9 : 18, 4);

                // TM Build Guide
                if (includeBuildGuide) {

                    // TM Build Guide Title
                    WLabel buildingGuideLabel = new WLabel(new TranslatableText("gui.tm_engineer_book.how_to").formatted(Formatting.BOLD));
                    tmPanel.add(buildingGuideLabel, 9, 1, 9, 1);
                    buildingGuideLabel.setHorizontalAlignment(HorizontalAlignment.CENTER);

                    // TM Build Guide Icons
                    WGridPanel[] layerPanels = new WGridPanel[tm.getStructureLayers().length];
                    int maxWidth = 0;
                    int maxHeight = 0;

                    List<ItemStack> controllerStacks = generateItemList(tm.getControllerStates());
                    List<ItemStack> coreStacks = generateItemList(tm.getCoreStates());
                    List<ItemStack> basicStacks = generateItemList(tm.getBasicStates());

                    for (int y = 0; y < tm.getStructureLayers().length; y++) {
                        WGridPanel layerPanel = new WGridPanel();
                        maxHeight = Math.max(maxHeight, tm.getStructureLayers()[y].length);
                        for (int z = 0; z < tm.getStructureLayers()[y].length; z++) {
                            maxWidth = Math.max(maxWidth, tm.getStructureLayers()[y][z].length());
                            for (int x = 0; x < tm.getStructureLayers()[y][z].length(); x++) {
                                List<ItemStack> stacks;
                                switch (tm.getStructureLayers()[y][z].charAt(x)) {
                                    case 'Z':
                                        stacks = controllerStacks;
                                        break;
                                    case 'C':
                                        stacks = coreStacks;
                                        break;
                                    case 'B':
                                        stacks = basicStacks;
                                        break;
                                    default:
                                        continue;
                                }
                                WItem element = new WItem(stacks);
                                layerPanel.add(element, x, z, 1, 1);
                            }
                        }
                        layerPanels[(tm.getStructureLayers().length - 1) - y] = layerPanel;
                    }

                    WCardPanel structurePanel = new WCardPanel();
                    tmPanel.add(structurePanel, 9 + ((9 - maxWidth) / 2), 2 + ((5 - maxHeight) / 2));

                    for (WGridPanel layerPanel : layerPanels) {
                        structurePanel.add(layerPanel);
                    }

                    // TM Build Guide Y Slider
                    WSlider layerSlider = new WSlider(0, structurePanel.getCardCount() - 1, Axis.VERTICAL);
                    tmPanel.add(layerSlider, 17, 2, 1, 5);
                    layerSlider.setValue(0);
                    layerSlider.setValueChangeListener(structurePanel::setSelectedIndex);
                }
            });

            // TM Upgrades Tabs
            WTabPanel timeMachineUpgradesTabs = new WTabPanel();
            tabCardPanel.add(timeMachineUpgradesTabs);

            WGridPanel upgradesPanel = new WGridPanel();
            timeMachineUpgradesTabs.add(upgradesPanel, tab -> tab.title(new TranslatableText("gui.tm_engineer_book.upgrades")));

            WText upgradesDescriptionText = new WText(new TranslatableText("gui.tm_engineer_book.upgrades_introduction"));
            upgradesPanel.add(upgradesDescriptionText, 0, 0, 18, 7);

            ModRegistries.TIME_MACHINE_UPGRADE.forEach(tmu -> {

                List<ITimeMachine> compatibleTms = ModRegistries.TIME_MACHINE.stream()
                        .filter(tmu::isTimeMachineCompatible)
                        .filter(tm -> tm.getControllerStates().length > 0)
                        .collect(Collectors.toList());

                if (compatibleTms.isEmpty()) return; // Skip unused upgrades to avoid confusion

                // TM Upgrade Panel
                WGridPanel tmuPanel = new WGridPanel();
                timeMachineUpgradesTabs.add(tmuPanel, tab -> tab.icon(new ItemIcon(tmu.getIcon())).tooltip(tmu.getName()));

                // TM Upgrade Name
                WLabel tmuNameLabel = new WLabel(tmu.getName().formatted(Formatting.BOLD));
                tmuPanel.add(tmuNameLabel, 0, 0, 18, 1);
                tmuNameLabel.setHorizontalAlignment(HorizontalAlignment.CENTER);

                // TM Upgrade Description
                WText tmuDescriptionText = new WText(tmu.getDescription());
                tmuPanel.add(tmuDescriptionText, 0, 1, 9, 6);

                // Compatible TMs Title
                WLabel compatibleTMsLabel = new WLabel(new TranslatableText("gui.tm_engineer_book.compatible_tms").formatted(Formatting.BOLD));
                tmuPanel.add(compatibleTMsLabel, 9, 1, 9, 1);
                compatibleTMsLabel.setHorizontalAlignment(HorizontalAlignment.CENTER);

                // Compatible TMs List
                WListPanel<ITimeMachine, WGridPanel> compatibleTMsList = new WListPanel<>(compatibleTms, WGridPanel::new, GuiDescription::configureCompatibleTMPanel);
                tmuPanel.add(compatibleTMsList, 9, 2, 9, 5);
            });

            root.validate(this);
        }

        private static List<ItemStack> generateItemList(BlockState... blockStates) {
            return Arrays.stream(blockStates)
                    .map(BlockState::getBlock)
                    .distinct()
                    .map(Block::asItem)
                    .map(Item::getDefaultStack)
                    .collect(Collectors.toList());
        }

        private static void configureCompatibleTMPanel(ITimeMachine tm, WGridPanel panel) {
            WItem icon = new WItem(tm.getIcon().getDefaultStack());
            panel.add(icon, 0, 0, 1, 1);

            WLabel name = new WLabel(tm.getName().formatted(Formatting.ITALIC));
            panel.add(name, 1, 0, 8, 1);
            name.setVerticalAlignment(VerticalAlignment.CENTER);
        }
    }
}
