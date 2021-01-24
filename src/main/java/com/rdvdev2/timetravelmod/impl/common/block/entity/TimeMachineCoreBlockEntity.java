package com.rdvdev2.timetravelmod.impl.common.block.entity;

import com.rdvdev2.timetravelmod.api.timemachine.block.TimeMachineCoreBlock;
import com.rdvdev2.timetravelmod.impl.ModBlocks;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Tickable;

public class TimeMachineCoreBlockEntity extends BlockEntity implements Tickable, BlockEntityClientSerializable {

    private int remainingTicks;

    public TimeMachineCoreBlockEntity() {
        super(ModBlocks.Entities.TIME_MACHINE_CORE);
        this.remainingTicks = 0;
    }

    @Override
    public void tick() {
        if (remainingTicks == 0) return;
        remainingTicks--;
        markDirty();
    }

    // TODO: Reserved for visuals
    public int getRemainingTicks() {
        return remainingTicks;
    }

    public void setRemainingTicks(int remainingTicks) {
        this.remainingTicks = remainingTicks;
        markDirty();
    }

    public boolean isReady() {
        return remainingTicks <= 0;
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        fromClientTag(tag);
        super.fromTag(state, tag);
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        tag = toClientTag(tag);
        return super.toTag(tag);
    }

    @Override
    public void fromClientTag(CompoundTag tag) {
        remainingTicks = tag.getInt("remaining_ticks");
    }

    @Override
    public CompoundTag toClientTag(CompoundTag tag) {
        tag.putInt("remaining_ticks", remainingTicks);
        return tag;
    }
}
