package com.sp.mixin;

import com.sp.entity.ik.util.PrAnCommonClass;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Keyboard;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Keyboard.class)
public abstract class DinoDebugRendererMixin {
    @Shadow protected abstract void debugLog(Text text);

    @Unique
    private static final int L = 74;

    @Inject(method = "processF3", at = @At("HEAD"), cancellable = true)
    private void onHandleDebugKeys(int keyCode, CallbackInfoReturnable<Boolean> cir) {
        if(FabricLoader.getInstance().isDevelopmentEnvironment()) {
            if (keyCode == L) {
                PrAnCommonClass.shouldRenderDebugLegs = !PrAnCommonClass.shouldRenderDebugLegs;
                this.debugLog(Text.translatable("debug.toggled_joint_debug.message"));
                cir.setReturnValue(true);
            }
        }
    }
}