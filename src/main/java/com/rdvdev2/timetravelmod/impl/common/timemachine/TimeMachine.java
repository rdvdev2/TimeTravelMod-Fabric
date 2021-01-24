package com.rdvdev2.timetravelmod.impl.common.timemachine;

import com.rdvdev2.timetravelmod.api.timemachine.ITimeMachine;
import com.rdvdev2.timetravelmod.impl.ModRegistries;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class TimeMachine implements ITimeMachine {

    private final Item icon;
    private final int cooldownTime;
    private final int tier;
    private final String[][] structureLayers;
    private final BlockState[] controllerStates;
    private final BlockState[] coreStates;
    private final BlockState[] basicStates;
    private final int entityMaxLoad;
    private final int corruptionMultiplier;

    public TimeMachine(Item icon, int cooldownTime, int tier, String[][] structureLayers, BlockState[] controllerStates, BlockState[] coreStates, BlockState[] basicStates, int entityMaxLoad, int corruptionMultiplier) {
        this.icon = icon;
        this.cooldownTime = cooldownTime;
        this.tier = tier;
        this.structureLayers = structureLayers;
        this.controllerStates = controllerStates;
        this.coreStates = coreStates;
        this.basicStates = basicStates;
        this.entityMaxLoad = entityMaxLoad;
        this.corruptionMultiplier = corruptionMultiplier;
    }

    @Override
    public Item getIcon() {
        return icon == null ? ITimeMachine.super.getIcon() : icon;
    }

    @Override
    public int getCooldownTime() {
        return cooldownTime;
    }

    @Override
    public int getTier() {
        return tier;
    }

    @Override
    public String[][] getStructureLayers() {
        return structureLayers;
    }

    @Override
    public BlockState[] getControllerStates() {
        return controllerStates;
    }

    @Override
    public BlockState[] getCoreStates() {
        return coreStates;
    }

    @Override
    public BlockState[] getBasicStates() {
        return basicStates;
    }

    @Override
    public int getEntityMaxLoad() {
        return entityMaxLoad;
    }

    @Override
    public int getCorruptionMultiplier() {
        return corruptionMultiplier;
    }
}
