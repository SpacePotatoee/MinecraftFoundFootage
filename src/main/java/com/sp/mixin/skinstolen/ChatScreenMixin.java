package com.sp.mixin.skinstolen;

import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import com.sp.entity.client.SkinWalkerCapturedFlavorText;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin extends Screen{

    protected ChatScreenMixin(Text title) {
        super(title);
    }

    @Redirect(method = "sendMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendChatMessage(Ljava/lang/String;)V"))
    private void noOneCanHearYouScream(ClientPlayNetworkHandler instance, String content){
        PlayerComponent component = InitializeComponents.PLAYER.get(this.client.player);

        if(!component.hasBeenCaptured()){
            instance.sendChatMessage(content);
        } else {
            SkinWalkerCapturedFlavorText.triedToChat = true;
        }
    }

    @Redirect(method = "sendMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendChatCommand(Ljava/lang/String;)V"))
    private void noOneCanHearYouScream2(ClientPlayNetworkHandler instance, String content){
        PlayerComponent component2 = InitializeComponents.PLAYER.get(this.client.player);

        if(!component2.hasBeenCaptured()){
            instance.sendChatCommand(content);
        } else {
            SkinWalkerCapturedFlavorText.triedToChat = true;
        }
    }

}
