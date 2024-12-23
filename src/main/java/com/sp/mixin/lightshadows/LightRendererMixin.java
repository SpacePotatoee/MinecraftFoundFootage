package com.sp.mixin.lightshadows;

import com.llamalad7.mixinextras.sugar.Local;
import com.sp.SPBRevampedClient;
import com.sp.render.ShadowMapRenderer;
import foundry.veil.api.client.render.deferred.light.renderer.LightRenderer;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LightRenderer.class)
public class LightRendererMixin {

    @Inject(method = "applyShader", at = @At(value = "INVOKE", target = "Lfoundry/veil/api/client/render/shader/program/ShaderProgram;bind()V"), remap=false)
    private void setUniforms(CallbackInfo ci, @Local ShaderProgram shader){
        setShadowUniforms(shader);
        if(SPBRevampedClient.getCutsceneManager().isPlaying) {
            shader.setInt("CutsceneActive", 1);
        } else {
            shader.setInt("CutsceneActive", 0);
        }
    }

    @Unique
    public void setShadowUniforms(foundry.veil.api.client.render.shader.program.ShaderProgram shaderProgram) {
        Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
        Matrix4f shadowModelView = new Matrix4f();
        shadowModelView.identity();
        ShadowMapRenderer.rotateShadowModelView(shadowModelView);
        Vector4f lightPosition = new Vector4f(0.0f, 0.0f, 1.0f, 0.0f);
        lightPosition.mul(shadowModelView.invert());

        Vector3f shadowLightDirection = new Vector3f(lightPosition.x(), lightPosition.y(), lightPosition.z());
        shaderProgram.setMatrix("viewMatrix", ShadowMapRenderer.createShadowModelView(camera.getPos().x, camera.getPos().y, camera.getPos().z, true).peek().getPositionMatrix());
        shaderProgram.setMatrix("orthographMatrix", ShadowMapRenderer.createProjMat());
        shaderProgram.setVector("lightAngled", shadowLightDirection);
    }
}
