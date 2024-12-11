package com.sp.entity.ai.goals;

import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.SkinWalkerComponent;
import com.sp.entity.custom.SkinWalkerEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.EntityPredicates;

import java.util.Comparator;
import java.util.List;

public class FinalFormActiveTargetGoal extends TrackTargetGoal {
    private final SkinWalkerComponent component;

    public FinalFormActiveTargetGoal(SkinWalkerEntity entity){
        super(entity, true, false);
        this.component = InitializeComponents.SKIN_WALKER.get(entity);
        this.maxTimeWithoutVisibility = 100;
    }

    @Override
    public boolean canStart() {
        if(this.component.isInTrueForm() && !this.component.shouldBeginReveal()){
            if(this.mob.getTarget() != null){
                this.target = this.mob.getTarget();
                return true;
            }

            List<PlayerEntity> playerEntityList = this.mob.getWorld().getPlayers(
                    TargetPredicate.DEFAULT
                            .ignoreDistanceScalingFactor()
                            .ignoreVisibility()
                            .setBaseMaxDistance(100)
                            .setPredicate(EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR::test),
                    this.mob,
                    this.mob.getBoundingBox().expand(100));
            playerEntityList.sort(Comparator.comparingDouble(player -> -this.mob.getPos().squaredDistanceTo(player.getPos().x, player.getPos().y, player.getPos().z)));

            for (PlayerEntity player : playerEntityList){
                if(this.mob.canSee(player)){
                    this.target = player;
                    ((SkinWalkerEntity)this.mob).beginTargeting(player);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean shouldContinue() {
        return super.shouldContinue() && this.canStart();
    }

    @Override
    public void tick() {
        if(this.target != null) {
            this.component.setLastKnownTargetLocation(this.target.getBlockPos());
        }
    }


}
