package com.rdvdev2.timetravelmod.api.timemachine;

import com.rdvdev2.timetravelmod.api.dimension.ITimeline;
import com.rdvdev2.timetravelmod.impl.ModRegistries;
import com.rdvdev2.timetravelmod.impl.common.timemachine.TimeMachineStructure;
import net.minecraft.item.Item;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ITimeMachineUpgrade {

    default TranslatableText getName() {
        Identifier id = ModRegistries.TIME_MACHINE_UPGRADE.getId(this);
        return new TranslatableText(String.format("tm_upgrade.%s.%s.name", id.getNamespace(), id.getPath()));
    }

    default TranslatableText getDescription() {
        Identifier id = ModRegistries.TIME_MACHINE_UPGRADE.getId(this);
        return new TranslatableText(String.format("tm_upgrade.%s.%s.description", id.getNamespace(), id.getPath()));
    }

    Item getIcon();

    default boolean isTimeMachineCompatible(ITimeMachine timeMachine) {
        return true;
    }

    default void beforeTeleporting(TimeMachineStructure structure, BlockPos root, BlockPos upgrade, World origWorld, World destWorld, ITimeline origTimeline, ITimeline destTimeline) { }

    default void afterTeleporting(TimeMachineStructure structure, BlockPos root, BlockPos upgrade, World origWorld, World destWorld, ITimeline origTimeline, ITimeline destTimeline) { }
}
