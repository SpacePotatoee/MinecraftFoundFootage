package com.sp.entity.ai;

import com.sp.entity.custom.SkinWalkerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.World;

public class SlightlyBetterMobNavigation extends MobNavigation {

    public SlightlyBetterMobNavigation(MobEntity mobEntity, World world) {
        super(mobEntity, world);
    }

    @Override
    public void tick() {
        LivingEntity livingEntity = this.entity.getTarget();
        if(livingEntity != null) {
            if (this.entity.canSee(livingEntity)) {
                if (!this.isIdle() || !((SkinWalkerEntity)this.entity).isIdle()) {
                    this.entity.getMoveControl().moveTo(livingEntity.getX(), this.adjustTargetY(livingEntity.getPos()), livingEntity.getZ(), this.speed);
                    return;
                }
            }
        }
        super.tick();
    }
}