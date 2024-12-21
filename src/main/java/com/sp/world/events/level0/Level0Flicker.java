package com.sp.world.events.level0;

import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.WorldEvents;
import com.sp.world.events.AbstractEvent;
import net.minecraft.world.World;

public class Level0Flicker implements AbstractEvent {
    boolean done = false;

    @Override
    public void init(World world) {
        WorldEvents events = InitializeComponents.EVENTS.get(world);
        events.setLevel0Flicker(true);
    }

    @Override
    public void reset(World world) {
        WorldEvents events = InitializeComponents.EVENTS.get(world);
        events.setLevel0Flicker(false);
        done = true;
    }

    @Override
    public boolean isDone() {
        return done;
    }

    @Override
    public int duration() {
        return 200;
    }
}
