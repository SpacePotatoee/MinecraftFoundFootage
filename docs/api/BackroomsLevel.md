# BackroomsLevel API

The `BackroomsLevel` system allows you to create custom dimensions with unique properties, events, and transition mechanics. This is the foundation for creating new Backrooms levels.

## Overview

A BackroomsLevel defines:
- **World Generation**: Custom chunk generators and terrain
- **Environmental Properties**: Lighting, sky rendering, fog
- **Event System**: Random events that occur in the level
- **Transition Logic**: How players move between levels
- **Persistence**: Data saving and loading

## Creating a Custom Level

### Basic Implementation

```java
public class MyCustomLevel extends BackroomsLevel {
    
    public MyCustomLevel() {
        super("my_level",                    // Level ID
              MyChunkGenerator.CODEC,        // Chunk generator
              new Vec3d(0, 64, 0),          // Spawn position
              MY_WORLD_KEY);                 // World registry key
    }
    
    @Override
    public void writeToNbt(NbtCompound nbt) {
        // Save level-specific data
        nbt.putInt("myCustomData", someValue);
    }
    
    @Override
    public void readFromNbt(NbtCompound nbt) {
        // Load level-specific data
        someValue = nbt.getInt("myCustomData");
    }
    
    @Override
    public void transitionIn(CrossDimensionTeleport teleport) {
        // Called when player enters this level
        PlayerComponent player = teleport.playerComponent();
        // Apply entry effects
    }
    
    @Override
    public void transitionOut(CrossDimensionTeleport teleport) {
        // Called when player leaves this level
        PlayerComponent player = teleport.playerComponent();
        // Apply exit effects
    }
}
```

### Advanced Constructor

```java
public class AdvancedLevel extends BackroomsLevel {
    
    public AdvancedLevel() {
        super("advanced_level",              // Level ID
              AdvancedChunkGenerator.CODEC,  // Chunk generator
              new RoomCount(10, 20, 15, 8, 12), // Room counts (A,B,C,D,E)
              new Vec3d(8, 32, 8),          // Spawn position
              ADVANCED_WORLD_KEY,            // World key
              "my_mod");                     // Mod ID (optional)
    }
}
```

## Core API Methods

### Environmental Properties

```java
// Lighting system
boolean hasVanillaLighting()        // Override for custom lighting
// Default: false (completely dark without light sources)

// Sky and weather
boolean rendersSky()                // Should render sky?
boolean rendersClouds()             // Should render clouds?
// Both default to true
```

### Event System

```java
// Register events in your constructor or register() method
this.registerEvents("event_name", MyEvent::new);

// Event management
AbstractEvent getRandomEvent(World world)  // Get random event
int nextEventDelay()                       // Delay between events
// Override nextEventDelay() to customize timing
```

### Transition System

```java
// Register transitions
this.registerTransition(this::checkMyTransition, "transition_name");

// Transition callback method
private List<LevelTransition> checkMyTransition(World world, 
                                               PlayerComponent player, 
                                               BackroomsLevel from) {
    List<LevelTransition> transitions = new ArrayList<>();
    
    // Check conditions
    if (shouldTransition(player)) {
        transitions.add(createTransition(player));
    }
    
    return transitions;
}
```

## Event System

### Creating Custom Events

```java
public class MyCustomEvent extends AbstractEvent {
    
    @Override
    public void init(World world) {
        // Event initialization
        playSound(world, MyModSounds.CUSTOM_SOUND);
    }
    
    @Override
    public void ticks(int ticks, World world) {
        // Called every tick during event
        if (ticks == 100) {
            // Do something at tick 100
        }
    }
    
    @Override
    public void finish(World world) {
        super.finish(world);
        // Event cleanup
    }
    
    @Override
    public int duration() {
        return 200; // Event duration in ticks
    }
}
```

### Event Utilities

```java
// Sound helpers (available in AbstractEvent)
playSound(world, soundEvent)                    // Play to all players
playSoundWithRandLocation(world, sound, y, range) // Random location
playDistantSound(world, soundEvent)             // Distant sound
```

## Transition System

### Creating Transitions

```java
public LevelTransition createMyTransition(PlayerComponent player) {
    return new LevelTransition(
        60,                                 // Duration in ticks
        this::transitionTick,              // Tick callback
        new CrossDimensionTeleport(        // Teleport data
            player,
            new Vec3d(0, 64, 0),          // Target position
            this,                          // From level
            targetLevel                    // To level
        ),
        this::transitionCancel             // Cancel callback
    );
}

private void transitionTick(CrossDimensionTeleport teleport, int tick) {
    // Called each tick during transition
    PlayerComponent player = teleport.playerComponent();
    
    if (tick == 30) {
        // Halfway through transition
        player.setShouldDoStatic(true);
    }
}
```

### Transition Helpers

