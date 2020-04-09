package com.rdvdev2.TimeTravelMod.common.block.blockentity;

import com.rdvdev2.TimeTravelMod.ModBlocks;
import com.rdvdev2.TimeTravelMod.ModItems;
import com.rdvdev2.TimeTravelMod.common.block.TemporalCauldronBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.Tickable;

import java.util.Random;

public class TemporalCauldronTileEntity extends BlockEntity implements Tickable {

    private Inventory inventory = new BasicInventory(2) {
        @Override
        public void markDirty() {
            super.markDirty();
            TemporalCauldronTileEntity.this.markDirty();
        }
    };

    private final static int CRYSTAL_SLOT = 0;
    private final static int ITEM_SLOT = 1;

    private int crystal_usages = 0;
    private int tick_count = 0;

    public TemporalCauldronTileEntity() {
        super(ModBlocks.TileEntities.TEMPORAL_CAULDRON);
    }

    public boolean containsItem() {
        return !inventory.getInvStack(ITEM_SLOT).isEmpty();
    }

    public void putItem(ItemStack item) {
        if (item.isDamageable()); inventory.setInvStack(ITEM_SLOT, item);
    }

    public ItemStack removeItem() {
        return inventory.takeInvStack(ITEM_SLOT, 1);
    }

    public boolean containsCrystal() {
        return !inventory.getInvStack(CRYSTAL_SLOT).isEmpty();
    }

    public void putCrystal(ItemStack item) {
        if (item.getItem() == ModItems.TIME_CRYSTAL) {
            inventory.setInvStack(CRYSTAL_SLOT, item);
            crystal_usages = 2000;
            this.world.setBlockState(this.pos, this.world.getBlockState(pos).with(TemporalCauldronBlock.LEVEL, 3));
        }
    }
    
    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
    
        ListTag inventoryTag = new ListTag();
        for (int slot = 0; slot < inventory.getInvSize(); slot++) {
            inventoryTag.add(slot, inventory.getInvStack(slot).toTag(new CompoundTag()));
        }
        tag.put("inventory", inventoryTag);
        
        tag.putInt("crystal_usages", crystal_usages);
        tag.putInt("tick_count", tick_count);
        
        return tag;
    }
    
    @Override
    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);
        
        ListTag inventoryTag = (ListTag) tag.get("inventory");
        if (inventoryTag != null) {
            for (int slot = 0; slot < inventory.getInvSize(); slot++) {
                inventory.setInvStack(slot, ItemStack.fromTag(inventoryTag.getCompound(slot)));
            }
            crystal_usages = tag.getInt("crystal_usages");
            tick_count = tag.getInt("tick_count");
        }
    }
    
    @Override
    public void tick() {
        if (world.isClient) return;
        if (!inventory.getInvStack(ITEM_SLOT).isEmpty() && !inventory.getInvStack(CRYSTAL_SLOT).isEmpty()) {
            if (crystal_usages == 1300) this.world.setBlockState(this.pos, this.world.getBlockState(pos).with(TemporalCauldronBlock.LEVEL, 2));
            if (crystal_usages == 600) this.world.setBlockState(this.pos, this.world.getBlockState(pos).with(TemporalCauldronBlock.LEVEL, 1));
            if (crystal_usages == 0) {
                this.world.setBlockState(this.pos, this.world.getBlockState(pos).with(TemporalCauldronBlock.LEVEL, 0));
                inventory.takeInvStack(CRYSTAL_SLOT, 1);
            }

            tick_count++;
            if (tick_count == 10) {
                tick_count = 0;
                crystal_usages--;

                ItemStack tool = inventory.takeInvStack(ITEM_SLOT, 1);
                int damage = tool.getDamage();
                Random r = new Random();

                int n = r.nextInt(100);
                if (n >= 98) damage++;
                else if (n < 95) damage--;

                tool.setDamage(damage);
                inventory.setInvStack(ITEM_SLOT, tool);
            }

            markDirty();
        }
    }
}
