package com.sp.util;

import foundry.veil.api.client.util.Easings;


public class Timer {
    private boolean started;
    private long startTime;
    private long time;
    private float currentTime;
    private Easings.Easing easing;
    private boolean done;


    public Timer(long time, Easings.Easing easing){
        this.time = time;
        this.easing = easing;
    }

    public void startTimer(){
        this.started = true;
        this.done = false;
        this.startTime = System.currentTimeMillis();
    }

    public float getCurrentTime(){
        if(started){
            currentTime = (float) (System.currentTimeMillis() - this.startTime) / time;

            if(currentTime >= 1.0){
                this.started = false;
                this.done = true;
                this.currentTime = 0;
            }

        }

        return easing.ease(currentTime);
    }

    public boolean hasStarted(){
        return this.started;
    }

    public boolean isDone(){
        return this.done;
    }


}
