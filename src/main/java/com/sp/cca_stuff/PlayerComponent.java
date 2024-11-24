package com.sp.cca_stuff;

import com.sp.Keybinds;
import com.sp.SPBRevamped;
import com.sp.SPBRevampedClient;
import com.sp.init.ModSounds;
import com.sp.networking.InitializePackets;
import com.sp.sounds.AmbientSoundInstance;
import com.sp.sounds.PoolroomsNoonAmbienceSoundInstance;
import com.sp.sounds.PoolroomsSunsetAmbienceSoundInstance;
import com.sp.sounds.pipes.GasPipeSoundInstance;
import com.sp.sounds.pipes.WaterPipeSoundInstance;
import com.sp.util.Timer;
import com.sp.init.BackroomsLevels;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.ClientTickingComponent;
import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent;
import foundry.veil.api.client.util.Easings;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.StopSoundS2CPacket;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("DataFlowIssue")
public class PlayerComponent implements AutoSyncedComponent, ClientTickingComponent, ServerTickingComponent {
    private final PlayerEntity player;
    private boolean flashLightOn;
    private boolean shouldRender;
    private boolean isDoingCutscene;
    private boolean playingGlitchSound;
    private boolean shouldNoClip;
    private boolean isTeleporting;
    private boolean isTeleportingToPoolrooms;
    private boolean readyForLevel1;
    private boolean readyForLevel2;
    private boolean readyForPoolrooms;

    private int suffocationTimer;
    private int level2Timer;
    private boolean shouldDoStatic;
    private Timer staticTimer;

    private Entity targetEntity;
    private int skinWalkerLookDelay;

    MovingSoundInstance DeepAmbience;
    MovingSoundInstance GasPipeAmbience;
    MovingSoundInstance WaterPipeAmbience;
    MovingSoundInstance PoolroomsNoonAmbience;
    MovingSoundInstance PoolroomsSunsetAmbience;


    private boolean prevFlashLightOn;

    public PlayerComponent(PlayerEntity player){
        this.player = player;
        this.flashLightOn = false;
        this.shouldRender = true;
        this.shouldNoClip = false;
        this.shouldDoStatic = false;

        this.isDoingCutscene = false;

        this.isTeleporting = false;
        this.isTeleportingToPoolrooms = false;
        this.readyForLevel1 = false;
        this.readyForLevel2 = false;
        this.readyForPoolrooms = false;

        this.skinWalkerLookDelay = 60;

        this.suffocationTimer = 0;
        this.level2Timer = 200;
    }


    public boolean isShouldRender() {
        return shouldRender;
    }
    public void setShouldRender(boolean shouldRender) {
        this.shouldRender = shouldRender;
    }

    public void setFlashLightOn(boolean set){
        this.flashLightOn = set;
    }
    public boolean isFlashLightOn() {
        return flashLightOn;
    }

    public boolean isDoingCutscene() {
        return isDoingCutscene;
    }
    public void setDoingCutscene(boolean doingCutscene) {
        isDoingCutscene = doingCutscene;
    }

    public boolean isTeleporting() {
        return isTeleporting;
    }
    public void setTeleporting(boolean teleporting) {
        isTeleporting = teleporting;
    }

    public boolean isTeleportingToPoolrooms() {
        return isTeleportingToPoolrooms;
    }
    public void setTeleportingToPoolrooms(boolean teleportingToPoolrooms) {
        isTeleportingToPoolrooms = teleportingToPoolrooms;
    }

    public boolean shouldNoClip() {
        return shouldNoClip;
    }
    public void setShouldNoClip(boolean shouldNoClip) {
        this.shouldNoClip = shouldNoClip;
    }

    public boolean isShouldDoStatic() {
        return shouldDoStatic;
    }
    public void setShouldDoStatic(boolean shouldDoStatic) {
        this.shouldDoStatic = shouldDoStatic;
    }

