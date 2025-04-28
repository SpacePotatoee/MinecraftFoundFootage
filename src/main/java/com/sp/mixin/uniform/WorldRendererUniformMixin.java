package com.sp.mixin.uniform;

import com.llamalad7.mixinextras.sugar.Local;
import com.sp.SPBRevampedClient;
import com.sp.mixininterfaces.uniformTest;
import com.sp.render.ShadowMapRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class WorldRendererUniformMixin {

    @Shadow private @Nullable ClientWorld world;

    //@Inject(method = "renderLayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gl/GlUniform;set(F)V", ordinal = 3, shift = At.Shift.BY, by = 2))
    public void uniformInject(RenderLayer renderLayer, MatrixStack matrices, double cameraX, double cameraY, double cameraZ, Matrix4f positionMatrix, CallbackInfo ci, @Local ShaderProgram shaderProgram){
        if (SPBRevampedClient.shouldRenderCameraEffect()) {
            if (shaderProgram instanceof uniformTest) {
                if (((uniformTest) shaderProgram).getWarpAngle() != null) {
                    MinecraftClient client = MinecraftClient.getInstance();

                    if(client.world != null) {
                        ((uniformTest) shaderProgram).getWarpAngle().set(SPBRevampedClient.getWarpTimer(client.world));
                    }
                }
            }
        }
    }

}
