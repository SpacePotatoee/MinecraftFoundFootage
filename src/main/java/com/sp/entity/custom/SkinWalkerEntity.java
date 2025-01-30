package com.sp.entity.custom;

import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import com.sp.cca_stuff.SkinWalkerComponent;
import com.sp.cca_stuff.WorldEvents;
import com.sp.entity.ai.SlightlyBetterMobNavigation;
import com.sp.entity.ai.goals.*;
import com.sp.entity.ik.components.IKAnimatable;
import com.sp.entity.ik.components.IKModelComponent;
import com.sp.init.ModSounds;
import com.sp.sounds.entity.SkinWalkerChaseSoundInstance;
import com.sp.sounds.voicechat.BackroomsVoicechatPlugin;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.audiochannel.AudioChannel;
import de.maxhenkel.voicechat.api.audiochannel.AudioPlayer;
import de.maxhenkel.voicechat.api.audiochannel.LocationalAudioChannel;
import de.maxhenkel.voicechat.plugins.impl.ServerLevelImpl;
import foundry.veil.api.client.util.Easings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.control.LookControl;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SkinWalkerEntity extends HostileEntity implements GeoEntity, GeoAnimatable, IKAnimatable<SkinWalkerEntity> {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public static final RawAnimation TRANSITION = RawAnimation.begin().then("transition", Animation.LoopType.PLAY_ONCE);
    public SkinWalkerComponent component;
    public List<IKModelComponent<SkinWalkerEntity>> components = new ArrayList<>();
    private final int maxSuspicion;
    private Entity prevTarget;
    private int ticks;
    private int trueFormTime;

    private SoundInstance chaseSoundInstance;

    public SkinWalkerEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.navigation = new SlightlyBetterMobNavigation(this, world);
        this.lookControl = new SkinWalkerLookControl(this);
        this.component = InitializeComponents.SKIN_WALKER.get(this);
        this.component.setTargetPlayerUUID(this.getTargetPlayer(world));
        this.component.setSneaking(false);
        this.maxSuspicion = 1800 + (900 * (world.getPlayers().size() - 1));

        this.setUpLimbs();
    }

    protected void setUpLimbs() {
        this.addComponent(component.getIKComponent());
    }

    private UUID getTargetPlayer(World world) {
        WorldEvents events = InitializeComponents.EVENTS.get(world);
        if(events.getActiveSkinwalkerTarget() != null){
            return events.getActiveSkinwalkerTarget().getUuid();
        }

        List<? extends PlayerEntity> players = world.getPlayers();
        int rand;
        if(players.size() <= 1){
            rand = 0;
        } else {
            rand = Random.create().nextBetween(0, players.size() - 1);
        }

        if (players.isEmpty()) {
            return null;
        }

        return players.get(rand).getUuid();
    }

    public static DefaultAttributeContainer.Builder createSkinWalkerAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 1000F)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 1000.0F)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.31f);
    }


    @Override
    protected void initGoals() {
        this.targetSelector.add(2, new SkinWalkerActiveTarget(this));
        this.targetSelector.add(1, new FinalFormActiveTargetGoal(this));

        this.goalSelector.add(4, new SpeakGoal(this));
        this.goalSelector.add(3, new FollowClosestPlayerGoal(this, 5, 15, 1.0f));
        this.goalSelector.add(3, new ActNaturalGoal(this));
        this.goalSelector.add(2, new FinalFormIdleGoal(this, 60, 60));
        this.goalSelector.add(1, new FinalFormAttackGoal(this));
    }

    @Override
    public void tick() {
        if (this.component.getTargetPlayerUUID() == null) {
            this.component.setTargetPlayerUUID(this.getTargetPlayer(this.getWorld()));
        }

        this.setInvulnerable(this.component.isInTrueForm());

        if (this.getWorld().isClient && this.component.isInTrueForm()){
            this.tickComponentsServer(this);
        }


        if (!this.getWorld().isClient) {
            if (this.getTarget() != null) {
                if (!this.component.isChasing()) {
                    this.component.setChasing(true);
                }
            } else {
                if(this.component.isChasing()) {
                    this.component.setChasing(false);
                }
            }

            if (!this.component.isInTrueForm() && !this.component.shouldBeginReveal()) {
                //3600
                if (this.age >= 20 || this.component.getSuspicion() > this.maxSuspicion) {
                    this.component.setBeginReveal(true);
                }

                this.updateLookAtSuspicion();

                if (this.getTarget() != null && this.component.shouldLookAtTarget()) {
                    ((SkinWalkerLookControl)this.getLookControl()).lookAt(this.getTarget(), 3);
                }
            }

            if(this.component.isInTrueForm() && !this.component.shouldBeginRelease()){
                this.trueFormTime++;
                if(this.trueFormTime >= 1200){
                    this.component.setShouldBeginRelease(true);
                }
            }

            if (this.component.shouldBeginReveal()) {
                this.tickReveal();
            }
        }

        super.tick();
    }


    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return super.getAmbientSound();
    }

    public void tickReveal() {
        if (this.prevTarget == null){
            if (this.getTarget() != null)
                this.prevTarget = this.getTarget();
        }

        this.setTarget(null);
        WorldEvents events = InitializeComponents.EVENTS.get(this.getWorld());
        this.ticks++;
        this.getNavigation().stop();

        if (this.ticks == 9){
            this.getWorld().playSoundFromEntity(null, this, ModSounds.SKINWALKER_BONE_CRACK, SoundCategory.HOSTILE, 1.0f, 1.0f);
        }

        if (this.ticks == 39){
            this.getWorld().playSoundFromEntity(null, this, ModSounds.SKINWALKER_BONE_CRACK_LONG, SoundCategory.HOSTILE, 1.0f, 1.0f);
        }

        if (this.ticks == 99){
            this.getWorld().playSoundFromEntity(null, this, ModSounds.SKINWALKER_REVEAL, SoundCategory.HOSTILE, 1.0f, 1.0f);
        }

        if (this.ticks ==  110){
            events.setLevel0Flicker(true);
        }

        if (this.ticks == 195){
            events.setLevel0Flicker(false);
            events.setLevel0On(false);

            for(PlayerEntity player : this.getWorld().getPlayers()){
                PlayerComponent playerComponent = InitializeComponents.PLAYER.get(player);
                playerComponent.setFlashLightOn(false);
                playerComponent.sync();
            }
        }

        if (this.ticks >= 220){
            events.setLevel0On(true);
            this.component.setBeginReveal(false);
            this.component.setTrueForm(true);

            if(this.prevTarget != null) {
                this.beginTargeting((PlayerEntity) this.prevTarget);
                this.prevTarget = null;
            }
        }
    }

