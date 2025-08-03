package com.sp.api;

import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import com.sp.networking.InitializePackets;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;

/**
 * High-level API for applying visual and audio effects to players.
 *
 * This was created after getting tired of manually handling PlayerComponent
 * synchronization everywhere. Now modders can just call simple methods
 * and everything works as expected.
 *
 * Usage is pretty straightforward:
 * - PlayerEffectsAPI.glitch(player, true) for glitch effects
 * - PlayerEffectsAPI.screenShake(player, 0.5f, 60) for camera shake
 * - PlayerEffectsAPI.startCutscene(player) for cutscenes
 *
 * All the networking and sync stuff is handled automatically, which
 * saves a lot of boilerplate code.
 *
 * @author SP-Backrooms Team
 * @since 1.0.0
 */
public class PlayerEffectsAPI {
    
    /**
     * Applies or removes a glitch effect on the player's screen.
     * This creates visual distortion and can optionally cause damage over time.
     * 
     * @param player The target player
     * @param enable Whether to enable or disable the glitch effect
     * @param causeDamage Whether the glitch should cause damage over time
     */
    public static void glitch(PlayerEntity player, boolean enable, boolean causeDamage) {
        PlayerComponent component = InitializeComponents.PLAYER.get(player);
        component.setShouldGlitch(enable);
        if (causeDamage) {
            component.setShouldInflictGlitchDamage(enable);
        }
        component.sync();
    }
    
    /**
     * Applies or removes a glitch effect on the player's screen.
     * This is a convenience method that doesn't cause damage.
     * 
     * @param player The target player
     * @param enable Whether to enable or disable the glitch effect
     */
    public static void glitch(PlayerEntity player, boolean enable) {
        glitch(player, enable, false);
    }
    
    /**
     * Applies or removes a static effect on the player's screen.
     * This creates a TV static-like visual overlay.
     * 
     * @param player The target player
     * @param enable Whether to enable or disable the static effect
     */
    public static void staticEffect(PlayerEntity player, boolean enable) {
        PlayerComponent component = InitializeComponents.PLAYER.get(player);
        component.setShouldDoStatic(enable);
        component.sync();
    }
    
    /**
     * Triggers a screen shake effect for the player.
     * This only works on the client side and requires the player to be online.
     * 
     * @param player The target player (must be ServerPlayerEntity)
     * @param intensity Shake intensity (0.0 to 1.0)
     * @param duration Duration in ticks (20 ticks = 1 second)
     */
    public static void screenShake(PlayerEntity player, float intensity, int duration) {
        if (!(player instanceof ServerPlayerEntity serverPlayer)) {
            return;
        }
        
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeFloat(Math.max(0.0f, Math.min(1.0f, intensity)));
        buf.writeInt(Math.max(0, duration));
        
        ServerPlayNetworking.send(serverPlayer, InitializePackets.SCREEN_SHAKE, buf);
    }
    
    /**
     * Triggers a black screen effect for the player.
     * This completely blacks out the player's screen for the specified duration.
     * 
     * @param player The target player (must be ServerPlayerEntity)
     * @param duration Duration in ticks
     * @param pauseSounds Whether to pause ambient sounds during the effect
     * @param allowEscape Whether the player can escape the effect early
     */
    public static void blackScreen(PlayerEntity player, int duration, boolean pauseSounds, boolean allowEscape) {
        if (!(player instanceof ServerPlayerEntity serverPlayer)) {
            return;
        }
        
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(Math.max(0, duration));
        buf.writeBoolean(pauseSounds);
        buf.writeBoolean(allowEscape);
        
        ServerPlayNetworking.send(serverPlayer, InitializePackets.BLACK_SCREEN, buf);
    }
    
    /**
     * Starts a cutscene for the player.
     * This disables player input and can enable noclip mode.
     * 
     * @param player The target player
     * @param enableNoclip Whether to enable noclip during the cutscene
     * @param hidePlayer Whether to hide the player model during the cutscene
     */
    public static void startCutscene(PlayerEntity player, boolean enableNoclip, boolean hidePlayer) {
        PlayerComponent component = InitializeComponents.PLAYER.get(player);
        component.setDoingCutscene(true);
        component.setShouldNoClip(enableNoclip);
        component.setShouldRender(!hidePlayer);
        component.sync();
    }
    
