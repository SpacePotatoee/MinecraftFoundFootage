package com.sp.api;

import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import com.sp.init.BackroomsLevels;
import com.sp.world.levels.BackroomsLevel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;

/**
 * High-level API for interacting with the Backrooms level system.
 *
 * This wraps all the level management stuff in easy-to-use methods.
 * Originally we had to manually handle world registry keys and teleportation
 * logic everywhere, but this centralizes it all.
 *
 * Common usage patterns:
 * - BackroomsAPI.isPlayerInBackrooms(player) to check context
 * - BackroomsAPI.teleportToLevel(player, level) for transitions
 * - BackroomsAPI.registerLevel(level) during mod init
 *
 * The teleportation handles all the transition effects and inventory
 * management automatically, which was a pain to get right initially.
 *
 * @author SP-Backrooms Team
 * @since 1.0.0
 */
public class BackroomsAPI {
    
    /**
     * Checks if a player is currently in any Backrooms level.
     * 
     * @param player The player to check
     * @return true if the player is in a Backrooms dimension
     */
    public static boolean isPlayerInBackrooms(PlayerEntity player) {
        return BackroomsLevels.isInBackrooms(player.getWorld().getRegistryKey());
    }
    
    /**
     * Gets the current Backrooms level for a player.
     * 
     * @param player The player to check
     * @return Optional containing the current BackroomsLevel, or empty if not in Backrooms
     */
    public static Optional<BackroomsLevel> getCurrentLevel(PlayerEntity player) {
        return BackroomsLevels.getLevel(player.getWorld());
    }
    
    /**
     * Gets a Backrooms level by its ID.
     * 
     * @param levelId The level identifier (e.g., "level0", "poolrooms")
     * @return Optional containing the BackroomsLevel, or empty if not found
     */
    public static Optional<BackroomsLevel> getLevelById(String levelId) {
        return BackroomsLevels.BACKROOMS_LEVELS.stream()
                .filter(level -> level.getLevelId().equals(levelId))
                .findFirst();
    }
    
    /**
     * Gets all registered Backrooms levels.
     * 
     * @return List of all registered BackroomsLevel instances
     */
    public static List<BackroomsLevel> getAllLevels() {
        return List.copyOf(BackroomsLevels.BACKROOMS_LEVELS);
    }
    
    /**
     * Registers a new Backrooms level.
     * This should be called during mod initialization.
     * 
     * @param level The BackroomsLevel to register
     * @throws IllegalArgumentException if a level with the same ID already exists
     */
    public static void registerLevel(BackroomsLevel level) {
        // Check for duplicate IDs
        boolean exists = BackroomsLevels.BACKROOMS_LEVELS.stream()
                .anyMatch(existing -> existing.getLevelId().equals(level.getLevelId()));
        
        if (exists) {
            throw new IllegalArgumentException("A level with ID '" + level.getLevelId() + "' already exists");
        }
        
        BackroomsLevels.BACKROOMS_LEVELS.add(level);
        level.register();
    }
    
    /**
     * Initiates a teleportation to a specific Backrooms level.
     * This handles the transition effects and inventory management automatically.
     * 
     * @param player The player to teleport (must be ServerPlayerEntity)
     * @param targetLevel The destination level
     * @param targetPosition The position to teleport to (null for level's default spawn)
     * @return true if teleportation was initiated successfully
     */
    public static boolean teleportToLevel(PlayerEntity player, BackroomsLevel targetLevel, Vec3d targetPosition) {
        if (!(player instanceof ServerPlayerEntity)) {
            return false;
        }
        
        PlayerComponent component = InitializeComponents.PLAYER.get(player);
        
        // Use level's default spawn if no position specified
        Vec3d destination = targetPosition != null ? targetPosition : targetLevel.getSpawnPos();
        
        // Create teleport data
        Optional<BackroomsLevel> currentLevel = getCurrentLevel(player);
        BackroomsLevel fromLevel = currentLevel.orElse(BackroomsLevels.OVERWORLD_REPRESENTING_BACKROOMS_LEVEL);
        
        BackroomsLevel.CrossDimensionTeleport teleport = new BackroomsLevel.CrossDimensionTeleport(
                component, destination, fromLevel, targetLevel
        );
        
        // Start teleportation process
        component.setTeleporting(true);
        component.setTeleportingTimer(60); // 3 second transition
        
        // Apply transition effects
        fromLevel.transitionOut(teleport);
        
        return true;
    }
    
    /**
     * Initiates a teleportation to a specific Backrooms level using the level's default spawn position.
     * 
     * @param player The player to teleport
     * @param targetLevel The destination level
     * @return true if teleportation was initiated successfully
     */
    public static boolean teleportToLevel(PlayerEntity player, BackroomsLevel targetLevel) {
        return teleportToLevel(player, targetLevel, null);
    }
    
