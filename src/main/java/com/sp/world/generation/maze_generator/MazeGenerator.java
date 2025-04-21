package com.sp.world.generation.maze_generator;

import net.minecraft.world.StructureWorldAccess;

public abstract class MazeGenerator {
    public abstract void setup(StructureWorldAccess world, boolean sky, boolean megaRooms, boolean spawnRandomRooms);

    public abstract void drawWalls(StructureWorldAccess world, String level);

    public abstract void removeWalls(HighVarCell currentCell, HighVarCell neighbor);

    public abstract void removeWalls(LowVarCell currentCell, LowVarCell neighbor);

    public abstract void removeWalls(CellWDoor currentCell, CellWDoor neighbor);
}
