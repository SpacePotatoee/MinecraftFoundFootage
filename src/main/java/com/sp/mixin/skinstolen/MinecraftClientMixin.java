package com.sp.mixin.skinstolen;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import com.sp.entity.client.SkinWalkerCapturedFlavorText;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @WrapOperation(method = "handleInputEvents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;setScreen(Lnet/minecraft/client/gui/screen/Screen;)V", ordinal = 1))
    private void disableInventory(MinecraftClient instance, Screen screen, Operation<Void> original){
        if(instance.player != null) {
            PlayerComponent component = InitializeComponents.PLAYER.get(instance.player);
            if (!component.hasBeenCaptured()) {
                original.call(instance, screen);
            } else {
                SkinWalkerCapturedFlavorText.triedToOpenInventory = true;
            }
        }
    }
}
