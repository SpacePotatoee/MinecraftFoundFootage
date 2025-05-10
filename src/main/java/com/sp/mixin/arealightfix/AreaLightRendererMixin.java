package com.sp.mixin.arealightfix;

import foundry.veil.api.client.render.deferred.light.AreaLight;
import foundry.veil.api.client.render.deferred.light.renderer.InstancedLightRenderer;
import foundry.veil.impl.client.render.deferred.light.AreaLightRenderer;
import org.lwjgl.opengl.GL20C;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = AreaLightRenderer.class, remap = false)
public abstract class AreaLightRendererMixin extends InstancedLightRenderer<AreaLight> {

    public AreaLightRendererMixin(int lightSize) {
        super(lightSize);
    }

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lfoundry/veil/api/client/render/deferred/light/renderer/InstancedLightRenderer;<init>(I)V"))
    private static int modify(int lightSize){
        return Float.BYTES * 23;
    }

    @ModifyArg(method = "setupBufferState", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL20C;glVertexAttribPointer(IIIZIJ)V", ordinal = 6), index = 2)
    private int lightFixUseFloat(int original) {
        return GL20C.GL_FLOAT;
    }

    @ModifyArg(method = "setupBufferState", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL20C;glVertexAttribPointer(IIIZIJ)V", ordinal = 6))
    private boolean lightFixUseUnnormalized(boolean original) {
        return false;
    }

    @ModifyArg(method = "setupBufferState", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL20C;glVertexAttribPointer(IIIZIJ)V", ordinal = 7))
    private long lightFix(long normalized){
        return Float.BYTES * 22;
    }

}
