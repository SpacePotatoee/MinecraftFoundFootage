# Entity System API

The SP-Backrooms mod provides a sophisticated entity system with IK (Inverse Kinematics) animation, component-based architecture, and GeckoLib integration. This guide covers creating custom entities for the Backrooms.

## Overview

The entity system includes:
- **Component-based architecture** for modular entity behavior
- **IK animation system** for realistic movement
- **GeckoLib integration** for advanced animations
- **AI goal system** for complex behaviors
- **PlayerComponent integration** for player interactions

## Basic Entity Structure

### Extending Base Classes

```java
public class MyCustomEntity extends HostileEntity 
    implements GeoEntity, GeoAnimatable, IKAnimatable<MyCustomEntity> {
    
    // Required for GeckoLib
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    
    // Required for IK system
    public List<IKModelComponent<MyCustomEntity>> components = new ArrayList<>();
    
    public MyCustomEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.setUpComponents();
    }
}
```

### Entity Registration

```java
public class MyModEntities {
    public static final EntityType<MyCustomEntity> MY_ENTITY = Registry.register(
        Registries.ENTITY_TYPE,
        new Identifier("my_mod", "my_entity"),
        FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, MyCustomEntity::new)
            .dimensions(EntityDimensions.fixed(0.6f, 2.0f))
            .build()
    );
}
```

## Component System

### Using Existing Components

```java
// In your entity constructor
private void setUpComponents() {
    // Add IK leg component for terrain adaptation
    this.addComponent(new IKLegComponent<>(
        new IKLegComponent.LegSetting.Builder()
            .maxDistance(2.0)
            .stepInFront(1.0)
            .movementSpeed(0.2)
            .maxStandingStillDistance(0.2)
            .standStillCounter(20)
            .build(),
        createLimbList()
    ));
}

private List<ServerLimb> createLimbList() {
    return List.of(
        new ServerLimb(1.0, 0, 0.5, this::onLimbMove),
        new ServerLimb(-1.0, 0, 0.5, this::onLimbMove),
        new ServerLimb(1.0, 0, -0.5, this::onLimbMove),
        new ServerLimb(-1.0, 0, -0.5, this::onLimbMove)
    );
}

private void onLimbMove(ServerLimb limb, IKLegComponent component, int index, double speed) {
    // Custom limb movement logic
}
```

### Creating Custom Components

```java
public class MyCustomComponent<E extends IKAnimatable<E>> implements IKModelComponent<E> {
    
    @Override
    public void tickClient(E animatable, ModelAccessor model) {
        // Client-side component logic
    }
    
    @Override
    public void tickServer(E animatable) {
        // Server-side component logic
    }
    
    @Override
    public void getModelPositions(E animatable, ModelAccessor model) {
        // Extract positions from the model
    }
    
    @Override
    public void renderDebug(MatrixStack matrices, E animatable, RenderLayer renderType,
                           VertexConsumerProvider bufferSource, VertexConsumer buffer,
                           float partialTick, int packedLight, int packedOverlay) {
        // Debug rendering
    }
}
```

## Animation System

### GeckoLib Integration

```java
// Define animations
public static final RawAnimation IDLE = RawAnimation.begin().then("idle", Animation.LoopType.LOOP);
public static final RawAnimation WALK = RawAnimation.begin().then("walk", Animation.LoopType.LOOP);
public static final RawAnimation ATTACK = RawAnimation.begin().then("attack", Animation.LoopType.PLAY_ONCE);

@Override
public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
}

private PlayState predicate(AnimationState<MyCustomEntity> animationState) {
    if (this.isAttacking()) {
        return animationState.setAndContinue(ATTACK);
    } else if (animationState.isMoving()) {
        return animationState.setAndContinue(WALK);
    } else {
        return animationState.setAndContinue(IDLE);
    }
}

@Override
public AnimatableInstanceCache getAnimatableInstanceCache() {
    return this.cache;
}
```

