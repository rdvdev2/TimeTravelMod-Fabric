package com.rdvdev2.TimeTravelMod.mixin;

import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.advancement.criterion.Criterions;
import org.apache.commons.lang3.NotImplementedException;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Criterions.class)
public interface ICriterions {
    
    @Invoker("register")
    static <T extends Criterion<?>> T doRegister(T object) {
        throw new NotImplementedException("ICriterions mixin failed to apply");
    }
}
