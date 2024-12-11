package com.sp.mixin.lightshadows;

import foundry.veil.api.client.render.deferred.light.IndirectLight;
import foundry.veil.api.client.render.deferred.light.Light;
import foundry.veil.api.client.render.deferred.light.renderer.IndirectLightRenderer;
import foundry.veil.api.client.render.deferred.light.renderer.LightRenderer;
import net.minecraft.client.gl.VertexBuffer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = IndirectLightRenderer.class, remap = false)
public abstract class IndirectLightRendererMixin<T extends Light & IndirectLight<T>> {

    @Mutable
    @Shadow @Final protected int lightSize;

    //@Redirect(method = "<init>", at = @At(value = "FIELD", target = "Lfoundry/veil/api/client/render/deferred/light/renderer/IndirectLightRenderer;lightSize:I"))
    private void changeSize(IndirectLightRenderer instance, int value){
        if(value == Float.BYTES * 7) {
            this.lightSize = Float.BYTES * 8;
        } else {
            this.lightSize = value;
        }
    }

}