### Animation Events

```java
// In your animation controller
controllers.add(new AnimationController<>(this, "controller", 0, this::predicate)
    .triggerableAnim("special_attack", SPECIAL_ATTACK)
    .setAnimationSpeed(1.2f));

// Trigger animations from code
public void performSpecialAttack() {
    this.triggerAnim("controller", "special_attack");
}
```

## AI System

### Custom AI Goals

```java
public class MyCustomGoal extends Goal {
    private final MyCustomEntity entity;
    private PlayerEntity targetPlayer;
    
    public MyCustomGoal(MyCustomEntity entity) {
        this.entity = entity;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }
    
    @Override
    public boolean canStart() {
        // Check if this goal should activate
        this.targetPlayer = entity.getWorld().getClosestPlayer(entity, 16.0);
        return this.targetPlayer != null && shouldTargetPlayer(targetPlayer);
    }
    
    @Override
    public void start() {
        // Initialize goal
    }
    
    @Override
    public void tick() {
        // Execute goal behavior each tick
        if (this.targetPlayer != null) {
            entity.getLookControl().lookAt(targetPlayer);
            entity.getNavigation().startMovingTo(targetPlayer, 1.0);
        }
    }
    
    @Override
    public boolean shouldContinue() {
        return this.targetPlayer != null && this.targetPlayer.isAlive();
    }
    
    @Override
    public void stop() {
        // Clean up when goal ends
        this.targetPlayer = null;
        entity.getNavigation().stop();
    }
}
```

### Integrating with PlayerComponent

```java
private boolean shouldTargetPlayer(PlayerEntity player) {
    PlayerComponent component = InitializeComponents.PLAYER.get(player);
    
    // Don't target players in cutscenes
    if (component.isDoingCutscene()) {
        return false;
    }
    
    // Target players with flashlights off
    if (!component.isFlashLightOn()) {
        return true;
    }
    
    // Target visible players
    return component.isVisibleToEntity();
}

private void affectPlayer(PlayerEntity player) {
    PlayerComponent component = InitializeComponents.PLAYER.get(player);
    
    // Apply effects based on distance
    double distance = this.squaredDistanceTo(player);
    
    if (distance < 16.0) { // 4 blocks
        component.setShouldGlitch(true);
    }
    
    if (distance < 4.0) { // 2 blocks
        component.setShouldInflictGlitchDamage(true);
    }
    
    component.sync();
}
```

## Entity Attributes

### Setting Up Attributes

```java
public static DefaultAttributeContainer.Builder createMyEntityAttributes() {
    return HostileEntity.createHostileAttributes()
        .add(EntityAttributes.GENERIC_MAX_HEALTH, 100.0)
        .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3)
        .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32.0)
        .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 8.0)
        .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.5);
}

// Register attributes in your mod initializer
FabricDefaultAttributeRegistry.register(MY_ENTITY, MyCustomEntity.createMyEntityAttributes());
```

### Dynamic Attribute Modification

```java
public void enhanceEntity() {
    // Temporarily boost speed
    EntityAttributeInstance speedAttribute = this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
    if (speedAttribute != null) {
        speedAttribute.addTemporaryModifier(new EntityAttributeModifier(
            "speed_boost", 0.5, EntityAttributeModifier.Operation.ADDITION
        ));
    }
}
```

## Rendering Integration

### Custom Renderers

