package com.example.mymod.levels;

import com.sp.cca_stuff.PlayerComponent;
import com.sp.init.BackroomsLevels;
import com.sp.init.ModSounds;
import com.sp.world.events.AbstractEvent;
import com.sp.world.levels.BackroomsLevel;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

/**
 * Example implementation of a custom Backrooms level.
 * This demonstrates the key concepts and patterns for creating your own levels.
 * 
 * Features demonstrated:
 * - Custom environmental properties
 * - Event system integration
 * - Transition mechanics
 * - Data persistence
 * - Player state management
 */
public class ExampleCustomLevel extends BackroomsLevel {
    
    // Level state variables
    private int eventCounter = 0;
    private boolean isInDarkPhase = false;
    private long lastEventTime = 0;
    
    // Constants for this level
    private static final int DARK_PHASE_DURATION = 600; // 30 seconds
    private static final int MIN_EVENT_INTERVAL = 1200; // 60 seconds
    
    // World registry key - must be unique
    public static final RegistryKey<World> EXAMPLE_WORLD_KEY = 
        RegistryKey.of(RegistryKeys.WORLD, 
                      new Identifier("example_mod", "example_level"));
    
    public ExampleCustomLevel() {
        super("example_level",                    // Unique level ID
              ExampleChunkGenerator.CODEC,        // Your chunk generator
              new Vec3d(8, 32, 8),               // Spawn position
              EXAMPLE_WORLD_KEY);                 // World key
    }
    
    @Override
    public void register() {
        // Always call super first
        super.register();
        
        // Register custom events
        this.registerEvents("darkness", ExampleDarknessEvent::new);
        this.registerEvents("whisper", ExampleWhisperEvent::new);
        this.registerEvents("flicker", ExampleFlickerEvent::new);
        
        // Register level transitions
        this.registerTransition(this::checkExitTransition, "exit_to_level0");
        this.registerTransition(this::checkSecretTransition, "secret_passage");
    }
    
    // Environmental properties
    @Override
    public boolean hasVanillaLighting() {
        return false; // Dark level requiring light sources
    }
    
    @Override
    public boolean rendersSky() {
        return false; // No sky rendering
    }
    
    @Override
    public boolean rendersClouds() {
        return false; // No clouds
    }
    
    // Event timing
    @Override
    public int nextEventDelay() {
        // Shorter delays during dark phase
        if (isInDarkPhase) {
            return 400 + random.nextInt(200); // 20-30 seconds
        }
        return 800 + random.nextInt(400); // 40-60 seconds
    }
    
    // Custom event selection
    @Override
    public AbstractEvent getRandomEvent(World world) {
        // Bias event selection based on level state
        if (isInDarkPhase) {
            // More intense events during dark phase
            return new ExampleDarknessEvent();
        }
        
        // Normal random selection
        return super.getRandomEvent(world);
    }
    
    // Transition logic
    private List<LevelTransition> checkExitTransition(World world, 
                                                     PlayerComponent player, 
                                                     BackroomsLevel from) {
        List<LevelTransition> transitions = new ArrayList<>();
        
        // Check if player is at the exit location
        Vec3d playerPos = player.player.getPos();
        if (playerPos.y < 16 && playerPos.distanceTo(new Vec3d(0, 16, 0)) < 3) {
            // Create transition back to Level 0
            transitions.add(getLevel0Transition(player));
        }
        
        return transitions;
    }
    
    private List<LevelTransition> checkSecretTransition(World world, 
                                                       PlayerComponent player, 
                                                       BackroomsLevel from) {
        List<LevelTransition> transitions = new ArrayList<>();
        
        // Secret transition requires specific conditions
        if (eventCounter >= 5 && 
            player.isFlashLightOn() && 
            player.getStamina() > 50) {
            
            Vec3d secretPos = new Vec3d(100, 32, 100);
            if (player.player.getPos().distanceTo(secretPos) < 2) {
                // Create custom transition with special effects
                transitions.add(createSecretTransition(player));
            }
        }
        
        return transitions;
    }
    
    private LevelTransition createSecretTransition(PlayerComponent player) {
        return new LevelTransition(
            120,                              // 6 second transition
            this::secretTransitionTick,       // Custom tick handler
            new CrossDimensionTeleport(       // Teleport to Level 2
                player,
                new Vec3d(0, 20, 8),
                this,
                BackroomsLevels.LEVEL2_BACKROOMS_LEVEL
            ),
            this::secretTransitionCancel     // Cancel handler
        );
    }
    
    private void secretTransitionTick(CrossDimensionTeleport teleport, int tick) {
        PlayerComponent player = teleport.playerComponent();
        
        // Progressive effects during transition
        if (tick > 100) {
            player.setShouldGlitch(true);
        }
        if (tick > 80) {
            player.setShouldDoStatic(true);
        }
        if (tick > 60) {
            player.setFlashLightOn(false);
        }
    }
    
