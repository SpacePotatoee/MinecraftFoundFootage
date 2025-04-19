package com.sp.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.sp.SPBRevampedClient;
import com.sp.compat.modmenu.ConfigStuff;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.GraphicsMode;
import net.minecraft.client.option.SimpleOption;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameOptions.class)
public class GameOptionsMixin {
    @Unique
    private Double prevFovEffectScale;

    @Shadow @Final private SimpleOption<Double> fovEffectScale;

    @ModifyReturnValue(method = "getEntityShadows", at = @At("RETURN"))
    private SimpleOption<Boolean> disableEntityShadows(SimpleOption<Boolean> original){
        if (SPBRevampedClient.shouldRenderCameraEffect()) {
            original.setValue(false);
        }
        return original;
    }

    @ModifyReturnValue(method = "getGraphicsMode", at = @At("RETURN"))
    private SimpleOption<GraphicsMode> disableGraphicsMode(SimpleOption<GraphicsMode> original){
        if (SPBRevampedClient.shouldRenderCameraEffect() && original.getValue() == GraphicsMode.FABULOUS) {
            original.setValue(GraphicsMode.FAST);
        }

        return original;
    }

    @Inject(method = "getFovEffectScale", at = @At("HEAD"), cancellable = true)
    private void disableSprintFOVChange(CallbackInfoReturnable<SimpleOption<Double>> cir){
        if (SPBRevampedClient.shouldRenderCameraEffect()) {
            cir.cancel();
            if (ConfigStuff.enableRealCamera){
                if(this.prevFovEffectScale == null){
                    this.prevFovEffectScale = this.fovEffectScale.getValue();
                }
                this.fovEffectScale.setValue(0.0);
                cir.setReturnValue(this.fovEffectScale);
            } else {
                if(this.prevFovEffectScale != null){
                    this.fovEffectScale.setValue(this.prevFovEffectScale);
                    this.prevFovEffectScale = null;
                }
                cir.setReturnValue(this.fovEffectScale);
            }
        }
    }

}
