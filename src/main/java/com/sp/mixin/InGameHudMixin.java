package com.sp.mixin;

import com.sp.compat.modmenu.ConfigStuff;
import com.sp.SPBRevampedClient;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    @Inject(method = {"renderHotbar", "renderCrosshair"}, at = @At("HEAD"), cancellable = true)
    private void disableHotbar(CallbackInfo ci){
        if(ConfigStuff.disableHud || SPBRevampedClient.getCutsceneManager().isPlaying || SPBRevampedClient.getCutsceneManager().blackScreen.isBlackScreen) {
            ci.cancel();
        }
    }

    @Inject(method = "renderVignetteOverlay", at = @At("HEAD"), cancellable = true)
    private void disableVignette(CallbackInfo ci){
        if(SPBRevampedClient.getCutsceneManager().isPlaying || SPBRevampedClient.getCutsceneManager().blackScreen.isBlackScreen) {
            ci.cancel();
        }
    }

}
