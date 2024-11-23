package com.sp.entity.ai;

import com.sp.entity.custom.SkinWalkerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class SlightlyBetterMobNavigation extends MobNavigation {

    public SlightlyBetterMobNavigation(MobEntity mobEntity, World world) {
        super(mobEntity, world);
    }

    @Override
    public void tick() {
        LivingEntity livingEntity = this.entity.getTarget();
        if(livingEntity != null) {
            if (this.canSee(this.entity, livingEntity)) {
                if (!this.isIdle() || ((SkinWalkerEntity)this.entity).component.isActive()) {
                    this.entity.getMoveControl().moveTo(livingEntity.getX(), this.adjustTargetY(livingEntity.getPos()), livingEntity.getZ(), this.speed);
                    return;
                }
            }
        }
        super.tick();
    }

    private boolean canSee(Entity entity1, Entity entity2) {
        if (entity2.getWorld() != entity1.getWorld()) {
            return false;
        } else {
            Vec3d vec3d = new Vec3d(entity1.getX(), entity1.getY(), entity1.getZ());
            Vec3d vec3d2 = new Vec3d(entity2.getX(), entity2.getEyeY(), entity2.getZ());
            return vec3d2.distanceTo(vec3d) > 128.0
                    ? false
                    : entity1.getWorld().raycast(new RaycastContext(vec3d, vec3d2, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, entity1)).getType()
                    == HitResult.Type.MISS;
        }
    }
}