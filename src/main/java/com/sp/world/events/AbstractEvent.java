package com.sp.world.events;

import com.sp.sounds.ModSounds;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface AbstractEvent {

    void init(World world);

    void reset(World world);

    default int duration(){
        return 20;
    }
    default void ticks(){

    }

    default void playSound(World world, SoundEvent soundEvent){
        for(PlayerEntity player : world.getPlayers()){
            BlockPos playerPos = player.getBlockPos();
            player.getWorld().playSound(null, playerPos, soundEvent, SoundCategory.AMBIENT, 1.0F, 1.0F);
        }
    }

}
