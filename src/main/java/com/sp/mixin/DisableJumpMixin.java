package com.sp.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.sp.SPBRevampedClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.entity.player.PlayerEntity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(KeyboardInput.class)
public class DisableJumpMixin {

    @WrapOperation(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/client/input/KeyboardInput;jumping:Z", opcode = Opcodes.PUTFIELD))
    private void disableJumping(KeyboardInput instance, boolean value, Operation<Void> original){
        PlayerEntity player = MinecraftClient.getInstance().player;

        if(player != null) {
            if(player.isTouchingWater() || player.isCreative() || player.isSpectator() || !SPBRevampedClient.isInBackrooms()) {
                original.call(instance, value);
            } else {
                instance.jumping = false;
            }
        } else {
            instance.jumping = false;
        }
    }

}
