package com.sp.world.events.infinite_grass;

import com.sp.init.ModSounds;
import com.sp.world.events.AbstractEvent;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class InfiniteGrassAmbience extends AbstractEvent {
    boolean done = false;

    @Override
    public void init(World world) {
        Random random = Random.create();
        boolean far = random.nextBoolean();

        if(far){
            playDistantSound(world, ModSounds.INFINITE_GRASS_SOUNDEVENT_FAR);
        } else {
            playSound(world, ModSounds.INFINITE_GRASS_SOUNDEVENT);
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
        return 200;
    }
}