    private void secretTransitionCancel(CrossDimensionTeleport teleport, int tick) {
        // Clean up if transition is cancelled
        PlayerComponent player = teleport.playerComponent();
        player.setShouldGlitch(false);
        player.setShouldDoStatic(false);
    }
    
    // Data persistence
    @Override
    public void writeToNbt(NbtCompound nbt) {
        nbt.putInt("eventCounter", eventCounter);
        nbt.putBoolean("isInDarkPhase", isInDarkPhase);
        nbt.putLong("lastEventTime", lastEventTime);
    }
    
    @Override
    public void readFromNbt(NbtCompound nbt) {
        eventCounter = nbt.getInt("eventCounter");
        isInDarkPhase = nbt.getBoolean("isInDarkPhase");
        lastEventTime = nbt.getLong("lastEventTime");
    }
    
    // Transition handlers
    @Override
    public void transitionIn(CrossDimensionTeleport teleport) {
        PlayerComponent player = teleport.playerComponent();
        
        // Apply entry effects
        player.setStamina(100); // Full stamina on entry
        player.setShouldDoStatic(false); // Clear any static
        
        // Play ambient sound
        if (player.player.getWorld().isClient) {
            player.player.playSound(ModSounds.LEVEL_AMBIENCE, 0.3f, 1.0f);
        }
        
        // Reset level state for new player
        if (eventCounter == 0) {
            isInDarkPhase = false;
            lastEventTime = player.player.getWorld().getTime();
        }
    }
    
    @Override
    public void transitionOut(CrossDimensionTeleport teleport) {
        PlayerComponent player = teleport.playerComponent();
        
        // Clean up player state
        player.setShouldGlitch(false);
        player.setShouldDoStatic(false);
        player.setShouldInflictGlitchDamage(false);
        
        // Mark level as completed
        eventCounter++;
        this.justChanged(); // Mark for synchronization
    }
    
    // Custom methods for level-specific logic
    public void enterDarkPhase(World world) {
        isInDarkPhase = true;
        lastEventTime = world.getTime();
        this.justChanged();
        
        // Notify all players in this level
        world.getPlayers().forEach(player -> {
            PlayerComponent component = 
                com.sp.cca_stuff.InitializeComponents.PLAYER.get(player);
            component.setShouldDoStatic(true);
        });
    }
    
    public void exitDarkPhase(World world) {
        isInDarkPhase = false;
        this.justChanged();
        
        // Clear effects from all players
        world.getPlayers().forEach(player -> {
            PlayerComponent component = 
                com.sp.cca_stuff.InitializeComponents.PLAYER.get(player);
            component.setShouldDoStatic(false);
        });
    }
    
    // Getters for other systems to use
    public boolean isInDarkPhase() {
        return isInDarkPhase;
    }
    
    public int getEventCounter() {
        return eventCounter;
    }
}

/**
 * Example custom event for the level
 */
class ExampleDarknessEvent extends AbstractEvent {
    
    @Override
    public void init(World world) {
        // Start darkness event
        if (BackroomsLevels.getLevel(world).orElse(null) instanceof ExampleCustomLevel level) {
            level.enterDarkPhase(world);
        }
        
        playDistantSound(world, ModSounds.LIGHTS_OUT);
    }
    
    @Override
    public void finish(World world) {
        super.finish(world);
        
        // End darkness event
        if (BackroomsLevels.getLevel(world).orElse(null) instanceof ExampleCustomLevel level) {
            level.exitDarkPhase(world);
        }
    }
    
    @Override
    public int duration() {
        return 600; // 30 seconds
    }
}

/**
 * Example whisper event
 */
class ExampleWhisperEvent extends AbstractEvent {
    
    @Override
    public void init(World world) {
        playSoundWithRandLocation(world, ModSounds.WHISPER, 32, 50);
    }
    
    @Override
    public int duration() {
        return 1; // Instant event
    }
}

/**
 * Example flicker event
 */
class ExampleFlickerEvent extends AbstractEvent {
    
    @Override
    public void init(World world) {
        playSound(world, ModSounds.LIGHT_FLICKER);
    }
    
    @Override
    public void ticks(int ticks, World world) {
        // Flicker effect every 10 ticks
        if (ticks % 10 == 0) {
            world.getPlayers().forEach(player -> {
                PlayerComponent component = 
                    com.sp.cca_stuff.InitializeComponents.PLAYER.get(player);
                component.setShouldDoStatic(ticks % 20 == 0);
            });
        }
    }
    
    @Override
    public int duration() {
        return 100; // 5 seconds
    }
}
