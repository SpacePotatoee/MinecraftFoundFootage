package com.sp.world.levels.custom;

import com.sp.init.BackroomsLevels;
import com.sp.world.events.level2.Level2Ambience;
import com.sp.world.events.level2.Level2Warp;
import com.sp.world.generation.Level2ChunkGenerator;
import com.sp.world.levels.BackroomsLevel;
import net.minecraft.util.math.BlockPos;

public class Level2BackroomsLevel extends BackroomsLevel {
    public Level2BackroomsLevel() {
        super("level2", Level2ChunkGenerator.CODEC, new BlockPos(0, 21, 8), BackroomsLevels.LEVEL2_WORLD_KEY);
    }

    @Override
    public void register() {
        super.register();

        events.add(Level2Warp::new);
        events.add(Level2Ambience::new);
    }

    @Override
    public int nextEventDelay() {
        return random.nextInt(500, 800);
    }
}
