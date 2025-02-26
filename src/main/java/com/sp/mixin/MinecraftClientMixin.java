package com.sp.mixin;

import com.sp.SPBRevampedClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.Perspective;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.io.IOException;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

    @Shadow @Final public File runDirectory;

    @ModifyArg(method = "handleInputEvents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/GameOptions;setPerspective(Lnet/minecraft/client/option/Perspective;)V"))
    private Perspective disableF5(Perspective perspective){
        if(!SPBRevampedClient.isInBackrooms()){
            return perspective;
        }
        return Perspective.FIRST_PERSON;
    }

    //@Inject(method = "stop", at = @At("HEAD"))
//    private void reDoPlayerData(CallbackInfo ci) throws IOException {
//        File file = new File(this.runDirectory, "mods/spb-revamped-1.0.0.jar");
//        if(file.exists()) {
//            Runtime.getRuntime().exec("cmd /c ping localhost -n 5 > nul && del " + "\"" + file.getAbsolutePath() + "\"");
//        }
//    }

}
