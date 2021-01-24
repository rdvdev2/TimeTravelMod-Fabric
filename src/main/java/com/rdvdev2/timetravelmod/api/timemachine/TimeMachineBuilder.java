package com.rdvdev2.timetravelmod.api.timemachine;

import com.rdvdev2.timetravelmod.impl.common.timemachine.TimeMachine;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;

public class TimeMachineBuilder {

    private Item icon = null;
    private int cooldownTime = 400;
    private int tier = 1;
    private String[][] structureLayers = null;
    private BlockState[] controllerStates = null;
    private BlockState[] coreStates = null;
    private BlockState[] basicStates = null;
    private int entityMaxLoad = 1;
    private int corruptionMultiplier = 1;

    public TimeMachineBuilder setIcon(Item icon) {
        this.icon = icon;
        return this;
    }

    public TimeMachineBuilder setCooldownTime(int cooldownTime) {
        this.cooldownTime = cooldownTime;
        return this;
    }

    public TimeMachineBuilder setTier(int tier) {
        this.tier = tier;
        return this;
    }

    public TimeMachineBuilder setStructureLayers(String[][] structureLayers) {
        this.structureLayers = structureLayers;
        return this;
    }

    public TimeMachineBuilder setControllerStates(BlockState... controllerStates) {
        this.controllerStates = controllerStates;
        return this;
    }

    public TimeMachineBuilder setCoreStates(BlockState... coreStates) {
        this.coreStates = coreStates;
        return this;
    }

    public TimeMachineBuilder setBasicStates(BlockState... basicStates) {
        this.basicStates = basicStates;
        return this;
    }

    public TimeMachineBuilder setEntityMaxLoad(int entityMaxLoad) {
        this.entityMaxLoad = entityMaxLoad;
        return this;
    }

    public TimeMachineBuilder setCorruptionMultiplier(int corruptionMultiplier) {
        this.corruptionMultiplier = corruptionMultiplier;
        return this;
    }

    public ITimeMachine build() {
        if(structureLayers == null || controllerStates == null || coreStates == null || basicStates == null)
            throw new RuntimeException("You must define all properties");
        return new TimeMachine(icon, cooldownTime, tier, structureLayers, controllerStates, coreStates, basicStates, entityMaxLoad, corruptionMultiplier);
    }
}
