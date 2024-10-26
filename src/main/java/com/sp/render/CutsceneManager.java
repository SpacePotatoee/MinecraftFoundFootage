package com.sp.render;

import com.sp.SPBRevampedClient;
import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import com.sp.mixin.PathAccessor;
import com.sp.networking.InitializePackets;
import com.sp.util.MathStuff;
import com.sp.world.BackroomsLevels;
import foundry.veil.api.client.anim.Frame;
import foundry.veil.api.client.anim.Keyframe;
import foundry.veil.api.client.anim.Path;
import foundry.veil.api.client.util.Easings;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.List;

/**
 * Will likely use in a future API if I ever feel like making it
 */
@SuppressWarnings("DataFlowIssue")
public class CutsceneManager {
    public boolean isPlaying;
    public boolean backroomsBySP;
    private long startTime;
    private int duration;
    public BlackScreen blackScreen;
    private Entity camera;
    private final Path cameraPathPos;
    private final Path cameraPathRot;
    public float cameraRotZ;
    private final MinecraftClient client;

    public CutsceneManager(){
        this.isPlaying = false;
        this.backroomsBySP = false;
        this.camera = null;
        this.duration = 7000;
        this.blackScreen = new BlackScreen();
        this.client = MinecraftClient.getInstance();
        this.cameraRotZ = 0;
        this.cameraPathPos = new Path(List.of(
                new Keyframe(new Vec3d(0.5,220,0.5), Vec3d.ZERO, Vec3d.ZERO,(int) (this.duration / 1000) * 20, Easings.Easing.easeInSine),
                new Keyframe(new Vec3d(0.5,27,0.5), Vec3d.ZERO, Vec3d.ZERO,0, Easings.Easing.linear)
        ), false, false);
        this.cameraPathRot = new Path(List.of(
                new Keyframe(Vec3d.ZERO, new Vec3d(60,3,-45), Vec3d.ZERO,(int) (this.duration / 1000) * 20, Easings.Easing.linear),
                new Keyframe(Vec3d.ZERO, new Vec3d(86,114,45), Vec3d.ZERO,0, Easings.Easing.linear)
        ), false, false);
    }


    public void tick(){
        if(client.player != null && client.world != null) {
            PlayerComponent playerComponent = InitializeComponents.PLAYER.get(client.player);
            if(playerComponent.isDoingCutscene() && client.world.getRegistryKey() == BackroomsLevels.LEVEL0_WORLD_KEY){
                this.Fall();
                this.BackroomsBySP();
            } else {
                playerComponent.setDoingCutscene(false);
            }
            this.blackScreen.tick();
        }
    }

    private void Fall() {
        if(!this.backroomsBySP) {
            if (!this.isPlaying) {
                this.startTime = System.currentTimeMillis();
                this.isPlaying = true;
                client.options.hudHidden = true;
            }
            float timer = (float) (System.currentTimeMillis() - this.startTime) / this.duration;
            if (this.camera == null) {
                this.initCamera();
            }

            if (timer >= 1.0) {
                this.blackScreen.showBlackScreen(40);
//                this.backroomsBySP = true;
                this.reset();
            } else {
                double interpolate = MathStuff.mod(timer * ((PathAccessor) cameraPathPos).getFrames().size(), 1);


                Vec3d newCameraPos = lerpedCameraPos(timer, interpolate);
                Vec3d newCameraRot = lerpedCameraRot(timer, interpolate);

                this.cameraRotZ = (float) newCameraRot.z;
                camera.refreshPositionAndAngles(newCameraPos.x, newCameraPos.y, newCameraPos.z, (float) newCameraRot.y, (float) newCameraRot.x);
                client.cameraEntity = camera;
            }
        }
    }

    private void BackroomsBySP(){
        if(this.backroomsBySP) {
//            camera.refreshPositionAndAngles(newCameraPos.x, newCameraPos.y, newCameraPos.z, (float) newCameraRot.y, (float) newCameraRot.x);
//            client.cameraEntity = camera;
        }
    }

    private Vec3d lerpedCameraRot(float timer, double interpolate){
        Vec3d currentFrameRotX = cameraPathRot.frameAtProgress(timer).getRotation();
        Vec3d prevFrameRotX = previousFrameAtProgress(cameraPathRot, timer).getRotation();

        return new Vec3d(
                MathHelper.lerp(interpolate, prevFrameRotX.x, currentFrameRotX.x),
                MathHelper.lerp(interpolate, prevFrameRotX.y, currentFrameRotX.y),
                MathHelper.lerp(interpolate, prevFrameRotX.z, currentFrameRotX.z)
        );
    }

    private Vec3d lerpedCameraPos(float timer, double interpolate){
        Vec3d currentFramePos = cameraPathPos.frameAtProgress(timer).getPosition();
        Vec3d prevFramePos = previousFrameAtProgress(cameraPathPos, timer).getPosition();

        return new Vec3d(
                MathHelper.lerp(interpolate, prevFramePos.x, currentFramePos.x),
                MathHelper.lerp(interpolate, prevFramePos.y, currentFramePos.y),
                MathHelper.lerp(interpolate, prevFramePos.z, currentFramePos.z)
        );
    }

    private void reset(){
        PlayerComponent playerComponent = InitializeComponents.PLAYER.get(client.player);
        this.isPlaying = false;
        this.camera.remove(Entity.RemovalReason.DISCARDED);
        this.camera = null;
        client.cameraEntity = client.player;
        client.options.hudHidden = false;
        playerComponent.setDoingCutscene(false);

        PacketByteBuf buffer = PacketByteBufs.create();
        buffer.writeBoolean(playerComponent.isDoingCutscene());
        ClientPlayNetworking.send(InitializePackets.CUTSCENE_SYNC, buffer);

    }

    private void initCamera() {
        this.camera = new ItemEntity(client.world, 1.5, 300, 1.5, ItemStack.EMPTY);
        this.camera.refreshPositionAndAngles(1.5, 300, 1.5, 0, 90);
    }

    private Frame previousFrameAtProgress(Path path, double progress){
        List<Frame> frames = ((PathAccessor) path).getFrames();
        int index = (int) (frames.size() * progress) - 1;
        if(index < 0){
            return frames.get(0);
        }
        return frames.get(index);
    }



    @SuppressWarnings("FieldCanBeLocal")
    public class BlackScreen {
        public boolean isBlackScreen;
        private long duration;
        private long startTime;

        //Duration in ticks
        public BlackScreen(){
            this.startTime = 0L;
            this.isBlackScreen = false;
            this.duration = 0;
        }

        public void showBlackScreen(int time){
            this.duration = time * 50L;
            this.isBlackScreen = true;
            this.startTime = System.currentTimeMillis();
            client.options.hudHidden = true;
        }

        //Tick
        public void tick(){
            if(isBlackScreen) {
                MinecraftClient client = MinecraftClient.getInstance();
                float timer = (float) (System.currentTimeMillis() - this.startTime) / this.duration;

                if (timer >= 1.0) {
                    SPBRevampedClient.blackScreen = false;
                    this.isBlackScreen = false;
                    client.options.hudHidden = false;
                    this.startTime = 0;
                } else {
                    SPBRevampedClient.blackScreen = true;
                }
            }
        }

    }

}
