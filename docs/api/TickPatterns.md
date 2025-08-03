# Tick-Based Programming Patterns

This guide covers the essential tick-based programming patterns for SP-Backrooms modding. **Always use tick counters instead of sleep() or schedulers** for better performance and reliability.

## Core Principles

### 1. Use Entity/Player Age for Timing
```java
// ✅ CORRECT: Use age for consistent timing
int endTick = player.age + 60; // 3 seconds from now

// ❌ WRONG: Never use system time or sleep
Thread.sleep(3000);
System.currentTimeMillis() + 3000;
```

### 2. Store End Ticks in Persistent Data
```java
public class PlayerData {
    public int glitchEndTick = 0;
    public int staticEndTick = 0;
    public int fearEffectEndTick = 0;
    
    // Check if any effects are active
    public boolean hasActiveEffects(int currentTick) {
        return glitchEndTick > currentTick || 
               staticEndTick > currentTick || 
               fearEffectEndTick > currentTick;
    }
}
```

### 3. Use Modulo for Intervals
```java
// ✅ CORRECT: Interval-based events
if (player.age % 200 == 0) { // Every 10 seconds
    triggerEvent(player);
}

if (entity.age % 20 == 0) { // Every 1 second
    updateAI(entity);
}

// ❌ WRONG: Don't use counters that reset
private int counter = 0;
if (++counter >= 200) {
    counter = 0; // This breaks on save/load
    triggerEvent(player);
}
```

## Common Patterns

### 1. Timed Effects
```java
public class TimedEffectManager {
    
    public static void applyGlitchEffect(PlayerEntity player, int durationTicks) {
        // Apply effect immediately
        PlayerEffectsAPI.glitch(player, true);
        
        // Store end tick
        PlayerData data = getPlayerData(player.getUuid());
        data.glitchEndTick = player.age + durationTicks;
        
        // Log for debugging
        SPBRevamped.LOGGER.debug("Glitch effect applied to {} until tick {}", 
                                player.getName().getString(), data.glitchEndTick);
    }
    
    public static void tickEffects(PlayerEntity player) {
        PlayerData data = getPlayerData(player.getUuid());
        
        // Check glitch effect
        if (data.glitchEndTick > 0 && player.age >= data.glitchEndTick) {
            PlayerEffectsAPI.glitch(player, false);
            data.glitchEndTick = 0;
            
            SPBRevamped.LOGGER.debug("Glitch effect ended for {} at tick {}", 
                                    player.getName().getString(), player.age);
        }
        
        // Check other effects...
    }
}
```

### 2. Staged Effects
```java
public class StagedEffectManager {
    
    public static void startSpookSequence(PlayerEntity player) {
        PlayerData data = getPlayerData(player.getUuid());
        data.spookStartTick = player.age;
        data.spookStage = 1;
        
        SPBRevamped.LOGGER.debug("Started spook sequence for {} at tick {}", 
                                player.getName().getString(), player.age);
    }
    
    public static void tickSpookSequence(PlayerEntity player) {
        PlayerData data = getPlayerData(player.getUuid());
        
        if (data.spookStage == 0) return;
        
        int elapsed = player.age - data.spookStartTick;
        
        switch (data.spookStage) {
            case 1 -> {
                if (elapsed >= 40) { // 2 seconds
                    PlayerEffectsAPI.staticEffect(player, true);
                    data.spookStage = 2;
                    
                    SPBRevamped.LOGGER.debug("Spook stage 2 for {} at tick {} (elapsed: {})", 
                                           player.getName().getString(), player.age, elapsed);
                }
            }
            case 2 -> {
                if (elapsed >= 80) { // 4 seconds total
                    PlayerEffectsAPI.blackScreen(player, 40, true, false);
                    data.spookStage = 3;
                    
                    SPBRevamped.LOGGER.debug("Spook stage 3 for {} at tick {}", 
                                           player.getName().getString(), player.age);
                }
            }
            case 3 -> {
                if (elapsed >= 160) { // 8 seconds total
                    PlayerEffectsAPI.clearAllEffects(player);
                    data.spookStage = 0;
                    
                    SPBRevamped.LOGGER.debug("Spook sequence completed for {} at tick {}", 
                                           player.getName().getString(), player.age);
                }
            }
        }
    }
}
```

