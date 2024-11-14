package com.sp.mixin;

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

    @Inject(method = "setRenderShadows", at = @At("TAIL"), cancellable = true)
    private void setRenderShadows(boolean renderShadows, CallbackInfo ci){
        this.renderShadows = false;
    }
}
