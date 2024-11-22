package com.sp.sounds.pipes;

import com.sp.init.ModSounds;
import com.sp.world.BackroomsLevels;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.sound.SoundCategory;
import net.minecraft.world.World;

public class WaterPipeSoundInstance extends MovingSoundInstance {
    private final PlayerEntity player;

    public WaterPipeSoundInstance(PlayerEntity player) {
        super(ModSounds.WATER_PIPE, SoundCategory.AMBIENT, SoundInstance.createRandom());
        this.x = 1.5;
        this.y = 20.5;
        this.z = player.getZ();
        this.player = player;
        this.repeat = true;
        this.repeatDelay = 0;
        this.volume = 0.3F;
    }

    @Override
    public void tick() {
        this.z = player.getZ();
        RegistryKey<World> level = this.player.method_48926().getRegistryKey();
        if(level != BackroomsLevels.LEVEL2_WORLD_KEY || this.player.isRemoved()){
            this.setDone();
        }
    }
}
