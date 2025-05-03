package com.sp.mixin.hudandresolution;

import com.sp.SPBRevampedClient;
import com.sp.compat.modmenu.ConfigStuff;
import com.sp.util.TickTimer;
import com.sp.util.Timer;
import foundry.veil.api.client.util.Easings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    @Shadow @Final private MinecraftClient client;

    @Shadow private int scaledHeight;

    @Shadow @Final private static Identifier ICONS;

    @Unique Timer hotbarSlideTimer = new Timer(500, Easings.Easing.easeInCirc, Easings.Easing.easeOutCirc);
    @Unique TickTimer hotbarHoldTimer = new TickTimer();
    @Unique Integer prevSelectedSlot = 0;
    @Unique double hotbarPosition;

    @Inject(method = {"renderHotbar"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;push()V", shift = At.Shift.AFTER))
    private void hotbarSlide1(float tickDelta, DrawContext context, CallbackInfo ci){
        if (SPBRevampedClient.shouldRenderCameraEffect()) {
            this.hotbarPosition = 45 * hotbarSlideTimer.getCurrentTime();
            if (!ConfigStuff.useDefaultGUI) {
                context.getMatrices().translate(0, this.hotbarPosition, 0);
            }
            MinecraftClient client = MinecraftClient.getInstance();

            if (client.player != null) {
                int selectedSlot = client.player.getInventory().selectedSlot;
                if (this.prevSelectedSlot != null) {
                    if (this.prevSelectedSlot != selectedSlot) {
                        hotbarSlideTimer.reverse();
                        hotbarSlideTimer.startTimer();
                        hotbarHoldTimer.resetToZero();
                    } else {
                        if (hotbarHoldTimer.getCurrentTick() >= 60) {
                            hotbarSlideTimer.forward();
                        }
                    }
                }
                this.prevSelectedSlot = selectedSlot;
            }
        }
    }

    @Inject(method = {"renderHotbarItem"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;translate(FFF)V", ordinal = 1, shift = At.Shift.AFTER))
    private void itemCountFix(DrawContext context, int x, int y, float f, PlayerEntity player, ItemStack stack, int seed, CallbackInfo ci){
        if (!ConfigStuff.useDefaultGUI) {
            context.getMatrices().translate(0, this.hotbarPosition, 0);
        }
    }

    @Inject(method = {"renderHotbarItem"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;III)V"))
    private void hotbarSlide2(DrawContext context, int x, int y, float f, PlayerEntity player, ItemStack stack, int seed, CallbackInfo ci){
        if (SPBRevampedClient.shouldRenderCameraEffect()) {
            context.getMatrices().push();

            if (!ConfigStuff.useDefaultGUI) {
                context.getMatrices().translate(0, this.hotbarPosition, 0);
            }
        }
    }

    @Inject(method = {"renderHotbarItem"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawItemInSlot(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;II)V", shift = At.Shift.AFTER))
    private void hotbarSlide3(DrawContext context, int x, int y, float f, PlayerEntity player, ItemStack stack, int seed, CallbackInfo ci) {
        if (SPBRevampedClient.shouldRenderCameraEffect()) {
            context.getMatrices().pop();
        }
    }

    


    //RENDER HEALTH BAR
    @Inject(method = "renderHealthBar", at = @At("HEAD"))
    private void setHealthOpacity1(DrawContext context, PlayerEntity player, int x, int y, int lines, int regeneratingHeartIndex, float maxHealth, int lastHealth, int health, int absorption, boolean blinking, CallbackInfo ci){
        if (SPBRevampedClient.shouldRenderCameraEffect()) {
            if (!ConfigStuff.useDefaultGUI) {
                context.setShaderColor(1.0f, 1.0f, 1.0f, 0.2f);
            }

            context.getMatrices().push();
            if (!ConfigStuff.useDefaultGUI) {
                context.getMatrices().translate(0, 5, 0);
            }
        }
    }

    @Inject(method = "renderHealthBar", at = @At("TAIL"))
    private void setHealthOpacity2(DrawContext context, PlayerEntity player, int x, int y, int lines, int regeneratingHeartIndex, float maxHealth, int lastHealth, int health, int absorption, boolean blinking, CallbackInfo ci){
        if (SPBRevampedClient.shouldRenderCameraEffect()) {
            context.getMatrices().pop();
            if (!ConfigStuff.useDefaultGUI) {
                context.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            }
        }
    }


    //RENDER HUNGER BAR
    @Inject(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;getHeartCount(Lnet/minecraft/entity/LivingEntity;)I", shift = At.Shift.AFTER))
    private void setHungerOpacity1(DrawContext context, CallbackInfo ci){
        if (SPBRevampedClient.shouldRenderCameraEffect()) {
            if (!ConfigStuff.useDefaultGUI) {
                context.setShaderColor(1.0f, 1.0f, 1.0f, 0.2f);
            }

            context.getMatrices().push();
            if (!ConfigStuff.useDefaultGUI) {
                context.getMatrices().translate(0, 5, 0);
            }
        }
    }

    @Inject(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V", ordinal = 2))
    private void setHungerOpacity2(DrawContext context, CallbackInfo ci){
        if (SPBRevampedClient.shouldRenderCameraEffect()) {
            context.getMatrices().pop();
            if (!ConfigStuff.useDefaultGUI) {
                context.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            }
        }
    }



    @Inject(method = {"renderExperienceBar", "renderCrosshair"}, at = @At("HEAD"), cancellable = true)
    private void disable(CallbackInfo ci){
        if (SPBRevampedClient.shouldRenderCameraEffect()) {
            if (!ConfigStuff.useDefaultGUI) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "renderVignetteOverlay", at = @At("HEAD"), cancellable = true)
    private void disableVignette(CallbackInfo ci){
        if (SPBRevampedClient.shouldRenderCameraEffect()) {
            if (SPBRevampedClient.getCutsceneManager().isPlaying || SPBRevampedClient.getCutsceneManager().blackScreen.isBlackScreen) {
                ci.cancel();
            }
        }
    }

}
