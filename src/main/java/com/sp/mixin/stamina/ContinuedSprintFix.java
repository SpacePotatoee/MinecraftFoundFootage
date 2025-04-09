package com.sp.mixin.stamina;

import com.sp.mixininterfaces.ServerPlayNetworkSprint;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class ContinuedSprintFix implements ServerPlayNetworkSprint {
    @Unique private boolean shouldStopSprinting = true;

    @Inject(method = "onClientCommand", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;setSprinting(Z)V", ordinal = 0))
    private void setToFalse(ClientCommandC2SPacket packet, CallbackInfo ci){
        this.shouldStopSprinting = false;
    }

    @Inject(method = "onClientCommand", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;setSprinting(Z)V", ordinal = 1))
    private void setToTrue(ClientCommandC2SPacket packet, CallbackInfo ci){
        this.shouldStopSprinting = true;
    }

    @Override
    public boolean getShouldStopSprinting() {
        return this.shouldStopSprinting;
    }
}
