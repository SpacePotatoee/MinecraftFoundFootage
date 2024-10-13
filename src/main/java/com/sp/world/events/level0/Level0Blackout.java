package com.sp.world.events.level0;

import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.WorldEvents;
import com.sp.sounds.ModSounds;
import com.sp.world.events.AbstractEvent;
import com.sp.world.events.EventVariableStorage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Level0Blackout implements AbstractEvent {

    @Override
    public void init(World world) {
        WorldEvents events = InitializeComponents.EVENTS.get(world);
        if(!events.isLevel0Blackout()) {
            events.setLevel0Blackout(true);
            this.playSound(world, ModSounds.LIGHTS_OUT);
        }
    }

    @Override
    public void reset(World world) {
        WorldEvents events = InitializeComponents.EVENTS.get(world);
        events.setLevel0Blackout(false);
    }
}
