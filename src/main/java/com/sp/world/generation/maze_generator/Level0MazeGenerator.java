package com.sp.world.generation.maze_generator;

import com.sp.init.ModBlocks;
import com.sp.world.generation.maze_generator.cells.CellWDoor;
import com.sp.world.generation.maze_generator.cells.HighVarCell;
import com.sp.world.generation.maze_generator.cells.LowVarCell;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static com.sp.block.custom.WallBlock.BOTTOM;

public class Level0MazeGenerator extends MazeGenerator {
    int cols;
    int rows;
    int size;

    LowVarCell[][] grid;
    LowVarCell currentCell;
    Stack<LowVarCell> cellStack = new Stack<>();

    int originX;
    int originY;

    String levelDirectory;

    public Level0MazeGenerator(int size, int rows, int cols, int originX, int originY, String levelDirectory){
        this.size = size;
        this.rows = rows;
        this.cols = cols;
        this.grid = new LowVarCell[rows][cols];

        this.originX = originX - 32;
        this.originY = originY - 32;

        this.levelDirectory = levelDirectory;
    }

    @Override
    public void setup(StructureWorldAccess world, boolean sky, boolean megaRooms, boolean spawnRandomRooms) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();

        for (int y = 0; y < this.rows; y++) {
            for (int x = 0; x < this.cols; x++) {
                BlockState blockState = world.getBlockState(mutable.set(x + ((this.size - 1) * x) + this.originX, 19, y + ((this.size - 1) * y) + this.originY));
                if(this.isAirOrNull(blockState)) {
                    grid[x][y] = new LowVarCell(y + ((this.size - 1) * y) + this.originY, x + ((this.size - 1) * x) + this.originX, this.size, y, x);
                }
            }
        }

        this.currentCell = grid[0][0];
        currentCell.setVisited(true);
        cellStack.push(currentCell);



        //*Actual maze generation
        while(!cellStack.isEmpty()) {
            LowVarCell randNeighbor = this.checkNeighbors(grid, currentCell.getGridPosY(), currentCell.getGridPosX(), world);

            while (randNeighbor != null) {
                randNeighbor.setVisited(true);
                this.removeWalls(currentCell, randNeighbor);
                this.currentCell = randNeighbor;
                cellStack.push(currentCell);
                randNeighbor = this.checkNeighbors(grid, currentCell.getGridPosY(), currentCell.getGridPosX(), world);
            }
            currentCell = cellStack.pop();
        }

        //*Connect the mazes together
        for(int i = 0; i < this.cols; i += 2) {
            LowVarCell cell = this.grid[i][0];
            if(cell != null) {
                cell.setSouth(false);
            }
        }

        for(int i = 0; i < this.cols; i += 2) {
            LowVarCell cell = this.grid[this.cols - 1][i];
            if(cell != null) {
                cell.setWest(false);
            }
        }

        for(int i = this.cols - 1; i >= 0; i -= 2) {
            LowVarCell cell = this.grid[i][this.cols - 1];
            if(cell != null) {
                cell.setNorth(false);
            }
        }

        for(int i = this.cols - 1; i >= 0; i -= 2) {
            LowVarCell cell = this.grid[0][i];
            if(cell != null) {
                cell.setEast(false);
            }
        }

        for (LowVarCell[] cell : grid){
            for(LowVarCell cells: cell){
                if (cells != null) {
                    cells.drawWalls(world, this.levelDirectory);
                }
            }
        }



    }

    public LowVarCell checkNeighbors(LowVarCell[][] grid, int y, int x, StructureWorldAccess world){
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        LowVarCell North = null;
        LowVarCell West = null;
        LowVarCell South = null;
        LowVarCell East = null;

        List<LowVarCell> neighbors = new ArrayList<>();


        if (y + 1 < this.rows) North = grid[x][y + 1];

        if (x + 1 < this.cols) West = grid[x + 1][y];

        if(y - 1 >= 0) South = grid[x][y - 1];

        if(x - 1 >= 0) East = grid[x - 1][y];


        if (North != null && !North.isVisited()){
            neighbors.add(North);
        }
        if (West != null && !West.isVisited()){
            neighbors.add(West);
        }
        if (South != null && !South.isVisited()){
            neighbors.add(South);
        }
        if (East != null && !East.isVisited()){
            neighbors.add(East);
        }

        if (world.getBlockState(mutable.set(currentCell.getX(), 19, currentCell.getY() + this.size)) == Blocks.LIME_WOOL.getDefaultState() ||
                world.getBlockState(mutable.set(currentCell.getX(), 4, currentCell.getY() + this.size)) == Blocks.LIME_WOOL.getDefaultState())
        {
            currentCell.setNorth(false);
        }
        if (world.getBlockState(mutable.set(currentCell.getX(), 19, currentCell.getY() - this.size)) == Blocks.LIME_WOOL.getDefaultState())
        {
            currentCell.setSouth(false);
        }
        if (world.getBlockState(mutable.set(currentCell.getX() + this.size, 19, currentCell.getY())) == Blocks.LIME_WOOL.getDefaultState() ||
                world.getBlockState(mutable.set(currentCell.getX() + this.size, 4, currentCell.getY())) == Blocks.LIME_WOOL.getDefaultState()){
            currentCell.setWest(false);
        }
        if (world.getBlockState(mutable.set(currentCell.getX() - this.size, 19, currentCell.getY())) == Blocks.LIME_WOOL.getDefaultState()){
            currentCell.setEast(false);
        }

        if (!neighbors.isEmpty()){
            Random random = Random.create();
            int r = random.nextBetween(0, neighbors.size() - 1);
            return neighbors.get(r);
        }
        else{
            return null;
        }


    }

    @Override
    public void drawWalls(StructureWorldAccess world, String level) {

    }

    @Override
    public void removeWalls(HighVarCell currentCell, HighVarCell neighbor) {

    }

    public void removeWalls(LowVarCell currentCell, LowVarCell neighbor) {
        if (currentCell.getGridPosX() - neighbor.getGridPosX() != 0) {
            int x = currentCell.getGridPosX() - neighbor.getGridPosX();

            if (x > 0) {
                currentCell.setEast(false);
                neighbor.setWest(false);
            } else {
                currentCell.setWest(false);
                neighbor.setEast(false);
            }
        }

        if (currentCell.getGridPosY() - neighbor.getGridPosY() != 0) {
            int y = currentCell.getGridPosY() - neighbor.getGridPosY();

            if (y > 0) {
                currentCell.setSouth(false);
                neighbor.setNorth(false);
            } else {
                currentCell.setNorth(false);
                neighbor.setSouth(false);
            }
        }
    }

    @Override
    public void removeWalls(CellWDoor currentCell, CellWDoor neighbor) {

    }
}

