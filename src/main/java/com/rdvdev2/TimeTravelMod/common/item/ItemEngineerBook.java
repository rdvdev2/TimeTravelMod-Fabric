package com.rdvdev2.TimeTravelMod.common.item;

import com.rdvdev2.TimeTravelMod.Mod;
import com.rdvdev2.TimeTravelMod.ModRegistries;
import com.rdvdev2.TimeTravelMod.client.gui.EngineerBookScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class ItemEngineerBook extends Item {
    
    private EngineerBookScreen generatedEngineerBook;
    
    public ItemEngineerBook() {
        super(new Item.Settings()
                .group(Mod.TAB_TTM)
                .maxCount(1));
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (world.isClient) {
            if (generatedEngineerBook == null) generatedEngineerBook = new EngineerBookScreen(ModRegistries.TIME_MACHINES.iterator());
            net.minecraft.client.MinecraftClient.getInstance().openScreen(generatedEngineerBook);
        }
        return TypedActionResult.pass(user.getStackInHand(hand));
    }
    
    @Override
    public boolean hasEnchantmentGlint(ItemStack stack) {
        return true;
    }
}
