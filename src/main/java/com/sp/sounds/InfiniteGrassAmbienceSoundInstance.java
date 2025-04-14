package com.sp.sounds;

import com.sp.init.BackroomsLevels;
import com.sp.init.ModSounds;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.sound.SoundCategory;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class InfiniteGrassAmbienceSoundInstance extends MovingSoundInstance {
    private final PlayerEntity player;

    public InfiniteGrassAmbienceSoundInstance(PlayerEntity player) {
        super(ModSounds.INFINITE_GRASS_AMBIENCE, SoundCategory.AMBIENT, SoundInstance.createRandom());
        this.player = player;
        this.repeat = true;
        this.repeatDelay = 0;
        this.volume = 0.85F;
        this.relative = true;
    }

    @Override
    public void tick() {
        RegistryKey<World> level = this.player.getWorld().getRegistryKey();
        if((level != BackroomsLevels.INFINITE_FIELD_WORLD_KEY) || this.player.isRemoved()){
            this.setDone();
        }
    }
}
