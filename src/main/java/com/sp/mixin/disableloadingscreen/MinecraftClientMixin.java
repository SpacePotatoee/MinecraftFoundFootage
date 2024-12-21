package com.sp.mixin.disableloadingscreen;

import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.util.Window;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

    @Shadow private Thread thread;
    @Shadow @Final private static Logger LOGGER;
    @Shadow @Nullable public Screen currentScreen;
    @Shadow @Nullable public ClientWorld world;
    @Shadow @Nullable public ClientPlayerEntity player;
    @Shadow @Final public Mouse mouse;
    @Shadow @Final private Window window;
    @Shadow public boolean skipGameRender;
    @Shadow @Final private SoundManager soundManager;

    @Shadow public abstract void updateWindowTitle();

    @Redirect(method = "joinWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ProgressScreen;setTitle(Lnet/minecraft/text/Text;)V"))
    private void doNothing(ProgressScreen instance, Text title){

    }

    @Inject(method = "setScreen", at = @At("HEAD"), cancellable = true)
    private void dontSetThatScreen(Screen screen, CallbackInfo ci){
        ci.cancel();

        if (SharedConstants.isDevelopment && Thread.currentThread() != this.thread) {
            LOGGER.error("setScreen called from non-game thread");
        }

        if (this.currentScreen != null) {
            this.currentScreen.removed();
        }

        if (screen == null && this.world == null) {
            screen = new TitleScreen();
        } else if (screen == null && this.player.isDead()) {
            if (this.player.showsDeathScreen()) {
                screen = new DeathScreen(null, this.world.getLevelProperties().isHardcore());
            } else {
                this.player.requestRespawn();
            }
        }

        this.currentScreen = screen;
        if (this.currentScreen != null) {
            this.currentScreen.onDisplayed();
        }

        BufferRenderer.reset();
        if (screen != null) {
            screen.init((MinecraftClient) (Object) this, this.window.getScaledWidth(), this.window.getScaledHeight());
            if(!(screen instanceof DownloadingTerrainScreen)) {
                this.mouse.unlockCursor();
                KeyBinding.unpressAll();
                this.skipGameRender = false;
            }
        } else {
            this.soundManager.resumeAll();
            this.mouse.lockCursor();
        }

        this.updateWindowTitle();
    }

}
