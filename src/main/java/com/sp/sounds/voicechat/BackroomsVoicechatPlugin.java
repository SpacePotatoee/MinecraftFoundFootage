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
    public static Vector<short[]> randomSpeakingList;
    private short[] totalSoundData;
    private int ticks = 0;



    @Override
    public String getPluginId() {
        return SPBRevamped.MOD_ID;
    }

    @Override
    public void initialize(VoicechatApi api) {
        decoders = new ConcurrentHashMap<>();
        randomSpeakingList = new Vector<>();
    }

    @Override
    public void registerEvents(EventRegistration registration) {
//        registration.registerEvent(OpenALSoundEvent.class, this::SkinWalkerVoicesPitchDown);
//        registration.registerEvent(MicrophonePacketEvent.class, this::recordPlayersTalking);
        registration.registerEvent(MicrophonePacketEvent.class, this::updateVisibility);
        registration.registerEvent(VoicechatServerStoppedEvent.class, this::onServerStop);
        registration.registerEvent(PlayerDisconnectedEvent.class, this::playerDisconnect);
        registration.registerEvent(VoicechatServerStartedEvent.class, this::onServerStart);
    }

    private void onServerStart(VoicechatServerStartedEvent voicechatServerStartedEvent) {
        voicechatApi = voicechatServerStartedEvent.getVoicechat();
    }

    private void playerDisconnect(PlayerDisconnectedEvent playerDisconnectedEvent) {
        this.removePlayerDecoder(playerDisconnectedEvent.getPlayerUuid());
    }

    private void onServerStop(VoicechatServerStoppedEvent voicechatServerStoppedEvent) {
        decoders.forEach((key, value) -> this.removePlayerDecoder(key));
        ticks = 0;
        this.totalSoundData = null;
    }

//    private void SkinWalkerVoicesPitchDown(OpenALSoundEvent openALSoundEvent) {
//        MinecraftClient client = MinecraftClient.getInstance();
//
//        if(client.player != null) {
//            if (client.player.getWorld() != null){
//
//                UUID channelID = openALSoundEvent.getChannelId();
//                if(channelID != null){
//
//                    List<SkinWalkerEntity> skinWalkerList = client.player.getWorld().getEntitiesByClass(SkinWalkerEntity.class, client.player.getBoundingBox().expand(100), EntityPredicates.VALID_LIVING_ENTITY);
//                    if(!skinWalkerList.isEmpty()){
//
//                        for(SkinWalkerEntity skinWalker : skinWalkerList){
//                            if(skinWalker.getUuid().equals(channelID)){
//                                AL10.alSourcei(openALSoundEvent.getSource(), 53248, 53251);
//                                AL10.alSourcef(openALSoundEvent.getSource(), 4131, 0.5f);
//                                AL10.alSourcef(openALSoundEvent.getSource(), AL10.AL_PITCH, 0.65f);
//                            }
//                        }
//
//                    }
//
//                }
//
//
//            }
//        }
//
//
//    }

    private void updateVisibility(MicrophonePacketEvent microphonePacketEvent) {
        VoicechatConnection senderConnection = microphonePacketEvent.getSenderConnection();
        if(senderConnection != null) {

            if (!(senderConnection.getPlayer().getPlayer() instanceof PlayerEntity player)) {
                return;
            }
            PlayerComponent component = InitializeComponents.PLAYER.get(player);

            if(!component.isVisibleToEntity()) {
                if (!decoders.containsKey(player.getUuid())) {
                    decoders.put(player.getUuid(), microphonePacketEvent.getVoicechat().createDecoder());
                }
                OpusDecoder decoder = decoders.get(player.getUuid());

                short[] data = decoder.decode(microphonePacketEvent.getPacket().getOpusEncodedData());
                double volume = Utils.dbToPerc(Utils.getHighestAudioLevel(data));

                if (volume >= 0.8) {
                    component.setVisibleToEntity(true);
                }
            }
        }
    }

//    private void recordPlayersTalking(MicrophonePacketEvent microphonePacketEvent) {
//        VoicechatConnection senderConnection = microphonePacketEvent.getSenderConnection();
//        if(senderConnection != null) {
//
//            if(!(senderConnection.getPlayer().getPlayer() instanceof PlayerEntity player)){
//                return;
//            }
//            ticks++;
//
//            byte[] encodedData = microphonePacketEvent.getPacket().getOpusEncodedData();
//
//            if(encodedData.length > 0 && ticks < 100) {
//                if(!decoders.containsKey(player.getUuid())){
//                    decoders.put(player.getUuid(), microphonePacketEvent.getVoicechat().createDecoder());
//                }
//                OpusDecoder decoder = decoders.get(player.getUuid());
//
//                short[] data = decoder.decode(microphonePacketEvent.getPacket().getOpusEncodedData());
//
//                if(this.totalSoundData == null){
//                    this.totalSoundData = data;
//                }
//                else {
//                    short[] result = new short[this.totalSoundData.length + data.length];
//                    System.arraycopy(this.totalSoundData, 0, result, 0, this.totalSoundData.length);
//                    System.arraycopy(data, 0, result, this.totalSoundData.length, data.length);
//                    this.totalSoundData = result;
//                }
//
//            } else {
//                decoders.get(player.getUuid()).resetState();
//
//                if (ticks >= 100){
//                    Random random = Random.create();
////                    if(random.nextBetween(1, 1) == 1) {
//                        if (randomSpeakingList.size() < 20) {
//                            randomSpeakingList.add(this.totalSoundData);
//                        } else {
//                            randomSpeakingList.set(random.nextBetween(0, randomSpeakingList.size() - 1), this.totalSoundData);
//                        }
////                    }
//                }
//                ticks = 0;
//                this.totalSoundData = null;
//            }
//        }
//
//
//
//    }

    private void removePlayerDecoder(UUID uuid){
        decoders.get(uuid).close();
        decoders.remove(uuid);
    }

}