package com.rdvdev2.TimeTravelMod.mixin.client;

import com.rdvdev2.TimeTravelMod.client.MusicManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.sound.SoundSystem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Environment(EnvType.CLIENT)
@Mixin(SoundManager.class)
public abstract class MixinSoundManager {
    
    @Final
    @Shadow
    private SoundSystem soundSystem;
    
    /**
     * @author rdvdev2 (rdvdev2@gmail.com)
     * @reason Until Fabric API provides this
     */
    @Overwrite
    public void play(SoundInstance sound) {
        MusicManager.onPlaySound(sound).ifPresent(this.soundSystem::play);
    }
}