    public Entity getTargetEntity() {
        return targetEntity;
    }
    public void setTargetEntity(Entity targetEntity) {
        this.targetEntity = targetEntity;
    }

    public int getSkinWalkerLookDelay() {
        return skinWalkerLookDelay;
    }
    public void setSkinWalkerLookDelay(int skinWalkerLookDelay) {
        this.skinWalkerLookDelay = skinWalkerLookDelay;
    }
    public void subtractSkinWalkerLookDelay() {
        this.skinWalkerLookDelay -= 1;
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        this.flashLightOn = tag.getBoolean("flashLightOn");
        this.shouldRender = tag.getBoolean("shouldRender");
        this.isDoingCutscene = tag.getBoolean("isDoingCutscene");
        this.playingGlitchSound = tag.getBoolean("playingGlitchSound");
        this.isTeleporting = tag.getBoolean("isTeleporting");
        this.isTeleportingToPoolrooms = tag.getBoolean("isTeleportingToPoolrooms");
        this.shouldNoClip = tag.getBoolean("shouldNoClip");
        this.shouldDoStatic = tag.getBoolean("shouldDoStatic");
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putBoolean("flashLightOn", this.flashLightOn);
        tag.putBoolean("shouldRender", this.shouldRender);
        tag.putBoolean("isDoingCutscene", this.isDoingCutscene);
        tag.putBoolean("playingGlitchSound", this.playingGlitchSound);
        tag.putBoolean("isTeleporting", this.isTeleporting);
        tag.putBoolean("isTeleportingToPoolrooms", this.isTeleportingToPoolrooms);
        tag.putBoolean("shouldNoClip", this.shouldNoClip);
        tag.putBoolean("shouldDoStatic", this.shouldDoStatic);
    }

    public void sync(){
        InitializeComponents.PLAYER.sync(this.player);
    }

    @Override
    public void clientTick() {
        MinecraftClient client = MinecraftClient.getInstance();

        //Sync Target Entity
        if(this.getTargetEntity() != client.targetedEntity) {
            this.setTargetEntity(client.targetedEntity);

            PacketByteBuf buffer = PacketByteBufs.create();
            if(this.getTargetEntity() != null){
                buffer.writeInt(this.getTargetEntity().getId());
            } else {
                buffer.writeInt(-1);
            }
            ClientPlayNetworking.send(InitializePackets.TARGET_ENTITY_SYNC, buffer);
        }

        //Client side stuff for level 0 -> 1 and 1 -> 2 transitions
        if(this.isTeleporting){
            ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
            this.isTeleporting = false;

            //Turn off the lights
            client.player.playSound(ModSounds.LIGHTS_OUT, SoundCategory.AMBIENT, 1, 1);
            SPBRevampedClient.getCutsceneManager().blackScreen.showBlackScreen(80, false, false);

            //PlaySound after black screen is over
            executorService.schedule(() -> {
                client.player.playSound(ModSounds.LIGHTS_ON, SoundCategory.AMBIENT, 1, 1);
                executorService.shutdown();
            }, 4000, TimeUnit.MILLISECONDS);
        }


        //Flashlight
        if (Keybinds.toggleFlashlight.wasPressed() && !SPBRevampedClient.getCutsceneManager().isPlaying && !SPBRevampedClient.getCutsceneManager().blackScreen.isBlackScreen) {
            player.playSound(ModSounds.FLASHLIGHT_CLICK, 1, 1);
            if (player.getWorld().getRegistryKey() != BackroomsLevels.POOLROOMS_WORLD_KEY) {
                this.setFlashLightOn(!this.isFlashLightOn());

                PacketByteBuf buffer = PacketByteBufs.create();
                buffer.writeBoolean(this.isFlashLightOn());
                ClientPlayNetworking.send(InitializePackets.FL_SYNC, buffer);
            } else {
                this.setFlashLightOn(false);
                player.sendMessage(Text.literal("Your flashlight got wet. ").append(Text.literal("It no longer works").formatted(Formatting.RED)), true);
            }
        }


        ////AMBIENCE////
        RegistryKey<World> level = this.player.getWorld().getRegistryKey();
        WorldEvents events = InitializeComponents.EVENTS.get(this.player.getWorld());

        if((level == BackroomsLevels.LEVEL1_WORLD_KEY || level == BackroomsLevels.LEVEL2_WORLD_KEY) && !client.getSoundManager().isPlaying(DeepAmbience)){
            DeepAmbience = new AmbientSoundInstance(this.player);
            client.getSoundManager().play(DeepAmbience);
        }

        if(level == BackroomsLevels.LEVEL2_WORLD_KEY && !client.getSoundManager().isPlaying(WaterPipeAmbience) && !client.getSoundManager().isPlaying(GasPipeAmbience)){
            WaterPipeAmbience = new WaterPipeSoundInstance(this.player);
            GasPipeAmbience = new GasPipeSoundInstance(this.player);

            client.getSoundManager().play(WaterPipeAmbience);
            client.getSoundManager().play(GasPipeAmbience);
        }

        if(level == BackroomsLevels.POOLROOMS_WORLD_KEY && events.isNoon() && !client.getSoundManager().isPlaying(PoolroomsNoonAmbience)){
            PoolroomsNoonAmbience = new PoolroomsNoonAmbienceSoundInstance(this.player);
            client.getSoundManager().play(PoolroomsNoonAmbience);
        } else if(level == BackroomsLevels.POOLROOMS_WORLD_KEY && !events.isNoon() && !client.getSoundManager().isPlaying(PoolroomsSunsetAmbience)) {
            PoolroomsSunsetAmbience = new PoolroomsSunsetAmbienceSoundInstance(this.player);
            client.getSoundManager().play(PoolroomsSunsetAmbience);
        }


        //Level0 Cutscene
        if(this.player.isInsideWall() && this.player.getWorld().getRegistryKey() == World.OVERWORLD && !this.isDoingCutscene){
            suffocationTimer++;
            if (suffocationTimer >= 40) {
                this.setDoingCutscene(true);
                suffocationTimer = 0;
            }
        }
    }

