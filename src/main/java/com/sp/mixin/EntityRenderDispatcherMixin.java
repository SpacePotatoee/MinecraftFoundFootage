package com.sp.mixin;

import com.sp.SPBRevampedClient;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {
    @Shadow
    private boolean renderShadows = false;

    @Inject(method = "setRenderShadows", at = @At("TAIL"))
    private void setRenderShadows(boolean renderShadows, CallbackInfo ci){
        if (SPBRevampedClient.shouldRenderCameraEffect()) {
            this.renderShadows = false;
        }
    }
}
