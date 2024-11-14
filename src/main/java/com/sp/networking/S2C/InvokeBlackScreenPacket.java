package com.sp.networking.S2C;

import com.sp.SPBRevampedClient;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

public class InvokeBlackScreenPacket {

    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender){
        int duration = buf.readInt();
        boolean shouldPauseSounds = buf.readBoolean();
        boolean noEscape = buf.readBoolean();

        client.execute(()->{
            SPBRevampedClient.getCutsceneManager().blackScreen.showBlackScreen(duration, shouldPauseSounds, noEscape);
        });
    }

}
