package com.sp.mixin.arealightfix;

import foundry.veil.api.client.render.deferred.light.AreaLight;
import foundry.veil.api.client.render.deferred.light.renderer.InstancedLightRenderer;
import foundry.veil.impl.client.render.deferred.light.AreaLightRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static org.lwjgl.opengl.GL11C.GL_FLOAT;
import static org.lwjgl.opengl.GL20C.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20C.glVertexAttribPointer;
import static org.lwjgl.opengl.GL33C.glVertexAttribDivisor;

@Mixin(value = AreaLightRenderer.class, remap = false)
public abstract class AreaLightRendererMixin extends InstancedLightRenderer<AreaLight> {

    public AreaLightRendererMixin(int lightSize) {
        super(lightSize);
    }

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lfoundry/veil/api/client/render/deferred/light/renderer/InstancedLightRenderer;<init>(I)V"))
    private static int modify(int lightSize){
        return Float.BYTES * 23;
    }

    @Inject(method = "setupBufferState", at = @At("HEAD"), cancellable = true)
    private void lightFix(CallbackInfo ci){
        ci.cancel();

        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);
        glEnableVertexAttribArray(3);
        glEnableVertexAttribArray(4);
        glEnableVertexAttribArray(5);
        glEnableVertexAttribArray(6);
        glEnableVertexAttribArray(7);
        glEnableVertexAttribArray(8);

        glVertexAttribPointer(1, 4, GL_FLOAT, false, this.lightSize, 0);
        glVertexAttribPointer(2, 4, GL_FLOAT, false, this.lightSize, Float.BYTES * 4);
        glVertexAttribPointer(3, 4, GL_FLOAT, false, this.lightSize, Float.BYTES * 8);
        glVertexAttribPointer(4, 4, GL_FLOAT, false, this.lightSize, Float.BYTES * 12); // matrix !

        glVertexAttribPointer(5, 3, GL_FLOAT, false, this.lightSize, Float.BYTES * 16); // color
        glVertexAttribPointer(6, 2, GL_FLOAT, false, this.lightSize, Float.BYTES * 19); // size
        glVertexAttribPointer(7, 1, GL_FLOAT, false, this.lightSize, Float.BYTES * 21); // angle
        glVertexAttribPointer(8, 1, GL_FLOAT, false, this.lightSize, Float.BYTES * 22); // distance

        glVertexAttribDivisor(1, 1);
        glVertexAttribDivisor(2, 1);
        glVertexAttribDivisor(3, 1);
        glVertexAttribDivisor(4, 1);
        glVertexAttribDivisor(5, 1);
        glVertexAttribDivisor(6, 1);
        glVertexAttribDivisor(7, 1);
        glVertexAttribDivisor(8, 1);
    }

}
