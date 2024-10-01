package com.sp.world.generation.maze_generator;

import com.sp.block.ModBlocks;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static com.sp.block.custom.WallBlock.BOTTOM;

public class Level0MazeGenerator {
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

    public void setup(StructureWorldAccess world){
        for (int y = 0; y < this.rows; y++) {
            for (int x = 0; x < this.cols; x++) {
                grid[x][y] = new LowVarCell(y + ((this.size - 1) * y) + this.originY, x + ((this.size - 1) * x) + this.originX, this.size, ModBlocks.WallBlock.getDefaultState().with(BOTTOM, false), y, x);

                LowVarCell selectedCell = grid[x][y];
                selectedCell.drawCorners(world);
            }
        }

        this.currentCell = grid[0][0];
        currentCell.setVisited(true);
        cellStack.push(currentCell);



        while(!cellStack.isEmpty()) {
            LowVarCell randNeighbor = this.checkNeighbors(grid, currentCell.getGridPosY(), currentCell.getGridPosX());

            while (randNeighbor != null) {
                randNeighbor.setVisited(true);
                this.removeWalls(currentCell, randNeighbor);
                this.currentCell = randNeighbor;
                cellStack.push(currentCell);
                randNeighbor = this.checkNeighbors(grid, currentCell.getGridPosY(), currentCell.getGridPosX());
            }
            currentCell = cellStack.pop();

        }

        for(int i = 0; i < this.cols; i += 2) {
            grid[i][0].setSouth(false);
        }

        for(int i = 0; i < this.cols; i += 2) {
            grid[this.cols - 1][i].setWest(false);
        }

        for(int i = this.cols - 1; i >= 0; i -= 2) {
            grid[i][this.cols - 1].setNorth(false);
        }

        for(int i = this.cols - 1; i >= 0; i -= 2) {
            grid[0][i].setEast(false);
        }

        for (LowVarCell[] cell : grid){
            for(LowVarCell cells: cell){
                cells.drawWalls(world, this.levelDirectory);
            }
        }



    }

    public LowVarCell checkNeighbors(LowVarCell[][] grid, int y, int x){
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

        if (!neighbors.isEmpty()){
            Random random = Random.create();
            int r = random.nextBetween(0, neighbors.size() - 1);
            return neighbors.get(r);
        }
        else{
            return null;
        }


    }

    public void removeWalls(LowVarCell currentCell, LowVarCell neighbor){

        if(currentCell.getGridPosX() - neighbor.getGridPosX() != 0){
            int x = currentCell.getGridPosX() - neighbor.getGridPosX();

            if(x > 0){
                currentCell.setEast(false);
                neighbor.setWest(false);
            }
            else{
                currentCell.setWest(false);
                neighbor.setEast(false);
            }
        }

        if(currentCell.getGridPosY() - neighbor.getGridPosY() != 0){
            int y = currentCell.getGridPosY() - neighbor.getGridPosY();

            if(y > 0){
                currentCell.setSouth(false);
                neighbor.setNorth(false);
            }
            else{
                currentCell.setNorth(false);
                neighbor.setSouth(false);
            }
        }
    }


}

