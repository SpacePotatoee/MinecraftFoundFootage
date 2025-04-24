package com.sp.world.events.level1;

import com.sp.init.ModSounds;
import com.sp.world.events.AbstractEvent;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class Level1Ambience extends AbstractEvent {
    @Override
    public void init(World world) {
        Random random = Random.create();
        int rand = random.nextBetween(1, 4);
        SoundEvent soundEvent = switch (rand) {
            case 1 -> ModSounds.LEVEL1_AMBIENCE1;
            case 2 -> ModSounds.LEVEL1_AMBIENCE2;
            case 3 -> ModSounds.LEVEL1_AMBIENCE3;
            default -> ModSounds.LEVEL1_AMBIENCE4;
        };

        playDistantSound(world, soundEvent);
    }

    @Override
    public int duration() {
        return 1200;
    }
}
