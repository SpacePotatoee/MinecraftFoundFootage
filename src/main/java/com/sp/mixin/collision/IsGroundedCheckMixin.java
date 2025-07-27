package com.sp.mixin.collision;

import com.sp.util.CollisionHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Entity.class)
public abstract class IsGroundedCheckMixin {
//    @Shadow
//    public abstract World getWorld();
//
//    @Shadow
//    public abstract void onLanding();
//
//    @Inject(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/hit/BlockHitResult;getType()Lnet/minecraft/util/hit/HitResult$Type;"), locals = LocalCapture.CAPTURE_FAILSOFT)
//    public void move(MovementType movementType, Vec3d movement, CallbackInfo ci, Vec3d vec3d, double d, BlockHitResult blockHitResult) {
//        if (CollisionHelper.doesCollide((Entity) (Object) this, movement)) {
//            this.onLanding();
//        }
//    }
}
