package com.rdvdev2.timetravelmod.api.timemachine;

import com.rdvdev2.timetravelmod.impl.ModRegistries;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public interface ITimeMachine {

    default TranslatableText getName() {
        Identifier id = ModRegistries.TIME_MACHINE.getId(this);
        return new TranslatableText(String.format("tm.%s.%s.name", id.getNamespace(), id.getPath()));
    }

    default TranslatableText getDescription() {
        Identifier id = ModRegistries.TIME_MACHINE.getId(this);
        return new TranslatableText(String.format("tm.%s.%s.description", id.getNamespace(), id.getPath()));
    }

    // Must be overridden by TMs that aren't physically built
    default Item getIcon() {
        return getControllerStates()[0].getBlock().asItem();
    }

    default int getCooldownTime() {
        return 400;
    }

    default int getTier() {
        return 1;
    }

    /*
    Z -> Controller
    C -> Core
    B -> Basic / Upgrade
      -> Air
    * -> Exclude
     */
    String[][] getStructureLayers();

    BlockState[] getControllerStates();

    BlockState[] getCoreStates();

    BlockState[] getBasicStates();

    default int getEntityMaxLoad() {
        return 1;
    }

    default int getCorruptionMultiplier() {
        return 1;
    }
}