    @Override
    public void serverTick() {

        getPrevSettings();

        //Cast him to the Backrooms
        if(this.player.isInsideWall()) {
            if (this.player.getWorld().getRegistryKey() == World.OVERWORLD && !this.isDoingCutscene()) {
                suffocationTimer++;
                if (suffocationTimer == 1) {
                    this.player.getWorld().playSoundFromEntity(null, this.player, ModSounds.GLITCH, SoundCategory.AMBIENT, 1.0f, 1.0f);
                    this.playingGlitchSound = true;
                }

                if (suffocationTimer == 40) {
                    RegistryKey<World> registryKey = BackroomsLevels.LEVEL0_WORLD_KEY;
                    ServerWorld backrooms = this.player.getWorld().getServer().getWorld(registryKey);
                    if (backrooms == null) {
                        return;
                    }

                    TeleportTarget target = new TeleportTarget(new Vec3d(1.5, 22, 1.5), Vec3d.ZERO, this.player.getYaw(), this.player.getPitch());
                    FabricDimensions.teleport(this.player, backrooms, target);
                    this.setDoingCutscene(true);
                    this.sync();
                    suffocationTimer = 0;
                }
            }
        } else {
            if (this.playingGlitchSound) {
                StopSoundS2CPacket stopSoundS2CPacket = new StopSoundS2CPacket(new Identifier(SPBRevamped.MOD_ID, "glitch"), null);
                ((ServerPlayerEntity) this.player).networkHandler.sendPacket(stopSoundS2CPacket);
            }
            this.playingGlitchSound = false;
            suffocationTimer = 0;
        }


        //Level Switcheroo//

        //Level 0 -> Level 1
        if (this.player.getWorld().getRegistryKey() == BackroomsLevels.LEVEL0_WORLD_KEY) {
            ServerWorld level1 = this.player.getWorld().getServer().getWorld(BackroomsLevels.LEVEL1_WORLD_KEY);

            if (this.player.getPos().getY() <= 11) {
                if (!this.isTeleporting() && !this.readyForLevel1) {
                    for (PlayerEntity players : this.player.getServer().getPlayerManager().getPlayerList()) {
                        if (players.getWorld().getRegistryKey() == BackroomsLevels.LEVEL0_WORLD_KEY) {
                            teleportPlayer(players, 1);
                        }
                    }
                }
                if (this.readyForLevel1) {
                    for (PlayerEntity players : this.player.getServer().getPlayerManager().getPlayerList()) {
                        if(players.getWorld().getRegistryKey() == BackroomsLevels.LEVEL0_WORLD_KEY) {
                            TeleportTarget target = new TeleportTarget(calculateLevel1TeleportCoords(players), players.getVelocity(), players.getYaw(), players.getPitch());
                            FabricDimensions.teleport(players, level1, target);
                        }
                    }
                    this.readyForLevel1 = false;
                }
            } else {
                this.readyForLevel1 = false;
            }

        }
        //Level 1 -> Level 2
        else if (this.player.getWorld().getRegistryKey() == BackroomsLevels.LEVEL1_WORLD_KEY) {
            ServerWorld level2 = this.player.getWorld().getServer().getWorld(BackroomsLevels.LEVEL2_WORLD_KEY);

            if (this.player.getPos().getY() <= 12.5) {
                if (!this.isTeleporting() && !this.readyForLevel2) {
                    for (PlayerEntity players : this.player.getServer().getPlayerManager().getPlayerList()) {
                        if (players.getWorld().getRegistryKey() == BackroomsLevels.LEVEL1_WORLD_KEY) {
                            teleportPlayer(players, 2);
                        }
                    }
                }
                if (this.readyForLevel2) {
                    for (PlayerEntity players : this.player.getServer().getPlayerManager().getPlayerList()) {
                        if(players.getWorld().getRegistryKey() == BackroomsLevels.LEVEL1_WORLD_KEY) {
                            TeleportTarget target = new TeleportTarget(calculateLevel2TeleportCoords(players), players.getVelocity(), players.getYaw(), players.getPitch());
                            FabricDimensions.teleport(players, level2, target);
                        }
                    }
                    this.readyForLevel2 = false;
                }
            } else {
                this.readyForLevel2 = false;
            }

        }
        //Level 2 -> Poolrooms
        else if (this.player.getWorld().getRegistryKey() == BackroomsLevels.LEVEL2_WORLD_KEY) {
            ServerWorld poolrooms = this.player.getWorld().getServer().getWorld(BackroomsLevels.POOLROOMS_WORLD_KEY);

            if (Math.abs(this.player.getPos().getZ()) >= 1000) {
                this.level2Timer--;
                if(level2Timer <= 0){
                    if (!this.isTeleporting() && !this.readyForPoolrooms) {
                        for (PlayerEntity players : this.player.getServer().getPlayerManager().getPlayerList()) {
                            if (players.getWorld().getRegistryKey() == BackroomsLevels.LEVEL2_WORLD_KEY) {
                                startLevel2Teleport(players);
                            }
                        }
                    }
                    if (this.readyForPoolrooms) {
                        for (PlayerEntity players : this.player.getServer().getPlayerManager().getPlayerList()) {
                            if(players.getWorld().getRegistryKey() == BackroomsLevels.LEVEL2_WORLD_KEY) {
                                TeleportTarget target = new TeleportTarget(new Vec3d(16, 106, 16), Vec3d.ZERO, players.getYaw(), players.getPitch());
                                FabricDimensions.teleport(players, poolrooms, target);
                            }
                        }
                        this.readyForPoolrooms = false;
                    }
                }
            } else {
                this.level2Timer = 200;
                this.readyForPoolrooms = false;
            }

        }


        shouldSync();
    }


