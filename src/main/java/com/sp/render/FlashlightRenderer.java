package com.sp.render;

import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.deferred.light.AreaLight;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class FlashlightRenderer {
    private final MinecraftClient client;
    public final HashMap<AbstractClientPlayerEntity, ArrayList<AreaLight>> flashLightList2;

    public FlashlightRenderer(){
        this.client = MinecraftClient.getInstance();
        this.flashLightList2 = new HashMap<>();
    }

    public void renderFlashlightForEveryPlayer(float partialTicks) {
        if(client.world != null) {
            List<AbstractClientPlayerEntity> playerList = client.world.getPlayers();

            for (AbstractClientPlayerEntity player : playerList) {
                if (player != null) {
                    if(player.isSpectator() && !player.equals(client.player)){
                        tryToRemoveFlashlight(player);
                        continue;
                    }
                    PlayerComponent playerComponent = InitializeComponents.PLAYER.get(player);
                    if (playerComponent.isFlashLightOn()) {
                        Vec3d playerPos = player.getCameraPosVec(partialTicks);
                        if (!flashLightList2.containsKey(player)) {
                            AreaLight areaLight = new AreaLight();
                            AreaLight areaLight2 = new AreaLight();
                            Quaternionf orientation = new Quaternionf().rotateXYZ((float) -Math.toRadians(player.getPitch(partialTicks)), (float) Math.toRadians(player.getYaw(partialTicks)), 0.0f);
                            VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().addLight(areaLight
                                    .setBrightness(1f)
                                    .setDistance(25f)
                                    .setSize(0, 0)
                                    .setPosition(playerPos.getX(), playerPos.getY(), playerPos.getZ())
                                    .setOrientation(orientation)
                            );
                            VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().addLight(areaLight2
                                    .setBrightness(1f)
                                    .setAngle(0.25f)
                                    .setDistance(25f)
                                    .setSize(0, 0)
                                    .setPosition(playerPos.getX(), playerPos.getY(), playerPos.getZ())
                                    .setOrientation(orientation)
                            );
                            ArrayList<AreaLight> list = new ArrayList<>();
                            list.add(areaLight);
                            list.add(areaLight2);
                            flashLightList2.put(player, list);
                        } else {
                            ArrayList<AreaLight> areaLightList = flashLightList2.get(player);

                            for(AreaLight areaLights : areaLightList) {
                                Quaternionf currentRot = new Quaternionf().rotateXYZ((float) -Math.toRadians(player.getPitch(partialTicks)), (float) Math.toRadians(player.getYaw(partialTicks)), 0.0f);
                                //*Fix for replay mod
                                float alpha = client.player.isSpectator() ? 1.0f : 0.7f * client.getLastFrameDuration();
                                areaLights.getOrientation().slerp(currentRot, alpha);
                                areaLights.setPosition(playerPos.getX(), playerPos.getY(), playerPos.getZ());
                            }
                        }
                    } else {
                        tryToRemoveFlashlight(player);
                    }
                }
            }
        }


    }

    private void tryToRemoveFlashlight(AbstractClientPlayerEntity player){
        if (flashLightList2.containsKey(player) && flashLightList2.get(player) != null) {
            ArrayList<AreaLight> areaLightList = flashLightList2.get(player);
            for(AreaLight areaLights : areaLightList){
                VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().removeLight(areaLights);
            }
            flashLightList2.remove(player);
        }
    }

}
