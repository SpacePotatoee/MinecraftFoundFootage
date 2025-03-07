package com.sp.mixin.lightshadows;

import foundry.veil.api.client.render.deferred.light.InstancedLight;
import foundry.veil.api.client.render.deferred.light.Light;
import foundry.veil.api.client.render.deferred.light.renderer.InstancedLightRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = InstancedLightRenderer.class, remap = false)
public abstract class InstancedLightRendererMixin<T extends Light & InstancedLight> {

    @Mutable
    @Shadow @Final protected int lightSize;

    //@Redirect(method = "<init>", at = @At(value = "FIELD", target = "Lfoundry/veil/api/client/render/deferred/light/renderer/InstancedLightRenderer;lightSize:I", ordinal = 0))
    private void changeSize(InstancedLightRenderer instance, int value){
        if(value == Float.BYTES * 7) {
            this.lightSize = Float.BYTES * 8;
        } else {
            this.lightSize = value;
        }
    }

}
