package com.sp.world.generation.maze_generator.cells;

import com.sp.SPBRevamped;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;

import java.util.Optional;

public class HighVarCell {
    private int y;
    private int x;

    private int gridPosX;
    private int gridPosY;

    private int cellSize;
    private boolean north;
    private boolean west;
    private boolean south;
    private boolean east;
    private boolean visited;
    String type;

    BlockState blockState;

    public HighVarCell(int y, int x, int cellSize, BlockState blockState, int gridPosY, int gridPosX){
        this.x = x;
        this.y = y;
        this.gridPosX = gridPosX;
        this.gridPosY = gridPosY;
        this.cellSize = cellSize;
        this.north = true;
        this.west = true;
        this.south = true;
        this.east = true;
        this.blockState = blockState;
        this.visited = false;
    }




    public void drawWalls(StructureWorldAccess world, String level){
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        MinecraftServer server = world.getServer();
        StructureTemplateManager structureTemplateManager = server.getStructureTemplateManager();
        Optional<StructureTemplate> optional;
        StructurePlacementData structurePlacementData = new StructurePlacementData();


        Identifier roomId;

        Random random = Random.create();
        int roomNumber = 1;





        if(!this.north && !this.west && !this.south && !this.east){
            this.type = "╬";
            roomNumber = random.nextBetween(1,6);
            roomId = new Identifier(SPBRevamped.MOD_ID, level + "/aroom" + roomNumber);
            structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.NONE).setIgnoreEntities(true);
        }
        else if(this.north && !this.west && !this.south && !this.east){
            this.type = "╦";
            //this.drawNorthWall(world, this.getY(), this.getX());
            roomNumber = random.nextBetween(1,24);
            roomId = new Identifier(SPBRevamped.MOD_ID, level + "/broom" + roomNumber);
            structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.CLOCKWISE_180).setIgnoreEntities(true);
        }

        else if(!this.north && this.west && !this.south && !this.east){
            this.type = "╠";
            //this.drawWestWall(world, this.getY(), this.getX());
            roomNumber = random.nextBetween(1,24);
            roomId = new Identifier(SPBRevamped.MOD_ID, level + "/broom" + roomNumber);
            structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.CLOCKWISE_90).setIgnoreEntities(true);
        }

        else if(!this.north && !this.west && this.south && !this.east){
            this.type = "╩";
            //this.drawSouthWall(world, this.getY(), this.getX());
            roomNumber = random.nextBetween(1,24);
            roomId = new Identifier(SPBRevamped.MOD_ID, level + "/broom" + roomNumber);
            structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.NONE).setIgnoreEntities(true);
        }

        else if(!this.north && !this.west && !this.south && this.east){
            this.type = "╣";
            //this.drawEastWall(world, this.getY(), this.getX());
            roomNumber = random.nextBetween(1,24);
            roomId = new Identifier(SPBRevamped.MOD_ID, level + "/broom" + roomNumber);
            structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.COUNTERCLOCKWISE_90).setIgnoreEntities(true);
        }

        else if(this.north && this.west && !this.south && !this.east){
            this.type = "╔";
            //this.drawNorthWall(world, this.getY(), this.getX());
            //this.drawWestWall(world, this.getY(), this.getX());
            roomNumber = random.nextBetween(1,24);
            roomId = new Identifier(SPBRevamped.MOD_ID, level + "/croom" + roomNumber);
            structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.CLOCKWISE_90).setIgnoreEntities(true);

        }

        else if(this.north && !this.west && this.south && !this.east){
            this.type = "═";
            //this.drawNorthWall(world, this.getY(), this.getX());
            //this.drawSouthWall(world, this.getY(), this.getX());
            roomNumber = random.nextBetween(1,12);
            roomId = new Identifier(SPBRevamped.MOD_ID, level + "/droom" + roomNumber);
            structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.CLOCKWISE_90).setIgnoreEntities(true);
        }

        else if(this.north && !this.west && !this.south && this.east){
            this.type = "╗";
            //this.drawNorthWall(world, this.getY(), this.getX());
            //this.drawEastWall(world, this.getY(), this.getX());
            roomNumber = random.nextBetween(1,24);
            roomId = new Identifier(SPBRevamped.MOD_ID, level + "/croom" + roomNumber);
            structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.CLOCKWISE_180).setIgnoreEntities(true);
        }

        else if(!this.north && this.west && this.south && !this.east){
            this.type = "╚";
            //this.drawWestWall(world, this.getY(), this.getX());
            //this.drawSouthWall(world, this.getY(), this.getX());
            roomNumber = random.nextBetween(1,24);
            roomId = new Identifier(SPBRevamped.MOD_ID, level + "/croom" + roomNumber);
            structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.NONE).setIgnoreEntities(true);
        }

        else if(!this.north && this.west && !this.south && this.east){
            this.type = "║";
            //this.drawWestWall(world, this.getY(), this.getX());
            //this.drawEastWall(world, this.getY(), this.getX());
            roomNumber = random.nextBetween(1,12);
            roomId = new Identifier(SPBRevamped.MOD_ID, level + "/droom" + roomNumber);
            structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.NONE).setIgnoreEntities(true);
        }

        else if(!this.north && !this.west && this.south && this.east){
            this.type = "╝";
            //this.drawSouthWall(world, this.getY(), this.getX());
            //this.drawEastWall(world, this.getY(), this.getX());
            roomNumber = random.nextBetween(1,24);
            roomId = new Identifier(SPBRevamped.MOD_ID, level + "/croom" + roomNumber);
            structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.COUNTERCLOCKWISE_90).setIgnoreEntities(true);
        }

        else if(this.north && this.west && this.south && !this.east){
            this.type = "╞";
            //this.drawNorthWall(world, this.getY(), this.getX());
            //this.drawWestWall(world, this.getY(), this.getX());
            //this.drawSouthWall(world, this.getY(), this.getX());
            roomNumber = random.nextBetween(1,24);
            roomId = new Identifier(SPBRevamped.MOD_ID, level + "/eroom" + roomNumber);
            structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.CLOCKWISE_90).setIgnoreEntities(true);
        }

        else if(!this.north && this.west && this.south && this.east){
            this.type = "╨";
            //this.drawWestWall(world, this.getY(), this.getX());
            //this.drawSouthWall(world, this.getY(), this.getX());
            //this.drawEastWall(world, this.getY(), this.getX());
            roomNumber = random.nextBetween(1,24);
            roomId = new Identifier(SPBRevamped.MOD_ID, level + "/eroom" + roomNumber);
            structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.NONE).setIgnoreEntities(true);
        }

        else if(this.north && !this.west && this.south && this.east){
            this.type = "╡";
            //this.drawNorthWall(world, this.getY(), this.getX());
            //this.drawSouthWall(world, this.getY(), this.getX());
            //this.drawEastWall(world, this.getY(), this.getX());
            roomNumber = random.nextBetween(1,24);
            roomId = new Identifier(SPBRevamped.MOD_ID, level + "/eroom" + roomNumber);
            structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.COUNTERCLOCKWISE_90).setIgnoreEntities(true);
        }

        else if(this.north && this.west && !this.south && this.east){
            this.type = "╥";
            //this.drawNorthWall(world, this.getY(), this.getX());
            //this.drawWestWall(world, this.getY(), this.getX());
            //this.drawEastWall(world, this.getY(), this.getX());
            roomNumber = random.nextBetween(1,24);
            roomId = new Identifier(SPBRevamped.MOD_ID, level + "/eroom" + roomNumber);
            structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.CLOCKWISE_180).setIgnoreEntities(true);
        }
        else{
//            this.drawNorthWall(world, this.getY(), this.getX());
//            this.drawWestWall(world, this.getY(), this.getX());
//            this.drawSouthWall(world, this.getY(), this.getX());
//            this.drawEastWall(world, this.getY(), this.getX());
            roomId = new Identifier(SPBRevamped.MOD_ID, level + "/aroom" + roomNumber);
        }

        optional = structureTemplateManager.getTemplate(roomId);

        switch (structurePlacementData.getRotation()){
            case NONE -> optional.ifPresent(structureTemplate ->
                    structureTemplate.place(
                            world,
                            mutable.set(this.getX(), 20, this.getY()),
                            mutable.set(this.getX(), 20, this.getY()),
                            structurePlacementData, random, 16));
            case CLOCKWISE_90 -> optional.ifPresent(structureTemplate ->
                    structureTemplate.place(
                            world,
                            mutable.set(this.getX() + (this.cellSize - 1), 20, this.getY()),
                            mutable.set(this.getX() + (this.cellSize - 1), 20, this.getY()),
                            structurePlacementData, random, 16));
            case COUNTERCLOCKWISE_90 -> optional.ifPresent(structureTemplate ->
                    structureTemplate.place(
                            world,
                            mutable.set(this.getX(), 20, this.getY() + (this.cellSize - 1)),
                            mutable.set(this.getX(), 20, this.getY() + (this.cellSize - 1)),
                            structurePlacementData, random, 16));
            case CLOCKWISE_180 -> optional.ifPresent(structureTemplate ->
                    structureTemplate.place(
                            world,
                            mutable.set(this.getX() + (this.cellSize - 1), 20, this.getY() + (this.cellSize - 1)),
                            mutable.set(this.getX() + (this.cellSize - 1), 20, this.getY() + (this.cellSize - 1)),
                            structurePlacementData, random, 16));
        }




    }

    public void drawNorthWall(StructureWorldAccess world, int y, int x){
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (int i= 0; i < this.cellSize; i++) {
            for (int j = 0; j < 4; j++) {
                world.setBlockState(mutable.set(x + i, 21 + j, y + (this.cellSize - 1)), this.blockState, 2);
            }
        }
    }

    public void drawWestWall(StructureWorldAccess world, int y, int x){
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (int i= 0; i < this.cellSize; i++) {
            for (int j = 0; j < 4; j++) {
                world.setBlockState(mutable.set(x + (this.cellSize - 1), 21 + j, y + i), this.blockState, 2);
            }
        }
    }

    public void drawSouthWall(StructureWorldAccess world, int y, int x){
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (int i= 0; i < this.cellSize; i++) {
            for (int j = 0; j < 4; j++) {
                world.setBlockState(mutable.set(x + i, 21 + j, y), this.blockState, 2);
            }
        }
    }

    public void drawEastWall(StructureWorldAccess world, int y, int x){
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (int i= 0; i < this.cellSize; i++) {
            for (int j = 0; j < 4; j++) {
                world.setBlockState(mutable.set(x, 21 + j, y + i), this.blockState, 2);
            }
        }
    }

    public void drawCorners(StructureWorldAccess world){
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (int j = 0; j < 4; j++) {
            world.setBlockState(mutable.set(this.x, 21 + j, this.y), this.blockState, 2);
        }

        for (int j = 0; j < 4; j++) {
            world.setBlockState(mutable.set(this.x + (this.cellSize - 1), 21 + j, this.y + (this.cellSize - 1)), this.blockState, 2);
        }

        for (int j = 0; j < 4; j++) {
            world.setBlockState(mutable.set(this.x + (this.cellSize - 1), 21 + j, this.y), this.blockState, 2);
        }

        for (int j = 0; j < 4; j++) {
            world.setBlockState(mutable.set(this.x, 21 + j, this.y + (this.cellSize - 1)), this.blockState, 2);
        }
    }


    public int getGridPosX() {
        return this.gridPosX;
    }

    public int getGridPosY() {
        return this.gridPosY;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public void setEast(boolean east) {
        this.east = east;
    }

    public void setSouth(boolean south) {
        this.south = south;
    }

    public void setWest(boolean west) {
        this.west = west;
    }

    public void setNorth(boolean north) {
        this.north = north;
    }

    public boolean isNorth() {
        return this.north;
    }

    public boolean isWest() {
        return this.west;
    }

    public boolean isSouth() {
        return this.south;
    }

    public boolean isEast() {
        return this.east;
    }

    public void drawMarker(StructureWorldAccess world){
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        world.setBlockState(mutable.set(this.getX() + 3, 24, this.getY() + 3), Blocks.PURPLE_WOOL.getDefaultState(), 2);
    }
    public void drawGold(StructureWorldAccess world){
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        world.setBlockState(mutable.set(this.getX() + 3, 25, this.getY() + 3), Blocks.GOLD_BLOCK.getDefaultState(), 2);
    }


}