```java
// Built-in transition creators (available in BackroomsLevel)
LevelTransition getLevel0Transition(PlayerComponent player)
LevelTransition getLevel1Transition(PlayerComponent player)
LevelTransition getLevel2Transition(PlayerComponent player)
LevelTransition getPoolRoomsTransition(PlayerComponent player)
LevelTransition getInfiniteFieldTransition(PlayerComponent player)
LevelTransition getOverworldTransition(PlayerComponent player)
```

## Registration and Initialization

### Registering Your Level

```java
public class MyModLevels {
    public static final BackroomsLevel MY_LEVEL = new MyCustomLevel();
    
    public static void init() {
        // Add to the global level list
        BackroomsLevels.BACKROOMS_LEVELS.add(MY_LEVEL);
        
        // Register the level
        MY_LEVEL.register();
    }
}
```

### World Key Setup

```java
public class MyModWorldKeys {
    public static final RegistryKey<World> MY_WORLD_KEY = 
        RegistryKey.of(RegistryKeys.WORLD, 
                      new Identifier("my_mod", "my_level"));
}
```

## Data Management

### NBT Serialization

```java
@Override
public void writeToNbt(NbtCompound nbt) {
    // Save primitive data
    nbt.putInt("eventCount", eventCount);
    nbt.putBoolean("specialState", specialState);
    nbt.putString("currentPhase", currentPhase.name());
    
    // Save complex data
    NbtList playerList = new NbtList();
    for (UUID uuid : trackedPlayers) {
        playerList.add(NbtHelper.fromUuid(uuid));
    }
    nbt.put("trackedPlayers", playerList);
}

@Override
public void readFromNbt(NbtCompound nbt) {
    // Load primitive data
    eventCount = nbt.getInt("eventCount");
    specialState = nbt.getBoolean("specialState");
    currentPhase = Phase.valueOf(nbt.getString("currentPhase"));
    
    // Load complex data
    trackedPlayers.clear();
    NbtList playerList = nbt.getList("trackedPlayers", NbtElement.INT_ARRAY_TYPE);
    for (NbtElement element : playerList) {
        trackedPlayers.add(NbtHelper.toUuid(element));
    }
}
```

## Utility Methods

```java
// Level identification
String getLevelId()                 // Get level identifier
String getModId()                   // Get mod identifier
RegistryKey<World> getWorldKey()    // Get world registry key
Vec3d getSpawnPos()                 // Get spawn position

// State management
void justChanged()                  // Mark for synchronization
boolean shouldSync()                // Check if needs sync

// Transition management
void unregisterTransition(String name)  // Remove transition
List<LevelTransition> checkForTransition(PlayerComponent, World) // Check all
```

## Example: Complete Custom Level

```java
public class HauntedOfficeLevel extends BackroomsLevel {
    private int flickerCount = 0;
    private boolean lightsOn = true;
    
    public HauntedOfficeLevel() {
        super("haunted_office", HauntedOfficeChunkGenerator.CODEC,
              new Vec3d(16, 24, 16), HAUNTED_OFFICE_WORLD_KEY);
    }
    
    @Override
    public void register() {
        super.register();
        
        // Register events
        this.registerEvents("flicker", OfficeFlickerEvent::new);
        this.registerEvents("phone_ring", PhoneRingEvent::new);
        
        // Register transitions
        this.registerTransition(this::checkElevatorTransition, "elevator");
    }
    
    @Override
    public int nextEventDelay() {
        return 800 + random.nextInt(400); // 40-60 seconds
    }
    
    private List<LevelTransition> checkElevatorTransition(World world, 
                                                         PlayerComponent player, 
                                                         BackroomsLevel from) {
        List<LevelTransition> transitions = new ArrayList<>();
        
        // Check if player is near elevator
        BlockPos elevatorPos = new BlockPos(0, 24, 0);
        if (player.player.getBlockPos().isWithinDistance(elevatorPos, 2)) {
            transitions.add(getLevel1Transition(player));
        }
        
        return transitions;
    }
    
    @Override
    public void writeToNbt(NbtCompound nbt) {
        nbt.putInt("flickerCount", flickerCount);
        nbt.putBoolean("lightsOn", lightsOn);
    }
    
    @Override
    public void readFromNbt(NbtCompound nbt) {
        flickerCount = nbt.getInt("flickerCount");
        lightsOn = nbt.getBoolean("lightsOn");
    }
    
    @Override
    public void transitionIn(CrossDimensionTeleport teleport) {
        // Play entrance sound
        teleport.playerComponent().player.playSound(
            MyModSounds.OFFICE_AMBIENCE, 0.5f, 1.0f);
    }
    
    @Override
    public void transitionOut(CrossDimensionTeleport teleport) {
        // Reset player effects
        teleport.playerComponent().setShouldDoStatic(false);
    }
}
```

## Best Practices

1. **Always call super.register()** in your register() method
2. **Use meaningful level IDs** that won't conflict with other mods
3. **Handle NBT serialization** for persistent data
4. **Test transitions thoroughly** in multiplayer
5. **Consider performance** when designing events
6. **Document your level's mechanics** for other modders
