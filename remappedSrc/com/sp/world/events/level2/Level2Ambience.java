package com.sp.world.events.level2;

import com.sp.init.ModSounds;
import com.sp.world.events.AbstractEvent;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class Level2Ambience implements AbstractEvent {
    boolean done = false;
    int duration = 200;

    @Override
    public void init(World world) {
        Random random = Random.create();
        int rand = random.nextBetween(1, 3);
        SoundEvent soundEvent;
        switch(rand){
            case 1: soundEvent = ModSounds.CREAKING1;
            break;
            case 2: soundEvent = ModSounds.CREAKING2;
            break;
            default: {
                soundEvent = ModSounds.LEVEL2_AMBIENCE;
                duration = 720;
            }
            break;
        }
        playLevel2Sound(world, soundEvent);
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
