package com.sp.sounds.entity;

import com.sp.init.BackroomsLevels;
import com.sp.init.ModSounds;
import com.sp.world.levels.custom.Level0BackroomsLevel;
import com.sp.world.levels.custom.Level1BackroomsLevel;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;

public class SmilerAmbienceSoundInstance extends MovingSoundInstance {
    private final PlayerEntity player;

    public SmilerAmbienceSoundInstance(PlayerEntity player) {
        super(ModSounds.SMILER_AMBIENCE, SoundCategory.AMBIENT, SoundInstance.createRandom());
        this.player = player;
        this.repeat = true;
        this.repeatDelay = 0;
        this.volume = 0.5F;
        this.relative = true;
    }

    @Override
    public void tick() {
        if (!((BackroomsLevels.getLevel(player.getWorld()).orElse(BackroomsLevels.OVERWORLD_REPRESENTING_BACKROOMS_LEVEL)) instanceof Level1BackroomsLevel level)) {
            return;
        }

        if(level.getLightState() != Level0BackroomsLevel.LightState.BLACKOUT || this.player.isRemoved()){
            this.setDone();
        }
    }
}
