package com.sp.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DebugHud.class)
public class DisableF3Mixin {

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void disableF3(CallbackInfo ci){
        PlayerEntity player = MinecraftClient.getInstance().player;

        if(player != null) {
            if (!player.isCreative() && !player.isSpectator()) {
                ci.cancel();
            }
        }
    }

}
