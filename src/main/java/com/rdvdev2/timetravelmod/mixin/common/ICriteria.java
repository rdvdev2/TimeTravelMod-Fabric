package com.rdvdev2.timetravelmod.mixin.common;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.advancement.criterion.Criterion;
import org.apache.commons.lang3.NotImplementedException;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Criteria.class)
public interface ICriteria {

    @Invoker("register")
    static <T extends Criterion<?>> T callRegister(T criteria) {
        throw new NotImplementedException("ICriteria failed to apply");
    }
}
