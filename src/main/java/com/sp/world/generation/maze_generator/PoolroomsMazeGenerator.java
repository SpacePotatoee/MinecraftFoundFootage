package com.sp.world.generation.maze_generator;

import com.sp.world.generation.maze_generator.cells.MazeCell;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class PoolroomsMazeGenerator extends MazeGenerator {
    int cols;
    int rows;
    int size;
    int lastRoomDir;

    MazeCell[][] grid;
    MazeCell currentCell;
    Stack<MazeCell> cellStack = new Stack<>();

    int originX;
    int originY;

    String levelDirectory;

    public PoolroomsMazeGenerator(int size, int rows, int cols, int originX, int originY, String levelDirectory){
        this.size = size;
        this.rows = rows;
        this.cols = cols;
        this.grid = new MazeCell[rows][cols];

        this.originX = originX - 32;
        this.originY = originY - 32;

        this.levelDirectory = levelDirectory;
    }

    @Override
    public void setup(StructureWorldAccess world, boolean sky, boolean megaRooms, boolean spawnRandomRooms) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();

        //Generate Maze
        for (int y = 0; y < this.rows; y++) {
            for (int x = 0; x < this.cols; x++) {
                int xPos = x + ((this.size - 1) * x) + this.originX;
                int yPos = y + ((this.size - 1) * y) + this.originY;

                BlockState blockState = world.getBlockState(mutable.set(xPos, 18, yPos));
                if(this.isAirOrNull(blockState)) {
                    grid[x][y] = new MazeCell(yPos, xPos, this.size, y, x);
                }
            }
        }

        this.currentCell = grid[0][0];
        currentCell.setVisited(true);
        cellStack.push(currentCell);



        while(!cellStack.isEmpty()) {
            MazeCell randNeighbor = this.checkNeighbors(grid, currentCell.getGridPosY(), currentCell.getGridPosX(), world);

            while (randNeighbor != null) {
                randNeighbor.setVisited(true);
                this.removeWalls(currentCell, randNeighbor);
                this.currentCell = randNeighbor;
                cellStack.push(currentCell);
                randNeighbor = this.checkNeighbors(grid, currentCell.getGridPosY(), currentCell.getGridPosX(), world);
            }
            currentCell = cellStack.pop();

        }

        for(int i = 0; i < this.cols; i += 2) {
            MazeCell cell = this.grid[i][0];
            if(cell != null) {
                cell.removeSouthWall();
            }
        }

        for(int i = 1; i < this.cols; i += 2) {
            MazeCell cell = this.grid[this.cols - 1][i];
            if(cell != null) {
                cell.removeWestWall();
            }
        }

        for(int i = this.cols - 2; i >= 0; i -= 2) {
            MazeCell cell = this.grid[i][this.cols - 1];
            if(cell != null) {
                cell.removeNorthWall();
            }
        }

        for(int i = this.cols - 1; i >= 0; i -= 2) {
            MazeCell cell = this.grid[0][i];
            if(cell != null) {
                cell.removeEastWall();
            }
        }

        for (MazeCell[] cell : grid){
            for(MazeCell cells: cell){
                if (cells != null) {
                    cells.drawWallsWithDoors(world, this.levelDirectory);
                }
            }
        }


    }

    public MazeCell checkNeighbors(MazeCell[][] grid, int y, int x, StructureWorldAccess world){
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        MazeCell North = null;
        MazeCell West = null;
        MazeCell South = null;
        MazeCell East = null;

        List<MazeCell> neighbors = new ArrayList<>();
        List<Integer> roomDir = new ArrayList<>();


        if (y + 1 < this.rows) North = grid[x][y + 1];

        if (x + 1 < this.cols) West = grid[x + 1][y];

        if(y - 1 >= 0) South = grid[x][y - 1];

        if(x - 1 >= 0) East = grid[x - 1][y];


        if (North != null && !North.isVisited()){
            neighbors.add(North);
            roomDir.add(1);
            if(lastRoomDir == 1){
                neighbors.add(North);
                roomDir.add(1);
            }
        }
        if (West != null && !West.isVisited()){
            neighbors.add(West);
            roomDir.add(2);
            if(lastRoomDir == 2){
                neighbors.add(West);
                roomDir.add(2);
            }

        }
        if (South != null && !South.isVisited()){
            neighbors.add(South);
            roomDir.add(3);
            if(lastRoomDir == 3){
                neighbors.add(South);
                roomDir.add(3);
            }

        }
        if (East != null && !East.isVisited()){
            neighbors.add(East);
            roomDir.add(4);
            if(lastRoomDir == 4){
                neighbors.add(East);
                roomDir.add(4);
            }
        }

        if (world.getBlockState(mutable.set(currentCell.getWorldXPos(), 19, currentCell.getWorldYPos() + this.size)) == Blocks.YELLOW_WOOL.getDefaultState() ||
                world.getBlockState(mutable.set(currentCell.getWorldXPos(), 19, currentCell.getWorldYPos() + this.size)) == Blocks.PINK_WOOL.getDefaultState()){
            currentCell.removeNorthWall();
        }
        if (world.getBlockState(mutable.set(currentCell.getWorldXPos(), 19, currentCell.getWorldYPos() - this.size)) == Blocks.RED_WOOL.getDefaultState() ||
                world.getBlockState(mutable.set(currentCell.getWorldXPos(), 19, currentCell.getWorldYPos() - this.size)) == Blocks.PINK_WOOL.getDefaultState()){
            currentCell.removeSouthWall();
        }
        if (world.getBlockState(mutable.set(currentCell.getWorldXPos() + this.size, 19, currentCell.getWorldYPos())) == Blocks.ORANGE_WOOL.getDefaultState() ||
                world.getBlockState(mutable.set(currentCell.getWorldXPos() + this.size, 19, currentCell.getWorldYPos())) == Blocks.PINK_WOOL.getDefaultState()){
            currentCell.removeWestWall();
        }
        if (world.getBlockState(mutable.set(currentCell.getWorldXPos() - this.size, 19, currentCell.getWorldYPos())) == Blocks.LIME_WOOL.getDefaultState() ||
                world.getBlockState(mutable.set(currentCell.getWorldXPos() - this.size, 19, currentCell.getWorldYPos())) == Blocks.PINK_WOOL.getDefaultState()){
            currentCell.removeEastWall();
        }



        if (!neighbors.isEmpty()){
            Random random = Random.create();
            int r = random.nextBetween(0, neighbors.size() - 1);
            lastRoomDir = roomDir.get(r);
            return neighbors.get(r);
        }
        else{
            return null;
        }


    }

    @Override
    public void removeWalls(MazeCell currentCell, MazeCell neighbor){
        Random random = Random.create();


        if(currentCell.getGridPosX() - neighbor.getGridPosX() != 0){
            int x = currentCell.getGridPosX() - neighbor.getGridPosX();
            int door = random.nextBetween(1,5);

            if(x > 0){
                if(door == 1){
                    currentCell.removeEastWall();
                    currentCell.addEastDoor();
                    neighbor.removeWestWall();
                    neighbor.addWestDoor();
                }
                else {
                    currentCell.removeEastWall();
                    neighbor.removeWestWall();
                }
            }
            else{
                if(door == 1){
                    currentCell.removeWestWall();
                    currentCell.addWestDoor();
                    neighbor.removeEastWall();
                    neighbor.addEastDoor();
                }
                else {
                    currentCell.removeWestWall();
                    neighbor.removeEastWall();
                }
            }
        }

        if(currentCell.getGridPosY() - neighbor.getGridPosY() != 0){
            int y = currentCell.getGridPosY() - neighbor.getGridPosY();
            int door = random.nextBetween(1,3);

            if(y > 0){
                if(door == 1) {
                    currentCell.removeSouthWall();
                    currentCell.addSouthDoor();
                    neighbor.removeNorthWall();
                    neighbor.addNorthDoor();
                }
                else{
                    currentCell.removeSouthWall();
                    neighbor.removeNorthWall();
                }
            }
            else{
                if(door == 1) {
                    currentCell.removeNorthWall();
                    currentCell.addNorthDoor();
                    neighbor.removeSouthWall();
                    neighbor.addSouthDoor();
                }
                else{
                    currentCell.removeNorthWall();
                    neighbor.removeSouthWall();
                }

            }
        }
    }
}