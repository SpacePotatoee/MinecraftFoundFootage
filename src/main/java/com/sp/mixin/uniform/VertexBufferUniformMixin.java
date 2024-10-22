package com.sp.mixin.uniform;

import com.llamalad7.mixinextras.sugar.Local;
import com.sp.SPBRevampedClient;
import com.sp.util.uniformTest;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.VertexBuffer;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VertexBuffer.class)
public class VertexBufferUniformMixin {

    @Inject(method = "drawInternal", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gl/GlUniform;set(F)V", ordinal = 3, shift = At.Shift.BY, by = 2))
    public void uniformInject(Matrix4f viewMatrix, Matrix4f projectionMatrix, ShaderProgram program, CallbackInfo ci, @Local ShaderProgram shaderProgram){
        if(shaderProgram instanceof uniformTest) {
            if (((uniformTest) shaderProgram).getWarpAngle() != null) {
                MinecraftClient client = MinecraftClient.getInstance();

                if(client.world != null) {
                ((uniformTest) shaderProgram).getWarpAngle().set(SPBRevampedClient.getWarpTimer(client.world));
                }
            }
        }
    }
}
