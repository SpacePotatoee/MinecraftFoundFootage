package com.sp.world.generation.maze_generator.cells;

import com.sp.SPBRevamped;
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

public class MazeCell {
    private final int worldYPos;
    private final int worldXPos;

    private final int gridPosX;
    private final int gridPosY;

    private final int cellSize;

    private int walls;
    private boolean visited;



    public MazeCell(int worldYPos, int worldXPos, int cellSize, int gridPosY, int gridPosX) {
        this.worldXPos = worldXPos;
        this.worldYPos = worldYPos;
        this.gridPosX = gridPosX;
        this.gridPosY = gridPosY;
        this.walls = 15; // 1111  North, West, South, East   1 meaning a wall is there, 0 meaning no wall
        this.cellSize = cellSize;
        this.visited = false;
    }

    public void drawWalls(StructureWorldAccess world, String level) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        StructureTemplateManager structureTemplateManager = world.getServer().getStructureTemplateManager();
        Optional<StructureTemplate> optional;
        StructurePlacementData structurePlacementData = new StructurePlacementData();

        Identifier roomId;

        Random random = Random.create();
        int roomNumber = random.nextBetween(1,8);

        switch (this.walls) {
            case 0 -> { // 0000   ╬
                roomId = new Identifier(SPBRevamped.MOD_ID, level + "/aroom" + roomNumber);
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.NONE).setIgnoreEntities(true);
            }

            case 8 -> { // 1000   ╦
                roomId = new Identifier(SPBRevamped.MOD_ID, level + "/broom" + roomNumber);
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.CLOCKWISE_180).setIgnoreEntities(true);
            }

            case 4 -> { // 0100   ╠
                roomId = new Identifier(SPBRevamped.MOD_ID, level + "/broom" + roomNumber);
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.CLOCKWISE_90).setIgnoreEntities(true);
            }

            case 2 -> { // 0010   ╩
                roomId = new Identifier(SPBRevamped.MOD_ID, level + "/broom" + roomNumber);
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.NONE).setIgnoreEntities(true);
            }

            case 1 -> { // 0001   ╣
                roomId = new Identifier(SPBRevamped.MOD_ID, level + "/broom" + roomNumber);
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.COUNTERCLOCKWISE_90).setIgnoreEntities(true);
            }

            case 12 -> { // 1100   ╔
                roomId = new Identifier(SPBRevamped.MOD_ID, level + "/croom" + roomNumber);
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.CLOCKWISE_90).setIgnoreEntities(true);
            }

            case 10 -> { // 1010   ═
                roomId = new Identifier(SPBRevamped.MOD_ID, level + "/droom" + roomNumber);
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.CLOCKWISE_90).setIgnoreEntities(true);
            }

            case 9 -> { // 1001   ╗
                roomId = new Identifier(SPBRevamped.MOD_ID, level + "/croom" + roomNumber);
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.CLOCKWISE_180).setIgnoreEntities(true);
            }

            case 6 -> { // 0110   ╚
                roomId = new Identifier(SPBRevamped.MOD_ID, level + "/croom" + roomNumber);
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.NONE).setIgnoreEntities(true);
            }

            case 5 -> { // 0101   ║
                roomId = new Identifier(SPBRevamped.MOD_ID, level + "/droom" + roomNumber);
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.NONE).setIgnoreEntities(true);
            }

            case 3 -> { // 0011   ╝
                roomId = new Identifier(SPBRevamped.MOD_ID, level + "/croom" + roomNumber);
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.COUNTERCLOCKWISE_90).setIgnoreEntities(true);
            }

            case 14 -> { // 1110   ╞
                roomId = new Identifier(SPBRevamped.MOD_ID, level + "/eroom" + roomNumber);
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.CLOCKWISE_90).setIgnoreEntities(true);
            }

            case 7 -> { // 0111   ╨
                roomId = new Identifier(SPBRevamped.MOD_ID, level + "/eroom" + roomNumber);
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.NONE).setIgnoreEntities(true);
            }

            case 11 -> { // 1011   ╡
                roomId = new Identifier(SPBRevamped.MOD_ID, level + "/eroom" + roomNumber);
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.COUNTERCLOCKWISE_90).setIgnoreEntities(true);
            }

            case 13 -> { // 1101   ╥
                roomId = new Identifier(SPBRevamped.MOD_ID, level + "/eroom" + roomNumber);
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.CLOCKWISE_180).setIgnoreEntities(true);
            }

            default -> { // Shouldn't be possible, but just in case
                roomId = new Identifier(SPBRevamped.MOD_ID, level + "/aroom" + roomNumber);
            }
        }

        optional = structureTemplateManager.getTemplate(roomId);

        switch (structurePlacementData.getRotation()){
            case NONE -> optional.ifPresent(structureTemplate ->
                    structureTemplate.place(
                            world,
                            mutable.set(this.getWorldXPos(), 20, this.getWorldYPos()),
                            mutable.set(this.getWorldXPos(), 20, this.getWorldYPos()),
                            structurePlacementData, random, 2));
            case CLOCKWISE_90 -> optional.ifPresent(structureTemplate ->
                    structureTemplate.place(
                            world,
                            mutable.set(this.getWorldXPos() + (this.cellSize - 1), 20, this.getWorldYPos()),
                            mutable.set(this.getWorldXPos() + (this.cellSize - 1), 20, this.getWorldYPos()),
                            structurePlacementData, random, 2));
            case COUNTERCLOCKWISE_90 -> optional.ifPresent(structureTemplate ->
                    structureTemplate.place(
                            world,
                            mutable.set(this.getWorldXPos(), 20, this.getWorldYPos() + (this.cellSize - 1)),
                            mutable.set(this.getWorldXPos(), 20, this.getWorldYPos() + (this.cellSize - 1)),
                            structurePlacementData, random, 2));
            case CLOCKWISE_180 -> optional.ifPresent(structureTemplate ->
                    structureTemplate.place(
                            world,
                            mutable.set(this.getWorldXPos() + (this.cellSize - 1), 20, this.getWorldYPos() + (this.cellSize - 1)),
                            mutable.set(this.getWorldXPos() + (this.cellSize - 1), 20, this.getWorldYPos() + (this.cellSize - 1)),
                            structurePlacementData, random, 2));
        }

    }

    public int getGridPosX() {
        return this.gridPosX;
    }

    public int getGridPosY() {
        return this.gridPosY;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public int getWorldXPos() {
        return this.worldXPos;
    }

    public int getWorldYPos() {
        return this.worldYPos;
    }

    public void removeNorthWall() {
        int mask = 1 << 3; // 1000
        mask = ~mask; // 0111
        this.walls &= mask; // Remove North wall
    }

    public void removeSouthWall(){
        int mask = 1 << 2; // 0100
        mask = ~mask; // 1011
        this.walls &= mask; // Remove South wall
    }

    public void removeEastWall() {
        int mask = 1 << 1; // 0010
        mask = ~mask; // 1101
        this.walls &= mask; // Remove East wall
    }

    public void removeWestWall() {
        int mask = 1; // 0001
        mask = ~mask; // 1110
        this.walls &= mask; // Remove West wall
    }
}
