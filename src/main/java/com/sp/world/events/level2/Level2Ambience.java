package com.sp.world.events.level2;

import com.sp.sounds.ModSounds;
import com.sp.world.events.AbstractEvent;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class Level2Ambience implements AbstractEvent {
    boolean done = false;

    @Override
    public void init(World world) {
        Random random = Random.create();
        int rand = random.nextBetween(1, 4);
        SoundEvent soundEvent;
        switch(rand){
            case 1: soundEvent = ModSounds.LEVEL1_AMBIENCE1;
            break;
            case 2: soundEvent = ModSounds.LEVEL1_AMBIENCE2;
            break;
            case 3: soundEvent = ModSounds.LEVEL1_AMBIENCE3;
            break;
            default: soundEvent = ModSounds.LEVEL1_AMBIENCE4;
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
        return 1200;
    }
}
