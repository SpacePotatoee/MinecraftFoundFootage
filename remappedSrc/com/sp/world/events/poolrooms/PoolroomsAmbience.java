package com.sp.world.events.poolrooms;

import com.sp.init.ModSounds;
import com.sp.world.events.AbstractEvent;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class PoolroomsAmbience implements AbstractEvent {
    boolean done = false;

    @Override
    public void init(World world) {
        Random random = Random.create();
        int rand = random.nextBetween(1, 4);
        SoundEvent soundEvent;
        switch(rand){
            case 1: soundEvent = ModSounds.POOLROOMS_SPLASH1;
            break;
            case 2: soundEvent = ModSounds.POOLROOMS_SPLASH2;
            break;
            case 3: soundEvent = ModSounds.POOLROOMS_DRIP1;
            break;
            default: soundEvent = ModSounds.POOLROOMS_DRIP2;
            break;
        }

        playDistantSound(world, soundEvent);
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
        return 200;
    }
}
