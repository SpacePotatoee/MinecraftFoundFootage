package com.sp.entity.ai.goals;

import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.SkinWalkerComponent;
import com.sp.entity.custom.SkinWalkerEntity;
import com.sp.sounds.voicechat.BackroomsVoicechatPlugin;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.audiochannel.AudioPlayer;
import de.maxhenkel.voicechat.api.audiochannel.LocationalAudioChannel;
import de.maxhenkel.voicechat.plugins.impl.ServerLevelImpl;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.random.Random;

public class SpeakGoal extends Goal {
    private final Random random = Random.create(7585889L);
    private final SkinWalkerEntity entity;
    private final SkinWalkerComponent component;
    private int actCooldown;
    public ServerLevelImpl serverLevel = null;
    public LocationalAudioChannel audioChannel;
    public AudioPlayer audioPlayer;

    public SpeakGoal(SkinWalkerEntity entity) {
        this.entity = entity;
        this.component = InitializeComponents.SKIN_WALKER.get(entity);
    }

    @Override
    public boolean canStart() {
        return this.entity.isAlive();
    }

    @Override
    public void start() {
        this.actCooldown = getTickCount(40);
    }

    @Override
    public void stop() {
        if (this.audioPlayer != null) {
            this.audioPlayer.stopPlaying();
        }
    }

    @Override
    public void tick() {
        if (!this.entity.getWorld().isClient) {
            if (this.component.shouldBeginReveal()) {
                if (this.audioPlayer != null) {
                    this.audioPlayer.stopPlaying();
                }
                return;
            }

            if (this.actCooldown > 0) {
                this.actCooldown--;
                return;
            }

            this.playRandomPlayerSounds();

            this.setRandomActCoolDown();
        }
    }

    private void setRandomActCoolDown(){
        this.actCooldown = getTickCount(random.nextBetween(60, 150));
    }

    private void playRandomPlayerSounds() {
        if (this.entity.getServer() == null) {
            return;
        }

        if (this.serverLevel == null) {
            this.serverLevel = new ServerLevelImpl(this.entity.getServer().getWorld(this.entity.getWorld().getRegistryKey()));
        }

        if (!this.serverLevel.getServerLevel().equals(this.entity.getServer().getWorld(this.entity.getWorld().getRegistryKey()))) {
            this.serverLevel = new ServerLevelImpl(this.entity.getServer().getWorld(this.entity.getWorld().getRegistryKey()));
        }

        VoicechatServerApi api = BackroomsVoicechatPlugin.voicechatApi;

        if (api == null) {
            return;
        }

        if (BackroomsVoicechatPlugin.randomSpeakingList.isEmpty()) {
            return;
        }

        if (!BackroomsVoicechatPlugin.randomSpeakingList.containsKey(this.component.getTargetPlayerUUID())) {
            return;
        }

        if (BackroomsVoicechatPlugin.randomSpeakingList.get(this.component.getTargetPlayerUUID()) == null) {
            return;
        }

        if (BackroomsVoicechatPlugin.randomSpeakingList.get(this.component.getTargetPlayerUUID()).isEmpty()) {
            return;
        }

        short[] data = BackroomsVoicechatPlugin.randomSpeakingList.get(this.component.getTargetPlayerUUID()).get(random.nextBetween(0, BackroomsVoicechatPlugin.randomSpeakingList.get(this.component.getTargetPlayerUUID()).size() - 1));

        if (this.component.isInTrueForm()) {
            if(data != null && data.length > 0) {
                data = demonizeVoice(data);
            }
        }

        if (this.audioChannel == null) {
            this.audioChannel = api.createLocationalAudioChannel(this.entity.getUuid(), this.serverLevel, api.createPosition(this.entity.getX(), this.entity.getY(), this.entity.getZ()));
        }

        if (this.audioChannel == null) {
            return;
        }

        this.audioChannel.updateLocation(api.createPosition(this.entity.getX(), this.entity.getY(), this.entity.getZ()));

        if (this.audioPlayer == null) {
            this.audioPlayer = api.createAudioPlayer(this.audioChannel, api.createEncoder(), data);
        }

        if (!this.audioPlayer.isPlaying()) {
            this.audioPlayer = api.createAudioPlayer(this.audioChannel, api.createEncoder(), data);
            this.audioPlayer.startPlaying();
        }
    }

    private short[] demonizeVoice(short[] data) {
        switch (this.random.nextBetween(0, 3)) {
            case 0:
                data = applyStutter(data);
                break;
            case 1:
                data = reverseData(data);
                break;
            default:
                data = bitCrush(data, 8);
                break;
        }
        //data = warpSpeed(data);
        //data = reverseSegments(data);
        //data = addStaticNoise(data);

        if (this.random.nextBetween(0, 3) < 2) {
            data = demonizeVoice(data);
        }

        return data;
    }

    private short[] bitCrush(short[] data, int resolution) {
        short[] crushedData = new short[data.length];
        int mask = -(1 << (16 - resolution));

        for (int i = 0; i < data.length; i++) {
            crushedData[i] = (short) (data[i] & mask);
        }

        return crushedData;
    }

    private short[] applyStutter(short[] data) {
        short[] stutteredData = new short[data.length];
        int stutterSize = 500;
        int stutterInterval = 1000;

        System.arraycopy(data, 0, stutteredData, 0, data.length);

        for (int i = 0; i < data.length; i += stutterInterval) {
            for (int j = 0; j < stutterSize && (i + j) < data.length; j++) {
                stutteredData[i + j] = data[i];
            }
        }

        return stutteredData;
    }

    private short[] reverseData(short[] data) {
        short[] reversedData = new short[data.length];

        for (int i = 0; i < data.length; i++) {
            reversedData[i] = data[data.length - 1 - i];
        }

        return reversedData;
    }

    private short[] addStaticNoise(short[] data) {
        short[] noisyData = new short[data.length];
        System.arraycopy(data, 0, noisyData, 0, data.length);
        for (int i = 0; i < noisyData.length; i++) {
            if (this.random.nextDouble() < 0.1) {
                noisyData[i] = (short) (this.random.nextInt(2000) - 1000);
            }
        }
        return noisyData;
    }

    private short[] warpSpeed(short[] data) {
        int chunkSize = 200;
        float speedFactor = 0.8f + (float) this.random.nextDouble() * 0.4f;
        short[] warpedData = new short[(int) (data.length * speedFactor)];

        for (int i = 0, j = 0; i < data.length && j < warpedData.length; i += chunkSize, j += (int) (chunkSize * speedFactor)) {
            int copyLength = Math.min(chunkSize, data.length - i);
            System.arraycopy(data, i, warpedData, j, Math.min(copyLength, warpedData.length - j));
        }

        return warpedData;
    }

    private short[] reverseSegments(short[] data) {
        short[] reversedData = new short[data.length];
        System.arraycopy(data, 0, reversedData, 0, data.length);

        int segmentSize = 500;
        for (int i = 0; i < data.length; i += segmentSize * 2) {
            if (this.random.nextBoolean()) {
                int start = i;
                int end = Math.min(i + segmentSize, data.length);
                while (start < end) {
                    short temp = reversedData[start];
                    reversedData[start] = reversedData[end - 1];
                    reversedData[end - 1] = temp;
                    start++;
                    end--;
                }
            }
        }

        return reversedData;
    }
}
