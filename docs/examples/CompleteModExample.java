package com.example.mymod;

import com.sp.api.BackroomsAPI;
import com.sp.api.PlayerEffectsAPI;
import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import com.sp.init.BackroomsLevels;
import com.sp.world.events.AbstractEvent;
import com.sp.world.levels.BackroomsLevel;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Complete example mod showing integration with SP-Backrooms APIs.
 * 
 * This example demonstrates:
 * - Custom level creation and registration
 * - Player effect management
 * - Event system usage
 * - Command integration
 * - Data persistence
 * - Best practices for modding with SP-Backrooms
 */
public class ExampleMod implements ModInitializer {
    
    public static final String MOD_ID = "example_mod";
    
    // Custom level registry key
    public static final RegistryKey<World> NIGHTMARE_WORLD_KEY = 
        RegistryKey.of(RegistryKeys.WORLD, new Identifier(MOD_ID, "nightmare_level"));
    
    // Custom level instance
    public static final BackroomsLevel NIGHTMARE_LEVEL = new NightmareLevel();
    
    // Player data tracking
    private static final Map<UUID, PlayerData> playerDataMap = new HashMap<>();
    
    @Override
    public void onInitialize() {
        // Register our custom level
        BackroomsAPI.registerLevel(NIGHTMARE_LEVEL);
        
        // Register commands
        CommandRegistrationCallback.EVENT.register(this::registerCommands);
        
        // Register server tick events for custom logic
        ServerTickEvents.END_SERVER_TICK.register(this::onServerTick);
        
        System.out.println("Example Backrooms mod initialized!");
    }
    
    private void registerCommands(CommandManager.RegistrationEnvironment environment,
                                 CommandRegistryAccess registryAccess,
                                 CommandManager.RegistrationEnvironment registrationEnvironment) {
        
        // Command to enter nightmare level
        CommandManager.literal("nightmare")
            .requires(source -> source.hasPermissionLevel(2))
            .executes(context -> {
                ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
                return enterNightmareLevel(player);
            })
            .build();
        
        // Command to apply custom effects
        CommandManager.literal("spook")
            .requires(source -> source.hasPermissionLevel(2))
            .executes(context -> {
                ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
                return applySpookEffects(player);
            })
            .build();
        
        // Command to check player status
        CommandManager.literal("status")
            .executes(context -> {
                ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
                return showPlayerStatus(player);
            })
            .build();
    }
    
    private int enterNightmareLevel(ServerPlayerEntity player) {
        if (BackroomsAPI.teleportToLevel(player, NIGHTMARE_LEVEL)) {
            player.sendMessage(Text.literal("Welcome to the Nightmare Level..."), false);
            
            // Track player entry
            getPlayerData(player.getUuid()).nightmareEntries++;
            
            return 1;
        } else {
            player.sendMessage(Text.literal("Failed to enter nightmare level"), false);
            return 0;
        }
    }
    
    private int applySpookEffects(ServerPlayerEntity player) {
        // Apply a sequence of effects
        PlayerEffectsAPI.glitch(player, true, false);
        PlayerEffectsAPI.screenShake(player, 0.5f, 60);

        // Track the effect sequence using player data
        PlayerData data = getPlayerData(player.getUuid());
        data.spookEffectStartTime = player.age;
        data.spookEffectStage = 1;

        player.sendMessage(Text.literal("Spook effects applied!"), false);
        return 1;
    }
    
    private int showPlayerStatus(ServerPlayerEntity player) {
        PlayerComponent component = InitializeComponents.PLAYER.get(player);
        PlayerData data = getPlayerData(player.getUuid());
        
        String status = String.format(
            "Player Status:\n" +
            "- In Backrooms: %s\n" +
            "- Current Level: %s\n" +
            "- Stamina: %d/100\n" +
            "- Flashlight: %s\n" +
            "- Nightmare Entries: %d\n" +
            "- Fear Level: %.2f",
            BackroomsAPI.isPlayerInBackrooms(player),
            BackroomsAPI.getCurrentLevel(player).map(BackroomsLevel::getLevelId).orElse("None"),
            PlayerEffectsAPI.getStamina(player),
            PlayerEffectsAPI.isFlashlightOn(player) ? "On" : "Off",
            data.nightmareEntries,
            data.fearLevel
        );
        
        player.sendMessage(Text.literal(status), false);
        return 1;
    }
    
