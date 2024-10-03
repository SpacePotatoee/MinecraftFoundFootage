package com.sp.cca_stuff;

import com.sp.networking.InitializePackets;
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
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Iterator;
import java.util.Objects;
import java.util.UUID;

public class PlayerComponent implements AutoSyncedComponent {
    private final PlayerEntity player;
    private boolean flashLightOn;

    private float cameraRoll;

    private int lightRenderDistance;

    public PlayerComponent(PlayerEntity player){
        this.player = player;
        this.flashLightOn = false;
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

}
