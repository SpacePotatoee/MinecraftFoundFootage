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
import com.sp.world.BackroomsLevels;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.ClientTickingComponent;
import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.VeilRenderer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.entity.LivingEntity;
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

public class PlayerComponent implements AutoSyncedComponent, ClientTickingComponent, ServerTickingComponent {
    private final PlayerEntity player;
    private boolean flashLightOn;
    private float cameraRoll;
    private int lightRenderDistance;
    private boolean on;

    private boolean isDoingCutscene;
    private int timer;
    private boolean playingGlitchSound;

    private boolean reloadLights;

    MovingSoundInstance DeepAmbience;
    MovingSoundInstance GasPipeAmbience;
    MovingSoundInstance WaterPipeAmbience;
    MovingSoundInstance PoolroomsNoonAmbience;
    MovingSoundInstance PoolroomsSunsetAmbience;


    private boolean prevFlashLightOn;

    public PlayerComponent(LivingEntity player){
        this.player = (PlayerEntity) player;
        this.flashLightOn = false;
        this.on = false;

        this.isDoingCutscene = false;
        this.timer = 0;

        this.reloadLights = false;
    }


    public int getLightRenderDistance() {
        return lightRenderDistance;
    }

    public void setLightRenderDistance(int lightRenderDistance) {
        this.lightRenderDistance = lightRenderDistance;
    }

    public void setCameraRoll(float roll){
        this.cameraRoll = roll;
    }

    public float getCameraRoll(){
        return this.cameraRoll;
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

    public boolean isReloadLights() {
        return reloadLights;
    }

    public void setReloadLights(boolean reloadLights) {
        this.reloadLights = reloadLights;
    }

    public boolean isPlayingGlitchSound() {
        return playingGlitchSound;
    }

    public void setPlayingGlitchSound(boolean playingGlitchSound) {
        this.playingGlitchSound = playingGlitchSound;
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        this.flashLightOn = tag.getBoolean("isFlashLightOn");
        this.isDoingCutscene = tag.getBoolean("isDoingCutscene");
        this.reloadLights = tag.getBoolean("reloadLights");
        this.playingGlitchSound = tag.getBoolean("playingGlitchSound");
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putBoolean("isFlashLightOn", this.flashLightOn);
        tag.putBoolean("isDoingCutscene", this.isDoingCutscene);
        tag.putBoolean("reloadLights", this.reloadLights);
        tag.putBoolean("playingGlitchSound", this.playingGlitchSound);
    }

    public void sync(){
        InitializeComponents.PLAYER.sync(this.player);
    }

    @Override
    public void clientTick() {
        MinecraftClient client = MinecraftClient.getInstance();
        VeilRenderer renderer = VeilRenderSystem.renderer();


        if(this.reloadLights){
            renderer.getDeferredRenderer().reset();
            this.reloadLights = false;

            PacketByteBuf buffer = PacketByteBufs.create();
            buffer.writeBoolean(this.reloadLights);
            ClientPlayNetworking.send(InitializePackets.RESET_SYNC, buffer);
        }

        if(Keybinds.toggleFlashlight.wasPressed()){
            player.playSound(ModSounds.FLASHLIGHT_CLICK, 1, 1);
            if (player.getWorld().getRegistryKey() != BackroomsLevels.POOLROOMS_WORLD_KEY) {
                if (on) {
                    this.setFlashLightOn(false);
                    on = false;
                } else {
                    this.setFlashLightOn(true);
                    on = true;
                }
            }
            else{
                this.setFlashLightOn(false);
                player.sendMessage(Text.literal("Your flashlight got wet. ").append(Text.literal("It no longer works").formatted(Formatting.RED)), true);
            }
        }


        //Ambience
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
            timer++;
            if (timer >= 40) {
                this.setDoingCutscene(true);
                timer = 0;
            }
        }
    }

    @Override
    public void serverTick() {
        getPrevSettings();

        //Cast him to the Backrooms
        if(this.player.isInsideWall()) {
            if (this.player.getWorld().getRegistryKey() == World.OVERWORLD && !this.isDoingCutscene()) {
                timer++;
                if (timer == 1) {
                    this.player.getWorld().playSoundFromEntity(null, this.player, ModSounds.GLITCH, SoundCategory.AMBIENT, 1.0f, 1.0f);
                    this.playingGlitchSound = true;
                }

                if (timer == 40) {
                    RegistryKey<World> registryKey = BackroomsLevels.LEVEL0_WORLD_KEY;
                    ServerWorld backrooms = (ServerWorld) this.player.getWorld().getServer().getWorld(registryKey);
                    if (backrooms == null) {
                        return;
                    }

                    TeleportTarget target = new TeleportTarget(new Vec3d(1.5, 22, 1.5), Vec3d.ZERO, this.player.getYaw(), this.player.getPitch());
                    FabricDimensions.teleport(this.player, backrooms, target);
                    this.setDoingCutscene(true);
                    this.sync();
                    timer = 0;
                }
            }
        } else {
            if (this.playingGlitchSound) {
                StopSoundS2CPacket stopSoundS2CPacket = new StopSoundS2CPacket(new Identifier(SPBRevamped.MOD_ID, "glitch"), null);
                ((ServerPlayerEntity) this.player).networkHandler.sendPacket(stopSoundS2CPacket);
            }
            this.playingGlitchSound = false;
            timer = 0;
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
}
