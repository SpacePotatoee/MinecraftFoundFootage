package com.sp.entity.custom;

import com.sp.entity.ai.SlightlyBetterMobNavigation;
import com.sp.entity.ai.goals.FollowClosestPlayerGoal;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.UUID;

public class SkinWalkerEntity extends HostileEntity implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final UUID targetPlayerID;
    private boolean active;
    private boolean trueForm;
    private int tick;

    public SkinWalkerEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.targetPlayerID = this.getRandomPlayer(world);
        this.navigation = new SlightlyBetterMobNavigation(this, world);
        this.active = false;
        this.trueForm = false;
        this.tick = 0;
    }

    private UUID getRandomPlayer(World world){
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
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, getMovementSpeedFloat());
    }


    @Override
    protected void initGoals() {
        this.targetSelector.add(1, new ActiveTargetGoal(this, PlayerEntity.class, false));
        this.goalSelector.add(1, new FollowClosestPlayerGoal(this, 5, 15));
//        this.goalSelector.add(1, new MeleeAttackGoal(this, 1.4, false));
    }

    @Override
    public void tick() {
        this.tick++;

        if(!this.isInTrueForm() && this.getTarget() != null) {
            this.getLookControl().lookAt(this.getTarget(), 20f, 20.0f);
        }

        super.tick();
    }

    //GECKO LIB STUFF
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {return this.cache;}




    public static float getMovementSpeedFloat() {
        return 0.25f;
    }

    public boolean isIdle() {
        return !this.active;
    }

    public boolean isInTrueForm() {
        return this.trueForm;
    }

    public AbstractClientPlayerEntity getTargetPlayer(ClientWorld world){
        return (AbstractClientPlayerEntity) world.getPlayerByUuid(this.targetPlayerID);
    }
}