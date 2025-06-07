package com.sp.world.events;

import net.minecraft.world.World;

public class EmptyEvent extends AbstractEvent {
    @Override
    public void init(World world) {

    }

    @Override
    public int duration() {
        return 0;
    }
}
