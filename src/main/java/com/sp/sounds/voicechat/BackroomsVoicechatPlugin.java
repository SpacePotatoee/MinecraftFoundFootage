package com.sp.sounds.voicechat;

import com.sp.SPBRevamped;
import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import com.sp.entity.custom.SkinWalkerEntity;
import de.maxhenkel.voicechat.api.VoicechatApi;
import de.maxhenkel.voicechat.api.VoicechatConnection;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.events.*;
import de.maxhenkel.voicechat.api.opus.OpusDecoder;
import de.maxhenkel.voicechat.voice.common.Utils;
import net.minecraft.client.MinecraftClient;
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
        registration.registerEvent(OpenALSoundEvent.class, this::SkinWalkerVoicesPitchDown);
        registration.registerEvent(MicrophonePacketEvent.class, this::recordPlayersTalking);
        registration.registerEvent(VoicechatServerStoppedEvent.class, this::onServerStop);
        registration.registerEvent(PlayerDisconnectedEvent.class, this::playerDisconnect);
        registration.registerEvent(PlayerConnectedEvent.class, this::playerConnect);
        registration.registerEvent(VoicechatServerStartedEvent.class, this::onServerStart);
    }

    private void SkinWalkerVoicesPitchDown(OpenALSoundEvent openALSoundEvent) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.player == null) {
            return;
        }

        if (client.player.getWorld() == null) {
            return;
        }

        UUID channelID = openALSoundEvent.getChannelId();

        if (channelID == null) {
            return;
        }

        try {
            List<SkinWalkerEntity> skinWalkerList = client.player.getWorld().getEntitiesByClass(SkinWalkerEntity.class, client.player.getBoundingBox().expand(50), EntityPredicates.VALID_LIVING_ENTITY);

            if (skinWalkerList.isEmpty()) {
                return;
            }

            for (SkinWalkerEntity skinWalker : skinWalkerList) {
                if (!skinWalker.getUuid().equals(channelID)) {
                    continue;
                }

                if (skinWalker.component.isInTrueForm()) {
                    AL10.alSourcei(openALSoundEvent.getSource(), 53248, 53251);
                    AL10.alSourcef(openALSoundEvent.getSource(), AL10.AL_PITCH, 0.8f);
                }
            }
        } catch (Exception e) {
            SPBRevamped.LOGGER.error("Error pitching down the Skinwalker's Voice: {}", String.valueOf(e));
        }
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

            PlayerComponent component = InitializeComponents.PLAYER.get(player);
            if(!component.shouldBeMuted()) {
                component.setSpeaking(true);

                //Update the amount of time that the players are talking for the skinstealer to determine who to take
                if(!player.isSpectator() && !player.isCreative()) {
                    if (!speakingTime.containsKey(player.getUuid())) {
                        speakingTime.put(player.getUuid(), 0.0f);
                    }

                    speakingTime.put(player.getUuid(), speakingTime.get(player.getUuid()) + 0.0001f);

                    //If the player is talking too loud, make them visible to the skinwalker
                    if (!component.isVisibleToEntity()) {
                        double volume = Utils.dbToPerc(Utils.getHighestAudioLevel(data));

                        if (volume >= 0.8) {
                            component.setTalkingTooLoud(true);
                            component.resetTalkingTooLoudTimer();
                        }
                    }
                } else if(!component.isBeingCaptured() && !component.hasBeenCaptured()) {
                    speakingTime.remove(player.getUuid());
                }
            } else {
                microphonePacketEvent.cancel();
                return;
            }

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

        if (ticks.get(player.getUuid()) > 40  && ticks.get(player.getUuid()) < 200) {
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

            randomSpeakingList.put(player.getUuid(), soundList);
        }

        ticks.put(player.getUuid(), 0);
        totalSoundData.remove(player.getUuid());
    }

    private void onServerStart(VoicechatServerStartedEvent voicechatServerStartedEvent) {
        voicechatApi = voicechatServerStartedEvent.getVoicechat();
    }

    private void playerConnect(PlayerConnectedEvent playerConnectedEvent) {
        PlayerEntity player = (PlayerEntity) playerConnectedEvent.getConnection().getPlayer().getPlayer();
        if(!player.isSpectator() && !player.isCreative()) {
            speakingTime.put(playerConnectedEvent.getConnection().getPlayer().getUuid(), 0.0f);
        }
    }

    private void playerDisconnect(PlayerDisconnectedEvent playerDisconnectedEvent) {
        UUID playerUUID = playerDisconnectedEvent.getPlayerUuid();
        this.removePlayerDecoder(playerUUID, decoders.get(playerUUID));
        speakingTime.remove(playerUUID);
    }

    private void onServerStop(VoicechatServerStoppedEvent voicechatServerStoppedEvent) {
        decoders.forEach(this::removePlayerDecoder);
        speakingTime.clear();
    }

    private void removePlayerDecoder(UUID uuid, OpusDecoder decoder){
        if(decoder != null) {
            decoder.close();
        }
        decoders.remove(uuid);
    }

}