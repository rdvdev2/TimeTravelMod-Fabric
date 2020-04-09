package com.rdvdev2.TimeTravelMod.api.timemachine.block;

import com.rdvdev2.TimeTravelMod.api.timemachine.upgrade.TimeMachineUpgrade;
import com.rdvdev2.TimeTravelMod.common.timemachine.TimeMachineManager;
import net.minecraft.block.Block;
import net.minecraft.util.registry.Registry;

/**
 * Blocks that pretend to act as a Time Machine Upgrade must extend from this class.
 * Subclasses will provide the specified Time Machine Upgrade without needing to overwrite nothing on the class.
 */
public class TimeMachineUpgradeBlock extends Block {

    private TimeMachineUpgrade upgrade;
    
    /**
     * This works exactly as {@link Block#Block(Settings)}, but you also have to provide the {@link TimeMachineUpgrade} associated to this block.
     * @param properties {@link Block#Block(Settings)}
     * @param upgrade The {@link TimeMachineUpgrade} associated to this block
     */
    public TimeMachineUpgradeBlock(Settings properties, TimeMachineUpgrade upgrade) {
        super(properties);
        this.upgrade = upgrade;
        TimeMachineManager.addUpgradeToBlockEntry(getUpgrade(), this);
    }

    /**
     * Returns the attached {@link TimeMachineUpgrade}
     * @return The attached {@link TimeMachineUpgrade}
     */
    public TimeMachineUpgrade getUpgrade() {
        if (this.upgrade == null) throw new NullPointerException("Tried to access to the TimeMachineUpgrade of an unconfigured block (" + Registry.BLOCK.getId(this) + ")");
        return this.upgrade;
    }
}
