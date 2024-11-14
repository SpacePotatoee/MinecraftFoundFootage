package com.sp.networking.S2C;

import com.sp.SPBRevampedClient;
import foundry.veil.api.client.util.Easings;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

public class InvokeScreenShakePacket {

    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender){
        int duration = buf.readInt();
        float intensity = buf.readFloat();
        Easings.Easing easing = buf.readEnumConstant(Easings.Easing.class);
        boolean inverted = buf.readBoolean();

        client.execute(()->{
            SPBRevampedClient.getCameraShake().setCameraShake(duration, intensity, easing, inverted);
        });
    }

}
