package com.sp.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.sp.SPBRevampedClient;
import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public class PlayerEntityRendererMixin {

    @Inject(method = "setModelPose", at = @At("TAIL"))
    private void shouldRender(AbstractClientPlayerEntity player, CallbackInfo ci, @Local PlayerEntityModel<AbstractClientPlayerEntity> playerEntityModel){
        if(SPBRevampedClient.getCutsceneManager().isPlaying) {
            playerEntityModel.setVisible(false);
        }

        PlayerComponent playerComponent = InitializeComponents.PLAYER.get(player);
        if (!playerComponent.isShouldRender()) {
            playerEntityModel.setVisible(false);
        }
    }

}
