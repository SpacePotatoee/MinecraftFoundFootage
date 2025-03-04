package com.sp.world.events.level2;

import com.sp.SPBRevampedClient;
import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.WorldEvents;
import com.sp.init.ModSounds;
import com.sp.world.events.AbstractEvent;
import net.minecraft.world.World;

public class Level2Warp implements AbstractEvent {
    boolean done = false;

    @Override
    public void init(World world) {
        WorldEvents events = InitializeComponents.EVENTS.get(world);
        events.setLevel2Warp(true);
    }

    @Override
    public void reset(World world) {
        WorldEvents events = InitializeComponents.EVENTS.get(world);
        if(SPBRevampedClient.tickTimer.getCurrentTick() == 0){
            events.setLevel2Warp(false);
            done = true;
        }
    }

    @Override
    public void end(World world) {
        WorldEvents events = InitializeComponents.EVENTS.get(world);
        events.setLevel2Warp(false);
        done = true;
    }

    @Override
    public boolean isDone() {
        return done;
    }


    @Override
    public int duration() {
        return 1200;
    }
}
