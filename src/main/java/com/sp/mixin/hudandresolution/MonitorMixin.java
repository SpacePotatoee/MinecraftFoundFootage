package com.sp.mixin.hudandresolution;

import com.llamalad7.mixinextras.sugar.Local;
import com.sp.SPBRevampedClient;
import com.sp.render.VhsAspectRatio;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Monitor;
import net.minecraft.client.util.VideoMode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.util.List;

@Mixin(Monitor.class)
public class MonitorMixin {

    @Shadow @Final private List<VideoMode> videoModes;
    @Unique private static Integer maxRefreshRate = null;

//    @Inject(method = "populateVideoModes", at = @At(value = "INVOKE", target = "Ljava/util/List;clear()V", shift = At.Shift.AFTER))
//    private void clearList(CallbackInfo ci){
////        VhsAspectRatio.vhsAspectRatiosList.clear();
//        System.out.println("========================================================================================");
//    }
//
//    @Inject(method = "populateVideoModes", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", shift = At.Shift.AFTER))
//    private void get43AspectRatios(CallbackInfo ci, @Local VideoMode videoMode){
//        if ((float) videoMode.getWidth() / (float)videoMode.getHeight() == 1.3333334f){
//            System.out.println(videoMode.asString());
//            if(MinecraftClient.getInstance().getWindow() != null)
//            System.out.println(MinecraftClient.getInstance().getWindow().getRefreshRate());
//            DisplayMode.get
////            System.out.println((float) videoMode.getWidth() / videoMode.getHeight());
//            VhsAspectRatio.vhsAspectRatiosList.add(videoMode);
//        }
//    }

    @Inject(method = "populateVideoModes", at = @At(value = "INVOKE", target = "Lorg/lwjgl/glfw/GLFW;glfwGetMonitorPos(J[I[I)V"))
    private void get43AspectRatios(CallbackInfo ci){
        //Find the highest refresh rate
        for(VideoMode videoMode : this.videoModes){
            if(maxRefreshRate == null || videoMode.getRefreshRate() > maxRefreshRate){
                maxRefreshRate = videoMode.getRefreshRate();
            }
        }


        for(VideoMode videoMode : this.videoModes){
            if((float) videoMode.getWidth() / videoMode.getHeight() == (float) 4/3 && videoMode.getRefreshRate() == maxRefreshRate){
                VhsAspectRatio.vhsAspectRatiosList.add(videoMode);
            }
        }

//        VhsAspectRatio.normalVideoModesList.clear();
        VhsAspectRatio.normalVideoModesList = this.videoModes;
    }


}
