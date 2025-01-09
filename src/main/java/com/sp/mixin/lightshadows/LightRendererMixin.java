package com.sp.mixin.lightshadows;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderSystem;
import com.sp.SPBRevampedClient;
import com.sp.init.BackroomsLevels;
import foundry.veil.api.client.render.deferred.light.renderer.LightRenderer;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LightRenderer.class)
public class LightRendererMixin {

    @Inject(method = "applyShader", at = @At(value = "INVOKE", target = "Lfoundry/veil/api/client/render/shader/program/ShaderProgram;bind()V"), remap=false)
    private void setUniforms(CallbackInfo ci, @Local ShaderProgram shader) {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;
        if(player != null && client.world != null) {
            if (SPBRevampedClient.getCutsceneManager().isPlaying || player.getWorld().getRegistryKey() != BackroomsLevels.LEVEL0_WORLD_KEY) {
                shader.setInt("ShouldRender", 0);
            } else {
                shader.setInt("ShouldRender", 1);
            }
        }
        shader.setFloat("gameTime", RenderSystem.getShaderGameTime());
    }
}
