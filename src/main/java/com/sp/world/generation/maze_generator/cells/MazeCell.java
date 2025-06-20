package com.sp.world.generation.maze_generator.cells;

import com.sp.SPBRevamped;
import com.sp.util.MathStuff;
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
    private int doors;
    private boolean visited;



    public MazeCell(int worldYPos, int worldXPos, int cellSize, int gridPosY, int gridPosX) {
        this.worldXPos = worldXPos;
        this.worldYPos = worldYPos;
        this.gridPosX = gridPosX;
        this.gridPosY = gridPosY;
        this.walls = 15; // 1111  North, West, South, East   1 meaning a wall is there, 0 meaning no wall
        this.doors = 0;  // 0000  1 meaning Door, 0 meaning no door
        this.cellSize = cellSize;
        this.visited = false;
    }

    public void drawWallsWithDoors(StructureWorldAccess world, String level) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        StructureTemplateManager structureTemplateManager = world.getServer().getStructureTemplateManager();
        Optional<StructureTemplate> optional;
        StructurePlacementData structurePlacementData = new StructurePlacementData();
        structurePlacementData.setIgnoreEntities(true);

        Identifier roomId;

        Random random = Random.create();
        int roomNumber = random.nextBetween(1,8);

        switch (this.walls) {
            case 0 -> { // 0000   ╬
                switch (this.doors) {
                    //NO DOORS
                    case 0 -> { // 0000
                        roomId = new Identifier(SPBRevamped.MOD_ID, level + "/aroom");
                        structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.NONE);
                    }

                    //ONE DOOR
                    case 8 -> { // 1000
                        roomId = new Identifier(SPBRevamped.MOD_ID, level + "/aroom_1door");
                        structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.NONE);
                    }
                    case 4 -> { // 0100
                        roomId = new Identifier(SPBRevamped.MOD_ID, level + "/aroom_1door");
                        structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.COUNTERCLOCKWISE_90);
                    }
                    case 2 -> { // 0010
                        roomId = new Identifier(SPBRevamped.MOD_ID, level + "/aroom_1door");
                        structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.CLOCKWISE_180);
                    }
                    case 1 -> { // 0001
                        roomId = new Identifier(SPBRevamped.MOD_ID, level + "/aroom_1door");
                        structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.CLOCKWISE_90);
                    }

                    //CORNER DOORS
                    case 9 -> { // 1001
                        roomId = new Identifier(SPBRevamped.MOD_ID, level + "/aroom_cornerdoor");
                        structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.NONE);
                    }
                    case 3 -> { // 0011
                        roomId = new Identifier(SPBRevamped.MOD_ID, level + "/aroom_cornerdoor");
                        structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.CLOCKWISE_90);
                    }
                    case 6 -> { // 0110
                        roomId = new Identifier(SPBRevamped.MOD_ID, level + "/aroom_cornerdoor");
                        structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.CLOCKWISE_180);
                    }
                    case 12 -> { // 1100
                        roomId = new Identifier(SPBRevamped.MOD_ID, level + "/aroom_cornerdoor");
                        structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.COUNTERCLOCKWISE_90);
                    }

                    //HALLWAY DOORS
                    case 10 -> { // 1010
                        roomId = new Identifier(SPBRevamped.MOD_ID, level + "/aroom_halldoor");
                        structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.NONE);
                    }
                    case 5 -> { // 0101
                        roomId = new Identifier(SPBRevamped.MOD_ID, level + "/aroom_halldoor");
                        structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.CLOCKWISE_90);
                    }

                    //CORNER DOORS
                    case 13 -> { // 1101
                        roomId = new Identifier(SPBRevamped.MOD_ID, level + "/aroom_3door");
                        structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.NONE);
                    }
                    case 11 -> { // 1011
                        roomId = new Identifier(SPBRevamped.MOD_ID, level + "/aroom_3door");
                        structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.CLOCKWISE_90);
                    }
                    case 7 -> { // 0111
                        roomId = new Identifier(SPBRevamped.MOD_ID, level + "/aroom_3door");
                        structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.CLOCKWISE_180);
                    }
                    case 14 -> { // 1110
                        roomId = new Identifier(SPBRevamped.MOD_ID, level + "/aroom_3door");
                        structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.COUNTERCLOCKWISE_90);
                    }

                    //ALL FOUR DOORS
                    default-> { // 1111
                        roomId = new Identifier(SPBRevamped.MOD_ID, level + "/aroom_4door");
                        structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.NONE);
                    }
                }
            }

            //3 North
            //2 West
            //1 South
            //0 East

            case 8 -> { // 1000   ╦
                this.doors = MathStuff.swapBits(this.doors, 1, 3);
                this.doors = MathStuff.swapBits(this.doors, 0, 2);
                roomId = threeWayDoor(level);
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.CLOCKWISE_180);
            }

            /*
            N E
            W N
            S W
            E S

            1 0
            0 1
            1 0
            0 1
            1111 -> 0011
            1100
            1111
             */

            case 4 -> { // 0100   ╠
                //NWSE -> SENW
                this.doors >>= 1;
                roomId = new Identifier(SPBRevamped.MOD_ID, level + "/broom" + roomNumber);
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.CLOCKWISE_90);
            }

            case 2 -> { // 0010   ╩
                roomId = new Identifier(SPBRevamped.MOD_ID, level + "/broom" + roomNumber);
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.NONE);
            }

            case 1 -> { // 0001   ╣
                roomId = new Identifier(SPBRevamped.MOD_ID, level + "/broom" + roomNumber);
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.COUNTERCLOCKWISE_90);
            }

            case 12 -> { // 1100   ╔
                roomId = new Identifier(SPBRevamped.MOD_ID, level + "/croom" + roomNumber);
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.CLOCKWISE_90);
            }

            case 10 -> { // 1010   ═
                roomId = new Identifier(SPBRevamped.MOD_ID, level + "/droom" + roomNumber);
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.CLOCKWISE_90);
            }

            case 9 -> { // 1001   ╗
                roomId = new Identifier(SPBRevamped.MOD_ID, level + "/croom" + roomNumber);
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.CLOCKWISE_180);
            }

            case 6 -> { // 0110   ╚
                roomId = new Identifier(SPBRevamped.MOD_ID, level + "/croom" + roomNumber);
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.NONE);
            }

            case 5 -> { // 0101   ║
                roomId = new Identifier(SPBRevamped.MOD_ID, level + "/droom" + roomNumber);
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.NONE);
            }

            case 3 -> { // 0011   ╝
                roomId = new Identifier(SPBRevamped.MOD_ID, level + "/croom" + roomNumber);
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.COUNTERCLOCKWISE_90);
            }

            case 14 -> { // 1110   ╞
                roomId = new Identifier(SPBRevamped.MOD_ID, level + "/eroom" + roomNumber);
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.CLOCKWISE_90);
            }

            case 7 -> { // 0111   ╨
                roomId = new Identifier(SPBRevamped.MOD_ID, level + "/eroom" + roomNumber);
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.NONE);
            }

            case 11 -> { // 1011   ╡
                roomId = new Identifier(SPBRevamped.MOD_ID, level + "/eroom" + roomNumber);
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.COUNTERCLOCKWISE_90);
            }

            case 13 -> { // 1101   ╥
                roomId = new Identifier(SPBRevamped.MOD_ID, level + "/eroom" + roomNumber);
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.CLOCKWISE_180);
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

    public void drawWalls(StructureWorldAccess world, String level) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        StructureTemplateManager structureTemplateManager = world.getServer().getStructureTemplateManager();
        Optional<StructureTemplate> optional;
        StructurePlacementData structurePlacementData = new StructurePlacementData();
        structurePlacementData.setIgnoreEntities(true);

        Identifier roomId;

        Random random = Random.create();
        int roomNumber = random.nextBetween(1,8);

        switch (this.walls) {
            case 0 -> { // 0000   ╬
                roomId = new Identifier(SPBRevamped.MOD_ID, level + "/aroom" + roomNumber);
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.NONE);
            }

            case 8 -> { // 1000   ╦
                roomId = new Identifier(SPBRevamped.MOD_ID, level + "/broom" + roomNumber);
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.CLOCKWISE_180);
            }

            case 4 -> { // 0100   ╠
                roomId = new Identifier(SPBRevamped.MOD_ID, level + "/broom" + roomNumber);
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.CLOCKWISE_90);
            }

            case 2 -> { // 0010   ╩
                roomId = new Identifier(SPBRevamped.MOD_ID, level + "/broom" + roomNumber);
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.NONE);
            }

            case 1 -> { // 0001   ╣
                roomId = new Identifier(SPBRevamped.MOD_ID, level + "/broom" + roomNumber);
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.COUNTERCLOCKWISE_90);
            }

            case 12 -> { // 1100   ╔
                roomId = new Identifier(SPBRevamped.MOD_ID, level + "/croom" + roomNumber);
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.CLOCKWISE_90);
            }

            case 10 -> { // 1010   ═
                roomId = new Identifier(SPBRevamped.MOD_ID, level + "/droom" + roomNumber);
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.CLOCKWISE_90);
            }

            case 9 -> { // 1001   ╗
                roomId = new Identifier(SPBRevamped.MOD_ID, level + "/croom" + roomNumber);
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.CLOCKWISE_180);
            }

            case 6 -> { // 0110   ╚
                roomId = new Identifier(SPBRevamped.MOD_ID, level + "/croom" + roomNumber);
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.NONE);
            }

            case 5 -> { // 0101   ║
                roomId = new Identifier(SPBRevamped.MOD_ID, level + "/droom" + roomNumber);
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.NONE);
            }

            case 3 -> { // 0011   ╝
                roomId = new Identifier(SPBRevamped.MOD_ID, level + "/croom" + roomNumber);
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.COUNTERCLOCKWISE_90);
            }

            case 14 -> { // 1110   ╞
                roomId = new Identifier(SPBRevamped.MOD_ID, level + "/eroom" + roomNumber);
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.CLOCKWISE_90);
            }

            case 7 -> { // 0111   ╨
                roomId = new Identifier(SPBRevamped.MOD_ID, level + "/eroom" + roomNumber);
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.NONE);
            }

            case 11 -> { // 1011   ╡
                roomId = new Identifier(SPBRevamped.MOD_ID, level + "/eroom" + roomNumber);
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.COUNTERCLOCKWISE_90);
            }

            case 13 -> { // 1101   ╥
                roomId = new Identifier(SPBRevamped.MOD_ID, level + "/eroom" + roomNumber);
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.CLOCKWISE_180);
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

    public Identifier threeWayDoor(String level){
        return switch (this.doors) {
            //NO DOORS
            case 0 -> // 0000
                new Identifier(SPBRevamped.MOD_ID, level + "/broom");

            //ONE DOOR
            case 1 -> // 0001
                new Identifier(SPBRevamped.MOD_ID, level + "/broom_1door_east");
            case 4 -> // 0100
                new Identifier(SPBRevamped.MOD_ID, level + "/broom_1door_west");
            case 8 -> // 1000
                new Identifier(SPBRevamped.MOD_ID, level + "/broom_1door_north");

            //CORNER DOORS
            case 9 -> // 1001
                new Identifier(SPBRevamped.MOD_ID, level + "/broom_2door_1");
            case 12 -> // 1100
                new Identifier(SPBRevamped.MOD_ID, level + "/broom_2door_2");

            //HALLWAY DOORS
            case 5 -> // 0101
                new Identifier(SPBRevamped.MOD_ID, level + "/broom_2door");

            //ALL THREE DOORS
            default -> // 1101
                new Identifier(SPBRevamped.MOD_ID, level + "/broom_3door");
        };
    }

    public Identifier cornerDoor(String level) {
        return switch (this.walls) {
            //NO DOORS
            case 0 -> // 0000
                new Identifier(SPBRevamped.MOD_ID, level + "/croom");

            //ONE DOOR
            case 1 -> // 0001
                new Identifier(SPBRevamped.MOD_ID, level + "/croom_1door_1");
            case 8 -> // 1000
                    new Identifier(SPBRevamped.MOD_ID, level + "/croom_1door_2");

            //BOTH DOORS
            default -> // 1001
                    new Identifier(SPBRevamped.MOD_ID, level + "/croom_2door");
        };
    }

    public Identifier HallwayDoor(String level) {
        return switch (this.walls) {
            //NO DOORS
            case 0 -> // 0000
                new Identifier(SPBRevamped.MOD_ID, level + "/droom");

            //ONE DOOR
            case 2 -> // 0010
                new Identifier(SPBRevamped.MOD_ID, level + "/droom_1door_1");
            case 8 -> // 1000
                    new Identifier(SPBRevamped.MOD_ID, level + "/droom_1door_2");

            //BOTH DOORS
            default -> // 1010
                    new Identifier(SPBRevamped.MOD_ID, level + "/droom_2door");
        };
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

    public void addNorthDoor() {
        int mask = 1 << 3; // 1000
        this.doors |= mask;
    }

    public void addWestDoor() {
        int mask = 1 << 2; // 0100
        this.doors |= mask;
    }

    public void addSouthDoor() {
        int mask = 1 << 1; // 0010
        this.doors |= mask;
    }

    public void addEastDoor() {
        int mask = 1; // 0001
        this.doors |= mask;
    }


    public void removeNorthWall() {
        int mask = 1 << 3; // 1000
        mask = ~mask; // 0111
        this.walls &= mask; // Remove North wall
    }

    public void removeWestWall() {
        int mask = 1 << 2; // 0100
        mask = ~mask; // 1011
        this.walls &= mask; // Remove West wall
    }

    public void removeSouthWall(){
        int mask = 1 << 1; // 0010
        mask = ~mask; // 1101
        this.walls &= mask; // Remove South wall
    }

    public void removeEastWall() {
        int mask = 1; // 0001
        mask = ~mask; // 1110
        this.walls &= mask; // Remove East wall
    }
}
