package com.example.mymod.entity;

import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import com.sp.entity.ai.goals.FollowClosestPlayerGoal;
import com.sp.entity.ik.components.IKAnimatable;
import com.sp.entity.ik.components.IKModelComponent;
import com.sp.init.BackroomsLevels;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Example custom entity for the Backrooms mod.
 * 
 * This demonstrates:
 * - Basic hostile entity setup
 * - GeckoLib animation integration
 * - IK (Inverse Kinematics) system usage
 * - Player interaction mechanics
 * - Backrooms-specific behavior
 * - Component-based architecture
 * 
 * Features:
 * - Stalks players in Backrooms levels
 * - Reacts to player flashlight
 * - Uses advanced animation system
 * - Integrates with PlayerComponent
 */
public class ExampleStalkerEntity extends HostileEntity implements GeoEntity, GeoAnimatable, IKAnimatable<ExampleStalkerEntity> {
    
    // Animation cache for GeckoLib
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    
    // IK components for advanced movement
    public List<IKModelComponent<ExampleStalkerEntity>> components = new ArrayList<>();
    
    // Entity state
    private PlayerEntity targetPlayer;
    private int stalkingTimer = 0;
    private int hideTimer = 0;
    private boolean isHiding = false;
    private boolean hasBeenSeen = false;
    private float fearLevel = 0.0f;
    private int staticRemovalTime = 0;
    
    // Behavior constants
    private static final int MAX_STALKING_TIME = 1200; // 60 seconds
    private static final int HIDE_DURATION = 400;      // 20 seconds
    private static final float DETECTION_RANGE = 16.0f;
    private static final float FLEE_RANGE = 8.0f;
    
    // Animation definitions
    public static final RawAnimation IDLE = RawAnimation.begin().then("idle", Animation.LoopType.LOOP);
    public static final RawAnimation WALK = RawAnimation.begin().then("walk", Animation.LoopType.LOOP);
    public static final RawAnimation STALK = RawAnimation.begin().then("stalk", Animation.LoopType.LOOP);
    public static final RawAnimation HIDE = RawAnimation.begin().then("hide", Animation.LoopType.PLAY_ONCE);
    public static final RawAnimation FLEE = RawAnimation.begin().then("flee", Animation.LoopType.LOOP);
    
    public ExampleStalkerEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        
        // Set up IK components if needed
        this.setUpIKComponents();
        
