package com.sp.networking.S2C;

import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.UUID;

public class OtherPlayerUUIDSyncS2C {

    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender){

        boolean inOtherInventory = buf.readBoolean();
        UUID playerUUID = buf.readUuid();

        PlayerEntity player = client.player;


        client.execute(()->{
            if (player != null) {
                PlayerEntity otherPlayer = player.getWorld().getPlayerByUuid(playerUUID);
                PlayerComponent playerComponent = InitializeComponents.PLAYER.get(player);
                playerComponent.setCameraInOtherInventory(inOtherInventory);
                if (otherPlayer != null) {
                    playerComponent.setOtherPlayerPos(otherPlayer);
                }
            }
        });
    }
}
