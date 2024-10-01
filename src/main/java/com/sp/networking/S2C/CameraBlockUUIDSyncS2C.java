package com.sp.networking.S2C;

import com.sp.block.entity.CameraBlockEntity;
import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;

import java.util.UUID;

public class CameraBlockUUIDSyncS2C {

    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender){

        UUID attachedPlayer = buf.readUuid();
        PlayerEntity player = client.player;


        client.execute(()->{

            if (player != null) {
                PlayerComponent playerComponent = InitializeComponents.PLAYER.get(player);
                BlockEntity cameraBlockEntity = player.getWorld().getBlockEntity(playerComponent.getCameraPos());
                if (cameraBlockEntity instanceof CameraBlockEntity) {
                    ((CameraBlockEntity) cameraBlockEntity).attatchedPlayer = attachedPlayer;
                }
            }
        });

    }
}
