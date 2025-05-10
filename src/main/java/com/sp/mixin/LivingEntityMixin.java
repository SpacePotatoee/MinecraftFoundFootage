package com.sp.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.sp.init.BackroomsLevels;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @WrapOperation(method = "fall", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;spawnParticles(Lnet/minecraft/particle/ParticleEffect;DDDIDDDD)I"))
    private <T extends ParticleEffect> int noFallParticles(ServerWorld instance, T particle, double x, double y, double z, int count, double deltaX, double deltaY, double deltaZ, double speed, Operation<Integer> original){
        if(!BackroomsLevels.isInBackrooms(instance.getRegistryKey())){
            //noinspection MixinExtrasOperationParameters
            return original.call(instance, particle, x, y, z, count, deltaX, deltaY, deltaZ, speed);
        }

        return 0;

    }
}
