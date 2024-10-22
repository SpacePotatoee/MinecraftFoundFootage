package com.sp.world.events.level2;

import com.sp.SPBRevampedClient;
import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.WorldEvents;
import com.sp.sounds.ModSounds;
import com.sp.world.events.AbstractEvent;
import net.minecraft.world.World;

public class Level2Warp implements AbstractEvent {
    boolean done = false;

    @Override
    public void init(World world) {
        WorldEvents events = InitializeComponents.EVENTS.get(world);
        events.setLevel2Warp(true);
        playSound(world, ModSounds.LEVEL2_WARP_CREAKING_LOOP);
    }

    @Override
    public void reset(World world) {
        WorldEvents events = InitializeComponents.EVENTS.get(world);
        System.out.println("RESET");
        if(SPBRevampedClient.tickTimer.getCurrentTick() == 0){
            events.setLevel2Warp(false);
            done = true;
        }
    }

    @Override
    public boolean isDone() {
        return done;
    }

    @Override
    public int duration() {
        return 100;
    }
}
