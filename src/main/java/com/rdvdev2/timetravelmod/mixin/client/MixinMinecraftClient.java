package com.rdvdev2.timetravelmod.mixin.client;

import com.rdvdev2.timetravelmod.impl.client.music.TimelineMusicManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.sound.MusicSound;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {

    @Shadow @Nullable public ClientWorld world;

    @Inject(method = "getMusicType", at = @At("HEAD"), cancellable = true)
    private void onGetMusicType(CallbackInfoReturnable<MusicSound> cir) {
        if (this.world == null || this.world.getRegistryKey() == null) return;
        Optional<MusicSound> musicSound = TimelineMusicManager.INSTANCE.get(this.world.getRegistryKey());
        musicSound.ifPresent(cir::setReturnValue);
    }
}
