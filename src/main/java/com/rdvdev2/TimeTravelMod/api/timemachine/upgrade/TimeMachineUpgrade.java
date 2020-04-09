package com.rdvdev2.TimeTravelMod.api.timemachine.upgrade;

import com.rdvdev2.TimeTravelMod.api.timemachine.TimeMachine;
import net.minecraft.text.TranslatableText;

/**
 * Contains all the {@link TimeMachineHook} needed to apply an upgrade to a {@link TimeMachine}. You can compose an upgrade using a builder-like scheme. Objects must be registered to work.
 */
public interface TimeMachineUpgrade {

    /**
     * Adds a {@link TimeMachineHook} to the upgrade. It will run in non exclusive mode.
     * @param hook The {@link TimeMachineHook} to add
     * @return The upgrade itself
     */
    TimeMachineUpgrade addHook(TimeMachineHook<?> hook);

    /**
     * Adds a {@link TimeMachineHook} to the upgrade
     * @param hook The {@link TimeMachineHook} to add
     * @param exclusiveMode If true, the {@link TimeMachineHook} will run in exclusive mode
     * @return The upgrade itself
     */
    TimeMachineUpgrade addHook(TimeMachineHook<?> hook, boolean exclusiveMode);

    /**
     * Adds one or more {@link TimeMachineHook} to the upgrade. They will run in non exclusive mode
     * @param hooks The {@link TimeMachineHook} to add
     * @return The upgrade itself
     */
    TimeMachineUpgrade addAllHooks(TimeMachineHook<?>... hooks);

    /**
     * Specifies which {@link TimeMachine} are compatible with this upgrade
     * @param compatibleTMs The list of compatible {@link TimeMachine}
     * @return The upgrade itself
     */
    TimeMachineUpgrade setCompatibleTMs(TimeMachine... compatibleTMs);

    /**
     * Returns which {@link TimeMachine} are compatible with this upgrade
     * @return The list of compatible {@link TimeMachine}
     */
    TimeMachine[] getCompatibleTMs();

    /**
     * Returns the name of the upgrade for use in GUIs
     * @return The name of the upgrade as a {@link TranslatableText}
     */
    TranslatableText getName();

    /**
     * Returns the description of the upgrade for use in GUIs
     * @return The description of the upgrade as a {@link TranslatableText}
     */
    TranslatableText getDescription();

    /**
     * Constructs a new empty upgrade
     * @return An empty upgrade
     */
    static TimeMachineUpgrade getNew() {
        return new com.rdvdev2.TimeTravelMod.common.timemachine.upgrade.TimeMachineUpgrade();
    }
}
