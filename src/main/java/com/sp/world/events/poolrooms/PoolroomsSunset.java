package com.sp.world.events.poolrooms;

import com.sp.SPBRevampedClient;
import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.WorldEvents;
import com.sp.init.ModSounds;
import com.sp.render.PoolroomsDayCycle;
import com.sp.world.events.AbstractEvent;
import net.minecraft.world.World;

public class PoolroomsSunset implements AbstractEvent {

    @Override
    public void init(World world) {
        WorldEvents events = InitializeComponents.EVENTS.get(world);
        float currentDayTime = PoolroomsDayCycle.getDayTime();
        events.setSunsetTransition(true);
        events.setCurrentPoolroomsTime(events.getCurrentPoolroomsTime() + 0.25f);
        events.sync();

        if(currentDayTime == 0.0) playSound(world, ModSounds.SUNSET_TRANSITION);
        else if (currentDayTime == 0.25 || currentDayTime == 0.5) playSound(world, ModSounds.MIDNIGHT_TRANSITION);
        else playSound(world, ModSounds.SUNSET_TRANSITION_END);

    }

    @Override
    public void reset(World world) {
        WorldEvents events = InitializeComponents.EVENTS.get(world);

        events.setCurrentPoolroomsTime(events.getCurrentPoolroomsTime() >= 1.0f ? 0.0f : events.getCurrentPoolroomsTime());
        events.setSunsetTransition(false);
        events.sync();

        float currentDayTime = events.getCurrentPoolroomsTime();

        events.setNoon(currentDayTime != 0.25 && currentDayTime != 0.75);
    }

    @Override
    public boolean isDone() {
        return true;
    }

    @Override
    public int duration() {
        return 200;
    }
}
