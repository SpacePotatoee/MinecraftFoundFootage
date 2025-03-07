package com.sp.networking.S2C;

import com.sp.SPBRevampedClient;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

public class InvokeScreenShakePacket {

    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender){
        double speed = buf.readDouble();
        double trauma = buf.readDouble();

        client.execute(()->{
            SPBRevampedClient.getCameraShake().noiseSpeed = speed;
            SPBRevampedClient.getCameraShake().trauma = trauma;
        });
    }

}
