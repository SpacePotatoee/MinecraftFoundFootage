package com.sp.sounds;

import com.sp.init.BackroomsLevels;
import com.sp.init.ModSounds;
import com.sp.world.levels.custom.Level2BackroomsLevel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;

@Environment(EnvType.CLIENT)
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
        if (!((BackroomsLevels.getLevel(player.getWorld())) instanceof Level2BackroomsLevel level)) {
            return;
        }

        if(this.player.isRemoved() || !level.isWarping()) {
            if(!level.isWarping()){
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
