package com.sp.world.events;

import net.minecraft.sound.SoundEvent;
import net.minecraft.world.World;

public abstract class AbstractEvent {

    public abstract void init(World world);

    public abstract void reset(World world);

    public abstract boolean isDone();

    public void end(World world){
        this.reset(world);
    }

    public abstract int duration();

    public void ticks(int ticks, World world) {

    }

    protected void playSound(World world, SoundEvent soundEvent){
        EventSounds.playSound(world, soundEvent);
    }

    protected void playSoundWithRandLocation(World world, SoundEvent soundEvent, int yLevel, int range){
        EventSounds.playSoundWithRandLocation(world, soundEvent, yLevel, range);
    }

    protected void playDistantSound(World world, SoundEvent soundEvent){
        EventSounds.playDistantSound(world, soundEvent);
    }
}