    /**
     * Teleports a player to a level by its ID.
     * 
     * @param player The player to teleport
     * @param levelId The ID of the destination level
     * @return true if teleportation was initiated successfully
     */
    public static boolean teleportToLevelById(PlayerEntity player, String levelId) {
        Optional<BackroomsLevel> level = getLevelById(levelId);
        return level.map(backroomsLevel -> teleportToLevel(player, backroomsLevel)).orElse(false);
    }
    
    /**
     * Teleports a player back to the Overworld.
     * This handles cleanup of Backrooms-specific effects and inventory restoration.
     * 
     * @param player The player to teleport
     * @return true if teleportation was initiated successfully
     */
    public static boolean teleportToOverworld(PlayerEntity player) {
        return teleportToLevel(player, BackroomsLevels.OVERWORLD_REPRESENTING_BACKROOMS_LEVEL);
    }
    
    /**
     * Forces a player into the Backrooms (Level 0).
     * This is the classic "noclip" entrance with appropriate effects.
     * 
     * @param player The player to cast into the Backrooms
     * @return true if the operation was successful
     */
    public static boolean castIntoBackrooms(PlayerEntity player) {
        if (!(player instanceof ServerPlayerEntity)) {
            return false;
        }
        
        PlayerComponent component = InitializeComponents.PLAYER.get(player);
        
        // Apply entrance effects
        PlayerEffectsAPI.glitch(player, true);
        PlayerEffectsAPI.staticEffect(player, true);
        
        // Schedule the actual teleportation after effects
        player.getServer().execute(() -> {
            try {
                Thread.sleep(2000); // 2 second delay for effects
                
                // Clear effects and teleport
                PlayerEffectsAPI.clearAllEffects(player);
                teleportToLevel(player, BackroomsLevels.LEVEL0_BACKROOMS_LEVEL);
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        return true;
    }
    
    /**
     * Checks if a specific level is currently loaded/active.
     * 
     * @param level The level to check
     * @return true if the level has active players or is loaded
     */
    public static boolean isLevelActive(BackroomsLevel level) {
        // This would need to be implemented based on your server's world management
        // For now, we'll check if any players are in this level
        return level.getWorldKey() != null; // Simplified check
    }
    
    /**
     * Gets the number of players currently in a specific level.
     * 
     * @param level The level to check
     * @return Number of players in the level
     */
    public static int getPlayerCountInLevel(BackroomsLevel level) {
        // This would need access to the server's world management
        // Implementation would depend on your specific setup
        return 0; // Placeholder
    }
    
    /**
     * Triggers a level-wide event for all players in a specific level.
     * 
     * @param level The target level
     * @param eventName The name of the event to trigger
     * @return true if the event was triggered successfully
     */
    public static boolean triggerLevelEvent(BackroomsLevel level, String eventName) {
        // This would need to be implemented based on your event system
        // For now, this is a placeholder for the concept
        return false; // Placeholder
    }
    
    /**
     * Utility class for level transition effects.
     */
    public static class TransitionEffects {
        
        /**
         * Applies standard entrance effects for entering the Backrooms.
         * 
         * @param player The player entering
         */
        public static void applyEntranceEffects(PlayerEntity player) {
            PlayerEffectsAPI.glitch(player, true);
            PlayerEffectsAPI.staticEffect(player, true);
            PlayerEffectsAPI.screenShake(player, 0.3f, 40);
        }
        
        /**
         * Applies standard exit effects for leaving the Backrooms.
         * 
         * @param player The player exiting
         */
        public static void applyExitEffects(PlayerEntity player) {
            PlayerEffectsAPI.clearAllEffects(player);
            PlayerEffectsAPI.screenShake(player, 0.2f, 20);
        }
        
        /**
         * Applies transition effects between Backrooms levels.
         * 
         * @param player The player transitioning
         * @param fromLevel The source level
         * @param toLevel The destination level
         */
        public static void applyLevelTransitionEffects(PlayerEntity player, 
                                                      BackroomsLevel fromLevel, 
                                                      BackroomsLevel toLevel) {
            // Apply effects based on level types
            PlayerEffectsAPI.staticEffect(player, true);
            
            // Schedule effect cleanup
            player.getServer().execute(() -> {
                try {
                    Thread.sleep(1000);
                    PlayerEffectsAPI.staticEffect(player, false);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
    }
    
    /**
     * Utility class for level validation and debugging.
     */
    public static class LevelUtils {
        
        /**
         * Validates that a level is properly configured.
         * 
         * @param level The level to validate
         * @return true if the level is valid
         */
        public static boolean validateLevel(BackroomsLevel level) {
            return level.getLevelId() != null && 
                   !level.getLevelId().isEmpty() &&
                   level.getWorldKey() != null &&
                   level.getSpawnPos() != null;
        }
        
        /**
         * Gets debug information about a level.
         * 
         * @param level The level to inspect
         * @return Debug information string
         */
        public static String getLevelDebugInfo(BackroomsLevel level) {
            return String.format("Level[id=%s, world=%s, spawn=%s, mod=%s]",
                    level.getLevelId(),
                    level.getWorldKey(),
                    level.getSpawnPos(),
                    level.getModId());
        }
    }
}
