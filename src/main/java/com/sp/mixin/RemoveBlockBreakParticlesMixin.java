package com.sp.mixin;

import com.sp.SPBRevampedClient;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ParticleManager.class)
public class RemoveBlockBreakParticlesMixin {

    @Inject(method = "addBlockBreakingParticles", at = @At("HEAD"), cancellable = true)
    private void cancel(BlockPos pos, Direction direction, CallbackInfo ci) {
        if (SPBRevampedClient.shouldRenderCameraEffect()) {
            ci.cancel();
        }
    }
}
