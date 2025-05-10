package com.sp.mixin.respawnsystem;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.sp.init.BackroomsLevels;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DeathScreen.class)
public abstract class DeathScreenMixin extends Screen {

    @Shadow private int ticksSinceDeath;

    @Shadow protected abstract void setButtonsActive(boolean active);

    protected DeathScreenMixin(Text title) {
        super(title);
    }

    @Unique private static boolean firstTimeDead = true;
    @Unique private int delay = 0;

    @Unique
    private boolean isInBackrooms(){
        if(this.client.player != null){
            if(this.client.player.getWorld() != null){
                return BackroomsLevels.isInBackrooms(this.client.player.getWorld().getRegistryKey());
            }
        }

        return false;
    }


    //Remove the functionality of the respawn button
    @WrapOperation(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/ButtonWidget;builder(Lnet/minecraft/text/Text;Lnet/minecraft/client/gui/widget/ButtonWidget$PressAction;)Lnet/minecraft/client/gui/widget/ButtonWidget$Builder;", ordinal = 0))
    private ButtonWidget.Builder disableRespawnButton(Text message, ButtonWidget.PressAction onPress, Operation<ButtonWidget.Builder> original){
        if (this.isInBackrooms()) {
            return new ButtonWidget.Builder(message, button -> {
                firstTimeDead = false;
                button.active = true;
            });
        }

        return original.call(message, onPress);
    }

    //Remove the functionality of the title screen button
    @WrapOperation(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/ButtonWidget;builder(Lnet/minecraft/text/Text;Lnet/minecraft/client/gui/widget/ButtonWidget$PressAction;)Lnet/minecraft/client/gui/widget/ButtonWidget$Builder;", ordinal = 1))
    private ButtonWidget.Builder disableTitleScreenButton(Text message, ButtonWidget.PressAction onPress, Operation<ButtonWidget.Builder> original){
        if (this.isInBackrooms()) {
            return new ButtonWidget.Builder(message, button -> {
                firstTimeDead = false;
                button.active = true;
            });
        }

        return original.call(message, onPress);
    }


    @Inject(method = "tick", at = @At("TAIL"))
    private void youAreNotDoneYet(CallbackInfo ci){
        if (this.isInBackrooms()) {
            this.setButtonsActive(true);
            if (!firstTimeDead) {
                delay++;
                if(delay == 80) {
                    this.client.player.requestRespawn();
                    this.client.setScreen(null);
                }
            }
        }
    }

}
