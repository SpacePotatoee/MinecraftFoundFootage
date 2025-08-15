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
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Enhanced FlashlightRenderer with modder-friendly features and debugging features caus it was missing.
 * Provides hooks and configuration options for easy extension.
 * Refactor by DarkFox
 */
public class FlashlightRenderer {
    private final MinecraftClient client;
    private final HashMap<AbstractClientPlayerEntity, ArrayList<AreaLight>> flashLightList2;

    private boolean enabled = true;
    private float brightness = 1.0f;
    private float distance = 25.0f;
    private float angle = 0.25f;
    private float smoothness = 0.7f;

    private Predicate<AbstractClientPlayerEntity> shouldRenderForPlayer = null;
    private Consumer<FlashlightLightData> lightCustomizer = null;
    private Runnable preRenderHook = null;
    private Runnable postRenderHook = null;

    public static class FlashlightLightData {
        public final AbstractClientPlayerEntity player;
        public final AreaLight primaryLight;
        public final AreaLight secondaryLight;
        public final Vec3d position;
        public final Quaternionf orientation;
        public final boolean isNewLight;

        public FlashlightLightData(AbstractClientPlayerEntity player, AreaLight primaryLight,
                                   AreaLight secondaryLight, Vec3d position, Quaternionf orientation, boolean isNewLight) {
            this.player = player;
            this.primaryLight = primaryLight;
            this.secondaryLight = secondaryLight;
            this.position = position;
            this.orientation = orientation;
            this.isNewLight = isNewLight;
        }
    }

    public FlashlightRenderer(){
        this.client = MinecraftClient.getInstance();
        this.flashLightList2 = new HashMap<>();
    }



