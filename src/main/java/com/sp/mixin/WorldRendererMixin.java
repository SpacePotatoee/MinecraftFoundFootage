package com.sp.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderSystem;
import com.sp.SPBRevamped;
import com.sp.SPBRevampedClient;
import com.sp.util.uniformTest;
import com.sp.util.MatrixMath;
import com.sp.world.levels.BackroomsLevels;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.VeilRenderer;
import foundry.veil.api.client.render.deferred.VeilDeferredRenderer;
import foundry.veil.api.client.render.framebuffer.AdvancedFbo;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.joml.*;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.Math;

import static net.minecraft.util.math.MathHelper.sin;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {
    //private boolean renderingShadows = false;

    @Shadow @Final private MinecraftClient client;

    @Shadow protected abstract void renderLayer(RenderLayer renderLayer, MatrixStack matrices, double cameraX, double cameraY, double cameraZ, Matrix4f positionMatrix);

    @Shadow public abstract void setupFrustum(MatrixStack matrices, Vec3d pos, Matrix4f projectionMatrix);

    @Shadow private Frustum frustum;

    @Shadow private @Nullable ClientWorld world;

    @Inject(method = "renderSky(Lnet/minecraft/client/util/math/MatrixStack;Lorg/joml/Matrix4f;FLnet/minecraft/client/render/Camera;ZLjava/lang/Runnable;)V", at = @At("HEAD"), cancellable = true)
    public void renderSky(MatrixStack matrices, Matrix4f projectionMatrix, float tickDelta, Camera camera, boolean thickFog, Runnable fogCallback, CallbackInfo ci) {
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null){
            if(player.getWorld().getDimensionKey() == BackroomsLevels.LEVEL0_DIM_TYPE || player.getWorld().getRegistryKey() == BackroomsLevels.POOLROOMS_WORLD_KEY){
                ci.cancel();
            }

        }

    }

    @Inject(method = "renderClouds(Lnet/minecraft/client/util/math/MatrixStack;Lorg/joml/Matrix4f;FDDD)V", at = @At("HEAD"), cancellable = true)
    public void renderClouds(MatrixStack matrices, Matrix4f projectionMatrix, float tickDelta, double cameraX, double cameraY, double cameraZ, CallbackInfo ci) {
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null){
            if(player.getWorld().getDimensionKey() == BackroomsLevels.LEVEL0_DIM_TYPE || player.getWorld().getRegistryKey() == BackroomsLevels.POOLROOMS_WORLD_KEY){
                ci.cancel();
            }

        }
    }

    @Inject(method = "renderLayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gl/GlUniform;set(F)V", ordinal = 3, shift = At.Shift.BY, by = 2))
    public void uniformInject(RenderLayer renderLayer, MatrixStack matrices, double cameraX, double cameraY, double cameraZ, Matrix4f positionMatrix, CallbackInfo ci, @Local ShaderProgram shaderProgram){
        if(shaderProgram instanceof uniformTest) {
            if (((uniformTest) shaderProgram).getOrthoMatrix() != null) {
                Matrix4f matrix4f = createProjMat();
                ((uniformTest) shaderProgram).getOrthoMatrix().set(matrix4f);
            }

            if (((uniformTest) shaderProgram).getViewMatrix() != null) {
                MatrixStack shadowModelView = SPBRevampedClient.createShadowModelView(cameraX, cameraY, cameraZ, true);

                ((uniformTest) shaderProgram).getViewMatrix().set(shadowModelView.peek().getPositionMatrix());
            }

            if (((uniformTest) shaderProgram).getLightAngle() != null) {
                Matrix4f shadowModelView = new Matrix4f();
                shadowModelView.identity();
                shadowModelView.rotate(RotationAxis.POSITIVE_X.rotationDegrees(25.0f * sin(RenderSystem.getShaderGameTime() * 200) + 90.0f));
                Vector4f lightPosition = new Vector4f(0.0f, 0.0f, 1.0f, 0.0f);
                lightPosition.mul(shadowModelView.invert());

                Vector3f shadowLightDirection = new Vector3f(lightPosition.x(), lightPosition.y(), lightPosition.z());

                ((uniformTest) shaderProgram).getLightAngle().set(shadowLightDirection);
            }
        }
    }

    @Unique
    public Matrix4f createProjMat(){
        return MatrixMath.orthographicMatrix(160, 0.05f, 256.0f);
    }



}

