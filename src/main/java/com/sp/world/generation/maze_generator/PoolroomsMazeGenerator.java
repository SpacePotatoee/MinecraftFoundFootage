package com.sp.world.generation.maze_generator;

import com.sp.SPBRevamped;
import com.sp.init.ModBlocks;
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

import java.util.*;

import static com.sp.block.custom.WallBlock.BOTTOM;

public class PoolroomsMazeGenerator {
    int cols;
    int rows;
    int size;
    int lastRoomDir;

    CellWDoor[][] grid;
    CellWDoor currentCell;
    Stack<CellWDoor> cellStack = new Stack<>();

    int originX;
    int originY;

    String levelDirectory;

    public PoolroomsMazeGenerator(int size, int rows, int cols, int originX, int originY, String levelDirectory){
        this.size = size;
        this.rows = rows;
        this.cols = cols;
        this.grid = new CellWDoor[rows][cols];

        this.originX = originX - 32;
        this.originY = originY - 32;

        this.levelDirectory = levelDirectory;
    }

    public void setup(StructureWorldAccess world){
        //Initial Random Mega Room
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        Random random = Random.create();
        StructureTemplateManager structureTemplateManager = world.getServer().getStructureTemplateManager();
        Identifier roomIdentifier = null;
        int w = random.nextBetween(1, 6);
        int p = random.nextBetween(1, 3);

        if(w == 1) roomIdentifier = new Identifier(SPBRevamped.MOD_ID, this.levelDirectory + "/megaroom_16x16_" + p);
        else if (w == 2) roomIdentifier = new Identifier(SPBRevamped.MOD_ID, this.levelDirectory + "/megaroom_16x24_" + p);
        else if (w == 3) roomIdentifier = new Identifier(SPBRevamped.MOD_ID, this.levelDirectory + "/megaroom_16x32_" + p);
        else if (w == 4) roomIdentifier = new Identifier(SPBRevamped.MOD_ID, this.levelDirectory + "/megaroom_24x24_" + p);
        else if (w == 5) roomIdentifier = new Identifier(SPBRevamped.MOD_ID, this.levelDirectory + "/megaroom_24x32_" + p);
        else if (w == 6) roomIdentifier = new Identifier(SPBRevamped.MOD_ID, this.levelDirectory + "/megaroom_32x32_" + p);

        StructurePlacementData structurePlacementData = new StructurePlacementData();
        structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.NONE).setIgnoreEntities(true);
        Optional<StructureTemplate> optional = structureTemplateManager.getTemplate(roomIdentifier);

        int roomWidth = Integer.parseInt(roomIdentifier.getPath().substring(19, 21));
        int roomHeight = Integer.parseInt(roomIdentifier.getPath().substring(22, 24));

        int randX = random.nextBetween(1, this.cols - (1 + ((int) roomWidth/this.size)));
        int randY = random.nextBetween(1, this.rows - (1 + ((int) roomHeight/this.size)));

        BlockPos structurePos = mutable.set(randX + ((this.size - 1) * randX) + this.originX, 18, randY + ((this.size - 1) * randY) + this.originY);

        if(optional.isPresent() && world.getBlockState(mutable.set(structurePos.getX(), 18, structurePos.getZ())) != Blocks.PURPLE_WOOL.getDefaultState()){
            optional.get().place(world, structurePos, structurePos, structurePlacementData, random, 2);
        }

        List<String> megaRoomList = new ArrayList<>();
        this.createMegaRoomList(megaRoomList);


