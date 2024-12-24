package com.sp.render.camera;

import com.sp.ConfigStuff;
import com.sp.util.MathStuff;
import foundry.veil.api.client.util.Easings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.PerlinNoiseSampler;
import net.minecraft.util.math.random.Random;

public class CameraShake {
    private double trauma;
    private double traumaGoal;
    private double noiseSpeed;
    private double noiseSpeedGoal;
    private double noiseY;
    private double amplitude;
    private PerlinNoiseSampler noiseSampler;
    private float cameraZRot;
//    private boolean shouldShake;
//    private boolean inverted;
//    private float intensity;
//    private float backupIntensity;
//    private long duration;
//    private long startTime;
//    private Easings.Easing easing;

    public CameraShake(){
        this.trauma = 0.1;
        this.noiseSpeed = 0.1;
        this.noiseY = 0;
        this.amplitude = 5;
        this.noiseSampler = new PerlinNoiseSampler(Random.create());
        this.cameraZRot = 0.0f;
//        this.shouldShake = false;
//        this.inverted = false;
//        this.intensity = 0;
//        this.backupIntensity = 0;
//        this.duration = 0;
//        this.startTime = 0;
//        this.easing = Easings.Easing.linear;
    }

//    public void tick(Camera camera) {
////        if(this.shouldShake && this.duration > 0L && this.intensity > 0) {
//
//            float timer = (float) (System.currentTimeMillis() - this.startTime) / this.duration;
//
//            float pitchOffset = 0;
//            float yawOffset = 0;
//
//            if (timer >= 1.0) {
//                this.reset();
//            } else {
//                Random random = Random.create();
//
//                pitchOffset = MathHelper.nextFloat(random, -this.intensity / 2, this.intensity / 2);
//                yawOffset = MathHelper.nextFloat(random, -this.intensity / 2, this.intensity / 2);
//
//
//                updateIntensity(timer);
//            }
//
//            camera.setRotation(camera.getYaw() + yawOffset, camera.getPitch() + pitchOffset);
////        }
//    }

    public void tick(Camera camera) {
        if (ConfigStuff.enableRealCamera) {
            float frameDelta = MinecraftClient.getInstance().getLastFrameDuration();
            if (this.noiseY >= 1000) {
                this.noiseY = 0;
            }

            PlayerEntity player = MinecraftClient.getInstance().player;
            if (player != null) {
                float playerSpeed = (player.horizontalSpeed - player.prevHorizontalSpeed) * 6;

                this.traumaGoal = Math.max(0.75 * playerSpeed, 0.5);
                this.noiseSpeedGoal = Math.max(0.2 * playerSpeed, 0.1);
                this.amplitude = 3;

                this.trauma = Math.max(MathStuff.Lerp((float) this.trauma, (float) this.traumaGoal, 0.93f, frameDelta), 0.5);
                this.noiseSpeed = Math.max(MathStuff.Lerp((float) this.noiseSpeed, (float) this.noiseSpeedGoal, 0.93f, frameDelta), 0.1);

                this.noiseY += (this.noiseSpeed * frameDelta);

                double pitchOffset = this.amplitude * this.getShakeIntensity() * (this.noiseSampler.sample(1, this.noiseY, 0));
                double yawOffset = this.amplitude * this.getShakeIntensity() * (this.noiseSampler.sample(73, this.noiseY, 0));
                double rollOffset = this.amplitude * this.getShakeIntensity() * (this.noiseSampler.sample(146, this.noiseY, 0));

                camera.setRotation((float) (camera.getYaw() + yawOffset), (float) (camera.getPitch() + pitchOffset));
                this.cameraZRot = (float) rollOffset * 2;
            }
        }
    }

    private double getShakeIntensity(){
        return this.trauma * this.trauma;
    }

    public float getCameraZRot() {
        return this.cameraZRot;
    }

//    private void updateIntensity(float timer) {
//        float ease = this.easing.ease(timer);
//        if(this.inverted){
//            this.intensity = MathHelper.lerp(ease, 0.1f, this.backupIntensity);
//        } else {
//            this.intensity = MathHelper.lerp(ease, this.backupIntensity, 0);
//        }
//
//    }
//
//    private void reset(){
//        this.shouldShake = false;
//        this.duration = 0;
//        this.intensity = 0;
//        this.backupIntensity = 0;
//    }
//
//    public void setCameraShake(int Duration, float Intensity, Easings.Easing Easing, boolean Inverted){
//        this.duration = Duration * 50L;
//        this.intensity = Intensity;
//        this.backupIntensity = intensity;
//        this.easing = Easing;
//        this.inverted = Inverted;
//        this.shouldShake = true;
//        this.startTime = System.currentTimeMillis();
//
//    }

}
