package com.sp.networking.C2S;

import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class BeingCapturedSync {

    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender){
        boolean isBeingCaptured = buf.readBoolean();

        server.execute(()->{
            PlayerComponent playerComponent = InitializeComponents.PLAYER.get(player);
            playerComponent.setBeingCaptured(isBeingCaptured);
            playerComponent.sync();
        });
    }
}
