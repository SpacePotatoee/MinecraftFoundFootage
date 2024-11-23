package com.sp.networking.C2S;

import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.UUID;

public class TargetEntitySync {
    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender){
        int targetID = buf.readInt();

        server.execute(()->{
            PlayerComponent playerComponent = InitializeComponents.PLAYER.get(player);

            if(targetID == -1){
                playerComponent.setTargetEntity(null);
            } else {
                playerComponent.setTargetEntity(player.getWorld().getEntityById(targetID));
            }
        });
    }
}
