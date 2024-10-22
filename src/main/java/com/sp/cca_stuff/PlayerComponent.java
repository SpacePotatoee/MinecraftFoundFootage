package com.sp.cca_stuff;

import com.sp.Keybinds;
import com.sp.networking.InitializePackets;
import com.sp.sounds.ModSounds;
import com.sp.sounds.instances.AmbientSoundInstance;
import com.sp.sounds.instances.pipes.GasPipeSoundInstance;
import com.sp.sounds.instances.pipes.WaterPipeSoundInstance;
import com.sp.world.levels.BackroomsLevels;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.ClientTickingComponent;
import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Iterator;
import java.util.Objects;
import java.util.UUID;

public class PlayerComponent implements AutoSyncedComponent, ClientTickingComponent {
    private final PlayerEntity player;
    private boolean flashLightOn;
    private float cameraRoll;
    private int lightRenderDistance;
    private boolean on;
    private boolean playingAmbience;
    private boolean playingPipeAmbience;

    private boolean prevFlashLightOn;

    public PlayerComponent(PlayerEntity player){
        this.player = player;
        this.flashLightOn = false;
        this.playingAmbience = false;
        this.playingPipeAmbience = false;
        this.on = false;
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

    @Override
    public void readFromNbt(NbtCompound tag) {
        this.flashLightOn = tag.getBoolean("isFlashLightOn");
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putBoolean("isFlashLightOn", this.flashLightOn);
    }

    public void sync(){
        InitializeComponents.PLAYER.sync(this.player);
    }

    @Override
    public void clientTick() {
        MinecraftClient client = MinecraftClient.getInstance();

        getPrevSettings();
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


        RegistryKey<World> level = this.player.getWorld().getRegistryKey();
        if((level == BackroomsLevels.LEVEL1_WORLD_KEY || level == BackroomsLevels.LEVEL2_WORLD_KEY) && !this.playingAmbience){
            client.getSoundManager().play(new AmbientSoundInstance(this.player));
            this.playingAmbience = true;
        }else if((level != BackroomsLevels.LEVEL1_WORLD_KEY && level != BackroomsLevels.LEVEL2_WORLD_KEY) || this.player.isRemoved()){
            this.playingAmbience = false;
        }

        if(level == BackroomsLevels.LEVEL2_WORLD_KEY && !this.playingPipeAmbience){
            client.getSoundManager().play(new WaterPipeSoundInstance(this.player));
            client.getSoundManager().play(new GasPipeSoundInstance(this.player));
            this.playingPipeAmbience = true;
        }else if(level != BackroomsLevels.LEVEL2_WORLD_KEY || this.player.isRemoved()){
            this.playingPipeAmbience = false;
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
