package com.sp.networking.C2S;

import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.UUID;

public class ShouldDoStaticPacket {
    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender){
        UUID id = buf.readUuid();

        server.execute(()->{
            PlayerEntity playerEntity = player.method_48926().getPlayerByUuid(id);
            if(playerEntity != null) {
                PlayerComponent playerComponent = InitializeComponents.PLAYER.get(playerEntity);
                playerComponent.setShouldDoStatic(false);
                playerComponent.sync();
            }
        });
    }
}
