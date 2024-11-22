package com.sp.world.events;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public interface AbstractEvent {

    void init(World world);

    void reset(World world);

    boolean isDone();

    default int duration(){
        return 20;
    }
    default void ticks(int ticks, World world){
    }

    default void playSound(World world, SoundEvent soundEvent){
        for(PlayerEntity player : world.getPlayers()){
            BlockPos playerPos = player.getBlockPos();
            player.method_48926().playSound(null, playerPos, soundEvent, SoundCategory.AMBIENT, 1.0F, 1.0F);
        }
    }

    default void playSoundWithRandLocation(World world, SoundEvent soundEvent, int yLevel, int range){
        for(PlayerEntity player : world.getPlayers()){
            for(int i = 0; i < 5; i++) {
                Random random = Random.create();
                BlockPos playerPos = player.getBlockPos();

                int randXOffset = random.nextBetween(-range, range);
                int randZOffset = random.nextBetween(-range, range);

                player.method_48926().playSound(null, new BlockPos(playerPos.getX() + randXOffset, yLevel, playerPos.getZ() + randZOffset), soundEvent, SoundCategory.AMBIENT, 10.0F, 1.0F);
            }
        }
    }

    default void playDistantSound(World world, SoundEvent soundEvent){
        Random random = Random.create();
        int rand = random.nextBetween(1, 2);
        int randXOffset;
        int randZOffset;

        if(rand == 1){
            randXOffset = random.nextBetween(-300, -200);
        } else {
            randXOffset = random.nextBetween(200, 300);
        }

        rand = random.nextBetween(3, 4);

        if(rand == 3){
            randZOffset = random.nextBetween(-300, -200);
        } else {
            randZOffset = random.nextBetween(200, 300);
        }

        for(PlayerEntity player : world.getPlayers()){
            BlockPos playerPos = player.getBlockPos();
            player.method_48926().playSound(null, new BlockPos(playerPos.getX() + randXOffset, playerPos.getY(), playerPos.getZ() + randZOffset), soundEvent, SoundCategory.AMBIENT, 1000.0F, 1.0F);
        }
    }

    default void playLevel2Sound(World world, SoundEvent soundEvent){
        Random random = Random.create();
        int rand = random.nextBetween(1, 2);

        for(PlayerEntity player : world.getPlayers()){
            BlockPos playerPos = player.getBlockPos();

            if(rand == 1) {
                player.method_48926().playSound(null, new BlockPos(playerPos.getX(), playerPos.getY(), playerPos.getZ() + 200), soundEvent, SoundCategory.AMBIENT, 1000.0F, 1.0F);
            } else {
                player.method_48926().playSound(null, new BlockPos(playerPos.getX(), playerPos.getY(), playerPos.getZ() - 200), soundEvent, SoundCategory.AMBIENT, 1000.0F, 1.0F);
            }
        }
    }

}
