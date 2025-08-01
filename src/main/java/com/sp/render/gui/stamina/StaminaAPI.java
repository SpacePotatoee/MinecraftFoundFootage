package com.sp.render.gui.stamina;

import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import com.sp.compat.modmenu.ConfigStuff;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Public API for accessing and modifying the stamina system.
 * This class provides a safe and stable interface for modders to interact with the stamina system.
 * 
 * @author SPBRevamped Team
 * @version 1.0
 */
public class StaminaAPI {
    
    /**
     * Maximum stamina value (default: 300)
     */
    public static final int MAX_STAMINA = 300;
    
    /**
     * Stamina threshold for being tired (default: 200)
     */
    public static final int TIRED_THRESHOLD = 200;
    
    /**
     * Gets the current stamina level of a player.
     * 
     * @param player The player to get stamina for
     * @return The current stamina level (0-300), or -1 if player is null or component not found
     */
    public static int getStamina(PlayerEntity player) {
        if (player == null) return -1;
        
        try {
            PlayerComponent component = InitializeComponents.PLAYER.get(player);
            return component.getStamina();
        } catch (Exception e) {
            return -1;
        }
    }
    
    /**
     * Sets the stamina level of a player.
     * 
     * @param player The player to set stamina for
     * @param stamina The stamina value to set (will be clamped to 0-300)
     * @return true if successful, false otherwise
     */
    public static boolean setStamina(PlayerEntity player, int stamina) {
        if (player == null) return false;
        
        try {
            PlayerComponent component = InitializeComponents.PLAYER.get(player);
            component.setStamina(Math.max(0, Math.min(MAX_STAMINA, stamina)));
            
            // Sync with client if on server
            if (player instanceof ServerPlayerEntity) {
                component.sync();
            }
            
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Adds stamina to a player.
     * 
     * @param player The player to add stamina to
     * @param amount The amount of stamina to add
     * @return true if successful, false otherwise
     */
    public static boolean addStamina(PlayerEntity player, int amount) {
        if (player == null) return false;
        
        int currentStamina = getStamina(player);
        if (currentStamina == -1) return false;
        
        return setStamina(player, currentStamina + amount);
    }
    
    /**
     * Removes stamina from a player.
     * 
     * @param player The player to remove stamina from
     * @param amount The amount of stamina to remove
     * @return true if successful, false otherwise
     */
    public static boolean removeStamina(PlayerEntity player, int amount) {
        return addStamina(player, -amount);
    }
    
    /**
     * Checks if a player is tired (stamina below threshold).
     * 
     * @param player The player to check
     * @return true if player is tired, false otherwise
     */
    public static boolean isTired(PlayerEntity player) {
        if (player == null) return false;
        
        try {
            PlayerComponent component = InitializeComponents.PLAYER.get(player);
            return component.isTired();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Sets the tired state of a player.
     * 
     * @param player The player to set tired state for
     * @param tired Whether the player should be tired
     * @return true if successful, false otherwise
     */
    public static boolean setTired(PlayerEntity player, boolean tired) {
        if (player == null) return false;
        
        try {
            PlayerComponent component = InitializeComponents.PLAYER.get(player);
            component.setTired(tired);
            
            // Sync with client if on server
            if (player instanceof ServerPlayerEntity) {
                component.sync();
            }
            
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Gets the stamina as a percentage (0.0 to 1.0).
     * 
     * @param player The player to get stamina percentage for
     * @return Stamina percentage, or -1.0 if error
     */
    public static float getStaminaPercentage(PlayerEntity player) {
        int stamina = getStamina(player);
        if (stamina == -1) return -1.0f;
        
        return (float) stamina / MAX_STAMINA;
    }
    
    /**
     * Gets the PlayerComponent for a player.
     * This provides direct access to the component for advanced usage.
     * 
     * @param player The player to get component for
     * @return The PlayerComponent, or null if not found
     */
    public static PlayerComponent getPlayerComponent(PlayerEntity player) {
        if (player == null) return null;
        
        try {
            return InitializeComponents.PLAYER.get(player);
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Checks if the stamina HUD is enabled in the configuration.
     * 
     * @return true if stamina HUD is enabled
     */
    public static boolean isStaminaHUDEnabled() {
        return ConfigStuff.showStaminaHUD;
    }
    
    /**
     * Gets the stamina HUD scale from configuration.
     * 
     * @return The HUD scale multiplier
     */
    public static float getStaminaHUDScale() {
        return ConfigStuff.staminaHUDScale;
    }
    
    /**
     * Gets the stamina HUD opacity from configuration.
     * 
     * @return The HUD opacity (0.0 to 1.0)
     */
    public static float getStaminaHUDOpacity() {
        return ConfigStuff.staminaHUDOpacity;
    }
    
    /**
     * Gets the stamina HUD X offset from configuration.
     * 
     * @return The X offset in pixels
     */
    public static int getStaminaHUDOffsetX() {
        return ConfigStuff.staminaHUDOffsetX;
    }
    
    /**
     * Gets the stamina HUD Y offset from configuration.
     * 
     * @return The Y offset in pixels
     */
    public static int getStaminaHUDOffsetY() {
        return ConfigStuff.staminaHUDOffsetY;
    }
}
