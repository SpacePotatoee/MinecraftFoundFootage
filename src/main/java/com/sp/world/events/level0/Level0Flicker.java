package com.sp.world.events.level0;

import com.sp.init.BackroomsLevels;
import com.sp.world.events.AbstractEvent;
import com.sp.world.levels.custom.Level0BackroomsLevel;
import net.minecraft.world.World;

public class Level0Flicker extends AbstractEvent {
    @Override
    public void init(World world) {
        if (!(BackroomsLevels.getLevel(world) instanceof Level0BackroomsLevel level)) {
            return;
        }

        level.setLightState(Level0BackroomsLevel.LightState.FLICKER);
    }

    @Override
    public void reset(World world) {
        super.reset(world);

        if (!(BackroomsLevels.getLevel(world) instanceof Level0BackroomsLevel level)) {
            return;
        }

        level.setLightState(Level0BackroomsLevel.LightState.ON);
    }


    @Override
    public int duration() {
        return 200;
    }
}
