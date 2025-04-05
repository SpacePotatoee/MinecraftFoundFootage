package com.sp.mixin;

import com.sp.SPBRevamped;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.resource.ResourcePackManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

    @Shadow private static MinecraftClient instance;

    @Shadow @Final public GameOptions options;

    @Shadow @Final private ResourcePackManager resourcePackManager;

    //@ModifyArg(method = "handleInputEvents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/GameOptions;setPerspective(Lnet/minecraft/client/option/Perspective;)V"))
//    private Perspective disableF5(Perspective perspective){
//        if(!SPBRevampedClient.isInBackrooms()){
//            return perspective;
//        }
//        return Perspective.FIRST_PERSON;
//    }

    //@Inject(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;render(Z)V", shift = At.Shift.AFTER))
    private void enableDeferredResourcePack(CallbackInfo ci){
        if(instance != null && resourcePackManager != null) {
            if(!resourcePackManager.getEnabledProfiles().contains(resourcePackManager.getProfile("veil:deferred"))) {
                SPBRevamped.LOGGER.info("Re-enabled Deferred Resourcepack");
                resourcePackManager.enable("veil:deferred");
                options.refreshResourcePacks(resourcePackManager);
            }
        }
    }
}
