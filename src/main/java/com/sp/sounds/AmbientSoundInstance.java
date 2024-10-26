package com.sp.sounds;

import com.sp.init.ModSounds;
import com.sp.world.BackroomsLevels;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.sound.SoundCategory;
import net.minecraft.world.World;

public class AmbientSoundInstance extends MovingSoundInstance {
    private final PlayerEntity player;

    public AmbientSoundInstance(PlayerEntity player) {
        super(ModSounds.AMBIENCE, SoundCategory.AMBIENT, SoundInstance.createRandom());
        this.player = player;
        this.repeat = true;
        this.repeatDelay = 0;
        this.volume = 0.5F;
        this.relative = true;
    }

    @Override
    public void tick() {
        RegistryKey<World> level = this.player.getWorld().getRegistryKey();
        if((level != BackroomsLevels.LEVEL1_WORLD_KEY && level != BackroomsLevels.LEVEL2_WORLD_KEY) || this.player.isRemoved()){
            this.setDone();
        }
    }
}
