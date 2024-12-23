package com.sp.world.events.level1;

import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.WorldEvents;
import com.sp.init.ModSounds;
import com.sp.world.events.AbstractEvent;
import net.minecraft.world.World;

public class Level1Blackout implements AbstractEvent {
    boolean done = false;

    @Override
    public void init(World world) {
        WorldEvents events = InitializeComponents.EVENTS.get(world);
        if(!events.isLevel1Blackout()) {
            events.setLevel1Blackout(true);
            this.playSound(world, ModSounds.LIGHTS_OUT);
        }
    }

    @Override
    public void reset(World world) {
        WorldEvents events = InitializeComponents.EVENTS.get(world);
        events.setLevel1Blackout(false);
        this.playSound(world, ModSounds.LIGHTS_ON);
        done = true;
    }

    @Override
    public boolean isDone() {
        return done;
    }

    @Override
    public int duration() {
        return 600;
    }
}
