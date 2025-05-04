package com.sp.world.generation.maze_generator;

import com.sp.SPBRevamped;
import com.sp.init.ModBlocks;
import com.sp.world.generation.maze_generator.cells.CellWDoor;
import com.sp.world.generation.maze_generator.cells.HighVarCell;
import com.sp.world.generation.maze_generator.cells.LowVarCell;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Stack;

import static com.sp.block.custom.WallBlock.BOTTOM;

public class Level1MazeGenerator extends MazeGenerator {
    int cols;
    int rows;
    int size;

    HighVarCell[][] grid;
    HighVarCell currentCell;
    Stack<HighVarCell> cellStack = new Stack<>();

    int originX;
    int originY;

    String levelDirectory;

    public Level1MazeGenerator(int size, int rows, int cols, int originX, int originY, String levelDirectory) {
        this.size = size;
        this.rows = rows;
        this.cols = cols;
        this.grid = new HighVarCell[rows][cols];

        this.originX = originX - 32;
        this.originY = originY - 32;

        this.levelDirectory = levelDirectory;
    }

    @Override
    public void setup(StructureWorldAccess world, boolean sky, boolean megaRooms, boolean spawnRandomRooms) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();

        if(spawnRandomRooms) {
            this.spawnRandomRooms(world, this.originX, this.originY);
        }

        for (int y = 0; y < this.rows; y++) {
            for (int x = 0; x < this.cols; x++) {
                BlockState blockState1 = world.getBlockState(mutable.set(x + ((this.size - 1) * x) + this.originX, 20, y + ((this.size - 1) * y) + this.originY));
                BlockState blockState2 = world.getBlockState(mutable.set(x + ((this.size - 1) * x) + this.originX, 26, y + ((this.size - 1) * y) + this.originY));

                if(this.isAirOrNull(blockState1) && this.isAirOrNull(blockState2)) {
                    grid[x][y] = new HighVarCell(y + ((this.size - 1) * y) + this.originY, x + ((this.size - 1) * x) + this.originX, this.size, ModBlocks.WallBlock.getDefaultState().with(BOTTOM, false), y, x);
                }
            }
        }

        this.currentCell = grid[0][0];
        currentCell.setVisited(true);
        cellStack.push(currentCell);



        while(!cellStack.isEmpty()) {
            HighVarCell randNeighbor = this.checkNeighbors(grid, currentCell.getGridPosY(), currentCell.getGridPosX(), world);

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
            HighVarCell cell = this.grid[i][0];
            if(cell != null) {
                cell.setSouth(false);
            }
        }

        for(int i = 1; i < this.cols; i += 2) {
            HighVarCell cell = this.grid[this.cols - 1][i];
            if(cell != null) {
                cell.setWest(false);
            }
        }

        for(int i = this.cols - 2; i >= 0; i -= 2) {
            HighVarCell cell = this.grid[i][this.cols - 1];
            if(cell != null) {
                cell.setNorth(false);
            }
        }

        for(int i = this.cols - 1; i >= 0; i -= 2) {
            HighVarCell cell = this.grid[0][i];
            if(cell != null) {
                cell.setEast(false);
            }
        }

        for (HighVarCell[] cell : grid){
            for(HighVarCell cells: cell){
                if(cells != null) {
                    cells.drawWalls(world, this.levelDirectory);
                }
            }
        }




    }



    public HighVarCell checkNeighbors(HighVarCell[][] grid, int y, int x, StructureWorldAccess world){
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        HighVarCell North = null;
        HighVarCell West = null;
        HighVarCell South = null;
        HighVarCell East = null;

        List<HighVarCell> neighbors = new ArrayList<>();


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

        if (world.getBlockState(mutable.set(currentCell.getX(), 19, currentCell.getY() + this.size)) == Blocks.LIME_WOOL.getDefaultState()){
            currentCell.setNorth(false);
        }
        if (world.getBlockState(mutable.set(currentCell.getX(), 19, currentCell.getY() - this.size)) == Blocks.LIME_WOOL.getDefaultState()){
            currentCell.setSouth(false);
        }
        if (world.getBlockState(mutable.set(currentCell.getX() + this.size, 19, currentCell.getY())) == Blocks.LIME_WOOL.getDefaultState()){
            currentCell.setWest(false);
        }
        if (world.getBlockState(mutable.set(currentCell.getX() - this.size, 19, currentCell.getY())) == Blocks.LIME_WOOL.getDefaultState() ||
                world.getBlockState(mutable.set(currentCell.getX() - this.size, 26, currentCell.getY())) == Blocks.YELLOW_WOOL.getDefaultState()){
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

    public void removeWalls(HighVarCell currentCell, HighVarCell neighbor){
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
    public void removeWalls(LowVarCell currentCell, LowVarCell neighbor) {

    }

    @Override
    public void removeWalls(CellWDoor currentCell, CellWDoor neighbor) {

    }

    public void spawnRandomRooms(StructureWorldAccess world, int x, int z) {
        if (world.getServer() != null) {
            boolean place = true;
            Random random = Random.create();
            BlockPos.Mutable mutable = new BlockPos.Mutable();
            StructureTemplateManager structureTemplateManager = world.getServer().getStructureTemplateManager();
            Optional<StructureTemplate> optional;

            Identifier roomIdentifier = new Identifier(SPBRevamped.MOD_ID, "level1/pillars");

            if(random.nextBetween(0, 8) == 0){
                roomIdentifier = new Identifier(SPBRevamped.MOD_ID, "level1/storage");
            }

            StructurePlacementData structurePlacementData = new StructurePlacementData().setMirror(BlockMirror.NONE).setRotation(BlockRotation.NONE).setIgnoreEntities(true);
            optional = structureTemplateManager.getTemplate(roomIdentifier);

            int randomPosX = random.nextBetween(1, 6);
            int randomPosZ = random.nextBetween(1, 6);

            int XOffset = x + (randomPosX * this.size);
            int ZOffset = z + (randomPosZ * this.size);

            for(int i = 0; i < 3; i++){
                for(int j = 0; j < 3; j++){
                    if(world.getBlockState(mutable.set(XOffset + this.size * i, 20, ZOffset + this.size * j)) != Blocks.AIR.getDefaultState() ||
                            world.getBlockState(mutable.set(XOffset + this.size * i, 26, ZOffset + this.size * j)) == Blocks.YELLOW_WOOL.getDefaultState()){
                        place = false;
                    }
                }
            }

            if (place) {
                optional.ifPresent(structureTemplate -> structureTemplate.place(
                        world,
                        mutable.set(XOffset, 19, ZOffset),
                        mutable.set(XOffset, 19, ZOffset),
                        structurePlacementData, random, 16));
            }
        }
    }
}

