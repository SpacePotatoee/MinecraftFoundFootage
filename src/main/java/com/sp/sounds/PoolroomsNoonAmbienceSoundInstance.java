package com.sp.sounds;

import com.sp.init.BackroomsLevels;
import com.sp.init.ModSounds;
import com.sp.world.levels.custom.PoolroomsBackroomsLevel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;

@Environment(EnvType.CLIENT)
public class PoolroomsNoonAmbienceSoundInstance extends MovingSoundInstance {
    private final PlayerEntity player;

    public PoolroomsNoonAmbienceSoundInstance(PlayerEntity player) {
        super(ModSounds.POOLROOMS_AMBIENCE_NOON, SoundCategory.AMBIENT, SoundInstance.createRandom());
        this.player = player;
        this.repeat = true;
        this.repeatDelay = 0;
        this.volume = 0.5F;
        this.relative = true;
    }

    @Override
    public void tick() {
        if (!((BackroomsLevels.getLevel(player.getWorld()).orElse(BackroomsLevels.OVERWORLD_REPRESENTING_BACKROOMS_LEVEL)) instanceof PoolroomsBackroomsLevel level)) {
            return;
        }

        if(!level.isNoon() || this.player.isRemoved()){
            this.setDone();
            this.repeat = true;
        }
    }
}
