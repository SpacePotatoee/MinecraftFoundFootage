package com.sp.entity.custom;

import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import com.sp.cca_stuff.SkinWalkerComponent;
import com.sp.entity.ai.SlightlyBetterMobNavigation;
import com.sp.entity.ai.goals.ActNaturalGoal;
import com.sp.entity.ai.goals.FollowClosestPlayerGoal;
import com.sp.entity.ai.goals.TalkInChatGoal;
import com.sp.entity.ik.components.IKAnimatable;
import com.sp.entity.ik.components.IKLegComponent;
import com.sp.entity.ik.components.IKModelComponent;
import com.sp.entity.ik.parts.Segment;
import com.sp.entity.ik.parts.ik_chains.TargetReachingIKChain;
import com.sp.entity.ik.parts.sever_limbs.ServerLimb;
import foundry.veil.api.client.util.Easings;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.control.LookControl;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.mob.HoglinBrain;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class SkinWalkerEntity extends HostileEntity implements GeoEntity, IKAnimatable<SkinWalkerEntity> {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final UUID targetPlayerID;
    public SkinWalkerComponent component;
    public List<IKModelComponent<SkinWalkerEntity>> components = new ArrayList<>();

    public SkinWalkerEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.targetPlayerID = this.getRandomPlayer(world);
        this.navigation = new SlightlyBetterMobNavigation(this, world);
        this.lookControl = new SkinWalkerLookControl(this);
        this.component = InitializeComponents.SKIN_WALKER.get(this);
        component.setTargetPlayerUUID(this.targetPlayerID);
        component.setSneaking(false);
        this.setUpLimbs();
    }

    protected void setUpLimbs() {
        this.addComponent(new IKLegComponent<>(
                new IKLegComponent.LegSetting.Builder()
                        .maxDistance(1.5)
                        .stepInFront(1)
                        .movementSpeed(0.4).build(),
                List.of(new ServerLimb(2, 0, 2),
                        new ServerLimb(-2, 0, 2),
                        new ServerLimb(2, 0, -2),
                        new ServerLimb(-2, 0, -2)),
                new TargetReachingIKChain(new Segment.Builder().length(0.65).build(), new Segment.Builder().length(0.9).build(), new Segment.Builder().length(1.3).build(), new Segment.Builder().length(0.85).build()),
                new TargetReachingIKChain(new Segment.Builder().length(0.65).build(), new Segment.Builder().length(0.9).build(), new Segment.Builder().length(1.3).build(), new Segment.Builder().length(0.85).build()),
                new TargetReachingIKChain(new Segment.Builder().length(0.65).build(), new Segment.Builder().length(0.9).build(), new Segment.Builder().length(1.3).build(), new Segment.Builder().length(0.85).build()),
                new TargetReachingIKChain(new Segment.Builder().length(0.65).build(), new Segment.Builder().length(0.9).build(), new Segment.Builder().length(1.3).build(), new Segment.Builder().length(0.85).build())
        ));
    }


    private UUID getRandomPlayer(World world) {
        List<? extends PlayerEntity> players = world.getPlayers();
        int rand;
        if(players.size() <= 1){
            rand = 0;
        } else {
            rand = Random.create().nextBetween(0, players.size() - 1);
        }

        return players.get(rand).getUuid();
    }



    public static DefaultAttributeContainer.Builder createSkinWalkerAttributes(){
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 1000F)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 1000.0F)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.35);
    }


    @Override
    protected void initGoals() {
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, false));
//        this.goalSelector.add(1, new WanderAroundGoal(this, 1.0, 1));
//        this.goalSelector.add(1, new FollowClosestPlayerGoal(this, 5, 15, 1.0f));
//        this.goalSelector.add(2, new ActNaturalGoal(this));
//        this.goalSelector.add(2, new TalkInChatGoal(this, 10));
    }

    @Override
    public void tick() {
        this.tickComponentsServer(this);

        if(!this.getWorld().isClient) {
            if(!this.component.isInTrueForm() && !this.component.shouldBeginReveal()) {
                if (this.age >= 3600 || this.component.getSuspicion() > 900) {
                    this.component.setBeginReveal(true);
                }

                this.updateLookAtSuspicion();

                if (this.getTarget() != null && this.component.shouldLookAtTarget()) {
                    ((SkinWalkerLookControl)this.getLookControl()).lookAt(this.getTarget(), 3);
                }
            }
        }

        super.tick();
    }

    private void updateLookAtSuspicion() {
        HashSet<PlayerEntity> otherPlayers = new HashSet<>(this.getWorld().getPlayers());
        List<PlayerEntity> players = this.getWorld().getPlayers(TargetPredicate.DEFAULT, this, new Box(this.getPos(), this.getPos().add(15, 15, 15)).offset(-7.5, -7.5, -7.5));
        players.forEach(otherPlayers::remove);

        for (PlayerEntity player : players){
            PlayerComponent playerComponent = InitializeComponents.PLAYER.get(player);
            Entity targetEntity = playerComponent.getTargetEntity();

            if(targetEntity != null) {
                if (targetEntity.equals(this)) {
                    if (playerComponent.getSkinWalkerLookDelay() <= 0) {
                        this.component.addSuspicion();
                    } else {
                        playerComponent.subtractSkinWalkerLookDelay();
                    }
                } else {
                    playerComponent.setSkinWalkerLookDelay(60);
                }
            } else {
                playerComponent.setSkinWalkerLookDelay(60);
            }
        }

        for(PlayerEntity player : otherPlayers){
            PlayerComponent playerComponent = InitializeComponents.PLAYER.get(player);
            playerComponent.setSkinWalkerLookDelay(60);
        }
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        boolean bl = super.damage(source, amount);
        if (this.getWorld().isClient) {
            return false;
        } else {
            if (bl && source.getAttacker() instanceof PlayerEntity) {
                this.component.addSuspicion(100);
            }

            return bl;
        }
    }

    @Override
    protected float turnHead(float bodyRotation, float headRotation) {
        if (this.handSwingProgress > 0.0F) {
            bodyRotation = this.getHeadYaw();
        }
        float f = MathHelper.wrapDegrees(bodyRotation - this.bodyYaw);
        this.bodyYaw += f * 0.3F;
        float g = MathHelper.wrapDegrees(this.getHeadYaw() - this.bodyYaw);
        if (Math.abs(g) > 50.0F) {
            this.bodyYaw = this.bodyYaw + (g - (float)(MathHelper.sign((double)g) * 50));
        }

        boolean bl = g < -90.0F || g >= 90.0F;
        if (bl) {
            headRotation *= -1.0F;
        }

        return headRotation;
    }

    @Override
    public int getMaxLookYawChange() {
        return 360;
    }

    @Override
    public int getMaxLookPitchChange() {
        return 360;
    }

    //GECKO LIB STUFF
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {return this.cache;}


    public static float getMovementSpeedFloat() {
        return 0.8f;
    }

    public AbstractClientPlayerEntity getTargetPlayer(ClientWorld world){
        return (AbstractClientPlayerEntity) world.getPlayerByUuid(this.targetPlayerID);
    }

    @Override
    public List<IKModelComponent<SkinWalkerEntity>> getComponents() {
        return this.components;
    }

    @Override
    public double getSize() {
        return 1;
    }

    @SuppressWarnings("InnerClassMayBeStatic")
    public class SkinWalkerLookControl extends LookControl{
        private int maxLookAtTimer = 5;
        private Easings.Easing easing;

        public SkinWalkerLookControl(MobEntity entity) {
            super(entity);
        }

        public void lookAt(Entity entity, int lookTimer){
            this.lookAt(new Vec3d(entity.getX(), getLookingHeightFor(entity), entity.getZ()), lookTimer);
        }

        public void lookAt(Vec3d vec3d, int lookTimer){
            this.x = vec3d.x;
            this.y = vec3d.y;
            this.z = vec3d.z;
            this.maxYawChange = (float)this.entity.getMaxLookYawChange();
            this.maxPitchChange = (float)this.entity.getMaxLookPitchChange();
            this.lookAtTimer = lookTimer;
            this.maxLookAtTimer = lookTimer;
            this.easing = Easings.Easing.easeInOutCubic;
        }

        @Override
        public void tick() {
            if (this.lookAtTimer > 0) {
                this.lookAtTimer--;
                this.getTargetYaw().ifPresent(yaw -> this.entity.headYaw = this.changeAngle2(this.entity.headYaw, yaw, this.maxYawChange));
                this.getTargetPitch().ifPresent(pitch -> this.entity.setPitch(this.changeAngle2(this.entity.getPitch(), pitch, this.maxPitchChange)));
            } else {
                this.entity.headYaw = this.changeAngle(this.entity.headYaw, this.entity.bodyYaw, 10.0F);
            }

            this.clampHeadYaw();
        }

        private float changeAngle2(float from, float to, float max){
            float f = MathHelper.subtractAngles(from, to);
            float g = MathHelper.clamp(f, -max, max);
            return from + (g * this.easing.ease(1 - ((float) this.lookAtTimer / maxLookAtTimer)));
        }

        private static double getLookingHeightFor(Entity entity) {
            return entity instanceof LivingEntity ? entity.getEyeY() : (entity.getBoundingBox().minY + entity.getBoundingBox().maxY) / 2.0;
        }
    }
}