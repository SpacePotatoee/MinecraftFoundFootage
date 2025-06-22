package com.sp.world.generation.maze_generator;

import com.sp.world.generation.maze_generator.cells.MazeCell;
import net.minecraft.block.BlockState;
import net.minecraft.world.StructureWorldAccess;

public abstract class MazeGenerator {
    // UNDER CONSTRUCTION -SP
    public void generate(StructureWorldAccess world) {

    }

    public abstract void setup(StructureWorldAccess world, boolean sky, boolean megaRooms, boolean spawnRandomRooms);

    public abstract void removeWalls(MazeCell currentCell, MazeCell neighbor);

    protected boolean isAirOrNull(BlockState blockState) {
        return blockState == null || blockState.isAir();  // Might fix some people randomly crashing bc of null cells
    }
}
