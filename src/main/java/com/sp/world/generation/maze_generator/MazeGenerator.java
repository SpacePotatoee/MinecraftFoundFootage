package com.sp.world.generation.maze_generator;

import com.sp.world.generation.maze_generator.cells.CellWDoor;
import com.sp.world.generation.maze_generator.cells.HighVarCell;
import com.sp.world.generation.maze_generator.cells.LowVarCell;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.StructureWorldAccess;

public abstract class MazeGenerator {
    // UNDER CONSTRUCTION -SP
    public void generate(StructureWorldAccess world) {

    }

    public abstract void setup(StructureWorldAccess world, boolean sky, boolean megaRooms, boolean spawnRandomRooms);

    public abstract void drawWalls(StructureWorldAccess world, String level);

    public abstract void removeWalls(HighVarCell currentCell, HighVarCell neighbor);

    public abstract void removeWalls(LowVarCell currentCell, LowVarCell neighbor);

    public abstract void removeWalls(CellWDoor currentCell, CellWDoor neighbor);


    protected boolean isAirOrNull(BlockState blockState){
        return blockState == Blocks.AIR.getDefaultState() || blockState == null;
    }
}
