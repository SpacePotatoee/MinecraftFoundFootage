package com.sp.entity.ai.goals;

import com.sp.entity.custom.SkinWalkerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.player.PlayerEntity;

public class FollowClosestPlayerGoal extends Goal {
    private final SkinWalkerEntity entity;
    private final EntityNavigation navigation;
    private final float minDistance;
    private final float maxDistance;
    private PlayerEntity target;
    private double speed;


    public FollowClosestPlayerGoal(SkinWalkerEntity entity, float minDistance, float maxDistance) {
        this.entity = entity;
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
        this.navigation = entity.getNavigation();
    }

    @Override
    public boolean canStart() {
        if(entity != null){
            if(!entity.isInTrueForm()) {
                PlayerEntity player = entity.method_48926().getClosestPlayer(this.entity, 200);

                if(player != null) {
                    if(!this.isTooClose(player)) {
                        this.target = player;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean shouldContinue() {
        if(this.isTooClose(this.target)){
            return false;
        }
        return super.shouldContinue();
    }

    @Override
    public void stop() {
        this.target = null;
        this.entity.getNavigation().stop();
    }

    @Override
    public void tick() {
        if(this.isTooFar(this.target)){
            this.entity.setSprinting(true);
            this.speed = SkinWalkerEntity.getMovementSpeedFloat();
        } else {
            this.entity.setSprinting(false);
            this.speed = SkinWalkerEntity.getMovementSpeedFloat();
        }

        this.entity.getNavigation().startMovingTo(this.target, this.speed);
    }

    private boolean isTooClose(Entity entity){
        return this.entity.squaredDistanceTo(entity) < (double) (this.minDistance * this.minDistance);
    }

    private boolean isTooFar(Entity entity){
        return this.entity.squaredDistanceTo(entity) > (double) (this.maxDistance * this.maxDistance);
    }
}