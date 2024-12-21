package com.sp.mixin.skinstolen;

import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import com.sp.entity.client.SkinWalkerCapturedFlavorText;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GameMenuScreen.class)
public abstract class GameMenuScreenMixin extends Screen {

    protected GameMenuScreenMixin(Text title) {
        super(title);
    }

    //You can't escape
    @Redirect(method = "initWidgets", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/ButtonWidget;builder(Lnet/minecraft/text/Text;Lnet/minecraft/client/gui/widget/ButtonWidget$PressAction;)Lnet/minecraft/client/gui/widget/ButtonWidget$Builder;", ordinal = 1))
    private ButtonWidget.Builder noEscape(Text message, ButtonWidget.PressAction onPress){
        MinecraftClient client1 = MinecraftClient.getInstance();
        if(client1.player != null) {
            PlayerComponent component = InitializeComponents.PLAYER.get(client1.player);

            if (component.hasBeenCaptured()) {
                return new ButtonWidget.Builder(Text.literal("There is no Escape").formatted(Formatting.RED), button -> {
                    button.active = true;
                    SkinWalkerCapturedFlavorText.triedToLeave = true;
                });
            }
        }

        return new ButtonWidget.Builder(message, onPress);
    }

}
