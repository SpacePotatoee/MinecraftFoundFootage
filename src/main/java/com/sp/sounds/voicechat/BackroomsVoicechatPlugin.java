package com.sp.sounds.voicechat;

import com.sp.SPBRevamped;
import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import com.sp.entity.custom.SkinWalkerEntity;
import de.maxhenkel.voicechat.api.*;
import de.maxhenkel.voicechat.api.events.*;
import de.maxhenkel.voicechat.api.opus.OpusDecoder;
import de.maxhenkel.voicechat.voice.common.Utils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.math.random.Random;
import org.lwjgl.openal.AL10;


import java.util.List;
import java.util.UUID;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

public class BackroomsVoicechatPlugin implements VoicechatPlugin {
    public static VoicechatServerApi voicechatApi;
    private ConcurrentHashMap<UUID, OpusDecoder> decoders;
    public static ConcurrentHashMap<UUID, Float> speakingTime;



    @Override
    public String getPluginId() {
        return SPBRevamped.MOD_ID;
    }

    @Override
    public void initialize(VoicechatApi api) {
        decoders = new ConcurrentHashMap<>();
        speakingTime = new ConcurrentHashMap<>();
    }

    @Override
    public void registerEvents(EventRegistration registration) {
        registration.registerEvent(MicrophonePacketEvent.class, this::updateVisibilityAndTalkTime);
        registration.registerEvent(VoicechatServerStoppedEvent.class, this::onServerStop);
        registration.registerEvent(PlayerDisconnectedEvent.class, this::playerDisconnect);
        registration.registerEvent(PlayerConnectedEvent.class, this::playerConnect);
        registration.registerEvent(VoicechatServerStartedEvent.class, this::onServerStart);
    }


    private void onServerStart(VoicechatServerStartedEvent voicechatServerStartedEvent) {
        voicechatApi = voicechatServerStartedEvent.getVoicechat();
    }

    private void playerConnect(PlayerConnectedEvent playerConnectedEvent) {
        speakingTime.put(playerConnectedEvent.getConnection().getPlayer().getUuid(), 0.0f);
    }

    private void playerDisconnect(PlayerDisconnectedEvent playerDisconnectedEvent) {
        this.removePlayerDecoder(playerDisconnectedEvent.getPlayerUuid());
        speakingTime.remove(playerDisconnectedEvent.getPlayerUuid());
    }

    private void onServerStop(VoicechatServerStoppedEvent voicechatServerStoppedEvent) {
        decoders.forEach((key, value) -> this.removePlayerDecoder(key));
        speakingTime.clear();
    }

    private void updateVisibilityAndTalkTime(MicrophonePacketEvent microphonePacketEvent) {
        VoicechatConnection senderConnection = microphonePacketEvent.getSenderConnection();
        if(senderConnection != null) {

            if (!(senderConnection.getPlayer().getPlayer() instanceof PlayerEntity player)) {
                return;
            }
            PlayerComponent component = InitializeComponents.PLAYER.get(player);
            if(!component.shouldBeMuted()){
                //Get the data
                if (!decoders.containsKey(player.getUuid())) {
                    decoders.put(player.getUuid(), microphonePacketEvent.getVoicechat().createDecoder());
                }
                OpusDecoder decoder = decoders.get(player.getUuid());
                short[] data = decoder.decode(microphonePacketEvent.getPacket().getOpusEncodedData());


                if(data.length > 0){
                    component.setSpeaking(true);

                    //Update talk time
                    if(!speakingTime.containsKey(player.getUuid())){
                        speakingTime.put(player.getUuid(), 0.0f);
                    }

                    speakingTime.put(player.getUuid(), speakingTime.get(player.getUuid()) + 0.0001f);
//                    System.out.println(speakingTime.get(player.getUuid()));

                    //Update entity visibility
                    if(!component.isVisibleToEntity()) {
                        double volume = Utils.dbToPerc(Utils.getHighestAudioLevel(data));

                        if (volume >= 0.8) {
                            component.setVisibleToEntity(true);
                        }
                    }
                }
            } else {
                microphonePacketEvent.cancel();
            }
        }
    }

    private void removePlayerDecoder(UUID uuid){
        OpusDecoder decoder = decoders.get(uuid);
            if(decoder != null){
                decoder.close();
            }
        decoders.remove(uuid);
    }

}