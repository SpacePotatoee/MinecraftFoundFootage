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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.random.Random;
import java.util.Vector;
import java.util.UUID;

public class SpeakGoal extends Goal {
    private final Random random = Random.create(7585889L);
    private final SkinWalkerEntity entity;
    private final SkinWalkerComponent component;
    private int actCooldown;
    public ServerLevelImpl serverLevel = null;
    public LocationalAudioChannel audioChannel;
    public AudioPlayer audioPlayer;
    
    // New conversation state variables
    private enum ConversationState {
        IDLE,      
        INITIATING,
        RESPONDING,
        CONCLUDING      
    }
    
    private ConversationState conversationState = ConversationState.IDLE;
    private int conversationSegmentCount = 0;
    private int maxConversationSegments = 0;
    private static final int MIN_CONVERSATION_COOLDOWN = 240;
    private static final int CONVERSATION_SEGMENT_DELAY = 40;
    private int segmentDelay = 0;
    
    // Index tracking to avoid repeating the same sounds too often
    private int lastSegmentIndex = -1;
    private int[] recentlyUsedIndices = new int[5];
    private int recentlyUsedCount = 0;

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
        this.actCooldown = getTickCount(random.nextBetween(80, 240));
    }

    @Override
    public void stop() {
        if (this.audioPlayer != null) {
            this.audioPlayer.stopPlaying();
        }
        this.conversationState = ConversationState.IDLE;
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

            // Handle conversation state machine
            switch (conversationState) {
                case IDLE:
                    if (this.actCooldown > 0) {
                        this.actCooldown--;
                        return;
                    }
                    
                    if (random.nextFloat() < 0.3f) {
                        this.actCooldown = getTickCount(random.nextBetween(120, 320));
                        return;
                    }
                    
                    startConversation();
                    break;
                    
                case INITIATING:
                case RESPONDING:
                case CONCLUDING:

                    handleConversation();
                    break;
            }
        }
    }
    
    private void startConversation() {
        // init convo params
        conversationState = ConversationState.INITIATING;
        maxConversationSegments = random.nextBetween(1, 4); // 1-4 segments cuz why not.
        conversationSegmentCount = 0;
        playConversationSegment();
    }
    
    private void handleConversation() {
        if (segmentDelay > 0) {
            segmentDelay--;
            return;
        }
        
        if (audioPlayer == null || !audioPlayer.isPlaying()) {
            conversationSegmentCount++;
            
            if (conversationSegmentCount >= maxConversationSegments) {
                conversationState = ConversationState.CONCLUDING;
                playConversationSegment();
                
                // end convo after final segment
                conversationState = ConversationState.IDLE;
                this.actCooldown = getTickCount(random.nextBetween(MIN_CONVERSATION_COOLDOWN, 500)); // Reduced max cooldown
            } else {
                // continue convo with next segment
                conversationState = ConversationState.RESPONDING;
                playConversationSegment();
            }
        }
    }
    
    private void playConversationSegment() {
        if (!isApiReady()) {
            return;
        }
        
        Vector<short[]> availableSegments = getAvailableVoiceSegments();
        if (availableSegments == null || availableSegments.isEmpty()) {
            return;
        }
        
        short[] data = selectContextAppropriateSegment(availableSegments);
        
        // note to self, this is the voice effect for when in true form, not just overall lol.
        if (this.component.isInTrueForm() && data != null && data.length > 0) {
            data = demonizeVoice(data);
        }
        
        playAudioSegment(data);
        
        // set delay before next segment
        segmentDelay = CONVERSATION_SEGMENT_DELAY + random.nextBetween(-10, 20);
    }
    
    private short[] selectContextAppropriateSegment(Vector<short[]> segments) {
        int size = segments.size();
        if (size <= 1) {
            return segments.get(0);
        }
        
        // avoid repeating recently used segments
        int index;
        int attempts = 0;
        do {
            index = random.nextInt(size);
            attempts++;
        } while (isRecentlyUsed(index) && attempts < 10);
        
        rememberUsedSegment(index);
        
        return segments.get(index);
    }
    
    private boolean isRecentlyUsed(int index) {
        if (index == lastSegmentIndex) {
            return true;
        }
        
        for (int i = 0; i < recentlyUsedCount; i++) {
            if (recentlyUsedIndices[i] == index) {
                return true;
            }
        }
        
        return false;
    }
    
    private void rememberUsedSegment(int index) {
        lastSegmentIndex = index;
        
        recentlyUsedIndices[recentlyUsedCount % recentlyUsedIndices.length] = index;
        recentlyUsedCount++;
    }
    
    private Vector<short[]> getAvailableVoiceSegments() {
        UUID targetPlayerUUID = this.component.getTargetPlayerUUID();
        if (targetPlayerUUID == null || !BackroomsVoicechatPlugin.randomSpeakingList.containsKey(targetPlayerUUID)) {
            return null;
        }
        
        Vector<short[]> segments = BackroomsVoicechatPlugin.randomSpeakingList.get(targetPlayerUUID);
        if (segments == null || segments.isEmpty()) {
            return null;
        }
        
        return segments;
    }
    
    private boolean isApiReady() {
        if (this.entity.getServer() == null) {
            return false;
        }

        if (this.serverLevel == null) {
            this.serverLevel = new ServerLevelImpl(this.entity.getServer().getWorld(this.entity.getWorld().getRegistryKey()));
        }

        if (!this.serverLevel.getServerLevel().equals(this.entity.getServer().getWorld(this.entity.getWorld().getRegistryKey()))) {
            this.serverLevel = new ServerLevelImpl(this.entity.getServer().getWorld(this.entity.getWorld().getRegistryKey()));
        }

        return BackroomsVoicechatPlugin.voicechatApi != null;
    }
    
    private void playAudioSegment(short[] data) {
        VoicechatServerApi api = BackroomsVoicechatPlugin.voicechatApi;
        
        if (api == null || data == null || data.length == 0) {
            return;
        }
        
        if (this.audioChannel == null) {
            this.audioChannel = api.createLocationalAudioChannel(this.entity.getUuid(), this.serverLevel, api.createPosition(this.entity.getX(), this.entity.getY(), this.entity.getZ()));
        }

        if (this.audioChannel == null) {
            return;
        }

        this.audioChannel.updateLocation(api.createPosition(this.entity.getX(), this.entity.getY(), this.entity.getZ()));

        if (this.audioPlayer == null || !this.audioPlayer.isPlaying()) {
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
}
