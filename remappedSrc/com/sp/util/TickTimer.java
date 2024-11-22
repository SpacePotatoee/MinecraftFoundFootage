package com.sp.util;

import java.util.Vector;

public class TickTimer {
    private static final Vector<TickTimer> allInstances = new Vector<>();
    private int currentTick;
    private boolean on;

    public TickTimer(){
        this.currentTick = 0;
        this.on = true;
        allInstances.add(this);
    }

    public int getCurrentTick(){
        return this.currentTick;
    }

    public void addCurrentTick(){
        if(on) this.currentTick++;
    }

    public void resetToZero(){
        this.currentTick = 0;
    }

    public void setOnOrOff(boolean on){
        this.on = on;
    }

    public static synchronized Vector<TickTimer> getAllInstances(){
        return (Vector<TickTimer>) allInstances.clone();
    }

}
