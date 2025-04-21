package com.sp.world.levels.custom;

import com.sp.init.BackroomsLevels;
import com.sp.world.events.poolrooms.PoolroomsAmbience;
import com.sp.world.events.poolrooms.PoolroomsSunset;
import com.sp.world.generation.PoolroomsChunkGenerator;
import com.sp.world.levels.BackroomsLevel;
import net.minecraft.util.math.BlockPos;

public class PoolroomsBackroomsLevel extends BackroomsLevel {
    public PoolroomsBackroomsLevel() {
        super("poolrooms", PoolroomsChunkGenerator.CODEC, new BlockPos(15, 104, 16), BackroomsLevels.POOLROOMS_WORLD_KEY);
    }

    @Override
    public void register() {
        super.register();

        events.add(PoolroomsSunset::new);
        events.add(PoolroomsAmbience::new);
    }

    @Override
    public int nextEventDelay() {
        return random.nextInt(800, 1000);
    }
}
