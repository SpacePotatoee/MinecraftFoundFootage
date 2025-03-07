package com.sp.networking.S2C;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;

public class SoundPacket {

    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender){
        RegistryEntry<SoundEvent> sound =  buf.readRegistryEntry(Registries.SOUND_EVENT.getIndexedEntries(), SoundEvent::fromBuf);
        float volume = buf.readFloat();
        float pitch = buf.readFloat();

        client.execute(()->{
            client.getSoundManager().play(PositionedSoundInstance.master(sound.value(), pitch, volume));
        });
    }

}
