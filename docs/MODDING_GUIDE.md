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
    
    // Schedule cleanup
    Scheduler.schedule(() -> {
        PlayerEffectsAPI.clearAllEffects(player);
    }, 3000); // 3 seconds
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

### 1. Performance Optimization
- **Use LOD systems** for complex rendering
- **Cache frequently accessed data** to avoid repeated calculations
- **Implement proper cleanup** for resources and timers
- **Profile your code** to identify bottlenecks

### 2. Code Organization
- **Separate client and server logic** clearly
- **Use meaningful package structure** for organization
- **Document your APIs** for other modders
- **Follow consistent naming conventions**

### 3. Player Experience
- **Respect user settings** and configuration
- **Provide clear feedback** for player actions
- **Handle edge cases gracefully** (disconnections, errors)
- **Test in multiplayer environments**

### 4. Compatibility
- **Check for mod conflicts** during development
- **Use proper version constraints** in dependencies
- **Handle missing dependencies** gracefully
- **Test with different mod combinations**

## Common Patterns

### 1. Timed Effects
```java
public class TimedEffect {
    public static void applyTemporaryEffect(PlayerEntity player, int duration) {
        PlayerEffectsAPI.glitch(player, true);
        
        // Schedule cleanup
        player.getServer().execute(() -> {
            try {
                Thread.sleep(duration * 50); // Convert ticks to milliseconds
                PlayerEffectsAPI.glitch(player, false);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }
}
```

### 2. Conditional Logic
```java
public class ConditionalEffects {
    public static void applyContextualEffect(PlayerEntity player) {
        if (!BackroomsAPI.isPlayerInBackrooms(player)) {
            return; // Only apply in Backrooms
        }
        
        PlayerComponent component = InitializeComponents.PLAYER.get(player);
        if (component.isDoingCutscene()) {
            return; // Don't interfere with cutscenes
        }
        
        // Apply effect
        PlayerEffectsAPI.staticEffect(player, true);
    }
}
```

### 3. Data Persistence
```java
public class PersistentData {
    private static final Map<UUID, PlayerData> playerData = new HashMap<>();
    
    public static PlayerData getPlayerData(PlayerEntity player) {
        return playerData.computeIfAbsent(player.getUuid(), uuid -> new PlayerData());
    }
    
    public static void savePlayerData(PlayerEntity player, NbtCompound nbt) {
        PlayerData data = getPlayerData(player);
        data.writeToNbt(nbt);
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
// Enable debug logging
SPBRevamped.LOGGER.info("Custom effect applied to player: {}", player.getName());

// Check component state
PlayerComponent component = InitializeComponents.PLAYER.get(player);
SPBRevamped.LOGGER.debug("Player state - Glitch: {}, Static: {}", 
                        component.shouldGlitch(), component.shouldDoStatic());

// Validate configuration
if (!ConfigStuff.enableGlitchEffects) {
    SPBRevamped.LOGGER.warn("Glitch effects disabled in configuration");
}
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
