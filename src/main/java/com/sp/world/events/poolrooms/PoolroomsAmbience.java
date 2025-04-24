package com.sp.world.events.poolrooms;

import com.sp.init.ModSounds;
import com.sp.world.events.AbstractEvent;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class PoolroomsAmbience extends AbstractEvent {

    @Override
    public void init(World world) {
        Random random = Random.create();
        int rand = random.nextBetween(1, 4);
        SoundEvent soundEvent = switch (rand) {
            case 1 -> ModSounds.POOLROOMS_SPLASH1;
            case 2 -> ModSounds.POOLROOMS_SPLASH2;
            case 3 -> ModSounds.POOLROOMS_DRIP1;
            default -> ModSounds.POOLROOMS_DRIP2;
        };

        playDistantSound(world, soundEvent);
    }

    @Override
    public int duration() {
        return 200;
    }
}
