# SP-Backrooms Modding Guide

This comprehensive guide will help you create amazing extensions for the SP-Backrooms mod. Whether you want to add new levels, entities, or visual effects, this guide covers everything you need to know.

## Getting Started

### Development Environment Setup

1. **Set up your mod project** with Fabric template
2. **Add SP-Backrooms as dependency** in `build.gradle`:
   ```gradle
   dependencies {
       modImplementation "com.sp:spb-revamped:${project.spb_version}"
   }
   ```
3. **Import required packages**:
   ```java
   import com.sp.api.*;
   import com.sp.cca_stuff.*;
   import com.sp.world.levels.*;
   ```

### Basic Mod Structure

```java
public class MyBackroomsMod implements ModInitializer {
    public static final String MOD_ID = "my_backrooms_mod";
    
    @Override
    public void onInitialize() {
        // Register custom levels
        BackroomsAPI.registerLevel(new MyCustomLevel());
        
        // Register commands
        CommandRegistrationCallback.EVENT.register(this::registerCommands);
        
        // Initialize systems
        initializeCustomSystems();
    }
}
```

## Core Concepts

### 1. PlayerComponent System
The heart of player state management:
- **Automatic synchronization** between client and server
- **Persistent data** across sessions
- **Event-driven updates** for reactive programming
- **Extensible design** for custom functionality

### 2. Level System
Create immersive Backrooms dimensions:
- **Custom world generation** with chunk generators
- **Event system** for dynamic experiences
- **Transition mechanics** between levels
- **Environmental properties** (lighting, sky, fog)

### 3. Entity Framework
Build terrifying creatures:
- **Component-based architecture** for modular behavior
- **IK animation system** for realistic movement
- **GeckoLib integration** for advanced animations
- **AI goal system** for complex behaviors

### 4. Visual Effects
Enhance the horror experience:
- **Screen effects** (glitch, static, distortion)
- **Post-processing pipeline** with custom shaders
- **PBR materials** for realistic surfaces
- **Dynamic lighting** and shadows

## Quick Start Examples

### 1. Simple Player Effect

```java
// Apply a glitch effect to a player
public void scarePlayer(PlayerEntity player) {
    PlayerEffectsAPI.glitch(player, true);
    PlayerEffectsAPI.screenShake(player, 0.5f, 60);

    // Use tick counter for cleanup (3 seconds = 60 ticks)
    PlayerData data = getPlayerData(player.getUuid());
    data.glitchEndTick = player.age + 60;

    // Log for debugging
    SPBRevamped.LOGGER.debug("Applied scare effect to player {} at tick {}",
                            player.getName().getString(), player.age);
}
```

### 2. Basic Custom Level

```java
public class MyBackroomsLevel extends BackroomsLevel {
    public MyBackroomsLevel() {
        super("backrooms_level", MyChunkGenerator.CODEC, 
              new Vec3d(0, 32, 0), MY_WORLD_KEY);
    }
    
    @Override
    public void register() {
        super.register();
        this.registerEvents("scare", ScareEvent::new);
    }
    
    @Override
    public void transitionIn(CrossDimensionTeleport teleport) {
        PlayerEffectsAPI.blackScreen(teleport.playerComponent().player, 
                                   60, true, false);
    }
}
```

### 3. Simple Custom Entity

```java
public class MyScaryEntity extends HostileEntity {
    public MyScaryEntity(EntityType<? extends HostileEntity> type, World world) {
        super(type, world);
    }
    
    @Override
    public void tick() {
        super.tick();
        
        // Affect nearby players
        PlayerEntity nearestPlayer = getWorld().getClosestPlayer(this, 8.0);
        if (nearestPlayer != null) {
            PlayerEffectsAPI.staticEffect(nearestPlayer, true);
        }
    }
}
```

## Advanced Topics

### Custom Component Development

```java
public class MyPlayerComponent implements AutoSyncedComponent {
    private int customValue = 0;
    private boolean customState = false;
    
    @Override
    public void writeToNbt(NbtCompound nbt) {
        nbt.putInt("customValue", customValue);
        nbt.putBoolean("customState", customState);
    }
    
    @Override
    public void readFromNbt(NbtCompound nbt) {
        customValue = nbt.getInt("customValue");
        customState = nbt.getBoolean("customState");
    }
    
    // Getters and setters with sync calls
    public void setCustomValue(int value) {
        this.customValue = value;
        sync();
    }
}
```

### Advanced Level Features

```java
public class AdvancedLevel extends BackroomsLevel {
    private LevelState currentState = LevelState.NORMAL;
    
    @Override
    public AbstractEvent getRandomEvent(World world) {
        // Custom event selection based on level state
        return switch (currentState) {
            case NORMAL -> new NormalEvent();
            case DANGEROUS -> new DangerousEvent();
            case NIGHTMARE -> new NightmareEvent();
        };
    }
    
    @Override
    public List<LevelTransition> checkForTransition(PlayerComponent player, World world) {
        // Complex transition logic
        if (shouldTriggerSpecialTransition(player)) {
            return List.of(createSpecialTransition(player));
        }
        return super.checkForTransition(player, world);
    }
}
```