    private void shouldSync() {
        boolean sync = false;

        if(this.prevFlashLightOn != this.flashLightOn){
            sync = true;
        }

        if(sync){
         this.sync();
        }
    }

    private void getPrevSettings() {
        this.prevFlashLightOn = this.flashLightOn;
    }

    private void teleportPlayer(PlayerEntity players, int destLevel){
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        PlayerComponent playerComponent = InitializeComponents.PLAYER.get(players);

        if(!playerComponent.isTeleporting()) {
            playerComponent.setTeleporting(true);
            playerComponent.sync();

            executorService.schedule(() -> {
                switch (destLevel){
                    case 1: this.readyForLevel1 = true; break;
                    case 2: this.readyForLevel2 = true; break;
                }
                playerComponent.setTeleporting(false);
                executorService.shutdown();
            }, 2500, TimeUnit.MILLISECONDS);
        }
    }

    private void startLevel2Teleport(PlayerEntity players){
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        PlayerComponent playerComponent = InitializeComponents.PLAYER.get(players);

        if(!playerComponent.isTeleportingToPoolrooms()) {
            //First send the signal to start the camera Shake
            playerComponent.setTeleportingToPoolrooms(true);
            playerComponent.sync();

            //Shake the Camera of each player
            SPBRevamped.sendCameraShakePacket((ServerPlayerEntity) players, 100, 1, Easings.Easing.linear, true);

            //Then Noclip
            executorService.schedule(() -> {
                playerComponent.setShouldNoClip(true);
                playerComponent.sync();
                executorService.shutdown();
            }, 4500, TimeUnit.MILLISECONDS);

            //Turn Player screen to Black
            executorService.schedule(() -> {
                SPBRevamped.sendBlackScreenPacket((ServerPlayerEntity) players, 20, true, false);
                executorService.shutdown();
            }, 4900, TimeUnit.MILLISECONDS);

            //After the screen turns black THEN teleport
            executorService.schedule(() -> {
                this.readyForPoolrooms = true;
                playerComponent.setTeleportingToPoolrooms(false);
                playerComponent.setShouldNoClip(false);
                playerComponent.sync();
                executorService.shutdown();
            }, 5500, TimeUnit.MILLISECONDS);

        }
    }

