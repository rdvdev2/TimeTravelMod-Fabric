package com.rdvdev2.TimeTravelMod.mixin;

import com.rdvdev2.TimeTravelMod.common.event.MiscCallbackHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public abstract class MixinPlayerManager {
    
    @Inject(method = "onPlayerConnect", at = @At("RETURN"))
    private void timetravelmod_doOnPlayerConnect(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        MiscCallbackHandler.playerJoin(player);
    }
}
