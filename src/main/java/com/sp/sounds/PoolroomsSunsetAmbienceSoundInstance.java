package com.sp.sounds;

import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.WorldEvents;
import com.sp.init.ModSounds;
import com.sp.init.BackroomsLevels;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.sound.SoundCategory;
import net.minecraft.world.World;

public class PoolroomsSunsetAmbienceSoundInstance extends MovingSoundInstance {
    private final PlayerEntity player;

    public PoolroomsSunsetAmbienceSoundInstance(PlayerEntity player) {
        super(ModSounds.POOLROOMS_AMBIENCE_SUNSET, SoundCategory.AMBIENT, SoundInstance.createRandom());
        this.player = player;
        this.repeat = true;
        this.repeatDelay = 0;
        this.volume = 0.5F;
        this.relative = true;
    }

    @Override
    public void tick() {
        RegistryKey<World> level = this.player.getWorld().getRegistryKey();
        WorldEvents events = InitializeComponents.EVENTS.get(this.player.getWorld());

        if(level != BackroomsLevels.POOLROOMS_WORLD_KEY || events.isNoon() || this.player.isRemoved()){
            this.setDone();
            this.repeat = true;
        }
    }
}
