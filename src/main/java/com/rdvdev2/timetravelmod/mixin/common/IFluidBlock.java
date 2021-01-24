package com.rdvdev2.timetravelmod.mixin.common;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.FluidBlock;
import net.minecraft.fluid.FlowableFluid;
import org.apache.commons.lang3.NotImplementedException;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(FluidBlock.class)
public interface IFluidBlock {

    @Invoker(value = "<init>")
    static FluidBlock create(FlowableFluid fluid, AbstractBlock.Settings settings) {
        throw new NotImplementedException("IFluidBlock mixin failed to apply");
    }
}
