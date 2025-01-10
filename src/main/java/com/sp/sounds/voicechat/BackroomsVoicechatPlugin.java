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


import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BackroomsVoicechatPlugin implements VoicechatPlugin {
    public static VoicechatServerApi voicechatApi;
    private ConcurrentHashMap<UUID, OpusDecoder> decoders;
    public static ConcurrentHashMap<UUID, Float> speakingTime;

    public static Map<UUID, Vector<short[]>> randomSpeakingList;
    private static Map<UUID, short[]> totalSoundData;
    private static Map<UUID, Integer> ticks;

    @Override
    public String getPluginId() {
        return SPBRevamped.MOD_ID;
    }

    @Override
    public void initialize(VoicechatApi api) {
        decoders = new ConcurrentHashMap<>();
        speakingTime = new ConcurrentHashMap<>();
        randomSpeakingList = new HashMap<>();
        ticks = new HashMap<>();
        totalSoundData = new HashMap<>();
    }

    @Override
    public void registerEvents(EventRegistration registration) {
        registration.registerEvent(MicrophonePacketEvent.class, this::recordPlayersTalking);
        registration.registerEvent(VoicechatServerStoppedEvent.class, this::onServerStop);
        registration.registerEvent(PlayerDisconnectedEvent.class, this::playerDisconnect);
        registration.registerEvent(PlayerConnectedEvent.class, this::playerConnect);
        registration.registerEvent(VoicechatServerStartedEvent.class, this::onServerStart);
    }

    private void recordPlayersTalking(MicrophonePacketEvent microphonePacketEvent) {
        VoicechatConnection senderConnection = microphonePacketEvent.getSenderConnection();
        if (senderConnection == null) {
            return;
        }

        if (!(senderConnection.getPlayer().getPlayer() instanceof PlayerEntity player)) {
            return;
        }

        if (ticks.containsKey(player.getUuid())) {
            ticks.put(player.getUuid(), ticks.get(player.getUuid()) + 1);
        } else {
            ticks.put(player.getUuid(), 0);
        }

        byte[] encodedData = microphonePacketEvent.getPacket().getOpusEncodedData();

        byte[] copyData = encodedData.clone();

        if (copyData.length != 0) {
            if (!decoders.containsKey(player.getUuid())) {
                decoders.put(player.getUuid(), microphonePacketEvent.getVoicechat().createDecoder());
            }

            OpusDecoder decoder = decoders.get(player.getUuid());

            short[] data = decoder.decode(encodedData);

            short[] totalData;
            if (totalSoundData.containsKey(player.getUuid())) {
                totalData = totalSoundData.get(player.getUuid());
            } else {
                totalData = new short[0];
            }

            short[] result = new short[totalData.length + data.length];
            System.arraycopy(totalData, 0, result, 0, totalData.length);
            System.arraycopy(data, 0, result, totalData.length, data.length);
            totalData = result;
            totalSoundData.put(player.getUuid(), totalData);

            return;
        }

        decoders.get(player.getUuid()).resetState();

        if (ticks.get(player.getUuid()) > 20  && ticks.get(player.getUuid()) < 100) {
            Random random = Random.create();

            Vector<short[]> soundList = new Vector<>();

            if (randomSpeakingList.containsKey(player.getUuid())) {
                soundList = randomSpeakingList.getOrDefault(player.getUuid(), new Vector<>());
            }

            if (soundList.size() < 20) {
                soundList.add(totalSoundData.get(player.getUuid()));
            } else {
                soundList.set(random.nextBetween(0, randomSpeakingList.size() - 1), totalSoundData.get(player.getUuid()));
            }

            System.out.println("Adding to random speaking list");
            randomSpeakingList.put(player.getUuid(), soundList);
        }

        ticks.put(player.getUuid(), 0);
        totalSoundData.remove(player.getUuid());
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
        /*
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

                    //Update entity visibility
                    if(!component.isVisibleToEntity()) {
                        double volume = Utils.dbToPerc(Utils.getHighestAudioLevel(data));

                        if (volume >= 0.8) {
                            component.setVisibleToEntity(true);
                        }
                    }
                }

                decoder.resetState();
            } else {
                //microphonePacketEvent.cancel();
            }
        }
         */
    }

    private void removePlayerDecoder(UUID uuid){
        OpusDecoder decoder = decoders.get(uuid);
            if(decoder != null){
                decoder.close();
            }
        decoders.remove(uuid);
    }

}