        // Initialize entity state
        this.stalkingTimer = 0;
        this.hideTimer = 0;
        this.isHiding = false;
        this.fearLevel = 0.0f;
    }
    
    private void setUpIKComponents() {
        // Add IK components for advanced movement
        // This would typically include leg IK for terrain adaptation
        // See SkinWalkerEntity for a complete example
    }
    
    // Entity attributes
    public static DefaultAttributeContainer.Builder createStalkerAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 50.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32.0)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6.0);
    }
    
    @Override
    protected void initGoals() {
        // Custom AI goals
        this.goalSelector.add(1, new StalkerHideGoal(this));
        this.goalSelector.add(2, new StalkerStalkGoal(this));
        this.goalSelector.add(3, new WanderAroundGoal(this, 0.8));
        this.goalSelector.add(4, new LookAroundGoal(this));
        
        // Target selection
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, 
                                                          0, true, false, this::shouldTargetPlayer));
    }
    
    private boolean shouldTargetPlayer(PlayerEntity player) {
        // Only target players in Backrooms levels
        if (!BackroomsLevels.isInBackrooms(player.getWorld().getRegistryKey())) {
            return false;
        }
        
        // Don't target players who are too close (hiding behavior)
        if (this.squaredDistanceTo(player) < FLEE_RANGE * FLEE_RANGE) {
            return false;
        }
        
        return true;
    }
    
    @Override
    public void tick() {
        super.tick();
        
        // Only active in Backrooms
        if (!BackroomsLevels.isInBackrooms(this.getWorld().getRegistryKey())) {
            this.discard();
            return;
        }
        
        // Update stalking behavior
        this.updateStalkingBehavior();
        
        // Update fear level
        this.updateFearLevel();
        
        // Handle hiding behavior
        if (isHiding) {
            this.hideTimer--;
            if (this.hideTimer <= 0) {
                this.isHiding = false;
                this.setInvisible(false);
            }
        }

        // Handle static effect removal using tick counter
        if (staticRemovalTime > 0 && this.age >= staticRemovalTime) {
            // Find the player and remove static effect
            PlayerEntity nearestPlayer = this.getWorld().getClosestPlayer(this, 16.0);
            if (nearestPlayer != null) {
                PlayerComponent playerComp = InitializeComponents.PLAYER.get(nearestPlayer);
                playerComp.setShouldDoStatic(false);

                // Log breakpoint
                SPBRevamped.LOGGER.debug("Entity {} removed static from player {} at tick {}",
                                       this.getId(), nearestPlayer.getName().getString(), this.age);
            }
            staticRemovalTime = 0;
        }
    }
    
    private void updateStalkingBehavior() {
        PlayerEntity nearestPlayer = this.getWorld().getClosestPlayer(this, DETECTION_RANGE);
        
        if (nearestPlayer != null) {
            PlayerComponent playerComp = InitializeComponents.PLAYER.get(nearestPlayer);
            
            // React to player flashlight
            if (playerComp.isFlashLightOn() && this.canSee(nearestPlayer)) {
                this.reactToFlashlight(nearestPlayer, playerComp);
            } else {
                // Continue stalking
                this.stalkPlayer(nearestPlayer, playerComp);
            }
        } else {
            // No player nearby, reset stalking
            this.stalkingTimer = 0;
            this.targetPlayer = null;
        }
    }
    
    private void reactToFlashlight(PlayerEntity player, PlayerComponent playerComp) {
        double distance = this.squaredDistanceTo(player);
        
        if (distance < FLEE_RANGE * FLEE_RANGE) {
            // Too close, hide immediately
            this.startHiding();
            
            // Increase player's fear
            if (playerComp.shouldGlitch()) {
                playerComp.setShouldInflictGlitchDamage(true);
            }
        } else {
            // Maintain distance, increase fear
            this.fearLevel += 0.1f;
            this.hasBeenSeen = true;
            
            // Move away slowly
            Vec3d awayDirection = this.getPos().subtract(player.getPos()).normalize();
            this.setVelocity(awayDirection.multiply(0.1));
        }
    }
    
    private void stalkPlayer(PlayerEntity player, PlayerComponent playerComp) {
        this.targetPlayer = player;
        this.stalkingTimer++;
        
        // Gradually increase player's unease using tick intervals
        if (this.stalkingTimer % 100 == 0) { // Every 5 seconds
            if (this.random.nextFloat() < 0.3f) {
                playerComp.setShouldDoStatic(true);

                // Set tick counter for static removal (1 second = 20 ticks)
                this.staticRemovalTime = this.age + 20;

                // Log breakpoint for debugging
                SPBRevamped.LOGGER.debug("Entity {} applied static to player {} until tick {}",
                                       this.getId(), player.getName().getString(), this.staticRemovalTime);
            }
        }
        
        // If stalked too long, become more aggressive
        if (this.stalkingTimer > MAX_STALKING_TIME) {
            this.becomeAggressive(player, playerComp);
        }
    }
    
    private void becomeAggressive(PlayerEntity player, PlayerComponent playerComp) {
        // Trigger intense effects
        playerComp.setShouldGlitch(true);
        playerComp.setShouldInflictGlitchDamage(true);
        
        // Play scary sound
        this.playSound(getAggressiveSound(), 2.0f, 0.8f);
        
        // Reset stalking timer
        this.stalkingTimer = 0;
        
        // Hide after becoming aggressive
        this.startHiding();
    }
    
    private void startHiding() {
        this.isHiding = true;
        this.hideTimer = HIDE_DURATION;
        this.setInvisible(true);
        this.getNavigation().stop();
        
        // Play hide sound
        this.playSound(getHideSound(), 1.0f, 1.0f);
    }
    
    private void updateFearLevel() {
        // Gradually decrease fear over time
        if (this.fearLevel > 0) {
            this.fearLevel -= 0.01f;
        }
        
        // Cap fear level
        this.fearLevel = Math.max(0.0f, Math.min(1.0f, this.fearLevel));
    }
    
    // Sound methods
    @Override
    protected SoundEvent getAmbientSound() {
        return this.isHiding ? null : SoundEvents.ENTITY_ENDERMAN_AMBIENT;
    }
    
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.ENTITY_ENDERMAN_HURT;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_ENDERMAN_DEATH;
    }
    
    protected SoundEvent getAggressiveSound() {
        return SoundEvents.ENTITY_ENDERMAN_SCREAM;
    }
    
    protected SoundEvent getHideSound() {
        return SoundEvents.ENTITY_ENDERMAN_TELEPORT;
    }
    
    // NBT serialization
    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("stalkingTimer", this.stalkingTimer);
        nbt.putInt("hideTimer", this.hideTimer);
        nbt.putBoolean("isHiding", this.isHiding);
        nbt.putBoolean("hasBeenSeen", this.hasBeenSeen);
        nbt.putFloat("fearLevel", this.fearLevel);
        nbt.putInt("staticRemovalTime", this.staticRemovalTime);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.stalkingTimer = nbt.getInt("stalkingTimer");
        this.hideTimer = nbt.getInt("hideTimer");
        this.isHiding = nbt.getBoolean("isHiding");
        this.hasBeenSeen = nbt.getBoolean("hasBeenSeen");
        this.fearLevel = nbt.getFloat("fearLevel");
        this.staticRemovalTime = nbt.getInt("staticRemovalTime");
    }
    
    // GeckoLib animation system
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }
    
    private PlayState predicate(AnimationState<ExampleStalkerEntity> animationState) {
        if (this.isHiding) {
            return animationState.setAndContinue(HIDE);
        } else if (this.fearLevel > 0.5f) {
            return animationState.setAndContinue(FLEE);
        } else if (this.targetPlayer != null) {
            return animationState.setAndContinue(STALK);
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
    
    // IK system implementation
    @Override
    public List<IKModelComponent<ExampleStalkerEntity>> getComponents() {
        return this.components;
    }
    
    @Override
    public double getSize() {
        return 1.0; // Normal size
    }
    
    // Getters for other systems
    public boolean isCurrentlyHiding() {
        return this.isHiding;
    }
    
    public float getFearLevel() {
        return this.fearLevel;
    }
    
    public boolean hasBeenSeenByPlayer() {
        return this.hasBeenSeen;
    }
    
    public PlayerEntity getCurrentTarget() {
        return this.targetPlayer;
    }
}

/**
 * Custom AI goal for hiding behavior
 */
class StalkerHideGoal extends net.minecraft.entity.ai.goal.Goal {
    private final ExampleStalkerEntity stalker;
    
    public StalkerHideGoal(ExampleStalkerEntity stalker) {
        this.stalker = stalker;
    }
    
    @Override
    public boolean canStart() {
        return stalker.isCurrentlyHiding();
    }
    
    @Override
    public void start() {
        stalker.getNavigation().stop();
    }
    
    @Override
    public boolean shouldContinue() {
        return stalker.isCurrentlyHiding();
    }
}

/**
 * Custom AI goal for stalking behavior
 */
class StalkerStalkGoal extends net.minecraft.entity.ai.goal.Goal {
    private final ExampleStalkerEntity stalker;
    private PlayerEntity targetPlayer;
    
    public StalkerStalkGoal(ExampleStalkerEntity stalker) {
        this.stalker = stalker;
    }
    
    @Override
    public boolean canStart() {
        if (stalker.isCurrentlyHiding()) return false;
        
        this.targetPlayer = stalker.getWorld().getClosestPlayer(stalker, 16.0);
        return this.targetPlayer != null;
    }
    
    @Override
    public void start() {
        // Start stalking behavior
    }
    
    @Override
    public void tick() {
        if (this.targetPlayer != null) {
            // Maintain distance while following
            double distance = stalker.squaredDistanceTo(targetPlayer);
            
            if (distance > 64.0) { // 8 blocks
                // Get closer
                stalker.getNavigation().startMovingTo(targetPlayer, 0.8);
            } else if (distance < 16.0) { // 4 blocks
                // Too close, back away
                stalker.getNavigation().stop();
            }
        }
    }
    
    @Override
    public boolean shouldContinue() {
        return !stalker.isCurrentlyHiding() && 
               this.targetPlayer != null && 
               this.targetPlayer.isAlive() &&
               stalker.squaredDistanceTo(targetPlayer) < 256.0;
    }
}
