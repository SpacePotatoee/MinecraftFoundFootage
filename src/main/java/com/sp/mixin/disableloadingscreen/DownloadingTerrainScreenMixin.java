package com.sp.mixin.disableloadingscreen;

import com.sp.SPBRevampedClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.DownloadingTerrainScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DownloadingTerrainScreen.class)
public class DownloadingTerrainScreenMixin {
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void removeScreen(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci){
        if (SPBRevampedClient.isInBackrooms()) {
            ci.cancel();
        }
    }
}
