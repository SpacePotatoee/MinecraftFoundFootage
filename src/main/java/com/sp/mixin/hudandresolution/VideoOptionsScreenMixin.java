package com.sp.mixin.hudandresolution;

import com.llamalad7.mixinextras.sugar.Local;
import com.sp.compat.modmenu.ConfigStuff;
import com.sp.render.VhsAspectRatio;
import net.minecraft.client.gui.screen.option.VideoOptionsScreen;
import net.minecraft.client.gui.widget.OptionListWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.util.Monitor;
import net.minecraft.client.util.VideoMode;
import net.minecraft.client.util.Window;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;
import java.util.function.Consumer;

@Mixin(VideoOptionsScreen.class)
public class VideoOptionsScreenMixin {
    @Shadow private OptionListWidget list;

    @Redirect(method = "init", at = @At(value = "NEW", target = "(Ljava/lang/String;Lnet/minecraft/client/option/SimpleOption$TooltipFactory;Lnet/minecraft/client/option/SimpleOption$ValueTextGetter;Lnet/minecraft/client/option/SimpleOption$Callbacks;Ljava/lang/Object;Ljava/util/function/Consumer;)Lnet/minecraft/client/option/SimpleOption;"))
    private SimpleOption<Integer> redirectOptionConstructor(String key, SimpleOption.TooltipFactory tooltipFactory, SimpleOption.ValueTextGetter valueTextGetter, SimpleOption.Callbacks callbacks, Object defaultValue, Consumer changeCallback, @Local Window window, @Local Monitor monitor, @Local(ordinal = 1) int j){
        int j2;
        if(ConfigStuff.enableVHSAspectRatio){
            Optional<VideoMode> optional = VhsAspectRatio.currentVhsVideoMode != null ? VhsAspectRatio.currentVhsVideoMode : window.getVideoMode();
            j2 = (Integer)optional.map(this::findClosestVhsVideoModeIndex).orElse(-1);
        } else {
            Optional<VideoMode> optional = window.getVideoMode();
            j2 = (Integer)optional.map(monitor::findClosestVideoModeIndex).orElse(-1);
        }





        return VhsAspectRatio.normalVideoMode = new SimpleOption<>(
                ConfigStuff.enableVHSAspectRatio ? "options.fullscreen.resolution" : "spb-revamped.options.fullscreen.resolution",
                SimpleOption.emptyTooltip(),
                (prefix, value) -> {
                    if (monitor == null) {
                        return Text.translatable("options.fullscreen.unavailable");
                    } else {
                        return value == -1
                                ? GameOptions.getGenericValueText(prefix, Text.translatable("options.fullscreen.current"))
                                : GameOptions.getGenericValueText(prefix, ConfigStuff.enableVHSAspectRatio ? Text.literal(VhsAspectRatio.vhsAspectRatiosList.get(value).asString()).formatted(Formatting.GREEN) : Text.literal(monitor.getVideoMode(value).toString()));
                    }
                },
                new SimpleOption.ValidatingIntSliderCallbacks(-1, monitor == null ? -1 : ConfigStuff.enableVHSAspectRatio ? VhsAspectRatio.vhsAspectRatiosList.size() - 1 : monitor.getVideoModeCount() - 1),
                j2,
                value -> {
                    if (monitor != null) {
                        Optional<VideoMode> videoMode = value == -1 ? Optional.empty() : ConfigStuff.enableVHSAspectRatio ? Optional.of(VhsAspectRatio.vhsAspectRatiosList.get(value)) : Optional.of(monitor.getVideoMode(value));
                        window.setVideoMode(videoMode);
                    }
                }
        );
    }

    @ModifyArg(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/OptionListWidget;addSingleOptionEntry(Lnet/minecraft/client/option/SimpleOption;)I", ordinal = 0))
    private SimpleOption addNormalVideoModeToList(SimpleOption<?> option){
        return VhsAspectRatio.normalVideoMode;
    }


//    @Redirect(method = "method_41844", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/Window;setVideoMode(Ljava/util/Optional;)V"))
//    private static void setVideoModeConditionally(Window instance, Optional<VideoMode> videoMode, @Local(argsOnly = true) Integer value, @Local(argsOnly = true) Monitor monitor){
//        if(!ConfigStuff.enableVHSAspectRatio){
//            instance.setVideoMode(value == -1 ? Optional.empty() : Optional.of(monitor.getVideoMode(value)));
//        }
//    }

//    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/OptionListWidget;addSingleOptionEntry(Lnet/minecraft/client/option/SimpleOption;)I", ordinal = 0))
//    private void createSimpleOption(CallbackInfo ci, @Local Monitor monitor, @Local Window window){
//        Optional<VideoMode> optional = VhsAspectRatio.currentVhsVideoMode != null ? VhsAspectRatio.currentVhsVideoMode : window.getVideoMode();
//        int j2 = (Integer)optional.map(this::findClosestVhsVideoModeIndex).orElse(-1);
//
//        VhsAspectRatio.vhsVideoMode = new SimpleOption<>(
//                "options.vhs.resolution",
//                SimpleOption.emptyTooltip(),
//                (prefix, value) -> {
//                    if (monitor == null) {
//                        return Text.translatable("options.vhs.resolutions.unavailable");
//                    } else {
//                        return value == -1
//                                ? GameOptions.getGenericValueText(prefix, Text.translatable("options.fullscreen.current"))
//                                : GameOptions.getGenericValueText(prefix, Text.literal(VhsAspectRatio.vhsAspectRatiosList.get(value).toString()));
//                    }
//                },
//                new SimpleOption.ValidatingIntSliderCallbacks(-1, monitor != null ? VhsAspectRatio.vhsAspectRatiosList.size() - 1 : -1),
//                j2,
//                value -> {
//                    if (monitor != null) {
//                        Optional<VideoMode> videoMode = value == -1 ? Optional.empty() : Optional.of(VhsAspectRatio.vhsAspectRatiosList.get(value));
//                        if(ConfigStuff.enableVHSAspectRatio) {
//                            System.out.println("SET");
//                            window.setVideoMode(videoMode);
//                        }
//                        VhsAspectRatio.currentVhsVideoMode = videoMode;
//                    }
//                }
//        );
//        this.list.addSingleOptionEntry(VhsAspectRatio.vhsVideoMode);
//    }

    @Unique
    private int findClosestVhsVideoModeIndex(VideoMode videoMode) {
        return VhsAspectRatio.vhsAspectRatiosList.indexOf(videoMode);
    }




}
