package com.sp.networking.C2S;

import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import com.sp.networking.InitializePackets;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class FlashLightSync {
    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender){

        boolean flashLightOn = buf.readBoolean();

        server.execute(()->{
            PlayerComponent playerComponent = InitializeComponents.PLAYER.get(player);
            playerComponent.setFlashLightOn(flashLightOn);
//            playerComponent.sync();
//            PacketByteBuf buffer = PacketByteBufs.create();
//            buffer.writeBoolean(flashLightOn);
//            ServerPlayNetworking.send(player, InitializePackets.FL_SYNCC, buffer);
        });
    }
}
