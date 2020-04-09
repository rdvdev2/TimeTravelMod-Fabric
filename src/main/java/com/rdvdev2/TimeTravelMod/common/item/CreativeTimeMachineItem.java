package com.rdvdev2.TimeTravelMod.common.item;

import com.rdvdev2.TimeTravelMod.Mod;
import com.rdvdev2.TimeTravelMod.ModTimeMachines;
import com.rdvdev2.TimeTravelMod.api.timemachine.TimeMachine;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class CreativeTimeMachineItem extends Item {

    private TimeMachine timeMachine = ModTimeMachines.CREATIVE;

    public CreativeTimeMachineItem() {
        super(new Item.Settings()
                .maxCount(1)
                .group(Mod.TAB_TTM)
        );
    }
    
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient) this.timeMachine.run(world, user, user.getBlockPos(), Direction.NORTH);
        return TypedActionResult.success(user.getStackInHand(hand));
    }
}
