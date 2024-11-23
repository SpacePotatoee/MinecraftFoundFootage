package com.sp.entity.ai.goals;

import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.SkinWalkerComponent;
import com.sp.entity.custom.SkinWalkerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;

public class FollowClosestPlayerGoal extends Goal {
    private final SkinWalkerEntity entity;
    private final SkinWalkerComponent component;
    private final float minDistance;
    private final float maxDistance;
    private PlayerEntity target;
    private double speed;


    public FollowClosestPlayerGoal(SkinWalkerEntity entity, float minDistance, float maxDistance, float speed) {
        this.entity = entity;
        this.component = InitializeComponents.SKIN_WALKER.get(entity);
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
        this.speed = speed;
    }

    @Override
    public boolean canStart() {
        if(!this.component.isInTrueForm() && !this.component.isCurrentlyActingNatural()) {
            PlayerEntity player = this.entity.getWorld().getClosestPlayer(this.entity, 200);

            if(player != null) {
                if(!this.isTooClose(player) && !player.isSpectator() && !player.isCreative()) {
                    this.target = player;
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void start() {
        this.component.setNearestTarget(this.target);
        this.component.setShouldActNatural(false);
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
        if(this.target == null){
            this.component.setNearestTarget(null);
        }
        this.target = null;
        this.component.setShouldActNatural(true);
        this.entity.getNavigation().stop();
    }

    @Override
    public void tick() {
        if(this.isTooFar(this.target)){
            this.entity.setSprinting(true);
        } else {
            this.entity.setSprinting(false);
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