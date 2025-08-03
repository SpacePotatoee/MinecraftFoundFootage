# Configuration System

The SP-Backrooms mod uses MidnightConfig for configuration management, providing both client and server-side settings. This guide covers how to integrate with and extend the configuration system.

## Overview

The configuration system provides:
- **Client-side settings** for visual and audio preferences
- **Server-side settings** for gameplay mechanics
- **Automatic GUI generation** through ModMenu integration
- **Runtime configuration changes** with immediate effect
- **Extensible API** for custom mod settings

## Accessing Configuration

### Reading Configuration Values

```java
import com.sp.compat.modmenu.ConfigStuff;

// Access configuration values
boolean realCameraEnabled = ConfigStuff.enableRealCamera;
float rollMultiplier = ConfigStuff.lookRollMultiplier;
boolean enableGlitchEffects = ConfigStuff.enableGlitchEffects;
```

### Available Settings

```java
// Video settings
ConfigStuff.enableRealCamera          // Enable realistic camera movement
ConfigStuff.lookRollMultiplier        // Camera roll intensity (0-10)
ConfigStuff.enableSmoothCamera        // Smooth camera transitions
ConfigStuff.cameraSmoothing          // Smoothing factor (0-1)

// Visual effects
ConfigStuff.enableGlitchEffects      // Enable glitch effects
ConfigStuff.enableStaticEffects      // Enable static overlay
ConfigStuff.grassQuality             // Grass rendering quality

// Server settings
ConfigStuff.exitSpawnRadius          // Exit spawn radius
ConfigStuff.difficultyMultiplier     // Global difficulty modifier
```

## Creating Custom Configuration

### Basic Configuration Class

```java
public class MyModConfig extends MidnightConfig {
    public static final String CATEGORY = "my_mod";
    
    @Entry(category = CATEGORY)
    public static boolean enableFeature = true;
    
    @Entry(category = CATEGORY, isSlider = true, min = 0, max = 100)
    public static int intensity = 50;
    
    @Entry(category = CATEGORY)
    public static MyEnum setting = MyEnum.DEFAULT;
    
    public enum MyEnum {
        DISABLED, DEFAULT, ENHANCED
    }
}

// Initialize in your mod
MidnightConfig.init("my_mod", MyModConfig.class);
```

### ModMenu Integration

```java
@Environment(EnvType.CLIENT)
public class MyModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> MidnightConfig.getScreen(parent, "my_mod");
    }
}
```

## Runtime Configuration

### Reacting to Changes

```java
public class ConfigHandler {
    public static void onConfigChanged() {
        // Update systems based on config
        if (ConfigStuff.enableGlitchEffects) {
            enableGlitchSystem();
        }
        
        updateShaderSettings();
        reloadResources();
    }
}
```

### Dynamic Updates

```java
// Update config programmatically
ConfigStuff.difficultyMultiplier = 2.0f;
MidnightConfig.write("spb-revamped");
```

## Best Practices

1. **Use descriptive names** for configuration options
2. **Provide sensible defaults** for all settings
3. **Add validation** for critical values
4. **Cache frequently accessed** configuration values
5. **Handle configuration migration** for version updates
6. **Sync important settings** between client and server

## Integration Examples

### Using Config in Rendering

```java
public void render() {
    if (ConfigStuff.enableGlitchEffects) {
        float intensity = ConfigStuff.effectIntensity;
        applyGlitchEffect(intensity);
    }
}
```

### Server-Side Configuration

```java
public void spawnEntity() {
    if (ConfigStuff.enableEntitySpawning) {
        float difficulty = ConfigStuff.difficultyMultiplier;
        spawnWithDifficulty(difficulty);
    }
}
```