        while(!megaRoomList.isEmpty()) {
            int ind = random.nextBetween(0, megaRoomList.size() - 1);
            String currentMegaRoom = megaRoomList.get(ind);
            int xx = Integer.parseInt(currentMegaRoom.substring(0, 2));
            int yy = Integer.parseInt(currentMegaRoom.substring(3, 5));
            p = random.nextBetween(1, 3);
            if (yy < xx){
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.COUNTERCLOCKWISE_90).setIgnoreEntities(true);
                roomIdentifier = new Identifier(SPBRevamped.MOD_ID, this.levelDirectory + "/megaroom_" + yy + "x" + xx + "_" + p);
            }
            else {
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.NONE).setIgnoreEntities(true);
                roomIdentifier = new Identifier(SPBRevamped.MOD_ID, this.levelDirectory + "/megaroom_" + xx + "x" + yy + "_" + p);
            }
            roomWidth = xx;
            roomHeight = yy;


            boolean placed = false;
            for (int ay = 1; ay < this.rows - (((int) roomHeight/this.size)); ay++) {
                for (int ax = 1; ax < this.cols - (((int) roomWidth/this.size)); ax++) {
                    if(!placed) {
                        BlockPos StructurePos = mutable.set(ax + ((this.size - 1) * ax) + this.originX, 18, ay + ((this.size - 1) * ay) + this.originY);

                        boolean clear = true;
                        for (int ry = -1; ry <= roomHeight; ry++) {
                            for (int bx = -1; bx <= roomWidth; bx++) {
                                if(clear) {
                                    if (world.getBlockState(new BlockPos(StructurePos.getX() + bx, 18, StructurePos.getZ() + ry)) == Blocks.PURPLE_WOOL.getDefaultState()) {
                                        clear = false;
                                        break;
                                    }
                                }
                            }
                        }


                        if (clear) {
                            optional = structureTemplateManager.getTemplate(roomIdentifier);
                            if(optional.isPresent()){
                                if(structurePlacementData.getRotation() == BlockRotation.COUNTERCLOCKWISE_90){
                                    optional.get().place(world, new BlockPos(StructurePos.getX(), 18, StructurePos.getZ() + (roomHeight - 1)), new BlockPos(StructurePos.getX(), 19, StructurePos.getZ() + (roomWidth - 1)), structurePlacementData, random, 2);
                                }
                                else {
                                    optional.get().place(world, StructurePos, StructurePos, structurePlacementData, random, 2);
                                }
                                placed = true;
                                break;
                            }
                        }
                    }
                }
            }
            megaRoomList.remove(ind);
        }

        //Generate Maze
        for (int y = 0; y < this.rows; y++) {
            for (int x = 0; x < this.cols; x++) {
                if(world.getBlockState(mutable.set(x + ((this.size - 1) * x) + this.originX, 18, y + ((this.size - 1) * y) + this.originY)) == Blocks.AIR.getDefaultState()) {
                    grid[x][y] = new CellWDoor(y + ((this.size - 1) * y) + this.originY, x + ((this.size - 1) * x) + this.originX, this.size, ModBlocks.WallBlock.getDefaultState().with(BOTTOM, false), y, x);
                }
            }
        }

        this.currentCell = grid[0][0];
        currentCell.setVisited(true);
        cellStack.push(currentCell);



        while(!cellStack.isEmpty()) {
            CellWDoor randNeighbor = this.checkNeighbors(grid, currentCell.getGridPosY(), currentCell.getGridPosX(), world);

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
            grid[i][0].setSouth(false);
        }

        for(int i = 1; i < this.cols; i += 2) {
            grid[this.cols - 1][i].setWest(false);
        }

        for(int i = this.cols - 2; i >= 0; i -= 2) {
            grid[i][this.cols - 1].setNorth(false);
        }

        for(int i = this.cols - 1; i >= 0; i -= 2) {
            grid[0][i].setEast(false);
        }

        for (CellWDoor[] cell : grid){
            for(CellWDoor cells: cell){
                if (cells != null) {
                    cells.drawWalls(world, this.levelDirectory);
                }
            }
        }


    }



    public CellWDoor checkNeighbors(CellWDoor[][] grid, int y, int x, StructureWorldAccess world){
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        CellWDoor North = null;
        CellWDoor West = null;
        CellWDoor South = null;
        CellWDoor East = null;

        List<CellWDoor> neighbors = new ArrayList<>();
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

        if (world.getBlockState(mutable.set(currentCell.getX(), 19, currentCell.getY() + this.size)) == Blocks.YELLOW_WOOL.getDefaultState() ||
            world.getBlockState(mutable.set(currentCell.getX(), 19, currentCell.getY() + this.size)) == Blocks.PINK_WOOL.getDefaultState()){
            currentCell.setNorth(false);
        }
        if (world.getBlockState(mutable.set(currentCell.getX(), 19, currentCell.getY() - this.size)) == Blocks.RED_WOOL.getDefaultState() ||
            world.getBlockState(mutable.set(currentCell.getX(), 19, currentCell.getY() - this.size)) == Blocks.PINK_WOOL.getDefaultState()){
            currentCell.setSouth(false);
        }
        if (world.getBlockState(mutable.set(currentCell.getX() + this.size, 19, currentCell.getY())) == Blocks.ORANGE_WOOL.getDefaultState() ||
            world.getBlockState(mutable.set(currentCell.getX() + this.size, 19, currentCell.getY())) == Blocks.PINK_WOOL.getDefaultState()){
            currentCell.setWest(false);
        }
        if (world.getBlockState(mutable.set(currentCell.getX() - this.size, 19, currentCell.getY())) == Blocks.LIME_WOOL.getDefaultState() ||
            world.getBlockState(mutable.set(currentCell.getX() - this.size, 19, currentCell.getY())) == Blocks.PINK_WOOL.getDefaultState()){
            currentCell.setEast(false);
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

    public void removeWalls(CellWDoor currentCell, CellWDoor neighbor){
        Random random = Random.create();


        if(currentCell.getGridPosX() - neighbor.getGridPosX() != 0){
            int x = currentCell.getGridPosX() - neighbor.getGridPosX();
            int door = random.nextBetween(1,5);

            if(x > 0){
                if(door == 1){
                    currentCell.setEast(false);
                    currentCell.setEastDoor(true);
                    neighbor.setWest(false);
                    neighbor.setWestDoor(true);
                }
                else {
                    currentCell.setEast(false);
                    neighbor.setWest(false);
                }
            }
            else{
                if(door == 1){
                    currentCell.setWest(false);
                    currentCell.setWestDoor(true);
                    neighbor.setEast(false);
                    neighbor.setEastDoor(true);
                }
                else {
                    currentCell.setWest(false);
                    neighbor.setEast(false);
                }
            }
        }

        if(currentCell.getGridPosY() - neighbor.getGridPosY() != 0){
            int y = currentCell.getGridPosY() - neighbor.getGridPosY();
            int door = random.nextBetween(1,3);

            if(y > 0){
                if(door == 1) {
                    currentCell.setSouth(false);
                    currentCell.setSouthDoor(true);
                    neighbor.setNorth(false);
                    neighbor.setNorthDoor(true);
                }
                else{
                    currentCell.setSouth(false);
                    neighbor.setNorth(false);
                }
            }
            else{
                if(door == 1) {
                    currentCell.setNorth(false);
                    currentCell.setNorthDoor(true);
                    neighbor.setSouth(false);
                    neighbor.setSouthDoor(true);
                }
                else{
                    currentCell.setNorth(false);
                    neighbor.setSouth(false);
                }

            }
        }

    }

    public void createMegaRoomList(List<String> megaRoomList){
        megaRoomList.add("16x16");
        megaRoomList.add("16x24");
        megaRoomList.add("16x32");
        megaRoomList.add("24x16");
        megaRoomList.add("24x24");
        megaRoomList.add("24x32");
        megaRoomList.add("32x16");
        megaRoomList.add("32x24");
        megaRoomList.add("32x32");
    }
}

