package com.sp.entity.ai.goals;

import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import com.sp.cca_stuff.SkinWalkerComponent;
import com.sp.entity.custom.SkinWalkerEntity;
import com.sp.init.ModSounds;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.EntityPredicates;

import java.util.List;
import java.util.function.Predicate;

public class FinalFormIdleGoal extends Goal {
    private final SkinWalkerEntity entity;
    private final SkinWalkerComponent component;
    private final int chance;
    private int idleActionCoolDown;
    private final int maxIdleActionCoolDown;

    public FinalFormIdleGoal(SkinWalkerEntity entity, int chance, int cooldown){
        this.entity = entity;
        this.component = InitializeComponents.SKIN_WALKER.get(entity);
        this.chance = chance;
        this.maxIdleActionCoolDown = toGoalTicks(cooldown);
    }


    @Override
    public boolean canStart() {
        if(this.component.isInTrueForm() && !this.component.isNoticing()){
            if(this.entity.getTarget() == null){
                return true;
            }
        }

        return false;
    }

    @Override
    public void start() {
        this.component.setIdle(true);
    }

    @Override
    public void stop() {
        this.component.setIdle(false);
    }

    @Override
    public void tick() {
        if(!this.entity.getWorld().isClient) {
            List<PlayerEntity> playerEntityList = this.entity.getWorld().getPlayers(
                    TargetPredicate.DEFAULT
                            .ignoreDistanceScalingFactor()
                            .ignoreVisibility()
                            .setBaseMaxDistance(100)
                            .setPredicate(EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR::test),
                    this.entity,
                    this.entity.getBoundingBox().expand(100));

            for (PlayerEntity player : playerEntityList) {
                PlayerComponent playerComponent = InitializeComponents.PLAYER.get(player);
                if (playerComponent.isVisibleToEntity()) {
                    this.entity.noticePlayer(player);
                    return;
                }
            }
        }

        if(this.idleActionCoolDown > 0){
            this.idleActionCoolDown--;

        } else if(this.entity.getRandom().nextInt(toGoalTicks(this.chance)) == 0){
            if(this.entity.getRandom().nextBoolean()){
                this.entity.playSound(ModSounds.SKINWALKER_AMBIENCE, 10.0f, 1.0f);
            } else {
                this.entity.playSound(ModSounds.SKINWALKER_SNIFF, 10.0f, 1.0f);
            }

            this.idleActionCoolDown = this.maxIdleActionCoolDown;
        }

    }

}
