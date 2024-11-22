package com.sp.mixin;

import com.sp.entity.custom.SkinWalkerEntity;
import net.minecraft.entity.ai.control.BodyControl;
import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BodyControl.class)
public class BodyControlMixin {

    @Shadow @Final private MobEntity entity;

    @Inject(method = "slowlyAdjustBody", at = @At("HEAD"), cancellable = true)
    private void redirect(CallbackInfo ci){
        if(this.entity instanceof SkinWalkerEntity) {
            ci.cancel();
        }
    }

}
