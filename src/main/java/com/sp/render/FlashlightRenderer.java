package com.sp.render;

import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.deferred.light.AreaLight;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;

import java.util.HashMap;
import java.util.List;


public class FlashlightRenderer {
    private final MinecraftClient client;
    public final HashMap<AbstractClientPlayerEntity, AreaLight> flashLightList;

    public FlashlightRenderer(){
        this.client = MinecraftClient.getInstance();
        this.flashLightList = new HashMap<>();
    }

    public void renderFlashlightForEveryPlayer(float partialTicks){
        if(client.world != null) {
            List<AbstractClientPlayerEntity> playerList = client.world.getPlayers();

            for (AbstractClientPlayerEntity player : playerList) {
                if (player != null) {
                    PlayerComponent playerComponent = InitializeComponents.PLAYER.get(player);
                    if (playerComponent.isFlashLightOn()) {
                        Vec3d playerPos = player.getCameraPosVec(partialTicks);
                        if (!flashLightList.containsKey(player)) {
                            AreaLight areaLight = new AreaLight();
                            VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().addLight(areaLight
                                    .setBrightness(1f)
                                    .setDistance(25f)
                                    .setSize(0, 0)
                                    .setPosition(playerPos.getX(), playerPos.getY(), playerPos.getZ())
                                    .setOrientation(new Quaternionf().rotateXYZ((float) -Math.toRadians(player.getPitch(partialTicks)), (float) Math.toRadians(player.getYaw(partialTicks)), 0.0f))
                            );
                            flashLightList.put(player, areaLight);
                        } else {
                            AreaLight areaLight = flashLightList.get(player);

                            Quaternionf currentRot = new Quaternionf().rotateXYZ((float) -Math.toRadians(player.getPitch(partialTicks)), (float) Math.toRadians(player.getYaw(partialTicks)), 0.0f);
                            areaLight.getOrientation().slerp(currentRot, 0.02f);
                            areaLight.setPosition(playerPos.getX(), playerPos.getY(), playerPos.getZ());
                        }
                    } else {
                        if (flashLightList.containsKey(player) && flashLightList.get(player) != null) {
                            AreaLight areaLight = flashLightList.get(player);
                            VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().removeLight(areaLight);
                            flashLightList.remove(player);
                        }
                    }
                }
            }
        }


    }

}
