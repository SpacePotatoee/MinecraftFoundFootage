package com.sp.sounds.entity;

import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import com.sp.init.ModSounds;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;

public class SmilerGlitchSoundInstance extends MovingSoundInstance {
    private final PlayerEntity player;
    private final PlayerComponent component;

    public SmilerGlitchSoundInstance(PlayerEntity player) {
        super(ModSounds.SMILER_GLITCH, SoundCategory.AMBIENT, SoundInstance.createRandom());
        this.player = player;
        this.component = InitializeComponents.PLAYER.get(player);
        this.repeat = true;
        this.repeatDelay = 0;
        this.volume = 1.0F;
        this.relative = true;
    }

    @Override
    public void tick() {
        if(this.player.isRemoved()){
            this.setDone();
        }

        this.volume = this.component.getGlitchTimer() + 0.1f;
    }
}
