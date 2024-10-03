package com.sp.render;

import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.deferred.light.AreaLight;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import org.joml.Quaternionf;

public class FlashLightlight {
static AreaLight flashLight = null;

    public void createFlashLightInstance(PlayerEntity player){
        MinecraftClient world = MinecraftClient.getInstance();

        if (world != null) {
            if (flashLight == null) {
                flashLight = new AreaLight();
                VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().addLight(flashLight
                        .setBrightness(0.5f)
                        .setDistance(100f)
                        .setSize(1, 1)
                );
            }
            flashLight.setOrientation(new Quaternionf().rotateXYZ((float) -(Math.toRadians(player.getPitch())), (float) Math.toRadians(player.getYaw()), 0.0f));
            flashLight.setPosition(player.getX(), player.getY(), player.getZ());
        }
    }

    public void destroyFlashLight(){
        VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().removeLight(flashLight);
        flashLight = null;
    }

    public void setFlashLightOrientation(PlayerEntity player){
        flashLight.setOrientation(new Quaternionf().rotateXYZ((float) -(Math.toRadians(player.getPitch())), (float) Math.toRadians(player.getYaw()), 0.0f));
        flashLight.setPosition(player.getX(), player.getY(), player.getZ());
    }


}
