package com.sp.world.events;

import net.minecraft.sound.SoundEvent;
import net.minecraft.world.World;

public abstract class AbstractEvent {
    boolean done = false;

    public abstract void init(World world);

    public void reset(World world) {
        done = true;
    }

    public boolean isDone() {
        return done;
    }

    public void end(World world){
        this.reset(world);
    }

    public abstract int duration();

    public void ticks(int ticks, World world) {

    }

    protected static void playSound(World world, SoundEvent soundEvent){
        EventSounds.playSound(world, soundEvent);
    }

    protected static void playSoundWithRandLocation(World world, SoundEvent soundEvent, int yLevel, int range){
        EventSounds.playSoundWithRandLocation(world, soundEvent, yLevel, range);
    }

    protected static void playDistantSound(World world, SoundEvent soundEvent){
        EventSounds.playDistantSound(world, soundEvent);
    }
}
