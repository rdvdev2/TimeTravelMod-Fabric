package com.rdvdev2.timetravelmod.mixin.common;

import com.rdvdev2.timetravelmod.impl.common.UpdateChecker;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public class MixinPlayerManager {

    @Inject(method = "onPlayerConnect", at = @At("RETURN"))
    private void timetravelmod_onPlayerConnect(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        UpdateChecker.run(player);
    }

}
