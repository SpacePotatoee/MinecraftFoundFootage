package com.sp.sounds;

import com.sp.init.ModSounds;
import com.sp.world.BackroomsLevels;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.sound.SoundCategory;
import net.minecraft.world.World;

public class GlitchSoundInstance extends MovingSoundInstance {
    private int timer;

    public GlitchSoundInstance(int timer) {
        super(ModSounds.GLITCH, SoundCategory.AMBIENT, SoundInstance.createRandom());
        this.timer = timer;
        this.volume = (float) timer / 40;
        this.relative = true;
    }

    @Override
    public void tick() {
        if(timer >= 40){
            this.setDone();
        }
    }
}
