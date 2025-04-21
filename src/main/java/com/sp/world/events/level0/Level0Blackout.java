package com.sp.world.events.level0;

import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.WorldEvents;
import com.sp.init.ModSounds;
import com.sp.world.events.AbstractEvent;
import net.minecraft.world.World;

public class Level0Blackout extends AbstractEvent {
    boolean done = false;

    @Override
    public void init(World world) {
        WorldEvents events = InitializeComponents.EVENTS.get(world);
        if(!events.isLevel0Blackout()) {
            events.setLevel0Blackout(true);
            this.playSound(world, ModSounds.LIGHTS_OUT);
        }
    }

    @Override
    public void reset(World world) {
        WorldEvents events = InitializeComponents.EVENTS.get(world);
        events.setLevel0Blackout(false);
        done = true;
    }

    @Override
    public boolean isDone() {
        return done;
    }

    @Override
    public int duration() {
        return 20;
    }
}
