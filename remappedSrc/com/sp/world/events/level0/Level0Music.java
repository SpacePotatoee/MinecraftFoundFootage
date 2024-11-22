package com.sp.world.events.level0;

import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.WorldEvents;
import com.sp.init.ModSounds;
import com.sp.world.events.AbstractEvent;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class Level0Music implements AbstractEvent {
    boolean done = false;
    int duration = 980;

    @Override
    public void init(World world) {
        WorldEvents events = InitializeComponents.EVENTS.get(world);
        int intercomCount = events.getIntercomCount();
        Random random = Random.create();


        if (intercomCount > 2) {
            int rand = random.nextBetween(1, 3);
            if (rand == 1) {
                playSoundWithRandLocation(world, ModSounds.CREEPY_MUSIC1, 25, 20);
            } else if (rand == 2) {
                playSoundWithRandLocation(world, ModSounds.CREEPY_MUSIC2, 25, 20);
            } else {
                playSoundWithRandLocation(world, ModSounds.FAR_CROWD, 25, 20);
            }
        }
        else{
            int rand = random.nextBetween(1, 2);
            if (rand == 1) {
                playSoundWithRandLocation(world, ModSounds.INTERCOM_BASIC1, 25, 20);
            } else {
                playSoundWithRandLocation(world, ModSounds.INTERCOM_BASIC2, 25, 20);
            }
            duration = 200;
            events.addIntercomCount();
        }
    }

    @Override
    public void reset(World world) {
        done = true;
    }

    @Override
    public boolean isDone() {
        return done;
    }

    @Override
    public int duration() {
        return this.duration;
    }
}
