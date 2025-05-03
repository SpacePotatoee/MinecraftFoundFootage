package com.sp.mixin.hudandresolution;

import com.sp.render.VhsAspectRatio;
import net.minecraft.client.util.Monitor;
import net.minecraft.client.util.VideoMode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Monitor.class)
public class MonitorMixin {

    @Shadow @Final private List<VideoMode> videoModes;
    @Unique private static Integer maxRefreshRate = null;

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
