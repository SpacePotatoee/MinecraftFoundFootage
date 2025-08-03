![Minecraft Found Footage](https://github.com/SpacePotatoee/SPBackrooms-Revamped/blob/master/MinecraftFoundFootage.png)

# SpacePotato's Found Footage Modding Guide

## Getting Started

### Prerequisites

- Minecraft 1.20.1
- Fabric Loader >=0.15.11
- Fabric API
- GeckoLib >=4.4.9
- Voice Chat >=1.20.1-2.4.32

### Adding SP-Backrooms as a Dependency

Add to your `build.gradle`:

```gradle
dependencies {
    modImplementation "com.sp:spb-revamped:${project.spb_version}"
}
```

## Core Systems

### PlayerComponent System

The `PlayerComponent` is the heart of player state management, handling:
- Stamina and movement mechanics
- Flashlight functionality
- Entity interactions
- Visual effects (glitch, static, etc.)
- Level transitions

**Key Features for Modders:**
- Extensible through inheritance
- Comprehensive getter/setter API
- Automatic synchronization
- Event-driven updates

### BackroomsLevel System

Create custom dimensions with unique properties:
- Custom chunk generators
- Event systems
- Transition mechanics
- Environmental effects

### Entity Framework

Build custom entities with:
- IK (Inverse Kinematics) animation system
- Component-based architecture
- Advanced AI goals
- GeckoLib integration

### Rendering Pipeline

Advanced rendering features:
- PBR (Physically Based Rendering) materials (this works but not really working fully... like you need to scale your textures under 256x256)
- Custom shaders
- Post-processing effects
- Dynamic lighting

## Quick Start Example

```java
// Create a custom Backrooms level
public class MyCustomLevel extends BackroomsLevel {
    public MyCustomLevel() {
        super("my_level", MyChunkGenerator.CODEC, 
              new Vec3d(0, 64, 0), MY_WORLD_KEY);
        
        // Register events
        this.registerEvents("ambient", MyAmbientEvent::new);
        
        // Register transitions
        this.registerTransition(this::checkTransition, "my_transition");
    }
    
    @Override
    public void transitionIn(CrossDimensionTeleport teleport) {
        // Handle player entering this level
    }
    
    @Override
    public void transitionOut(CrossDimensionTeleport teleport) {
        // Handle player leaving this level
    }
}
```

## API Documentation

See the individual documentation files:
- [PlayerComponent API](api/PlayerComponent.md) - Player state management
- [BackroomsLevel API](api/BackroomsLevel.md) - Custom level creation
- [Entity System](api/EntitySystem.md) - Custom entity development
- [Rendering API](api/RenderingAPI.md) - Visual effects and shaders
- [Tick Patterns](api/TickPatterns.md) - **ESSENTIAL: Proper timing patterns**

## Examples

Check the `examples/` directory for complete implementation examples:
- Custom level creation
- Entity development
- Visual effect integration
- Event system usage

## Best Practices

1. **Always extend base classes** rather than implementing from scratch
2. **Use the component system** for entity data management
3. **Follow naming conventions** for consistency
4. **Document your APIs** for other modders
5. **Test thoroughly** in both single-player and multiplayer

## Support

- Discord: [Join the Discord!](https://discord.gg/z3sfGTdjEn)
- YouTube: [SpacePotato](https://www.youtube.com/@SpacePotatoee)
- Issues: [GitHub Issues](https://github.com/SpacePotatoee/MinecraftFoundFootage/issues)

## Contributing

We welcome contributions!  submit pull requests for improvements but not everything may get accepted and new levels will not be accepted.
