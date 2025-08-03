# PlayerComponent API

The `PlayerComponent` is the central system for managing player state in the Backrooms mod. It handles everything from stamina to visual effects and provides a comprehensive API for modders.

## Overview

The PlayerComponent is attached to every player and manages:
- **Stamina System**: Movement and exhaustion mechanics
- **Flashlight**: Light source management
- **Visual Effects**: Glitch effects, static, screen distortion
- **Entity Interactions**: SkinWalker detection, Smiler encounters
- **Level Management**: Backrooms transitions and inventory handling
- **Audio Integration**: Voice chat and sound effects

## Getting the Component

```java
// Get player component for any player
PlayerComponent component = InitializeComponents.PLAYER.get(player);

// In your entity or system
PlayerEntity player = // ... get player
PlayerComponent playerComp = InitializeComponents.PLAYER.get(player);
```

## Core API Methods

### Stamina System

```java
// Stamina management
int getStamina()                    // Current stamina (0-100)
void setStamina(int stamina)        // Set stamina value
boolean isTired()                   // Is player exhausted?
void setTired(boolean tired)        // Set exhaustion state

// Constants
int DEFAULT_MAX_STAMINA = 100       // Maximum stamina value
```

### Flashlight System

```java
// Flashlight control
boolean isFlashLightOn()            // Is flashlight active?
void setFlashLightOn(boolean on)    // Toggle flashlight
```

### Visual Effects

```java
// Screen effects
boolean shouldGlitch()              // Should render glitch effect?
void setShouldGlitch(boolean glitch) // Enable/disable glitch
boolean shouldDoStatic()            // Should render static?
void setShouldDoStatic(boolean static) // Enable/disable static

// Rendering control
boolean shouldRender()              // Should render player?
void setShouldRender(boolean render) // Show/hide player
```

### Cutscene System

```java
// Cutscene management
boolean isDoingCutscene()           // Is player in cutscene?
void setDoingCutscene(boolean cutscene) // Start/stop cutscene
boolean shouldNoClip()              // Should player noclip?
void setShouldNoClip(boolean noclip) // Enable/disable noclip
```

### Entity Interactions

```java
// SkinWalker system
boolean canSeeActiveSkinWalker()    // Can see active SkinWalker?
void setCanSeeActiveSkinWalkerTarget(boolean canSee) // Set visibility

// Smiler system
boolean shouldInflictGlitchDamage() // Should take glitch damage?
void setShouldInflictGlitchDamage(boolean damage) // Enable damage

// General entity targeting
Entity getTargetEntity()            // Get current target
void setTargetEntity(Entity entity) // Set target entity
```

### Level Management

```java
// Backrooms utilities
boolean isInBackrooms()             // Is player in Backrooms?
Optional<BackroomsLevel> getCurrentBackroomsLevel() // Get current level

// Teleportation
boolean isTeleporting()             // Is currently teleporting?
void setTeleporting(boolean teleporting) // Set teleport state
int getTeleportingTimer()           // Get teleport timer
void setTeleportingTimer(int timer) // Set teleport timer
```

## Advanced Features

### Voice Chat Integration

```java
// Voice system
boolean isSpeaking()                // Is player speaking?
void setSpeaking(boolean speaking)  // Set speaking state
boolean shouldBeMuted()             // Should be muted?
void setShouldBeMuted(boolean muted) // Mute/unmute player
boolean isTalkingTooLoud()          // Is talking too loud?
```

### Visibility System

```java
// Entity visibility mechanics
boolean isVisibleToEntity()         // Is visible to entities?
void setVisibleToEntity(boolean visible) // Set visibility
int getVisibilityTimer()            // Get visibility timer
```

## Utility Methods

```java
// State management
void resetAllTimers()               // Reset all internal timers
void resetAllStates()               // Reset all boolean states
void sync()                         // Synchronize with client

// Convenience methods
void justChanged()                  // Mark for synchronization
```

## Events and Callbacks

The PlayerComponent automatically handles:
- **Client Tick**: Updates visual effects and input
- **Server Tick**: Manages game logic and synchronization
- **NBT Serialization**: Saves/loads persistent data
- **Network Sync**: Keeps client and server in sync

## Example Usage

```java
public class MyCustomSystem {
    
    public void handlePlayerEffect(PlayerEntity player) {
        PlayerComponent component = InitializeComponents.PLAYER.get(player);
        
        // Check if player is in Backrooms
        if (component.isInBackrooms()) {
            // Apply custom effect
            component.setShouldGlitch(true);
            component.setStamina(component.getStamina() - 5);
            
            // Sync changes
            component.sync();
        }
    }
    
    public void createCustomCutscene(PlayerEntity player) {
        PlayerComponent component = InitializeComponents.PLAYER.get(player);
        
        // Start cutscene
        component.setDoingCutscene(true);
        component.setShouldNoClip(true);
        component.setShouldRender(false);
        
        // Schedule end of cutscene
        // ... your timing logic here
    }
}
```

## Best Practices

1. **Always call sync()** after modifying component state
2. **Check isInBackrooms()** before applying Backrooms-specific effects
3. **Use the provided constants** instead of magic numbers
4. **Handle both client and server** when needed
5. **Respect existing timers** and states when making changes

## Extension Points

For advanced modders, consider:
- **Extending PlayerComponent** for additional functionality
- **Creating custom mixins** for deeper integration
- **Using the event system** for reactive programming
- **Implementing custom networking** for complex features
