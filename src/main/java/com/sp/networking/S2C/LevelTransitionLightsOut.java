package com.sp.networking.S2C;

import com.sp.SPBRevampedClient;
import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import com.sp.init.ModSounds;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.sound.SoundCategory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LevelTransitionLightsOut {

    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        int time = buf.readInt();

        client.execute(()->{
            if(client.player != null) {
                PlayerComponent playerComponent = InitializeComponents.PLAYER.get(client.player);
                ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

                //Turn off the lights
                playerComponent.player.playSound(ModSounds.LIGHTS_OUT, SoundCategory.AMBIENT, 1, 1);
                SPBRevampedClient.getCutsceneManager().blackScreen.showBlackScreen(time, false, false);

                //PlaySound after black screen is over
                executorService.schedule(() -> {
                    playerComponent.player.playSound(ModSounds.LIGHTS_ON, SoundCategory.AMBIENT, 1, 1);
                    SPBRevampedClient.sendComponentSyncPacket(false, "teleporting");
                    executorService.shutdown();
                }, (time * 100L)/2, TimeUnit.MILLISECONDS);
            }
        });
    }

}
