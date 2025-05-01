package com.sp.world.events.level2;

import com.sp.init.BackroomsLevels;
import com.sp.world.events.AbstractEvent;
import com.sp.world.levels.custom.Level2BackroomsLevel;
import net.minecraft.world.World;

public class Level2Warp extends AbstractEvent {
    @Override
    public void init(World world) {
        if (!((BackroomsLevels.getLevel(world)) instanceof Level2BackroomsLevel level)) {
            return;
        }

        level.setWarping(true);
    }

    @Override
    public void reset(World world) {
        super.reset(world);

        if (!((BackroomsLevels.getLevel(world)) instanceof Level2BackroomsLevel level)) {
            return;
        }

        level.setWarping(false);
    }

    @Override
    public int duration() {
        return 200;
    }
}
