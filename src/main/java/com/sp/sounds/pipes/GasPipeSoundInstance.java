package com.sp.sounds.pipes;

import com.sp.init.ModSounds;
import com.sp.init.BackroomsLevels;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.sound.SoundCategory;
import net.minecraft.world.World;

public class GasPipeSoundInstance extends MovingSoundInstance {
    private final PlayerEntity player;

    public GasPipeSoundInstance(PlayerEntity player) {
        super(ModSounds.GAS_PIPE, SoundCategory.AMBIENT, SoundInstance.createRandom());
        this.x = 1.5;
        this.y = 22.5;
        this.z = player.getZ();
        this.player = player;
        this.repeat = true;
        this.repeatDelay = 0;
        this.volume = 0.1F;
    }

    @Override
    public void tick() {
        this.z = player.getZ();
        RegistryKey<World> level = this.player.getWorld().getRegistryKey();
        if(level != BackroomsLevels.LEVEL2_WORLD_KEY || this.player.isRemoved()){
            this.setDone();
        }
    }
}
