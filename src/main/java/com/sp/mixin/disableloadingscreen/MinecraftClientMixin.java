package com.sp.mixin.disableloadingscreen;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.sp.SPBRevampedClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ProgressScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

    @WrapWithCondition(method = "joinWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ProgressScreen;setTitle(Lnet/minecraft/text/Text;)V"))
    private boolean doNothing(ProgressScreen instance, Text title){
        return !SPBRevampedClient.isInBackrooms();
    }
}
