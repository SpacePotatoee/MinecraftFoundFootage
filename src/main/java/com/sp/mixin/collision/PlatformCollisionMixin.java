package com.sp.mixin.collision;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.sp.util.CollisionHelper;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Entity.class)
public abstract class PlatformCollisionMixin {
    @WrapMethod(method = "adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;")
    private Vec3d adjustMovementForCollisions(Vec3d movement, Operation<Vec3d> original) {
        Entity entity = (Entity) (Object) this;

        Vec3d adjustedMovement = movement;

        if (entity != null) {
            if (movement.y != 0) {
                double yAxisCollision = CollisionHelper.getCollisionOffset(entity, adjustedMovement);

                adjustedMovement = adjustedMovement.add(0, yAxisCollision, 0);
            }

            return original.call(adjustedMovement);
        }

        return original.call(movement);
    }
}