    private Vec3d calculateLevel1TeleportCoords(PlayerEntity player){
        if(this.player.getChunkPos().equals(player.getChunkPos())) {
            int chunkX = this.player.getChunkPos().getStartX();
            int chunkZ = this.player.getChunkPos().getStartZ();

            double playerX = player.getPos().x;
            double playerZ = player.getPos().z;

            return new Vec3d(playerX - chunkX, player.getPos().y + 15, playerZ - chunkZ);
        } else {
            return new Vec3d(8.5, 36.5, 2.5);
        }
    }

    private Vec3d calculateLevel2TeleportCoords(PlayerEntity player){
        if(this.player.getChunkPos().equals(player.getChunkPos())) {
            int chunkX = this.player.getChunkPos().getStartX();
            int chunkZ = this.player.getChunkPos().getStartZ();

            double playerX = player.getPos().x;
            double playerZ = player.getPos().z;

            return new Vec3d((playerX - chunkX) - 1, player.getPos().y + 8, playerZ - chunkZ);
        } else {
            return new Vec3d(11.5, 29, 7.5);
        }
    }

    public float setSetStaticTimer() {
        if(this.isShouldDoStatic()) {
            if (this.staticTimer == null) {
                this.staticTimer = new Timer(2000, Easings.Easing.easeOutCirc);
                this.staticTimer.startTimer();
            }

            if(this.staticTimer.isDone()){
                this.staticTimer = null;

                PacketByteBuf buffer = PacketByteBufs.create();
                ClientPlayNetworking.send(InitializePackets.STATIC_PACKET, buffer);
                return 2.0f;
            }

            return this.staticTimer.getCurrentTime();
        }

        return 2.0f;
    }
}
