package com.sp.mixin.lightshadows;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderSystem;
import com.sp.SPBRevampedClient;
import com.sp.init.BackroomsLevels;
import com.sp.render.ShadowMapRenderer;
import foundry.veil.api.client.render.deferred.light.renderer.LightRenderer;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
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
            RegistryKey<World> registryKey = player.getWorld().getRegistryKey();
            if(registryKey == BackroomsLevels.LEVEL0_WORLD_KEY && !SPBRevampedClient.getCutsceneManager().isPlaying){
                setShadowUniforms(shader);
                shader.setInt("InOverWorld", registryKey == World.OVERWORLD ? 1 : 0);
                shader.setInt("ShouldRender", 1);
            } else {
                shader.setInt("InOverWorld", registryKey == World.OVERWORLD ? 1 : 0);
                shader.setInt("ShouldRender", 0);
            }
        }
        shader.setFloat("gameTime", RenderSystem.getShaderGameTime());
    }

    @Unique
    public void setShadowUniforms(foundry.veil.api.client.render.shader.program.ShaderProgram shaderProgram) {
        Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();

        shaderProgram.setMatrix("viewMatrix", ShadowMapRenderer.createShadowModelView(camera.getPos().x, camera.getPos().y, camera.getPos().z, true).peek().getPositionMatrix());
        shaderProgram.setMatrix("orthographMatrix", ShadowMapRenderer.createProjMat());
    }
}
