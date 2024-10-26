package com.sp.mixin.cutscene;

import com.sp.SPBRevampedClient;
import net.minecraft.client.sound.SoundSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SoundSystem.class)
public abstract class SoundSystemMixin {
    @Unique boolean isPaused = false;

    @Shadow protected abstract void tick();

    @Shadow public abstract void pauseAll();

    @Shadow public abstract void resumeAll();

    @Redirect(method = "tick(Z)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sound/SoundSystem;tick()V"))
    private void redirect(SoundSystem instance){
        if (!SPBRevampedClient.getCutsceneManager().blackScreen.isBlackScreen) {
            this.tick();

            if(this.isPaused){
                this.resumeAll();
                this.isPaused = false;
            }
        } else {
            this.pauseAll();
            this.isPaused = true;
        }
    }

}