### Custom Rendering Effects

```java
public class MyCustomRenderer {
    public static void renderCustomEffect(PlayerComponent player) {
        if (!shouldRenderEffect(player)) return;
        
        ShaderProgram shader = VeilRenderSystem.setShader(MY_CUSTOM_SHADER);
        if (shader != null) {
            shader.setFloat("intensity", calculateIntensity(player));
            shader.setFloat("time", RenderSystem.getShaderGameTime());
            
            renderFullscreenQuad();
        }
    }
}
```

## Best Practices

### 1. Tick-Based Timing (CRITICAL)
- **ALWAYS use tick counters** instead of sleep() or schedulers
- **Use player.age or entity.age** for consistent timing across saves/loads
- **Store end ticks in persistent data** for effect management
- **Use modulo operations** for interval-based events (e.g., `player.age % 200 == 0`)

```java
// ✅ CORRECT: Tick-based timing
public void applyEffect(PlayerEntity player, int durationTicks) {
    PlayerData data = getPlayerData(player.getUuid());
    data.effectEndTick = player.age + durationTicks;

    // Check in tick method
    if (data.effectEndTick > 0 && player.age >= data.effectEndTick) {
        removeEffect(player);
        data.effectEndTick = 0;
    }
}

// ❌ WRONG: Never use sleep or schedulers
public void applyEffect(PlayerEntity player) {
    // DON'T DO THIS
    Thread.sleep(3000);
    Scheduler.schedule(() -> removeEffect(player), 3000);
}
```

### 2. Logging Breakpoints
- **Use logging instead of print statements** for debugging
- **Add breakpoints at key decision points** for easier debugging
- **Include relevant context** (player name, tick, values)
- **Use appropriate log levels** (DEBUG, INFO, WARN, ERROR)

```java
// ✅ CORRECT: Proper logging breakpoints
public void checkPlayerState(PlayerEntity player) {
    SPBRevamped.LOGGER.debug("Checking player state for {} at tick {}",
                            player.getName().getString(), player.age);

    if (condition) {
        SPBRevamped.LOGGER.debug("Condition met, applying effect");
        applyEffect(player);
    } else {
        SPBRevamped.LOGGER.debug("Condition not met, skipping");
    }
}
```

### 3. Performance Optimization
- **Cache frequently accessed data** to avoid repeated calculations
- **Use tick intervals** for expensive operations (e.g., every 20 ticks)
- **Implement proper cleanup** for resources and timers
- **Profile your code** to identify bottlenecks

### 4. Code Organization
- **Separate client and server logic** clearly
- **Use meaningful package structure** for organization
- **Document your APIs** for other modders
- **Follow consistent naming conventions**

### 5. Player Experience
- **Respect user settings** and configuration
- **Provide clear feedback** for player actions
- **Handle edge cases gracefully** (disconnections, errors)
- **Test in multiplayer environments**

### 6. Compatibility
- **Check for mod conflicts** during development
- **Use proper version constraints** in dependencies
- **Handle missing dependencies** gracefully
- **Test with different mod combinations**

## Common Patterns

### 1. Tick-Based Effects
```java
public class TickBasedEffects {

    public static void applyTemporaryGlitch(PlayerEntity player, int durationTicks) {
        PlayerEffectsAPI.glitch(player, true);

        // Store end tick in player data
        PlayerData data = getPlayerData(player.getUuid());
        data.glitchEndTick = player.age + durationTicks;

        // Log breakpoint for debugging
        SPBRevamped.LOGGER.debug("Glitch effect started for {} until tick {}",
                                player.getName().getString(), data.glitchEndTick);
    }

    // Call this from your main tick handler
    public static void tickPlayerEffects(PlayerEntity player) {
        PlayerData data = getPlayerData(player.getUuid());

        // Check if glitch effect should end
        if (data.glitchEndTick > 0 && player.age >= data.glitchEndTick) {
            PlayerEffectsAPI.glitch(player, false);
            data.glitchEndTick = 0;

            // Log breakpoint
            SPBRevamped.LOGGER.debug("Glitch effect ended for {} at tick {}",
                                    player.getName().getString(), player.age);
        }

        // Check other timed effects
        if (data.staticEndTick > 0 && player.age >= data.staticEndTick) {
            PlayerEffectsAPI.staticEffect(player, false);
            data.staticEndTick = 0;
        }
    }
}
```