```java
public class MyEntityRenderer extends LivingEntityRenderer<MyCustomEntity, MyEntityModel> {
    
    public MyEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new MyEntityModel(), 0.5f);
    }
    
    @Override
    public void render(MyCustomEntity entity, float yaw, float tickDelta,
                      MatrixStack matrices, VertexConsumerProvider provider, int light) {
        
        // Apply special effects based on player state
        PlayerEntity nearestPlayer = entity.getWorld().getClosestPlayer(entity, 16.0);
        if (nearestPlayer != null) {
            PlayerComponent component = InitializeComponents.PLAYER.get(nearestPlayer);
            
            if (component.shouldGlitch()) {
                // Render with distortion effect
                RenderLayer layer = RenderLayers.getDistortedEntity(getTexture(entity));
                // Custom rendering logic
            }
        }
        
        super.render(entity, yaw, tickDelta, matrices, provider, light);
    }
    
    @Override
    public Identifier getTexture(MyCustomEntity entity) {
        return new Identifier("my_mod", "textures/entity/my_entity.png");
    }
}
```

## Data Persistence

### NBT Serialization

```java
@Override
public void writeCustomDataToNbt(NbtCompound nbt) {
    super.writeCustomDataToNbt(nbt);
    
    // Save entity-specific data
    nbt.putInt("aggressionLevel", this.aggressionLevel);
    nbt.putBoolean("hasSeenPlayer", this.hasSeenPlayer);
    nbt.putFloat("fearLevel", this.fearLevel);
    
    // Save complex data
    if (this.targetPlayer != null) {
        nbt.putUuid("targetPlayer", this.targetPlayer.getUuid());
    }
}

@Override
public void readCustomDataFromNbt(NbtCompound nbt) {
    super.readCustomDataFromNbt(nbt);
    
    // Load entity-specific data
    this.aggressionLevel = nbt.getInt("aggressionLevel");
    this.hasSeenPlayer = nbt.getBoolean("hasSeenPlayer");
    this.fearLevel = nbt.getFloat("fearLevel");
    
    // Load complex data
    if (nbt.containsUuid("targetPlayer")) {
        UUID targetUuid = nbt.getUuid("targetPlayer");
        // Find player by UUID when needed
    }
}
```

## Spawning and Management

### Custom Spawning Logic

```java
public class MyEntitySpawner {
    
    public static void spawnInBackrooms(World world, PlayerEntity nearPlayer) {
        if (!BackroomsLevels.isInBackrooms(world.getRegistryKey())) {
            return;
        }
        
        // Find suitable spawn location
        Vec3d spawnPos = findSpawnLocation(world, nearPlayer.getPos());
        if (spawnPos != null) {
            MyCustomEntity entity = MY_ENTITY.create(world);
            if (entity != null) {
                entity.setPosition(spawnPos);
                world.spawnEntity(entity);
            }
        }
    }
    
    private static Vec3d findSpawnLocation(World world, Vec3d playerPos) {
        // Find a location out of player sight but within range
        for (int attempts = 0; attempts < 10; attempts++) {
            Vec3d candidate = playerPos.add(
                (world.random.nextDouble() - 0.5) * 32,
                0,
                (world.random.nextDouble() - 0.5) * 32
            );
            
            BlockPos blockPos = new BlockPos((int)candidate.x, (int)candidate.y, (int)candidate.z);
            if (world.isSpaceEmpty(entity.getBoundingBox().offset(candidate))) {
                return candidate;
            }
        }
        return null;
    }
}
```

## Best Practices

1. **Always implement IKAnimatable** for entities that need advanced movement
2. **Use the component system** for modular behavior
3. **Integrate with PlayerComponent** for player interactions
4. **Handle both client and server** logic appropriately
5. **Use proper NBT serialization** for persistent data
6. **Test entity behavior** in multiplayer environments
7. **Optimize AI goals** to prevent performance issues
8. **Use appropriate spawn conditions** for Backrooms entities

## Debugging

### Debug Rendering

```java
// Enable debug rendering in development
if (PrAnCommonClass.shouldRenderDebugLegs) {
    this.renderDebug(matrices, this, renderType, bufferSource, buffer, 
                    partialTick, packedLight, packedOverlay);
}
```

### Logging

```java
// Use the mod's logger for debugging
SPBRevamped.LOGGER.info("Entity {} spawned at {}", this.getType(), this.getPos());
```
