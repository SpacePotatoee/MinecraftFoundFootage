package com.sp.entity.ai.goals;

import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.SkinWalkerComponent;
import com.sp.entity.custom.SkinWalkerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.util.math.BlockPos;

public class FinalFormAttackGoal extends MeleeAttackGoal {
    private final SkinWalkerComponent component;

    public FinalFormAttackGoal(SkinWalkerEntity entity) {
        super(entity, 1.15, false);
        this.component = InitializeComponents.SKIN_WALKER.get(entity);
    }

    @Override
    public boolean canStart() {
        if(this.component.isInTrueForm()) {
            long l = this.mob.getWorld().getTime();
            if (l - this.lastUpdateTime < 20L) {
                return false;
            } else {
                this.lastUpdateTime = l;
                LivingEntity livingEntity = this.mob.getTarget();
                if (livingEntity == null) {
                    this.moveToLastKnownLocation();
                    return false;
                } else if (!livingEntity.isAlive()) {
                    this.moveToLastKnownLocation();
                    return false;
                } else {
                    this.path = this.mob.getNavigation().findPathTo(livingEntity, 0);
                    return this.path != null
                            ? true
                            : this.getSquaredMaxAttackDistance(livingEntity) >= this.mob.squaredDistanceTo(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ());
                }
            }
        }

        return false;
    }

    private void moveToLastKnownLocation() {
        BlockPos pos = this.component.getLastKnownTargetLocation();

        if(pos != null){
            this.mob.getNavigation().startMovingTo(pos.getX(), pos.getY(), pos.getZ(), 1.0);
        }
    }


}
