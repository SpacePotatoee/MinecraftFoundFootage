package com.sp.world.events.poolrooms;

import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.WorldEvents;
import com.sp.init.BackroomsLevels;
import com.sp.init.ModSounds;
import com.sp.render.PoolroomsDayCycle;
import com.sp.world.events.AbstractEvent;
import com.sp.world.levels.custom.PoolroomsBackroomsLevel;
import net.minecraft.world.World;

public class PoolroomsSunset extends AbstractEvent {
    @Override
    public void init(World world) {
        if (!(BackroomsLevels.getLevel(world) instanceof PoolroomsBackroomsLevel level)) {
            return;
        }

        WorldEvents events = InitializeComponents.EVENTS.get(world);
        float currentDayTime = PoolroomsDayCycle.getDayTime(world);
        level.setSunsetTransitioning(true);
        level.setTimeOfDay(level.getTimeOfDay() + 0.25f);
        events.sync();

        if(currentDayTime == 0.0) playSound(world, ModSounds.SUNSET_TRANSITION);
        else if (currentDayTime == 0.25 || currentDayTime == 0.5) playSound(world, ModSounds.MIDNIGHT_TRANSITION);
        else playSound(world, ModSounds.SUNSET_TRANSITION_END);
    }

    @Override
    public void reset(World world) {
        super.reset(world);

        if (!(BackroomsLevels.getLevel(world) instanceof PoolroomsBackroomsLevel level)) {
            return;
        }

        WorldEvents events = InitializeComponents.EVENTS.get(world);

        level.setTimeOfDay(level.getTimeOfDay() >= 1.0f ? 0.0f : level.getTimeOfDay());
        level.setSunsetTransitioning(false);
        events.sync();
    }

    @Override
    public int duration() {
        return 200;
    }
}
