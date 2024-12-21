package com.sp.mixin;

import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {

    @Inject(method = "canBeSpectated", at = @At("HEAD"), cancellable = true)
    private void stopSpectatorPlayersFromNotBeingCounted(ServerPlayerEntity spectator, CallbackInfoReturnable<Boolean> cir){
        PlayerComponent component = InitializeComponents.PLAYER.get((ServerPlayerEntity) (Object) this);
        if(component.hasBeenCaptured() || component.isBeingCaptured()){
            cir.setReturnValue(true);
        }
    }

}
