package com.sp.networking.C2S;

import com.sp.block.entity.CameraBlockEntity;
import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

import java.util.UUID;

public class CameraBlockUUIDSync {

    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender){

        UUID attachedPlayer = buf.readUuid();
        BlockPos pos = buf.readBlockPos();


        server.execute(()->{
            PlayerComponent playerComponent = InitializeComponents.PLAYER.get(player);
            BlockEntity cameraBlockEntity = player.getWorld().getBlockEntity(pos);
            if(cameraBlockEntity instanceof CameraBlockEntity) {
                ((CameraBlockEntity) cameraBlockEntity).attatchedPlayer = attachedPlayer;
            }
            playerComponent.setCameraPos(pos);
        });
    }
}
