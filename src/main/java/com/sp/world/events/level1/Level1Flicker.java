package com.sp.world.events.level1;

import com.sp.init.BackroomsLevels;
import com.sp.world.events.AbstractEvent;
import com.sp.world.levels.custom.Level0BackroomsLevel;
import com.sp.world.levels.custom.Level1BackroomsLevel;
import net.minecraft.world.World;

public class Level1Flicker extends AbstractEvent {
    @Override
    public void init(World world) {
        if (!((BackroomsLevels.getLevel(world).orElse(BackroomsLevels.OVERWORLD_REPRESENTING_BACKROOMS_LEVEL)) instanceof Level1BackroomsLevel level)) {
            return;
        }

        level.setLightState(Level0BackroomsLevel.LightState.FLICKER);
    }

    @Override
    public void finish(World world) {
        super.finish(world);

        if (!((BackroomsLevels.getLevel(world).orElse(BackroomsLevels.OVERWORLD_REPRESENTING_BACKROOMS_LEVEL)) instanceof Level1BackroomsLevel level)) {
            return;
        }

        level.setLightState(Level0BackroomsLevel.LightState.ON);
    }

    @Override
    public int duration() {
        return 100;
    }
}