    /**
     * Starts a cutscene for the player with default settings.
     * This enables noclip and hides the player model.
     * 
     * @param player The target player
     */
    public static void startCutscene(PlayerEntity player) {
        startCutscene(player, true, false);
    }
    
    /**
     * Ends a cutscene for the player.
     * This restores normal player control and visibility.
     * 
     * @param player The target player
     */
    public static void endCutscene(PlayerEntity player) {
        PlayerComponent component = InitializeComponents.PLAYER.get(player);
        component.setDoingCutscene(false);
        component.setShouldNoClip(false);
        component.setShouldRender(true);
        component.sync();
    }
    
    /**
     * Controls the player's flashlight state.
     * 
     * @param player The target player
     * @param enabled Whether the flashlight should be on or off
     */
    public static void setFlashlight(PlayerEntity player, boolean enabled) {
        PlayerComponent component = InitializeComponents.PLAYER.get(player);
        component.setFlashLightOn(enabled);
        component.sync();
    }
    
    /**
     * Modifies the player's stamina.
     * 
     * @param player The target player
     * @param stamina New stamina value (0-100)
     */
    public static void setStamina(PlayerEntity player, int stamina) {
        PlayerComponent component = InitializeComponents.PLAYER.get(player);
        component.setStamina(Math.max(0, Math.min(100, stamina)));
        component.sync();
    }
    
    /**
     * Adds or removes stamina from the player.
     * 
     * @param player The target player
     * @param amount Amount to add (positive) or remove (negative)
     */
    public static void modifyStamina(PlayerEntity player, int amount) {
        PlayerComponent component = InitializeComponents.PLAYER.get(player);
        int currentStamina = component.getStamina();
        setStamina(player, currentStamina + amount);
    }
    
    /**
     * Sets the player's exhaustion state.
     * Exhausted players move slower and cannot sprint.
     * 
     * @param player The target player
     * @param tired Whether the player should be tired
     */
    public static void setTired(PlayerEntity player, boolean tired) {
        PlayerComponent component = InitializeComponents.PLAYER.get(player);
        component.setTired(tired);
        component.sync();
    }
    
    /**
     * Makes the player visible or invisible to entities.
     * This affects entity AI targeting and detection.
     * 
     * @param player The target player
     * @param visible Whether the player should be visible to entities
     */
    public static void setVisibleToEntities(PlayerEntity player, boolean visible) {
        PlayerComponent component = InitializeComponents.PLAYER.get(player);
        component.setVisibleToEntity(visible);
        component.sync();
    }
    
    /**
     * Checks if the player is currently in a Backrooms level.
     * 
     * @param player The player to check
     * @return true if the player is in any Backrooms dimension
     */
    public static boolean isInBackrooms(PlayerEntity player) {
        PlayerComponent component = InitializeComponents.PLAYER.get(player);
        return component.isInBackrooms();
    }
    
    /**
     * Gets the player's current stamina level.
     * 
     * @param player The player to check
     * @return Current stamina (0-100)
     */
    public static int getStamina(PlayerEntity player) {
        PlayerComponent component = InitializeComponents.PLAYER.get(player);
        return component.getStamina();
    }
    
    /**
     * Checks if the player's flashlight is currently on.
     * 
     * @param player The player to check
     * @return true if the flashlight is on
     */
    public static boolean isFlashlightOn(PlayerEntity player) {
        PlayerComponent component = InitializeComponents.PLAYER.get(player);
        return component.isFlashLightOn();
    }
    
    /**
     * Checks if the player is currently in a cutscene.
     * 
     * @param player The player to check
     * @return true if the player is in a cutscene
     */
    public static boolean isInCutscene(PlayerEntity player) {
        PlayerComponent component = InitializeComponents.PLAYER.get(player);
        return component.isDoingCutscene();
    }
    
    /**
     * Resets all visual effects for the player.
     * This is useful for cleaning up after complex sequences.
     * 
     * @param player The target player
     */
    public static void clearAllEffects(PlayerEntity player) {
        PlayerComponent component = InitializeComponents.PLAYER.get(player);
        component.setShouldGlitch(false);
        component.setShouldDoStatic(false);
        component.setShouldInflictGlitchDamage(false);
        component.setDoingCutscene(false);
        component.setShouldNoClip(false);
        component.setShouldRender(true);
        component.sync();
    }
}