### 3. Cooldown Management
```java
public class CooldownManager {
    
    public static boolean canUseAbility(PlayerEntity player, String abilityName, int cooldownTicks) {
        PlayerData data = getPlayerData(player.getUuid());
        int lastUseTick = data.getLastUseTick(abilityName);
        
        boolean canUse = player.age >= lastUseTick + cooldownTicks;
        
        SPBRevamped.LOGGER.debug("Ability {} for {}: canUse={}, lastUse={}, current={}, cooldown={}", 
                                abilityName, player.getName().getString(), canUse, 
                                lastUseTick, player.age, cooldownTicks);
        
        return canUse;
    }
    
    public static void useAbility(PlayerEntity player, String abilityName) {
        PlayerData data = getPlayerData(player.getUuid());
        data.setLastUseTick(abilityName, player.age);
        
        SPBRevamped.LOGGER.debug("Used ability {} for {} at tick {}", 
                                abilityName, player.getName().getString(), player.age);
    }
}
```

### 4. Interval-Based Events
```java
public class IntervalEventManager {
    
    public static void tickPlayerEvents(PlayerEntity player) {
        // Every 10 seconds (200 ticks)
        if (player.age % 200 == 0) {
            checkFearLevel(player);
        }
        
        // Every 30 seconds (600 ticks)
        if (player.age % 600 == 0) {
            triggerRandomEvent(player);
        }
        
        // Every 5 minutes (6000 ticks)
        if (player.age % 6000 == 0) {
            performMajorEvent(player);
        }
    }
    
    private static void checkFearLevel(PlayerEntity player) {
        PlayerData data = getPlayerData(player.getUuid());
        
        SPBRevamped.LOGGER.debug("Fear check for {} at tick {}: level={}", 
                                player.getName().getString(), player.age, data.fearLevel);
        
        if (data.fearLevel > 0.5f) {
            applyFearEffect(player);
        }
    }
}
```

## Entity Tick Patterns

### 1. Entity State Management
```java
public class MyCustomEntity extends HostileEntity {
    private int stalkingEndTick = 0;
    private int hideEndTick = 0;
    private int nextActionTick = 0;
    
    @Override
    public void tick() {
        super.tick();
        
        // Check state transitions
        if (stalkingEndTick > 0 && this.age >= stalkingEndTick) {
            stopStalking();
            stalkingEndTick = 0;
        }
        
        if (hideEndTick > 0 && this.age >= hideEndTick) {
            stopHiding();
            hideEndTick = 0;
        }
        
        // Periodic actions
        if (this.age >= nextActionTick) {
            performAction();
            nextActionTick = this.age + 100; // Next action in 5 seconds
        }
    }
    
    public void startStalking(int durationTicks) {
        this.stalkingEndTick = this.age + durationTicks;
        
        SPBRevamped.LOGGER.debug("Entity {} started stalking until tick {}", 
                                this.getId(), this.stalkingEndTick);
    }
}
```

### 2. Entity AI with Tick Timing
```java
public class StalkingGoal extends Goal {
    private final MyCustomEntity entity;
    private int stalkDuration = 0;
    private int startTick = 0;
    
    @Override
    public boolean canStart() {
        PlayerEntity target = findTarget();
        if (target != null) {
            this.stalkDuration = 200 + entity.getRandom().nextInt(400); // 10-30 seconds
            return true;
        }
        return false;
    }
    
    @Override
    public void start() {
        this.startTick = entity.age;
        
        SPBRevamped.LOGGER.debug("Entity {} started stalking goal at tick {} for {} ticks", 
                                entity.getId(), this.startTick, this.stalkDuration);
    }
    
    @Override
    public boolean shouldContinue() {
        boolean shouldContinue = entity.age < startTick + stalkDuration;
        
        if (!shouldContinue) {
            SPBRevamped.LOGGER.debug("Entity {} stalking goal ended at tick {} (duration: {})", 
                                    entity.getId(), entity.age, entity.age - startTick);
        }
        
        return shouldContinue;
    }
}
```

