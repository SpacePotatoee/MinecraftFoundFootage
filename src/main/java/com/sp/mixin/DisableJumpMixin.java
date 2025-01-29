package com.sp.mixin;

import com.sp.SPBRevampedClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.entity.player.PlayerEntity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(KeyboardInput.class)
public class DisableJumpMixin {

    @Redirect(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/client/input/KeyboardInput;jumping:Z", opcode = Opcodes.PUTFIELD))
    private void disableJumping(KeyboardInput instance, boolean value){
        PlayerEntity player = MinecraftClient.getInstance().player;

        if(player != null) {
            if(player.isTouchingWater() || player.isCreative() || player.isSpectator() || !SPBRevampedClient.isInBackrooms()) {
                instance.jumping = value;
            } else {
                instance.jumping = false;
            }
        } else {
            instance.jumping = false;
        }
    }

}
