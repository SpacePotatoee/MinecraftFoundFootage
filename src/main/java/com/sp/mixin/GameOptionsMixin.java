package com.sp.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GameOptions.class)
public class GameOptionsMixin {

    @ModifyReturnValue(method = "getEntityShadows", at = @At("RETURN"))
    private SimpleOption<Boolean> disableEntityShadows(SimpleOption<Boolean> original){
        original.setValue(false);
        return original;
    }

}
