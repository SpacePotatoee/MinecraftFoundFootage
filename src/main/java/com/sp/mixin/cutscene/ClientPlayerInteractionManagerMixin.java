package com.sp.mixin.cutscene;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.sp.SPBRevampedClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {
    @WrapMethod(method = "attackEntity")
    public void attackEntity(PlayerEntity player, Entity target, Operation<Void> original) {
        if (SPBRevampedClient.getCutsceneManager().isPlaying) {
            return;
        }

        original.call(player, target);
    }
}
