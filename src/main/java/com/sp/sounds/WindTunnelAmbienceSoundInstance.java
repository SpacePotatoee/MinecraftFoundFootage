package com.sp.sounds;

import com.sp.init.ModSounds;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;

@Environment(EnvType.CLIENT)
public class WindTunnelAmbienceSoundInstance extends MovingSoundInstance {
    private final PlayerEntity player;

    public WindTunnelAmbienceSoundInstance(PlayerEntity player) {
        super(ModSounds.WINDTUNNEL_GRASS_AMBIENCE, SoundCategory.AMBIENT, SoundInstance.createRandom());
        this.player = player;
        this.repeat = true;
        this.repeatDelay = 0;
        this.volume = 0.85F;
        this.relative = true;
    }

    @Override
    public void tick() {
        if(this.player.isRemoved()){
            this.setDone();
        }
    }
}
