package com.sp.util;

import foundry.veil.api.client.util.Easings;
import net.minecraft.util.math.MathHelper;


public class Timer {
    private boolean started;
    private boolean reversed;
    private long startTime;
    private long duration;
    private float currentTime = 0.0f;
    private final Easings.Easing easingIn;
    private final Easings.Easing easingOut;
    private boolean done;


    public Timer(long duration, Easings.Easing easing){
        this.duration = duration;
        this.easingIn = easing;
        this.easingOut = easing;
    }

    public Timer(long duration, Easings.Easing easeIn, Easings.Easing easeOut){
        this.duration = duration;
        this.easingIn = easeIn;
        this.easingOut = easeOut;
    }

    public void startTimer(){
        if(!this.started) {
            this.currentTime = 0.0f;
            this.started = true;
            this.done = false;
            this.startTime = System.currentTimeMillis();
        }
    }

    public float getCurrentTime(){
        if(started){
            currentTime = MathHelper.clamp((float) (System.currentTimeMillis() - this.startTime) / duration, 0.0f, 1.0f);

            if(currentTime >= 1.0){
//                this.started = false;
                this.done = true;
            }

        }

        if(reversed){
            return 1.0f - this.easingOut.ease(currentTime);
        }
        return this.easingIn.ease(currentTime);
    }

    public void forward(){
        if(reversed) {
            this.started = false;
            this.startTimer();
            this.reversed = false;
        }
    }

    public void reverse(){
        if(!reversed) {
            this.started = false;
            this.startTimer();
            this.reversed = true;
        }
    }

    public boolean hasStarted(){
        return this.started;
    }

    public boolean isDone(){
        return this.done;
    }


}
