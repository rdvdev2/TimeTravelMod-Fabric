package com.rdvdev2.timetravelmod.impl.common.block.entity;

import com.rdvdev2.timetravelmod.impl.ModBlocks;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Tickable;

public class TemporalCauldronBlockEntity extends BlockEntity implements BlockEntityClientSerializable, Tickable {

    public static final int MAX_TIME_CRYSTAL_MBS = 3000;
    private static final int REPAIR_INTERVAL = 20;

    private ItemStack itemInside = ItemStack.EMPTY;
    private int timeCrystalMbs = 0;
    private int ticksForNextConsumption = 0;

    public TemporalCauldronBlockEntity() {
        super(ModBlocks.Entities.TEMPORAL_CAULDRON);
    }

    public ItemStack getItemInside() {
        return itemInside;
    }

    public void setItemInside(ItemStack itemInside) {
        this.itemInside = itemInside;
    }

    public ItemStack consumeItemInside() {
        ItemStack ret = itemInside;
        itemInside = ItemStack.EMPTY;
        return ret;
    }

    public boolean doesFullBucketFit() {
        return timeCrystalMbs <= (MAX_TIME_CRYSTAL_MBS - 1000);
    }

    public void addFullBucket() {
        timeCrystalMbs = Math.min(timeCrystalMbs + 1000, MAX_TIME_CRYSTAL_MBS);
    }

    public int getTimeCrystalMbs() {
        return timeCrystalMbs;
    }

    @Override
    public void tick() {
        if (world.isClient || !getItemInside().isDamaged() || timeCrystalMbs == 0) return;

        if (ticksForNextConsumption == 0) {
            ticksForNextConsumption = REPAIR_INTERVAL;
            timeCrystalMbs--;
            int prevDmg = getItemInside().getDamage();
            getItemInside().setDamage(prevDmg - 1);
        }
        ticksForNextConsumption--;
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        tag = toClientTag(tag);
        tag.putInt("ticksForNextConsumption", ticksForNextConsumption);
        return super.toTag(tag);
    }

    @Override
    public CompoundTag toClientTag(CompoundTag tag) {
        tag.put("itemstack", itemInside.toTag(new CompoundTag()));
        tag.putInt("timeCrystalMbs", timeCrystalMbs);
        return tag;
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        fromClientTag(tag);
        ticksForNextConsumption = tag.getInt("ticksForNextConsumption");
        super.fromTag(state, tag);
    }

    @Override
    public void fromClientTag(CompoundTag tag) {
        if (tag.contains("itemstack")) {
            itemInside = ItemStack.fromTag(tag.getCompound("itemstack"));
            timeCrystalMbs = tag.getInt("timeCrystalMbs");
        }
    }
}
