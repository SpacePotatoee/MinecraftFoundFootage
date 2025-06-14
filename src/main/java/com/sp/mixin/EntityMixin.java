package com.sp.mixin;

import com.sp.init.BackroomsLevels;
import com.sp.init.ModDamageTypes;
import com.sp.init.ModSounds;
import com.sp.world.levels.custom.PoolroomsBackroomsLevel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Shadow private World world;

    @Shadow public abstract boolean isTouchingWater();

    @Shadow public abstract boolean damage(DamageSource source, float amount);

    @Inject(method = "baseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;updateSwimming()V"))
    private void acidWater(CallbackInfo ci) {

        BackroomsLevels.getLevel(world).ifPresent(backroomsLevel -> {
            if (!(backroomsLevel instanceof PoolroomsBackroomsLevel level)) {
                return;
            }

            if(!level.isNoon()){
                if(this.isTouchingWater()){
                    this.damage(ModDamageTypes.of(world, ModDamageTypes.ACID_WATER), 1.0f);
                }
            }
        });
    }

    @Inject(method = "getSwimSound", at = @At("RETURN"), cancellable = true)
    private void newSwimSound(CallbackInfoReturnable<SoundEvent> cir){
        cir.setReturnValue(ModSounds.SWIM);
    }

    @ModifyArg(method = "playSwimSound()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;playSwimSound(F)V"))
    private float sfxLouder(float volume){
        return volume + 0.1f;
    }


}
