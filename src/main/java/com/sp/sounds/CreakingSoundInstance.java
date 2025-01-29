package com.sp.sounds;

import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.WorldEvents;
import com.sp.init.BackroomsLevels;
import com.sp.init.ModSounds;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.sound.SoundCategory;
import net.minecraft.world.World;

public class CreakingSoundInstance extends MovingSoundInstance {
    private final PlayerEntity player;

    public CreakingSoundInstance(PlayerEntity player) {
        super(ModSounds.LEVEL2_WARP_CREAKING_LOOP, SoundCategory.AMBIENT, SoundInstance.createRandom());
        this.player = player;
        this.repeat = true;
        this.repeatDelay = 0;
        this.volume = 0.9F;
        this.relative = true;
    }

    @Override
    public void tick() {
        RegistryKey<World> level = this.player.getWorld().getRegistryKey();
        WorldEvents events = InitializeComponents.EVENTS.get(this.player.getWorld());
        System.out.println(events.isLevel2Warp());
        if(level != BackroomsLevels.LEVEL2_WORLD_KEY || this.player.isRemoved() || !events.isLevel2Warp()) {
            if(!events.isLevel2Warp()){
                this.volume -= 0.01f;
            }else {
                this.volume = 0.0f;
                this.setDone();
            }

            if(this.volume <= 0.0f){
                this.setDone();
            }

        }
    }
}
