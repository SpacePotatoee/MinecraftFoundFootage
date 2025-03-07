package com.sp.mixin.lightshadows;

import foundry.veil.api.client.render.deferred.light.PointLight;
import foundry.veil.api.client.render.deferred.light.renderer.InstancedLightRenderer;
import foundry.veil.impl.client.render.deferred.light.InstancedPointLightRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static org.lwjgl.opengl.GL11C.GL_FLOAT;
import static org.lwjgl.opengl.GL20C.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20C.glVertexAttribPointer;
import static org.lwjgl.opengl.GL33C.glVertexAttribDivisor;

@Mixin(value = InstancedPointLightRenderer.class, remap = false)
public abstract class InstancedPointLightRendererMixin extends InstancedLightRenderer<PointLight> {
    public InstancedPointLightRendererMixin(int lightSize) {
        super(lightSize);
    }


    @Inject(method = "setupBufferState", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL20C;glEnableVertexAttribArray(I)V", ordinal = 2))
    private void inject1(CallbackInfo ci){
        glEnableVertexAttribArray(4);
    }

    @Inject(method = "setupBufferState", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL20C;glVertexAttribPointer(IIIZIJ)V", ordinal = 2))
    private void inject2(CallbackInfo ci){
        glVertexAttribPointer(4, 1, GL_FLOAT, false, this.lightSize, Float.BYTES * 7);
    }

    @Inject(method = "setupBufferState", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL33C;glVertexAttribDivisor(II)V", ordinal = 2))
    private void inject3(CallbackInfo ci){
        glVertexAttribDivisor(4, 1);
    }

}
