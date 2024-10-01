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

public class PlayerComponent implements AutoSyncedComponent, ServerTickingComponent, ClientTickingComponent {
    private final PlayerEntity player;
    private boolean flashLightOn;

    private float cameraRoll;
    private float OtherCameraRoll;

    private boolean setCameraDown;
    private BlockPos CameraPos;
    private boolean cameraInOtherInventory;
    private PlayerEntity OtherPlayer;
    private boolean hasOtherCamera;

    private Vec3d cameraItemPos;
    private boolean isCameraItem;
    private float cameraItemYaw;
    private int lightRenderDistance;

    public PlayerComponent(PlayerEntity player){
        this.player = player;
        this.flashLightOn = false;
        this.setCameraDown = false;
    }


    public int getLightRenderDistance() {
        return lightRenderDistance;
    }

    public void setLightRenderDistance(int lightRenderDistance) {
        this.lightRenderDistance = lightRenderDistance;
    }

    public float getCameraItemYaw() {
        return cameraItemYaw;
    }

    public void setCameraItemYaw(float cameraItemYaw) {
        this.cameraItemYaw = cameraItemYaw;
    }


    public Vec3d getCameraItemPos() {
        return cameraItemPos;
    }

    public void setCameraItemPos(Vec3d cameraItemPos) {
        this.cameraItemPos = cameraItemPos;
    }

    public boolean isCameraItem() {
        return isCameraItem;
    }

    public void setCameraItem(boolean cameraItem) {
        isCameraItem = cameraItem;
    }



    public float getOtherCameraRoll() {
        return OtherCameraRoll;
    }

    public void setOtherCameraRoll(float otherCameraRoll) {
        OtherCameraRoll = otherCameraRoll;
    }


    public void setHasOtherCamera(boolean r){
        this.hasOtherCamera = r;
    }

    public boolean HasOtherCamera(){
        return this.hasOtherCamera;
    }



    public void setCameraPos(BlockPos pos){
        this.CameraPos = pos;
    }

    public BlockPos getCameraPos(){
        return this.CameraPos;
    }

    public void setOtherPlayerPos(PlayerEntity player){
        this.OtherPlayer = player;
    }

    public PlayerEntity getOtherPlayerPos(){
        return this.OtherPlayer;
    }

    public void setCameraInOtherInventory(boolean r){
        this.cameraInOtherInventory = r;
    }

    public boolean isCameraInOtherInventory(){
        return this.cameraInOtherInventory;
    }

    public void setCameraRoll(float roll){
        this.cameraRoll = roll;
    }

    public float getCameraRoll(){
        return this.cameraRoll;
    }

    public void SetCameraDown(boolean set){
        this.setCameraDown = set;
    }

    public boolean isCameraDown(){
        return this.setCameraDown;
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
        this.cameraInOtherInventory = tag.getBoolean("cameraInOtherInventory");
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putBoolean("isFlashLightOn", this.flashLightOn);
        tag.putBoolean("cameraInOtherInventory", this.cameraInOtherInventory);
    }

    public void sync(){
        InitializeComponents.PLAYER.sync(this.player);
    }





    @Override
    public void serverTick() {

        ///////////////////////////////////////////////////////////////////////////////

        int boundCameras = 0;
        World world = this.player.getWorld();
        //ClientWorld world = MinecraftClient.getInstance().world;
        PacketByteBuf buffer1 = PacketByteBufs.create();
        PacketByteBuf buffer2 = PacketByteBufs.create();
        PacketByteBuf buffer3 = PacketByteBufs.create();
        UUID AttachedPlayerUUID = null;
        ServerPlayerEntity AttachedPlayer = null;
        ItemStack stack;


        if (world != null) {
            Iterator<ServerPlayerEntity> listOfPlayers = (Iterator<ServerPlayerEntity>) world.getPlayers().iterator();

            while (listOfPlayers.hasNext()) {
                ServerPlayerEntity player = listOfPlayers.next();

                if (player != null) {
                    PlayerComponent playerComponentPlayer = InitializeComponents.PLAYER.get(player);
                    PlayerInventory inventory = player.getInventory();

                    for (int i = 0; i < inventory.size(); ++i) {
                        inventory.updateItems();
                        stack = inventory.getStack(i);
                        if (stack.isEmpty()) stack.setNbt(null);

                        if (stack.getSubNbt("attachedPlayer") != null) {
                            AttachedPlayerUUID = stack.getSubNbt("attachedPlayer").getUuid("attachedPlayer");
                            AttachedPlayer = (ServerPlayerEntity) world.getPlayerByUuid(AttachedPlayerUUID);
                        }

                        if (AttachedPlayer != null) {
                            PlayerComponent playerComponent = InitializeComponents.PLAYER.get(AttachedPlayer);


                            if (player != AttachedPlayer) {
                                boundCameras++;

                                if (!Objects.equals(AttachedPlayerUUID.toString(), player.getUuid().toString())) {
                                    playerComponentPlayer.setHasOtherCamera(true);
                                    playerComponent.setOtherPlayerPos(player);
                                    playerComponent.setCameraInOtherInventory(true);

                                    buffer1.writeBoolean(playerComponent.isCameraInOtherInventory());
                                    buffer1.writeUuid(player.getUuid());
                                    ServerPlayNetworking.send((ServerPlayerEntity) AttachedPlayer, InitializePackets.OTHER_PLAYER_UUID, buffer1);

                                    buffer3.writeBoolean(playerComponentPlayer.HasOtherCamera());
                                    ServerPlayNetworking.send(player, InitializePackets.HAS_OTHER_CAMERA, buffer3);
                                }
                            } else {
                                playerComponent.setCameraInOtherInventory(false);
                                playerComponent.setCameraItem(false);
                                playerComponent.SetCameraDown(false);
                                AttachedPlayer.setCameraEntity(AttachedPlayer);
                                buffer2.writeBoolean(playerComponent.isCameraInOtherInventory());
                                ServerPlayNetworking.send((ServerPlayerEntity) AttachedPlayer, InitializePackets.IN_OTHER_INVENTORY, buffer2);
                            }

                        }
                        AttachedPlayerUUID = null;
                        AttachedPlayer = null;

                    }
                    if (boundCameras == 0) {
                        if (playerComponentPlayer.HasOtherCamera()) {
                            playerComponentPlayer.setHasOtherCamera(false);
                            buffer3.writeBoolean(playerComponentPlayer.HasOtherCamera());
                            ServerPlayNetworking.send(player, InitializePackets.HAS_OTHER_CAMERA, buffer3);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void clientTick() {

    }
}