    public void setEnabled(boolean enabled) {
        if (!enabled && this.enabled) {
            clearAllLights();
        }
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    /**
     * Set custom flashlight properties. Useful for battery systems or upgrades or idk systems.
     * (everything here is self-explanatory)
     */
    public void setFlashlightProperties(float brightness, float distance, float angle, float smoothness) {
        this.brightness = brightness;
        this.distance = distance;
        this.angle = angle;
        this.smoothness = smoothness;
    }

    /**
     * Set a custom predicate to determine if flashlight should render for a specific player.
     * Useful for permissions, or special conditions or stuff like that.
     *
     * @param predicate function that takes a player and returns whether to render flashlight
     */
    public void setPlayerRenderPredicate(Predicate<AbstractClientPlayerEntity> predicate) {
        this.shouldRenderForPlayer = predicate;
    }

    /**
     * Set a custom light customizer that gets called for each flashlight.
     * Allows modders to modify light properties per player or add special effects (useful for spv).
     *
     * @param customizer function that receives light data and can modify it
     */
    public void setLightCustomizer(Consumer<FlashlightLightData> customizer) {
        this.lightCustomizer = customizer;
    }

    /**
     * Set hooks that run before and after flashlight rendering.
     * Useful for performance monitoring and debugging i guess.
     *
     * @param preRender runs before flashlight rendering starts
     * @param postRender runs after flashlight rendering completes
     */
    public void setRenderHooks(Runnable preRender, Runnable postRender) {
        this.preRenderHook = preRender;
        this.postRenderHook = postRender;
    }

    public void renderFlashlightForEveryPlayer(float partialTicks) {
        if (!enabled || client.world == null) {
            return;
        }

        if (preRenderHook != null) {
            preRenderHook.run();
        }

        try {
            List<AbstractClientPlayerEntity> playerList = client.world.getPlayers();

            for (AbstractClientPlayerEntity player : playerList) {
                if (player != null) {
                    if(player.isSpectator() && !player.equals(client.player)){
                        tryToRemoveFlashlight(player);
                        continue;
                    }

                    PlayerComponent playerComponent = InitializeComponents.PLAYER.get(player);
                    boolean shouldRender = playerComponent.isFlashLightOn();

                    if (shouldRenderForPlayer != null) {
                        shouldRender = shouldRender && shouldRenderForPlayer.test(player);
                    }

                    if (shouldRender) {
                        Vec3d playerPos = player.getCameraPosVec(partialTicks);
                        if (!flashLightList2.containsKey(player)) {
                            AreaLight primaryLight = new AreaLight();
                            AreaLight secondaryLight = new AreaLight();
                            Quaternionf orientation = new Quaternionf().rotateXYZ(
                                    (float) -Math.toRadians(player.getPitch(partialTicks)),
                                    (float) Math.toRadians(player.getYaw(partialTicks)),
                                    0.0f
                            );

                            primaryLight
                                    .setBrightness(brightness)
                                    .setDistance(distance)
                                    .setSize(0, 0)
                                    .setPosition(playerPos.getX(), playerPos.getY(), playerPos.getZ())
                                    .setOrientation(orientation);

                            secondaryLight
                                    .setBrightness(brightness)
                                    .setAngle(angle)
                                    .setDistance(distance)
                                    .setSize(0, 0)
                                    .setPosition(playerPos.getX(), playerPos.getY(), playerPos.getZ())
                                    .setOrientation(orientation);

                            // Allow modders to customize lights before adding
                            if (lightCustomizer != null) {
                                FlashlightLightData lightData = new FlashlightLightData(
                                        player, primaryLight, secondaryLight, playerPos, orientation, true
                                );
                                lightCustomizer.accept(lightData);
                            }

                            VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().addLight(primaryLight);
                            VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().addLight(secondaryLight);

                            ArrayList<AreaLight> list = new ArrayList<>();
                            list.add(primaryLight);
                            list.add(secondaryLight);
                            flashLightList2.put(player, list);
                        } else {
                            // Update existing lights
                            ArrayList<AreaLight> areaLightList = flashLightList2.get(player);
                            if (areaLightList != null && !areaLightList.isEmpty()) {
                                Quaternionf currentRot = new Quaternionf().rotateXYZ(
                                        (float) -Math.toRadians(player.getPitch(partialTicks)),
                                        (float) Math.toRadians(player.getYaw(partialTicks)),
                                        0.0f
                                );

                                float alpha = client.player != null && client.player.isSpectator() ?
                                        1.0f : smoothness * client.getLastFrameDuration();

                                for(AreaLight areaLight : areaLightList) {
                                    if (areaLight != null) {
                                        areaLight.getOrientation().slerp(currentRot, alpha);
                                        areaLight.setPosition(playerPos.getX(), playerPos.getY(), playerPos.getZ());
                                    }
                                }

                                if (lightCustomizer != null && areaLightList.size() >= 2) {
                                    FlashlightLightData lightData = new FlashlightLightData(
                                            player, areaLightList.get(0), areaLightList.get(1),
                                            playerPos, currentRot, false
                                    );
                                    lightCustomizer.accept(lightData);
                                }
                            }
                        }
                    } else {
                        tryToRemoveFlashlight(player);
                    }
                }
            }
        } finally {
            // Run post-render hook
            if (postRenderHook != null) {
                postRenderHook.run();
            }
        }
    }

    private void tryToRemoveFlashlight(AbstractClientPlayerEntity player){
        if (flashLightList2.containsKey(player) && flashLightList2.get(player) != null) {
            ArrayList<AreaLight> areaLightList = flashLightList2.get(player);
            for(AreaLight areaLight : areaLightList){
                if (areaLight != null) {
                    VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().removeLight(areaLight);
                }
            }
            flashLightList2.remove(player);
        }
    }

    /**
     * Clear all flashlight data but don't remove lights from renderer.
     * Use this when you want to reset the internal state without affecting rendering.
     */
    public void clearlights(){
        this.flashLightList2.clear();
    }

    /**
     * Properly remove all flashlights from both internal tracking and the light renderer.
     * This ensures no lights are left behind when disabling the system.
     */
    public void clearAllLights(){
        for (ArrayList<AreaLight> lightList : flashLightList2.values()) {
            if (lightList != null) {
                for (AreaLight light : lightList) {
                    if (light != null) {
                        VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().removeLight(light);
                    }
                }
            }
        }
        flashLightList2.clear();
    }

    /**
     * Remove flashlight for a specific player.
     * Useful for battery systems or player-specific disabling.
     *
     * @param player the player whose flashlight should be removed
     */
    public void removelightForPlayer(AbstractClientPlayerEntity player) {
        tryToRemoveFlashlight(player);
    }

    /**
     * Check if a player currently has an active flashlight.
     * tbh could be useful for future expansions
     *
     * @param player the player to check
     * @return true if the player has an active flashlight
     */
    public boolean hasActiveFlashlight(AbstractClientPlayerEntity player) {
        return flashLightList2.containsKey(player) && flashLightList2.get(player) != null;
    }

    /**
     * just some random debugging stuff
     */
    public int getActiveFlashlightCount() {
        return flashLightList2.size();
    }

    /**
     * Get current flashlight properties for debugging or display.
     *
     * @return array containing [brightness, distance, angle, smoothness]
     */
    public float[] getCurrentProperties() {
        return new float[]{brightness, distance, angle, smoothness};
    }
}

/*
 * (i feel like this could be useful if anyone wanna mae an addon XD and it's also useful for me...)
 * - Made by DarkFox
 * MODDER USAGE EXAMPLES:
 *
 * // Disable flashlight rendering (useful for battery systems)
 * flashlightRenderer.setEnabled(false);
 *
 * // Set custom flashlight properties (dimmer, shorter range)
 * flashlightRenderer.setFlashlightProperties(0.5f, 15.0f, 0.3f, 0.8f);
 *
 * // Only render flashlight for players with permission
 * flashlightRenderer.setPlayerRenderPredicate(player -> {
 *     return hasFlashlightPermission(player) && getBatteryLevel(player) > 0;
 * });
 *
 * // Customize light properties per player (e.g., colored lights, battery effects)
 * flashlightRenderer.setLightCustomizer(lightData -> {
 *     if (getBatteryLevel(lightData.player) < 20) {
 *         // Dim light when battery is low
 *         lightData.primaryLight.setBrightness(0.3f);
 *         lightData.secondaryLight.setBrightness(0.3f);
 *     }
 *     // Add colored light based on player
 *     if (isVIPPlayer(lightData.player)) {
 *         lightData.primaryLight.setColor(1.0f, 0.8f, 0.6f); // Warm light
 *     }
 * });
 *
 * // Add performance monitoring
 * flashlightRenderer.setRenderHooks(
 *     () -> startPerformanceTimer(),
 *     () -> endPerformanceTimer()
 * );
 */
