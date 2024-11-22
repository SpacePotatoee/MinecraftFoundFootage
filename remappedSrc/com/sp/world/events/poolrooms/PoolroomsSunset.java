package com.sp.world.events.poolrooms;

import com.sp.SPBRevampedClient;
import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.WorldEvents;
import com.sp.init.ModSounds;
import com.sp.world.events.AbstractEvent;
import net.minecraft.world.World;

public class PoolroomsSunset implements AbstractEvent {
    boolean done = false;

    @Override
    public void init(World world) {
        WorldEvents events = InitializeComponents.EVENTS.get(world);
        events.setSunsetTransition(true);

        if(events.isNoon()) playSound(world, ModSounds.SUNSET_TRANSITION);
        else playSound(world, ModSounds.SUNSET_TRANSITION_END);
    }

    @Override
    public void reset(World world) {
        WorldEvents events = InitializeComponents.EVENTS.get(world);
        if(SPBRevampedClient.SunsetTimer.getCurrentTick() >= 160){
            events.setNoon(!events.isNoon());
            events.setSunsetTransition(false);
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
