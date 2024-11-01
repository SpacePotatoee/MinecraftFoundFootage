package com.sp.render;

import foundry.veil.api.client.util.Easings;
import net.minecraft.client.render.Camera;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;

public class CameraShake {
    private boolean shouldShake;
    private boolean inverted;
    private float intensity;
    private float backupIntensity;
    private long duration;
    private long startTime;
    private Easings.Easing easing;

    public CameraShake(){
        this.shouldShake = false;
        this.inverted = false;
        this.intensity = 0;
        this.backupIntensity = 0;
        this.duration = 0;
        this.startTime = 0;
        this.easing = Easings.Easing.linear;
    }

    public void tick(Camera camera) {
        if(this.shouldShake && this.duration > 0L && this.intensity > 0) {

            float timer = (float) (System.currentTimeMillis() - this.startTime) / this.duration;

            if (timer >= 1.0) {
                this.reset();
            } else {
                Random random = Random.create();

                float pitchOffset = MathHelper.nextFloat(random, -this.intensity / 2, this.intensity / 2);
                float yawOffset = MathHelper.nextFloat(random, -this.intensity / 2, this.intensity / 2);

                camera.setRotation(camera.getYaw() + yawOffset, camera.getPitch() + pitchOffset);
                updateIntensity(timer);
                System.out.println(intensity);
            }
        }
    }

    private void updateIntensity(float timer) {
        float ease = this.easing.ease(timer);
        if(this.inverted){
            this.intensity = MathHelper.lerp(ease, 0.1f, this.backupIntensity);
        } else {
            this.intensity = MathHelper.lerp(ease, this.backupIntensity, 0);
        }

    }

    private void reset(){
        this.shouldShake = false;
        this.duration = 0;
        this.intensity = 0;
        this.backupIntensity = 0;
    }

    public void setCameraShake(int Duration, float Intensity, Easings.Easing Easing, boolean Inverted){
        this.duration = Duration * 50L;
        this.intensity = Intensity;
        this.backupIntensity = intensity;
        this.easing = Easing;
        this.inverted = Inverted;
        this.shouldShake = true;
        this.startTime = System.currentTimeMillis();

    }

}
