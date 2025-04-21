package com.sp.world.events.level0;

import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.WorldEvents;
import com.sp.init.ModSounds;
import com.sp.world.events.AbstractEvent;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class Level0IntercomBasic extends AbstractEvent {
    boolean done = false;
    boolean friend = false;
    int duration = 200;

    @Override
    public void init(World world) {
        WorldEvents events = InitializeComponents.EVENTS.get(world);
        int intercomCount = events.getIntercomCount();
        Random random = Random.create();

        if (intercomCount <= 1) {
            int rand = random.nextBetween(1, 2);
            if (rand == 1) {
                playSoundWithRandLocation(world, ModSounds.INTERCOM_BASIC1, 25, 20);
            } else {
                playSoundWithRandLocation(world, ModSounds.INTERCOM_BASIC2, 25, 20);
            }
        }


        else if (intercomCount == 2) {
            playSoundWithRandLocation(world, ModSounds.INTERCOM_REVERSED, 25, 20);
        }


        else{
            int rand = random.nextBetween(1, 1);
            if (rand == 1) {
                playSoundWithRandLocation(world, ModSounds.INTERCOM_FRIEND, 25, 20);
                friend = true;
                duration = 800;
            } else {
                playSoundWithRandLocation(world, ModSounds.INTERCOM_REVERSED, 25, 20);
            }
        }
        events.addIntercomCount();
    }

    @Override
    public void ticks(int ticks, World world) {
        WorldEvents events = InitializeComponents.EVENTS.get(world);
        if (friend){
            if(ticks == 460){
                events.setLevel0Flicker(true);
            } else if (ticks == 528){
                events.setLevel0Flicker(false);
                events.setLevel0On(false);
                playSound(world, ModSounds.LIGHTS_OUT);
            } else if (ticks == 656){
                events.setLevel0On(true);
            }
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
        return duration;
    }
}