### 2. Conditional Logic with Logging
```java
public class ConditionalEffects {
    public static void applyContextualEffect(PlayerEntity player) {
        // Log entry point
        SPBRevamped.LOGGER.debug("Checking contextual effect for player {}",
                                player.getName().getString());

        if (!BackroomsAPI.isPlayerInBackrooms(player)) {
            SPBRevamped.LOGGER.debug("Player {} not in Backrooms, skipping effect",
                                    player.getName().getString());
            return;
        }

        PlayerComponent component = InitializeComponents.PLAYER.get(player);
        if (component.isDoingCutscene()) {
            SPBRevamped.LOGGER.debug("Player {} in cutscene, skipping effect",
                                    player.getName().getString());
            return;
        }

        // Apply effect with tick counter
        PlayerEffectsAPI.staticEffect(player, true);
        PlayerData data = getPlayerData(player.getUuid());
        data.staticEndTick = player.age + 100; // 5 seconds

        SPBRevamped.LOGGER.debug("Applied static effect to {} until tick {}",
                                player.getName().getString(), data.staticEndTick);
    }
}
```

### 3. Data Persistence with Tick Counters
```java
public class PersistentData {
    private static final Map<UUID, PlayerData> playerData = new HashMap<>();

    public static PlayerData getPlayerData(PlayerEntity player) {
        return playerData.computeIfAbsent(player.getUuid(), uuid -> {
            PlayerData data = new PlayerData();
            SPBRevamped.LOGGER.debug("Created new PlayerData for {}",
                                    player.getName().getString());
            return data;
        });
    }

    public static void savePlayerData(PlayerEntity player, NbtCompound nbt) {
        PlayerData data = getPlayerData(player);
        data.writeToNbt(nbt);

        SPBRevamped.LOGGER.debug("Saved PlayerData for {} with {} active effects",
                                player.getName().getString(), data.getActiveEffectCount());
    }

    public static class PlayerData {
        public int glitchEndTick = 0;
        public int staticEndTick = 0;
        public int fearLevel = 0;
        public int lastEventTick = 0;

        public int getActiveEffectCount() {
            int count = 0;
            if (glitchEndTick > 0) count++;
            if (staticEndTick > 0) count++;
            return count;
        }

        public void writeToNbt(NbtCompound nbt) {
            nbt.putInt("glitchEndTick", glitchEndTick);
            nbt.putInt("staticEndTick", staticEndTick);
            nbt.putInt("fearLevel", fearLevel);
            nbt.putInt("lastEventTick", lastEventTick);
        }

        public void readFromNbt(NbtCompound nbt) {
            glitchEndTick = nbt.getInt("glitchEndTick");
            staticEndTick = nbt.getInt("staticEndTick");
            fearLevel = nbt.getInt("fearLevel");
            lastEventTick = nbt.getInt("lastEventTick");
        }
    }
}
```

## Troubleshooting

### Common Issues

1. **Effects not applying**: Check if player is in correct context (Backrooms, not in cutscene)
2. **Synchronization problems**: Ensure you call `sync()` after modifying component state
3. **Performance issues**: Implement proper caching and LOD systems
4. **Rendering problems**: Verify shader availability and OpenGL context

### Debugging Tips

```java
// Proper logging breakpoints with context
SPBRevamped.LOGGER.debug("=== Effect Application Debug ===");
SPBRevamped.LOGGER.debug("Player: {}, Tick: {}, Age: {}",
                        player.getName().getString(),
                        player.getWorld().getTime(),
                        player.age);

// Check component state with tick information
PlayerComponent component = InitializeComponents.PLAYER.get(player);
SPBRevamped.LOGGER.debug("Player state - Glitch: {}, Static: {}, InBackrooms: {}",
                        component.shouldGlitch(),
                        component.shouldDoStatic(),
                        component.isInBackrooms());

// Log tick-based timing
PlayerData data = getPlayerData(player.getUuid());
SPBRevamped.LOGGER.debug("Effect timers - Glitch ends: {}, Static ends: {}, Current tick: {}",
                        data.glitchEndTick,
                        data.staticEndTick,
                        player.age);

// Validate configuration with context
if (!ConfigStuff.enableGlitchEffects) {
    SPBRevamped.LOGGER.warn("Glitch effects disabled in configuration for player {}",
                           player.getName().getString());
}

// Log performance metrics
long startTime = System.nanoTime();
performExpensiveOperation();
long duration = System.nanoTime() - startTime;
SPBRevamped.LOGGER.debug("Operation took {} ns ({} ms)", duration, duration / 1_000_000);
```

### Testing Checklist

- [ ] Test in single-player and multiplayer
- [ ] Verify effects work with different configurations
- [ ] Check compatibility with other Backrooms levels
- [ ] Test edge cases (player disconnection, world reload)
- [ ] Validate performance with multiple players
- [ ] Ensure proper cleanup of resources

## Resources

- **API Documentation**: Complete reference for all available APIs
- **Example Code**: Working examples for common use cases
- **Community Discord**: Get help from other modders
- **Source Code**: Study the main mod's implementation
- **Video Tutorials**: Visual guides for complex topics

## Contributing

We welcome contributions to the SP-Backrooms ecosystem:

1. **Submit bug reports** for issues you encounter
2. **Share your creations** with the community
3. **Contribute to documentation** improvements
4. **Help other modders** in community channels
5. **Suggest new features** for the core mod

---

*Happy modding! And create something good and that fits in.* 👻
