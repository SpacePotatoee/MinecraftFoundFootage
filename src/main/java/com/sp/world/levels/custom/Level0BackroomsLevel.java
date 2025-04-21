package com.sp.world.levels.custom;

import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.WorldEvents;
import com.sp.init.BackroomsLevels;
import com.sp.world.events.AbstractEvent;
import com.sp.world.events.level0.Level0Blackout;
import com.sp.world.events.level0.Level0Flicker;
import com.sp.world.events.level0.Level0IntercomBasic;
import com.sp.world.events.level0.Level0Music;
import com.sp.world.generation.Level0ChunkGenerator;
import com.sp.world.levels.BackroomsLevel;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Level0BackroomsLevel extends BackroomsLevel {
    public Level0BackroomsLevel() {
        super("level0", Level0ChunkGenerator.CODEC, new BlockPos(1, 22, 1), BackroomsLevels.LEVEL0_WORLD_KEY);
    }

    @Override
    public void register() {
        super.register();
        events.add(Level0Blackout::new);
        events.add(Level0Flicker::new);
        events.add(Level0IntercomBasic::new);
        events.add(Level0Music::new);
    }

    @Override
    public AbstractEvent getRandomEvent(World world) {
        AbstractEvent activeEvent = super.getRandomEvent(world);

        if (activeEvent instanceof Level0Blackout) {
            WorldEvents worldEvents = InitializeComponents.EVENTS.get(world);
            worldEvents.setBlackoutCount(worldEvents.getBlackoutCount());
            if (worldEvents.getBlackoutCount() > 2) {
                while (activeEvent instanceof Level0Blackout) {
                    activeEvent = super.getRandomEvent(world);
                }
            }
        }

        return activeEvent;
    }

    @Override
    public int nextEventDelay() {
        return random.nextInt(1000, 1500);
    }
}
