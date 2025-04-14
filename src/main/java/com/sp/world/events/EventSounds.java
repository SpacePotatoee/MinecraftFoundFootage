package com.sp.world.events;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class EventSounds {

    public static void playSound(World world, SoundEvent soundEvent){
        for(PlayerEntity player : world.getPlayers()){
            BlockPos playerPos = player.getBlockPos();
            ((ServerPlayerEntity) player).networkHandler.sendPacket(
                    new PlaySoundS2CPacket(
                            RegistryEntry.of(soundEvent),
                            SoundCategory.AMBIENT,
                            playerPos.getX(),
                            playerPos.getY(),
                            playerPos.getZ(),
                            100.0f,
                            1.0f,
                            player.getRandom().nextLong()
                    )
            );
        }
    }

    public static void playSoundWithRandLocation(World world, SoundEvent soundEvent, int yLevel, int range){
        for(PlayerEntity player : world.getPlayers()){
            if(player instanceof ServerPlayerEntity) {
                for (int i = 0; i < 5; i++) {
                    Random random = Random.create();
                    BlockPos playerPos = player.getBlockPos();

                    int randXOffset = random.nextBetween(-range, range);
                    int randZOffset = random.nextBetween(-range, range);

                    ((ServerPlayerEntity) player).networkHandler.sendPacket(
                            new PlaySoundS2CPacket(
                                    RegistryEntry.of(soundEvent),
                                    SoundCategory.AMBIENT,
                                    playerPos.getX() + randXOffset,
                                    yLevel,
                                    playerPos.getZ() + randZOffset,
                                    100.0f,
                                    1.0f,
                                    player.getRandom().nextLong()
                            )
                    );
                }
            }
        }
    }

    public static void playDistantSound(World world, SoundEvent soundEvent){
        Random random = Random.create();

        int randXOffset = random.nextBetween(-300, 300);
        int randZOffset = random.nextBetween(-300, 300);

        for(PlayerEntity player : world.getPlayers()){
            BlockPos playerPos = player.getBlockPos();
            ((ServerPlayerEntity) player).networkHandler.sendPacket(
                    new PlaySoundS2CPacket(
                            RegistryEntry.of(soundEvent),
                            SoundCategory.AMBIENT,
                            playerPos.getX() + randXOffset,
                            playerPos.getY(),
                            playerPos.getZ() + randZOffset,
                            1000.0f,
                            1.0f,
                            player.getRandom().nextLong()
                    )
            );
        }
    }

    public static void playLevel2Sound(World world, SoundEvent soundEvent) {
        Random random = Random.create();
        int rand = random.nextBetween(1, 2);

        for(PlayerEntity player : world.getPlayers()){
            BlockPos playerPos = player.getBlockPos();

            if(rand == 1) {
                ((ServerPlayerEntity) player).networkHandler.sendPacket(
                        new PlaySoundS2CPacket(
                                RegistryEntry.of(soundEvent),
                                SoundCategory.AMBIENT,
                                playerPos.getX(),
                                playerPos.getY(),
                                playerPos.getZ() + 200,
                                1000.0f,
                                1.0f,
                                player.getRandom().nextLong()
                        )
                );
            } else {
                ((ServerPlayerEntity) player).networkHandler.sendPacket(
                        new PlaySoundS2CPacket(
                                RegistryEntry.of(soundEvent),
                                SoundCategory.AMBIENT,
                                playerPos.getX(),
                                playerPos.getY(),
                                playerPos.getZ() - 200,
                                1000.0f,
                                1.0f,
                                player.getRandom().nextLong()
                        )
                );
            }
        }
    }

}
