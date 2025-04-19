package com.sp.mixin.disableloadingscreen;

import com.sp.SPBRevampedClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ProgressScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

    @Redirect(method = "joinWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ProgressScreen;setTitle(Lnet/minecraft/text/Text;)V"))
    private void doNothing(ProgressScreen instance, Text title){
        if (SPBRevampedClient.isInBackrooms()) {
            return;
        }

        instance.setTitle(Text.translatable("connect.joining"));
    }
}
