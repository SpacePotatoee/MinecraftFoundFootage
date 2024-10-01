package com.sp.mixin;

import com.sp.ConfigStuff;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class DisableHotbarMixin {

    //@Inject(method = "renderHotbar", at = @At("HEAD"), cancellable = true)
    private void disableHotbar(CallbackInfo ci){
            ci.cancel();
    }

    //@Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
    private void disableCrosshair(CallbackInfo ci){
        ci.cancel();
    }

}
