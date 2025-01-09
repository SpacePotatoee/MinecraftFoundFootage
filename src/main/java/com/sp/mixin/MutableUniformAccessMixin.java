package com.sp.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.sp.SPBRevampedClient;
import foundry.veil.api.client.render.CameraMatrices;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.shader.program.MutableUniformAccess;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import static com.sp.SPBRevampedClient.getCutsceneManager;
import static com.sp.SPBRevampedClient.getSunsetTimer;

@Mixin(value = MutableUniformAccess.class, remap = false)
public interface MutableUniformAccessMixin {
    @Shadow public abstract void setVector(CharSequence name, float x, float y);

    @Shadow public abstract void setFloat(CharSequence name, float value);

    @Shadow public abstract void setMatrix(CharSequence name, Matrix4fc value);

    @Shadow public abstract void setInt(CharSequence name, int value);

    @Shadow public abstract void setVector(CharSequence name, float[] values);

    //Because apparently you can't inject into interfaces :\
    @Overwrite
    default void applyRenderSystem() {
        this.setMatrix("RenderModelViewMat", RenderSystem.getModelViewMatrix());
        this.setMatrix("RenderProjMat", RenderSystem.getProjectionMatrix());
        this.setVector("ColorModulator", RenderSystem.getShaderColor());
        this.setFloat("GlintAlpha", RenderSystem.getShaderGlintAlpha());
        this.setFloat("FogStart", RenderSystem.getShaderFogStart());
        this.setFloat("FogEnd", RenderSystem.getShaderFogEnd());
        this.setVector("FogColor", RenderSystem.getShaderFogColor());
        this.setInt("FogShape", RenderSystem.getShaderFogShape().getId());
        this.setMatrix("TextureMatrix", RenderSystem.getTextureMatrix());
        this.setFloat("GameTime", RenderSystem.getShaderGameTime());
        this.setMatrix("PrevViewMat", SPBRevampedClient.prevViewMat);
        this.setMatrix("PrevProjMat", SPBRevampedClient.prevProjMat);
        MinecraftClient client = MinecraftClient.getInstance();
        Window window = client.getWindow();

        this.setVector("ScreenSize", window.getWidth(), window.getHeight());

        if(client.world != null && SPBRevampedClient.camera != null) {
            this.setFloat("sunsetTimer", getSunsetTimer(client.world));
            SPBRevampedClient.setShadowUniforms((MutableUniformAccess) (Object) this, client.world);
        }


        SPBRevampedClient.prevViewMat = VeilRenderSystem.renderer().getCameraMatrices().getViewMatrix();
        SPBRevampedClient.prevProjMat = VeilRenderSystem.renderer().getCameraMatrices().getProjectionMatrix();
    }

}
