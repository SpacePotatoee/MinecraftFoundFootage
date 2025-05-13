package com.sp.mixin.skinstolen;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import com.sp.entity.client.SkinWalkerCapturedFlavorText;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin extends Screen{

    protected ChatScreenMixin(Text title) {
        super(title);
    }

    @WrapOperation(method = "sendMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendChatMessage(Ljava/lang/String;)V"))
    private void noOneCanHearYouScream(ClientPlayNetworkHandler instance, String content, Operation<Void> original){
        PlayerComponent component = InitializeComponents.PLAYER.get(this.client.player);

        if(!component.hasBeenCaptured()){
            original.call(instance, content);
        } else {
            SkinWalkerCapturedFlavorText.triedToChat = true;
        }
    }

    @WrapOperation(method = "sendMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendChatCommand(Ljava/lang/String;)V"))
    private void noOneCanHearYouScream2(ClientPlayNetworkHandler instance, String content, Operation<Void> original){
        PlayerComponent component2 = InitializeComponents.PLAYER.get(this.client.player);

        if(!component2.hasBeenCaptured() || content.contains("release")){
            original.call(instance, content);
        } else {
            SkinWalkerCapturedFlavorText.triedToChat = true;
        }
    }
}
