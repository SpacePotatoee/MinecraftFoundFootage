package com.sp.mixin;

import com.sp.init.BackroomsLevels;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(foundry.veil.impl.client.render.deferred.light.VanillaLightRenderer.class)
public class VanillaLightRenderer {

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;getBrightness(Lnet/minecraft/util/math/Direction;Z)F"))
    private float noWeirdBrightnessThing(ClientWorld instance, Direction direction, boolean shaded) {
        if(instance.getRegistryKey() == BackroomsLevels.POOLROOMS_WORLD_KEY){
            return 0.9f;
        }

        return instance.getBrightness(direction, shaded);
    }

}
