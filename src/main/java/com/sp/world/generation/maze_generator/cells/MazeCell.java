package com.sp.world.generation.maze_generator.cells;

import com.sp.SPBRevamped;
import com.sp.init.BackroomsLevels;
import com.sp.util.MathStuff;
import com.sp.world.levels.BackroomsLevel;
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

    public void drawWallsWithDoors(StructureWorldAccess world, String levelId) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        StructureTemplateManager structureTemplateManager = world.getServer().getStructureTemplateManager();
        Optional<StructureTemplate> optional;
        StructurePlacementData structurePlacementData = new StructurePlacementData().setIgnoreEntities(true);

        Identifier roomId;

        Optional<BackroomsLevel> level = BackroomsLevels.getLevel(world.toServerWorld());

        if(level.isEmpty()) {
            throw new IllegalStateException("No Backrooms Level found for the world: " + world.getDimension() + ". Cannot draw walls.");
        }

        BackroomsLevel backroomsLevel = level.get();

        if(backroomsLevel.getRoomCount().isEmpty()) {
            throw new IllegalStateException("No Room Count found for the level: " + backroomsLevel.getLevelId() + ". Please state a room count in the level's super constructor.");
        }



        Random random = Random.create();
        BackroomsLevel.RoomCount roomCount = backroomsLevel.getRoomCount().get();
        int aroomNumber = random.nextBetween(1, roomCount.aRoomCount());
        int broomNumber = random.nextBetween(1, roomCount.bRoomCount());
        int croomNumber = random.nextBetween(1, roomCount.cRoomCount());
        int droomNumber = random.nextBetween(1, roomCount.dRoomCount());
        int eroomNumber = random.nextBetween(1, roomCount.eRoomCount());

        switch (this.walls) {
            case 0 -> { // 0000   ╬
                switch (this.doors) {
                    //NO DOORS
                    case 0 -> { // 0000
                        roomId = new Identifier(SPBRevamped.MOD_ID, levelId + "/aroom_" + aroomNumber);
                        structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.NONE);
                    }

                    //ONE DOOR
                    case 8 -> { // 1000
                        roomId = new Identifier(SPBRevamped.MOD_ID, levelId + "/aroom_1door_" + aroomNumber);
                        structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.NONE);
                    }
                    case 4 -> { // 0100
                        roomId = new Identifier(SPBRevamped.MOD_ID, levelId + "/aroom_1door_" + aroomNumber);
                        structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.COUNTERCLOCKWISE_90);
                    }
                    case 2 -> { // 0010
                        roomId = new Identifier(SPBRevamped.MOD_ID, levelId + "/aroom_1door_" + aroomNumber);
                        structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.CLOCKWISE_180);
                    }
                    case 1 -> { // 0001
                        roomId = new Identifier(SPBRevamped.MOD_ID, levelId + "/aroom_1door_" + aroomNumber);
                        structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.CLOCKWISE_90);
                    }

                    //CORNER DOORS
                    case 9 -> { // 1001
                        roomId = new Identifier(SPBRevamped.MOD_ID, levelId + "/aroom_cornerdoor_" + aroomNumber);
                        structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.NONE);
                    }
                    case 3 -> { // 0011
                        roomId = new Identifier(SPBRevamped.MOD_ID, levelId + "/aroom_cornerdoor_" + aroomNumber);
                        structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.CLOCKWISE_90);
                    }
                    case 6 -> { // 0110
                        roomId = new Identifier(SPBRevamped.MOD_ID, levelId + "/aroom_cornerdoor_" + aroomNumber);
                        structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.CLOCKWISE_180);
                    }
                    case 12 -> { // 1100
                        roomId = new Identifier(SPBRevamped.MOD_ID, levelId + "/aroom_cornerdoor_" + aroomNumber);
                        structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.COUNTERCLOCKWISE_90);
                    }

                    //HALLWAY DOORS
                    case 10 -> { // 1010
                        roomId = new Identifier(SPBRevamped.MOD_ID, levelId + "/aroom_halldoor_" + aroomNumber);
                        structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.NONE);
                    }
                    case 5 -> { // 0101
                        roomId = new Identifier(SPBRevamped.MOD_ID, levelId + "/aroom_halldoor_" + aroomNumber);
                        structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.CLOCKWISE_90);
                    }

                    //CORNER DOORS
                    case 13 -> { // 1101
                        roomId = new Identifier(SPBRevamped.MOD_ID, levelId + "/aroom_3door_" + aroomNumber);
                        structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.NONE);
                    }
                    case 11 -> { // 1011
                        roomId = new Identifier(SPBRevamped.MOD_ID, levelId + "/aroom_3door_" + aroomNumber);
                        structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.CLOCKWISE_90);
                    }
                    case 7 -> { // 0111
                        roomId = new Identifier(SPBRevamped.MOD_ID, levelId + "/aroom_3door_" + aroomNumber);
                        structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.CLOCKWISE_180);
                    }
                    case 14 -> { // 1110
                        roomId = new Identifier(SPBRevamped.MOD_ID, levelId + "/aroom_3door_" + aroomNumber);
                        structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.COUNTERCLOCKWISE_90);
                    }

                    //ALL FOUR DOORS
                    default-> { // 1111
                        roomId = new Identifier(SPBRevamped.MOD_ID, levelId + "/aroom_4door_" + aroomNumber);
                        structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.NONE);
                    }
                }
            }

            case 8 -> { // 1000   ╦
                this.doors = MathStuff.rotateBits(this.doors, 2);
                roomId = threeWayDoor(levelId, broomNumber);
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.CLOCKWISE_180);
            }

            case 4 -> { // 0100   ╠
                this.doors = MathStuff.rotateBits(this.doors, 1);
                roomId = threeWayDoor(levelId, broomNumber);
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.CLOCKWISE_90);
            }

            case 2 -> { // 0010   ╩
                roomId = threeWayDoor(levelId, broomNumber);
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.NONE);
            }

            case 1 -> { // 0001   ╣
                this.doors = MathStuff.rotateBits(this.doors, 3);
                roomId = threeWayDoor(levelId, broomNumber);
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.COUNTERCLOCKWISE_90);
            }

            case 12 -> { // 1100   ╔
                this.doors = MathStuff.rotateBits(this.doors, 1);
                roomId = cornerDoor(levelId, croomNumber);
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.CLOCKWISE_90);
            }

            case 10 -> { // 1010   ═
                this.doors = MathStuff.rotateBits(this.doors, 1);
                roomId = hallwayDoor(levelId, droomNumber);
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.CLOCKWISE_90);
            }

            case 9 -> { // 1001   ╗
                this.doors = MathStuff.rotateBits(this.doors, 2);
                roomId = cornerDoor(levelId, croomNumber);
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.CLOCKWISE_180);
            }

            case 6 -> { // 0110   ╚
                roomId = cornerDoor(levelId, croomNumber);
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.NONE);
            }

            case 5 -> { // 0101   ║
                roomId = hallwayDoor(levelId, droomNumber);
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.NONE);
            }

            case 3 -> { // 0011   ╝
                this.doors = MathStuff.rotateBits(this.doors, 3);
                roomId = cornerDoor(levelId, croomNumber);
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.COUNTERCLOCKWISE_90);
            }

            case 14 -> { // 1110   ╞
                this.doors = MathStuff.rotateBits(this.doors, 1);
                roomId = singleDoor(levelId, eroomNumber);
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.CLOCKWISE_90);
            }

            case 7 -> { // 0111   ╨
                roomId = singleDoor(levelId, eroomNumber);
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.NONE);
            }

            case 11 -> { // 1011   ╡
                this.doors = MathStuff.rotateBits(this.doors, 3);
                roomId = singleDoor(levelId, eroomNumber);
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.COUNTERCLOCKWISE_90);
            }

            case 13 -> { // 1101   ╥
                this.doors = MathStuff.rotateBits(this.doors, 2);
                roomId = singleDoor(levelId, eroomNumber);
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.CLOCKWISE_180);
            }

            default -> // Shouldn't be possible, but just in case
                    roomId = new Identifier(SPBRevamped.MOD_ID, levelId + "/aroom_" + aroomNumber);
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

    public void drawWalls(StructureWorldAccess world, String levelId) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        StructureTemplateManager structureTemplateManager = world.getServer().getStructureTemplateManager();
        Optional<StructureTemplate> optional;
        StructurePlacementData structurePlacementData = new StructurePlacementData().setIgnoreEntities(true);

        Identifier roomId;

        Optional<BackroomsLevel> level = BackroomsLevels.getLevel(world.toServerWorld());

        if(level.isEmpty()) {
            throw new IllegalStateException("No Backrooms Level found for the world: " + world.getDimension() + ". Cannot draw walls.");
        }

        BackroomsLevel backroomsLevel = level.get();

        if(backroomsLevel.getRoomCount().isEmpty()) {
            throw new IllegalStateException("No Room Count found for the level: " + backroomsLevel.getLevelId() + ". Please state a room count in the level's super constructor.");
        }



        Random random = Random.create();
        BackroomsLevel.RoomCount roomCount = backroomsLevel.getRoomCount().get();
        int aroomNumber = random.nextBetween(1, roomCount.aRoomCount());
        int broomNumber = random.nextBetween(1, roomCount.bRoomCount());
        int croomNumber = random.nextBetween(1, roomCount.cRoomCount());
        int droomNumber = random.nextBetween(1, roomCount.dRoomCount());
        int eroomNumber = random.nextBetween(1, roomCount.eRoomCount());
        switch (this.walls) {
            case 0 -> { // 0000   ╬
                roomId = new Identifier(SPBRevamped.MOD_ID, levelId + "/aroom_" + aroomNumber);
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.NONE);
            }

            case 8 -> { // 1000   ╦
                roomId = new Identifier(SPBRevamped.MOD_ID, levelId + "/broom_" + broomNumber);
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.CLOCKWISE_180);
            }

            case 4 -> { // 0100   ╠
                roomId = new Identifier(SPBRevamped.MOD_ID, levelId + "/broom_" + broomNumber);
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.CLOCKWISE_90);
            }

            case 2 -> { // 0010   ╩
                roomId = new Identifier(SPBRevamped.MOD_ID, levelId + "/broom_" + broomNumber);
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.NONE);
            }

            case 1 -> { // 0001   ╣
                roomId = new Identifier(SPBRevamped.MOD_ID, levelId + "/broom_" + broomNumber);
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.COUNTERCLOCKWISE_90);
            }

            case 12 -> { // 1100   ╔
                roomId = new Identifier(SPBRevamped.MOD_ID, levelId + "/croom_" + croomNumber);
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.CLOCKWISE_90);
            }

            case 10 -> { // 1010   ═
                roomId = new Identifier(SPBRevamped.MOD_ID, levelId + "/droom_" + droomNumber);
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.CLOCKWISE_90);
            }

            case 9 -> { // 1001   ╗
                roomId = new Identifier(SPBRevamped.MOD_ID, levelId + "/croom_" + croomNumber);
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.CLOCKWISE_180);
            }

            case 6 -> { // 0110   ╚
                roomId = new Identifier(SPBRevamped.MOD_ID, levelId + "/croom_" + croomNumber);
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.NONE);
            }

            case 5 -> { // 0101   ║
                roomId = new Identifier(SPBRevamped.MOD_ID, levelId + "/droom_" + droomNumber);
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.NONE);
            }

            case 3 -> { // 0011   ╝
                roomId = new Identifier(SPBRevamped.MOD_ID, levelId + "/croom_" + croomNumber);
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.COUNTERCLOCKWISE_90);
            }

            case 14 -> { // 1110   ╞
                roomId = new Identifier(SPBRevamped.MOD_ID, levelId + "/eroom_" + eroomNumber);
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.CLOCKWISE_90);
            }

            case 7 -> { // 0111   ╨
                roomId = new Identifier(SPBRevamped.MOD_ID, levelId + "/eroom_" + eroomNumber);
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.NONE);
            }

            case 11 -> { // 1011   ╡
                roomId = new Identifier(SPBRevamped.MOD_ID, levelId + "/eroom_" + eroomNumber);
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.COUNTERCLOCKWISE_90);
            }

            case 13 -> { // 1101   ╥
                roomId = new Identifier(SPBRevamped.MOD_ID, levelId + "/eroom_" + eroomNumber);
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.CLOCKWISE_180);
            }

            default -> // Shouldn't be possible, but just in case
                    roomId = new Identifier(SPBRevamped.MOD_ID, levelId + "/aroom_" + aroomNumber);
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

    private Identifier threeWayDoor(String level, int broomNumber) {
        return switch (this.doors) {
            //NO DOORS
            case 0 -> // 0000
                new Identifier(SPBRevamped.MOD_ID, level + "/broom_" + broomNumber);

            //ONE DOOR
            case 1 -> // 0001
                new Identifier(SPBRevamped.MOD_ID, level + "/broom_1door_east_" + broomNumber);
            case 4 -> // 0100
                new Identifier(SPBRevamped.MOD_ID, level + "/broom_1door_west_" + broomNumber);
            case 8 -> // 1000
                new Identifier(SPBRevamped.MOD_ID, level + "/broom_1door_north_" + broomNumber);

            //CORNER DOORS
            case 9 -> // 1001
                new Identifier(SPBRevamped.MOD_ID, level + "/broom_2door_1_" + broomNumber);
            case 12 -> // 1100
                new Identifier(SPBRevamped.MOD_ID, level + "/broom_2door_2_" + broomNumber);

            //HALLWAY DOORS
            case 5 -> // 0101
                new Identifier(SPBRevamped.MOD_ID, level + "/broom_2door_" + broomNumber);

            //ALL THREE DOORS
            default -> // 1101
                new Identifier(SPBRevamped.MOD_ID, level + "/broom_3door_" + broomNumber);
        };
    }

    private Identifier cornerDoor(String level, int croomNumber) {
        return switch (this.doors) {
            //NO DOORS
            case 0 -> // 0000
                new Identifier(SPBRevamped.MOD_ID, level + "/croom_" + croomNumber);

            //ONE DOOR
            case 1 -> // 0001
                new Identifier(SPBRevamped.MOD_ID, level + "/croom_1door_1_" + croomNumber);
            case 8 -> // 1000
                    new Identifier(SPBRevamped.MOD_ID, level + "/croom_1door_2_" + croomNumber);

            //BOTH DOORS
            default -> // 1001
                    new Identifier(SPBRevamped.MOD_ID, level + "/croom_2door_" + croomNumber);
        };
    }

    private Identifier hallwayDoor(String level, int droomNumber) {
        return switch (this.doors) {
            //NO DOORS
            case 0 -> // 0000
                new Identifier(SPBRevamped.MOD_ID, level + "/droom_" + droomNumber);

            //ONE DOOR
            case 2 -> // 0010
                new Identifier(SPBRevamped.MOD_ID, level + "/droom_1door_1_" + droomNumber);
            case 8 -> // 1000
                    new Identifier(SPBRevamped.MOD_ID, level + "/droom_1door_2_" + droomNumber);

            //BOTH DOORS
            default -> // 1010
                    new Identifier(SPBRevamped.MOD_ID, level + "/droom_2door_" + droomNumber);
        };
    }

    private Identifier singleDoor(String level, int eroomNumber) {
        if(this.doors == 8){
            return new Identifier(SPBRevamped.MOD_ID, level + "/eroom_door_" + eroomNumber);
        }

        return new Identifier(SPBRevamped.MOD_ID, level + "/eroom_" + eroomNumber);
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
