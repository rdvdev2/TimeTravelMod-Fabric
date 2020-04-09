package com.rdvdev2.TimeTravelMod.common.block.blockentity;

import com.rdvdev2.TimeTravelMod.ModBlocks;
import com.rdvdev2.TimeTravelMod.api.timemachine.block.TimeMachineCoreBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Tickable;

/**
 * This TileEntity is attached to non cooled down Time Machine cores and is used to calculate when they are ready
 */
public class TMCooldownBlockEntity extends BlockEntity implements Tickable {

    Integer remainingTicks;

    /**
     * Constructor of the TileEntity
     * @param ticks How many ticks needs this Time Machine core to cool down
     */
    public TMCooldownBlockEntity(Integer ticks) {
        super(ModBlocks.TileEntities.TM_COOLDOWN);
        this.remainingTicks = ticks;
    }

    /**
     * Default constructor of the TileEntity (Time Machine static HashMap stores time)
     */
    public TMCooldownBlockEntity() {
        this(null);
    }

    public void setTime(int ticks) {
        this.remainingTicks = ticks;
    }
    
    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        if (this.remainingTicks != null) tag.putInt("ticks", remainingTicks);
        return tag;
    }
    
    @Override
    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);
        if (tag.contains("ticks")) this.remainingTicks = tag.getInt("ticks");
    }
    
    @Override
    public void tick() {
        if (remainingTicks == null || remainingTicks < 0) return;
        this.remainingTicks -= 1;
        if (this.remainingTicks == 0) {
            this.world.setBlockState(this.pos, this.world.getBlockState(this.pos).getBlock().getDefaultState().with(TimeMachineCoreBlock.TM_READY, true));
        }
        this.markDirty();
    }
}