    private void onServerTick(net.minecraft.server.MinecraftServer server) {
        // Custom logic that runs every server tick
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            updatePlayerEffects(player);
        }
    }
    
    private void updatePlayerEffects(ServerPlayerEntity player) {
        PlayerComponent component = InitializeComponents.PLAYER.get(player);
        PlayerData data = getPlayerData(player.getUuid());

        // Only apply effects in Backrooms
        if (!BackroomsAPI.isPlayerInBackrooms(player)) {
            return;
        }

        // Handle spook effect sequence using tick counters
        if (data.spookEffectStage > 0) {
            int elapsed = player.age - data.spookEffectStartTime;

            switch (data.spookEffectStage) {
                case 1 -> {
                    if (elapsed >= 40) { // 2 seconds
                        PlayerEffectsAPI.staticEffect(player, true);
                        data.spookEffectStage = 2;
                        data.staticEffectEndTime = player.age + 20; // 1 second duration

                        SPBRevamped.LOGGER.debug("Spook stage 2 for player {} at tick {}",
                                               player.getName().getString(), player.age);
                    }
                }
                case 2 -> {
                    if (elapsed >= 60) { // 3 seconds total
                        PlayerEffectsAPI.blackScreen(player, 40, true, false);
                        data.spookEffectStage = 3;

                        SPBRevamped.LOGGER.debug("Spook stage 3 for player {} at tick {}",
                                               player.getName().getString(), player.age);
                    }
                }
                case 3 -> {
                    if (elapsed >= 120) { // 6 seconds total
                        PlayerEffectsAPI.clearAllEffects(player);
                        data.spookEffectStage = 0;

                        SPBRevamped.LOGGER.debug("Spook effect completed for player {} at tick {}",
                                               player.getName().getString(), player.age);
                    }
                }
            }
        }

        // Increase fear over time in Backrooms
        data.fearLevel += 0.001f;
        data.fearLevel = Math.min(1.0f, data.fearLevel);

        // Apply effects based on fear level using tick intervals
        if (data.fearLevel > 0.5f && player.age % 200 == 0) { // Every 10 seconds
            PlayerEffectsAPI.staticEffect(player, true);
            data.staticEffectEndTime = player.age + 10; // Remove after 10 ticks

            SPBRevamped.LOGGER.debug("Fear static effect applied to {} (fear: {:.2f})",
                                   player.getName().getString(), data.fearLevel);
        }

        // Remove static effect when tick counter expires
        if (data.staticEffectEndTime > 0 && player.age >= data.staticEffectEndTime) {
            PlayerEffectsAPI.staticEffect(player, false);
            data.staticEffectEndTime = 0;
        }

        // High fear effects with tick-based timing
        if (data.fearLevel > 0.8f && player.age % 600 == 0) { // Every 30 seconds
            PlayerEffectsAPI.glitch(player, true, true);
            PlayerEffectsAPI.screenShake(player, 0.3f, 40);
            data.glitchEffectEndTime = player.age + 60; // Remove after 3 seconds

            SPBRevamped.LOGGER.warn("High fear glitch effect triggered for {} at tick {}",
                                  player.getName().getString(), player.age);
        }

        // Remove glitch effect when tick counter expires
        if (data.glitchEffectEndTime > 0 && player.age >= data.glitchEffectEndTime) {
            PlayerEffectsAPI.glitch(player, false, false);
            data.glitchEffectEndTime = 0;
        }
    }
    
    private PlayerData getPlayerData(UUID playerUuid) {
        return playerDataMap.computeIfAbsent(playerUuid, uuid -> new PlayerData());
    }
    
    /**
     * Custom Backrooms level implementation
     */
    public static class NightmareLevel extends BackroomsLevel {
        
        private int activeNightmares = 0;
        private boolean isNightmarePhase = false;
        
        public NightmareLevel() {
            super("nightmare", 
                  ExampleChunkGenerator.CODEC, // You'd need to implement this
                  new Vec3d(0, 32, 0), 
                  NIGHTMARE_WORLD_KEY);
        }
        
        @Override
        public void register() {
            super.register();
            
            // Register custom events
            this.registerEvents("nightmare_start", NightmareStartEvent::new);
            this.registerEvents("whispers", WhispersEvent::new);
            this.registerEvents("darkness", DarknessEvent::new);
            
            // Register transitions
            this.registerTransition(this::checkEscapeTransition, "escape");
        }
        
        @Override
        public boolean hasVanillaLighting() {
            return false; // Dark level
        }
        
        @Override
        public boolean rendersSky() {
            return false;
        }
        
        @Override
        public int nextEventDelay() {
            return isNightmarePhase ? 200 : 800; // Faster events during nightmare phase
        }
        
        private java.util.List<LevelTransition> checkEscapeTransition(World world, 
                                                                     PlayerComponent player, 
                                                                     BackroomsLevel from) {
            java.util.List<LevelTransition> transitions = new java.util.ArrayList<>();
            
            // Escape condition: survive 5 minutes and find the exit
            PlayerData data = getPlayerData(player.player.getUuid());
            if (data.timeInNightmare > 6000 && // 5 minutes
                player.player.getPos().distanceTo(new Vec3d(100, 32, 100)) < 3) {
                
                transitions.add(getLevel0Transition(player));
            }
            
            return transitions;
        }
        
        @Override
        public void writeToNbt(NbtCompound nbt) {
            nbt.putInt("activeNightmares", activeNightmares);
            nbt.putBoolean("isNightmarePhase", isNightmarePhase);
        }
        
        @Override
        public void readFromNbt(NbtCompound nbt) {
            activeNightmares = nbt.getInt("activeNightmares");
            isNightmarePhase = nbt.getBoolean("isNightmarePhase");
        }
        
        @Override
        public void transitionIn(CrossDimensionTeleport teleport) {
            PlayerComponent player = teleport.playerComponent();
            
            // Apply entrance effects
            PlayerEffectsAPI.blackScreen(player.player, 60, true, false);
            PlayerEffectsAPI.setStamina(player.player, 100);
            
            // Start tracking time
            PlayerData data = getPlayerData(player.player.getUuid());
            data.timeInNightmare = 0;
            data.fearLevel = 0.2f; // Start with some fear
        }
        
        @Override
        public void transitionOut(CrossDimensionTeleport teleport) {
            PlayerComponent player = teleport.playerComponent();
            
            // Clean up effects
            PlayerEffectsAPI.clearAllEffects(player.player);
            
            // Reset fear
            PlayerData data = getPlayerData(player.player.getUuid());
            data.fearLevel = 0.0f;
        }
        
        public void startNightmarePhase() {
            isNightmarePhase = true;
            activeNightmares++;
            this.justChanged();
        }
        
        public void endNightmarePhase() {
            isNightmarePhase = false;
            this.justChanged();
        }
    }
    
    /**
     * Custom events for the nightmare level
     */
    public static class NightmareStartEvent extends AbstractEvent {
        
        @Override
        public void init(World world) {
            if (BackroomsAPI.getCurrentLevel(world).orElse(null) instanceof NightmareLevel level) {
                level.startNightmarePhase();
                
                // Apply effects to all players in level
                world.getPlayers().forEach(player -> {
                    PlayerEffectsAPI.glitch(player, true, false);
                    PlayerEffectsAPI.screenShake(player, 0.4f, 80);
                });
            }
        }
        
        @Override
        public void finish(World world) {
            super.finish(world);
            if (BackroomsAPI.getCurrentLevel(world).orElse(null) instanceof NightmareLevel level) {
                level.endNightmarePhase();
            }
        }
        
        @Override
        public int duration() {
            return 1200; // 60 seconds
        }
    }
    
    public static class WhispersEvent extends AbstractEvent {
        
        @Override
        public void init(World world) {
            // Play whisper sounds to all players
            world.getPlayers().forEach(player -> {
                // You'd use your custom whisper sound here
                player.playSound(net.minecraft.sound.SoundEvents.AMBIENT_CAVE, 0.3f, 0.8f);
            });
        }
        
        @Override
        public int duration() {
            return 1; // Instant
        }
    }
    
    public static class DarknessEvent extends AbstractEvent {
        
        @Override
        public void init(World world) {
            world.getPlayers().forEach(player -> {
                PlayerEffectsAPI.setFlashlight(player, false);
                PlayerEffectsAPI.staticEffect(player, true);
            });
        }
        
        @Override
        public void finish(World world) {
            super.finish(world);
            world.getPlayers().forEach(player -> {
                PlayerEffectsAPI.staticEffect(player, false);
            });
        }
        
        @Override
        public int duration() {
            return 400; // 20 seconds
        }
    }
    
    /**
     * Player data tracking
     */
    public static class PlayerData {
        public int nightmareEntries = 0;
        public float fearLevel = 0.0f;
        public int timeInNightmare = 0;

        // Spook effect timing
        public int spookEffectStartTime = 0;
        public int spookEffectStage = 0;

        // Effect end times
        public int staticEffectEndTime = 0;
        public int glitchEffectEndTime = 0;

        // Add more tracking data as needed
    }
}