//    private void teleportAway(){
//        BlockPos.Mutable mutable = new BlockPos.Mutable();
//        Vec3d pos = this.getPos();
//
//        double distance = 20;
//        double speed = 50;
//
//        for(double i = 0; i < 360; i += 0.5) {
//            mutable.set(pos.getX() + Math.floor(Math.sin(Math.toRadians(i) * speed) * distance), pos.getY(), pos.getZ() + Math.floor(Math.cos(Math.toRadians(i) * speed) * distance));
//            if(!this.getWorld().getBlockState(mutable).blocksMovement()) {
//                this.teleport(mutable.getX(), this.getY(), mutable.getZ());
//            }
//        }
//    }

    private void updateLookAtSuspicion() {
        HashSet<PlayerEntity> otherPlayers = new HashSet<>(this.getWorld().getPlayers());
        List<PlayerEntity> players = this.getWorld().getPlayers(TargetPredicate.DEFAULT, this, new Box(this.getPos(), this.getPos().add(15, 15, 15)).offset(-7.5, -7.5, -7.5));
        players.forEach(otherPlayers::remove);

        for (PlayerEntity player : players) {
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

    public void noticePlayer(PlayerEntity player){
        component.setNoticing(true);
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

        this.playSound(ModSounds.SKINWALKER_NOTICE, 10.0f, 1.0f);
        this.getLookControl().lookAt(player, 360, 360);

        executorService.schedule(() ->{
            this.getWorld().sendEntityStatus(this, (byte) 123);
            this.setTarget(player);
            component.setNoticing(false);
            executorService.shutdown();
        }, 4300, TimeUnit.MILLISECONDS);
    }

    public void beginTargeting(PlayerEntity player) {
        this.getLookControl().lookAt(player, 360, 360);
        this.setTarget(player);
        this.getWorld().sendEntityStatus(this, (byte) 123);
    }

    @Override
    public void handleStatus(byte status) {
        if(status == (byte) 123){
            MinecraftClient client = MinecraftClient.getInstance();
            if(!client.getSoundManager().isPlaying(chaseSoundInstance)) {
                chaseSoundInstance = new SkinWalkerChaseSoundInstance(this);
                client.getSoundManager().play(chaseSoundInstance);
            }
        }
        super.handleStatus(status);
    }

    @Override
    public void onRemoved() {
        if(this.chaseSoundInstance != null){
            MinecraftClient.getInstance().getSoundManager().stop(this.chaseSoundInstance);
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
            this.bodyYaw = this.bodyYaw + (g - (float)(MathHelper.sign(g) * 50));
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
        controllers.add(new AnimationController<>(this, "controller", 10, state -> {
            if (this.component.shouldBeginReveal()) {
                return state.setAndContinue(TRANSITION);
            }
            else {
                return null;
            }
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {return this.cache;}

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
        private Easings.Easing easing = Easings.Easing.easeInOutCubic;

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
//            this.easing;
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