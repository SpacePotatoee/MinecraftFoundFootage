package com.sp.world.events.level0;

import com.sp.init.BackroomsLevels;
import com.sp.init.ModSounds;
import com.sp.world.events.AbstractEvent;
import com.sp.world.levels.custom.Level0BackroomsLevel;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class Level0IntercomBasic extends AbstractEvent {
    boolean friend = false;
    int duration = 200;

    @Override
    public void init(World world) {
        if (!(BackroomsLevels.getLevel(world) instanceof Level0BackroomsLevel level)) {
            return;
        }

        int intercomCount = level.getIntercomCount();
        Random random = Random.create();

        if (intercomCount <= 1) {
            int rand = random.nextBetween(1, 2);
            if (rand == 1) {
                playSoundWithRandLocation(world, ModSounds.INTERCOM_BASIC1, 25, 20);
            } else {
                playSoundWithRandLocation(world, ModSounds.INTERCOM_BASIC2, 25, 20);
            }
        }


        else if (intercomCount == 2) {
            playSoundWithRandLocation(world, ModSounds.INTERCOM_REVERSED, 25, 20);
        }


        else {
            int rand = random.nextBetween(1, 1);
            if (rand == 1) {
                playSoundWithRandLocation(world, ModSounds.INTERCOM_FRIEND, 25, 20);
                friend = true;
                duration = 800;
            } else {
                playSoundWithRandLocation(world, ModSounds.INTERCOM_REVERSED, 25, 20);
            }
        }

        level.addIntercomCount();
    }

    @Override
    public void ticks(int ticks, World world) {
        if (!(BackroomsLevels.getLevel(world) instanceof Level0BackroomsLevel level)) {
            return;
        }

        if (friend){
            if (ticks == 460){
                level.setLightState(Level0BackroomsLevel.LightState.FLICKER);
            } else if (ticks == 528){
                level.setLightState(Level0BackroomsLevel.LightState.OFF);
                playSound(world, ModSounds.LIGHTS_OUT);
            } else if (ticks == 656){
                level.setLightState(Level0BackroomsLevel.LightState.ON);
            }
        }
    }

    @Override
    public int duration() {
        return duration;
    }
}