## Performance Optimization

### 1. Batch Processing
```java
public class BatchProcessor {
    private static final int BATCH_SIZE = 10;
    
    public static void processBatch(List<PlayerEntity> players, int currentTick) {
        // Process players in batches to spread load
        int startIndex = (currentTick % players.size()) / BATCH_SIZE * BATCH_SIZE;
        int endIndex = Math.min(startIndex + BATCH_SIZE, players.size());
        
        for (int i = startIndex; i < endIndex; i++) {
            processPlayer(players.get(i));
        }
        
        SPBRevamped.LOGGER.debug("Processed players {}-{} at tick {}", 
                                startIndex, endIndex - 1, currentTick);
    }
}
```

### 2. Conditional Processing
```java
public class ConditionalProcessor {
    
    public static void tickPlayer(PlayerEntity player) {
        // Only process every 5 ticks for expensive operations
        if (player.age % 5 == 0) {
            performExpensiveCheck(player);
        }
        
        // Always process critical operations
        processCriticalEffects(player);
        
        // Log performance metrics occasionally
        if (player.age % 1200 == 0) { // Every minute
            logPerformanceMetrics(player);
        }
    }
}
```

## Common Mistakes to Avoid

### ❌ Don't Use Sleep or Schedulers
```java
// NEVER DO THIS
Thread.sleep(1000);
Timer timer = new Timer();
timer.schedule(task, 1000);
CompletableFuture.delayedExecutor(1, TimeUnit.SECONDS);
```

### ❌ Don't Use System Time
```java
// NEVER DO THIS
long endTime = System.currentTimeMillis() + 3000;
if (System.currentTimeMillis() >= endTime) {
    // This breaks on server lag and save/load
}
```

### ❌ Don't Use Resetting Counters
```java
// NEVER DO THIS
private int counter = 0;
if (++counter >= 100) {
    counter = 0; // Breaks on save/load
    doSomething();
}
```

## Debugging Tick-Based Code

### 1. Comprehensive Logging
```java
public static void logTickState(PlayerEntity player, String context) {
    PlayerData data = getPlayerData(player.getUuid());
    
    SPBRevamped.LOGGER.debug("=== {} Debug ===", context);
    SPBRevamped.LOGGER.debug("Player: {}", player.getName().getString());
    SPBRevamped.LOGGER.debug("Current tick: {}", player.age);
    SPBRevamped.LOGGER.debug("World time: {}", player.getWorld().getTime());
    SPBRevamped.LOGGER.debug("Active effects: glitch={}, static={}", 
                            data.glitchEndTick > player.age, 
                            data.staticEndTick > player.age);
    SPBRevamped.LOGGER.debug("Effect end ticks: glitch={}, static={}", 
                            data.glitchEndTick, data.staticEndTick);
}
```

### 2. Validation Checks
```java
public static void validateTickData(PlayerEntity player) {
    PlayerData data = getPlayerData(player.getUuid());
    
    // Check for invalid tick values
    if (data.glitchEndTick < 0) {
        SPBRevamped.LOGGER.warn("Invalid glitch end tick for {}: {}", 
                               player.getName().getString(), data.glitchEndTick);
        data.glitchEndTick = 0;
    }
    
    // Check for effects that should have ended
    if (data.glitchEndTick > 0 && data.glitchEndTick < player.age - 1200) { // 1 minute ago
        SPBRevamped.LOGGER.warn("Stale glitch effect for {}, cleaning up", 
                               player.getName().getString());
        data.glitchEndTick = 0;
    }
}
```

## Best Practices Summary

1. **Always use `player.age` or `entity.age`** for timing
2. **Store end ticks in persistent data** structures
3. **Use modulo operations** for interval-based events
4. **Add comprehensive logging** with tick information
5. **Validate tick data** to catch edge cases
6. **Never use sleep, schedulers, or system time**
7. **Test save/load scenarios** to ensure timing persists
8. **Use batch processing** for performance optimization
