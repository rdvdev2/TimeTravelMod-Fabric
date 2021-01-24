package com.rdvdev2.timetravelmod.mixin.common;

import net.minecraft.entity.damage.DamageSource;
import org.apache.commons.lang3.NotImplementedException;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(DamageSource.class)
public interface IDamageSource {

    @Invoker("<init>")
    static DamageSource create(String name) {
        throw new NotImplementedException("IDamageSource mixin failed to apply");
    }

    @Invoker
    DamageSource callSetUnblockable();
}
