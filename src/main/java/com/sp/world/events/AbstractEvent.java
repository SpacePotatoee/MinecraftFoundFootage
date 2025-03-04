package com.sp.world.events;

import net.minecraft.sound.SoundEvent;
import net.minecraft.world.World;

public interface AbstractEvent {

    void init(World world);

    void reset(World world);

    boolean isDone();

    default void end(World world){
        this.reset(world);
    }

    default int duration(){
        return 20;
    }
    default void ticks(int ticks, World world){
    }

    default void playSound(World world, SoundEvent soundEvent){
        EventSounds.playSound(world, soundEvent);
    }

    default void playSoundWithRandLocation(World world, SoundEvent soundEvent, int yLevel, int range){
        EventSounds.playSoundWithRandLocation(world, soundEvent, yLevel, range);
    }

    default void playDistantSound(World world, SoundEvent soundEvent){
        EventSounds.playDistantSound(world, soundEvent);
    }

    default void playLevel2Sound(World world, SoundEvent soundEvent){
        EventSounds.playLevel2Sound(world, soundEvent);
    }

}
