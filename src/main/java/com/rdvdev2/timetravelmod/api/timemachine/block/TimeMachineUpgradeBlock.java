package com.rdvdev2.timetravelmod.api.timemachine.block;

import com.rdvdev2.timetravelmod.api.timemachine.ITimeMachineUpgrade;
import net.minecraft.block.Block;

public class TimeMachineUpgradeBlock extends Block {

    private final ITimeMachineUpgrade upgrade;

    public TimeMachineUpgradeBlock(Settings settings, ITimeMachineUpgrade upgrade) {
        super(settings);
        this.upgrade = upgrade;
    }

    public ITimeMachineUpgrade getUpgrade() {
        return upgrade;
    }
}
