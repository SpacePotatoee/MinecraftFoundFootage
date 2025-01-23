package com.sp.world.generation.maze_generator;

import com.sp.SPBRevamped;
import net.minecraft.block.BlockState;
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

public class CellWDoor {
    private final int y;
    private final int x;

    private final int gridPosX;
    private final int gridPosY;

    private final int cellSize;

    private boolean north;
    private boolean west;
    private boolean south;
    private boolean east;

    private boolean northDoor;
    private boolean westDoor;
    private boolean southDoor;
    private boolean eastDoor;

    private boolean visited;
    String type;

    BlockState blockState;

    public CellWDoor(int y, int x, int cellSize, BlockState blockState, int gridPosY, int gridPosX){
        this.x = x;
        this.y = y;
        this.gridPosX = gridPosX;
        this.gridPosY = gridPosY;
        this.cellSize = cellSize;
        this.north = true;
        this.west = true;
        this.south = true;
        this.east = true;

        this.northDoor = false;
        this.westDoor = false;
        this.southDoor = false;
        this.eastDoor = false;

        this.blockState = blockState;
        this.visited = false;
    }




    public void drawWalls(StructureWorldAccess world, String level, boolean sky){
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        MinecraftServer server = world.getServer();
        StructureTemplateManager structureTemplateManager = server.getStructureTemplateManager();
        Optional<StructureTemplate> optional;
        StructurePlacementData structurePlacementData = new StructurePlacementData();


        Identifier roomId = new Identifier(SPBRevamped.MOD_ID, level + "/aroom");;

        Random random = Random.create();
        boolean buffer;

        String shouldSky = "";
        if(!sky){
            shouldSky = "_light";
        }


        if(!this.north && !this.west && !this.south && !this.east){ // 4 way room
            this.type = "╬";
            structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.NONE).setIgnoreEntities(true);

            if(!this.northDoor && !this.westDoor && !this.southDoor && !this.eastDoor){ // 4 way no Doors
                roomId = new Identifier(SPBRevamped.MOD_ID, level + "/aroom" + shouldSky);
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.NONE).setIgnoreEntities(true);
            }

            //ONE DOOR
            else if(this.northDoor && !this.westDoor && !this.southDoor && !this.eastDoor){ // 4 way with North Door
                roomId = new Identifier(SPBRevamped.MOD_ID, level + "/aroom_1door" + shouldSky);
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.NONE).setIgnoreEntities(true);
            }
            else if(!this.northDoor && this.westDoor && !this.southDoor && !this.eastDoor){ // 4 way with West Door
                roomId = new Identifier(SPBRevamped.MOD_ID, level + "/aroom_1door" + shouldSky);
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.COUNTERCLOCKWISE_90).setIgnoreEntities(true);
            }
            else if(!this.northDoor && !this.westDoor && this.southDoor && !this.eastDoor){ // 4 way with South Door
                roomId = new Identifier(SPBRevamped.MOD_ID, level + "/aroom_1door" + shouldSky);
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.CLOCKWISE_180).setIgnoreEntities(true);
            }
            else if(!this.northDoor && !this.westDoor && !this.southDoor && this.eastDoor){ // 4 way with East Door
                roomId = new Identifier(SPBRevamped.MOD_ID, level + "/aroom_1door" + shouldSky);
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.CLOCKWISE_90).setIgnoreEntities(true);
            }

            //CORNER DOORS
            else if(this.northDoor && !this.westDoor && !this.southDoor && this.eastDoor){ // ╚
                roomId = new Identifier(SPBRevamped.MOD_ID, level + "/aroom_cornerdoor" + shouldSky);
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.NONE).setIgnoreEntities(true);
            }
            else if(!this.northDoor && !this.westDoor && this.southDoor && this.eastDoor){ // East and South Doors
                roomId = new Identifier(SPBRevamped.MOD_ID, level + "/aroom_cornerdoor" + shouldSky);
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.CLOCKWISE_90).setIgnoreEntities(true);
            }
            else if(!this.northDoor && this.westDoor && this.southDoor && !this.eastDoor){ // South and West Doors
                roomId = new Identifier(SPBRevamped.MOD_ID, level + "/aroom_cornerdoor" + shouldSky);
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.CLOCKWISE_180).setIgnoreEntities(true);
            }
            else if(this.northDoor && this.westDoor && !this.southDoor && !this.eastDoor){ // West and North
                roomId = new Identifier(SPBRevamped.MOD_ID, level + "/aroom_cornerdoor" + shouldSky);
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.COUNTERCLOCKWISE_90).setIgnoreEntities(true);
            }

            //HALLWAY DOORS
            else if(this.northDoor && !this.westDoor && this.southDoor && !this.eastDoor){
                roomId = new Identifier(SPBRevamped.MOD_ID, level + "/aroom_halldoor" + shouldSky);
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.NONE).setIgnoreEntities(true);
            }
            else if(!this.northDoor && this.westDoor && !this.southDoor && this.eastDoor){ // 4 way no Doors
                roomId = new Identifier(SPBRevamped.MOD_ID, level + "/aroom_halldoor" + shouldSky);
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.CLOCKWISE_90).setIgnoreEntities(true);
            }

            //THREE DOORS
            else if(this.northDoor && this.westDoor && !this.southDoor && this.eastDoor){
                roomId = new Identifier(SPBRevamped.MOD_ID, level + "/aroom_3door" + shouldSky);
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.NONE).setIgnoreEntities(true);
            }
            else if(this.northDoor && !this.westDoor && this.southDoor && this.eastDoor){
                roomId = new Identifier(SPBRevamped.MOD_ID, level + "/aroom_3door" + shouldSky);
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.CLOCKWISE_90).setIgnoreEntities(true);
            }
            else if(!this.northDoor && this.westDoor && this.southDoor && this.eastDoor){
                roomId = new Identifier(SPBRevamped.MOD_ID, level + "/aroom_3door" + shouldSky);
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.CLOCKWISE_180).setIgnoreEntities(true);
            }
            else if(this.northDoor && this.westDoor && this.southDoor && !this.eastDoor){
                roomId = new Identifier(SPBRevamped.MOD_ID, level + "/aroom_3door" + shouldSky);
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.COUNTERCLOCKWISE_90).setIgnoreEntities(true);
            }

            //ALL 4 DOORS
            else {
                roomId = new Identifier(SPBRevamped.MOD_ID, level + "/aroom_4door" + shouldSky);
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.NONE).setIgnoreEntities(true);
            }
        }

        else if(this.north && !this.west && !this.south && !this.east){
            this.type = "╦";
            this.north = !this.north;
            this.south = !this.south;
            this.northDoor = this.southDoor;
            buffer = this.westDoor;
            this.westDoor = this.eastDoor;
            this.eastDoor = buffer;
            this.southDoor = false;
            roomId = this.threeWayDoor(roomId, level, shouldSky);
            structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.CLOCKWISE_180).setIgnoreEntities(true);
        }

        else if(!this.north && this.west && !this.south && !this.east){
            this.type = "╠";
            this.west = !this.west;
            this.south = !this.south;
            this.westDoor = this.northDoor;
            this.northDoor = this.eastDoor;
            this.eastDoor = this.southDoor;
            this.southDoor = false;
            roomId = this.threeWayDoor(roomId, level, shouldSky);
            structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.CLOCKWISE_90).setIgnoreEntities(true);
        }

        else if(!this.north && !this.west && this.south && !this.east){
            this.type = "╩";
            roomId = this.threeWayDoor(roomId, level, shouldSky);
            structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.NONE).setIgnoreEntities(true);
        }

        else if(!this.north && !this.west && !this.south && this.east){
            this.type = "╣";
            this.east = !this.east;
            this.south = !this.south;
            this.eastDoor = this.northDoor;
            this.northDoor = this.westDoor;
            this.westDoor = this.southDoor;
            this.southDoor = false;
            roomId = this.threeWayDoor(roomId, level, shouldSky);
            structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.COUNTERCLOCKWISE_90).setIgnoreEntities(true);
        }

        else if(this.north && this.west && !this.south && !this.east){
            this.type = "╔";
            this.north = !this.north;
            this.west = !this.west;
            this.south = !this.south;
            this.northDoor = this.eastDoor;
            this.eastDoor = this.southDoor;
            this.westDoor = false;
            this.southDoor = false;
            roomId = this.cornerDoor(roomId, level, shouldSky);
            structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.CLOCKWISE_90).setIgnoreEntities(true);

        }

        else if(this.north && !this.west && this.south && !this.east){
            this.type = "═";
            this.north = !this.north;
            this.west = !this.west;
            this.south = !this.south;
            this.east = !this.east;
            this.northDoor = this.eastDoor;
            this.southDoor = this.westDoor;
            this.westDoor = false;
            this.eastDoor = false;
            roomId = this.HallwayDoor(roomId, level, shouldSky);
            structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.CLOCKWISE_90).setIgnoreEntities(true);
        }

        else if(this.north && !this.west && !this.south && this.east){
            this.type = "╗";
            this.north = !this.north;
            this.west = !this.west;
            this.south = !this.south;
            this.east = !this.east;
            this.northDoor = this.southDoor;
            this.eastDoor = this.westDoor;
            this.westDoor = false;
            this.southDoor = false;
            roomId = this.cornerDoor(roomId, level, shouldSky);
            structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.CLOCKWISE_180).setIgnoreEntities(true);
        }

        else if(!this.north && this.west && this.south && !this.east){
            this.type = "╚";
            roomId = this.cornerDoor(roomId, level, shouldSky);
            structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.NONE).setIgnoreEntities(true);
        }

        else if(!this.north && this.west && !this.south && this.east){
            this.type = "║";
            roomId = this.HallwayDoor(roomId, level, shouldSky);
            structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.NONE).setIgnoreEntities(true);
        }

        else if(!this.north && !this.west && this.south && this.east){
            this.type = "╝";
            this.west = !this.west;
            this.south = !this.south;
            this.east = !this.east;
            this.eastDoor = this.northDoor;
            this.northDoor = this.westDoor;
            this.westDoor = false;
            this.southDoor = false;
            roomId = this.cornerDoor(roomId, level, shouldSky);
            structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.COUNTERCLOCKWISE_90).setIgnoreEntities(true);
        }

        else if(this.north && this.west && this.south && !this.east){
            this.type = "╞";

            if(this.eastDoor){
                roomId = new Identifier(SPBRevamped.MOD_ID, level + "/eroom_door" + shouldSky);
            }
            else{
                roomId = new Identifier(SPBRevamped.MOD_ID, level + "/eroom" + shouldSky);
            }
            structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.CLOCKWISE_90).setIgnoreEntities(true);
        }

        else if(!this.north && this.west && this.south && this.east){
            this.type = "╨";

            if(this.northDoor){
                roomId = new Identifier(SPBRevamped.MOD_ID, level + "/eroom_door" + shouldSky);
            }
            else{
                roomId = new Identifier(SPBRevamped.MOD_ID, level + "/eroom" + shouldSky);
            }
            structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.NONE).setIgnoreEntities(true);
        }

        else if(this.north && !this.west && this.south && this.east){
            this.type = "╡";

            if(this.westDoor){
                roomId = new Identifier(SPBRevamped.MOD_ID, level + "/eroom_door" + shouldSky);
            }
            else{
                roomId = new Identifier(SPBRevamped.MOD_ID, level + "/eroom" + shouldSky);
            }
            structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.COUNTERCLOCKWISE_90).setIgnoreEntities(true);
        }

        else if(this.north && this.west && !this.south && this.east){
            this.type = "╥";

            if(this.southDoor){
                roomId = new Identifier(SPBRevamped.MOD_ID, level + "/eroom_door" + shouldSky);
            }
            else{
                roomId = new Identifier(SPBRevamped.MOD_ID, level + "/eroom" + shouldSky);
            }
            structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.CLOCKWISE_180).setIgnoreEntities(true);
        }
        else{
            roomId = new Identifier(SPBRevamped.MOD_ID, level + "/aroom" + shouldSky);
        }

        optional = structureTemplateManager.getTemplate(roomId);

        switch (structurePlacementData.getRotation()){
            case NONE -> optional.ifPresent(structureTemplate ->
                    structureTemplate.place(
                            world,
                            mutable.set(this.getX(), 20, this.getY()),
                            mutable,
                            structurePlacementData, random, 2));
            case CLOCKWISE_90 -> optional.ifPresent(structureTemplate ->
                    structureTemplate.place(
                            world,
                            mutable.set(this.getX() + (this.cellSize - 1), 20, this.getY()),
                            mutable,
                            structurePlacementData, random, 2));
            case COUNTERCLOCKWISE_90 -> optional.ifPresent(structureTemplate ->
                    structureTemplate.place(
                            world,
                            mutable.set(this.getX(), 20, this.getY() + (this.cellSize - 1)),
                            mutable,
                            structurePlacementData, random, 2));
            case CLOCKWISE_180 -> optional.ifPresent(structureTemplate ->
                    structureTemplate.place(
                            world,
                            mutable.set(this.getX() + (this.cellSize - 1), 20, this.getY() + (this.cellSize - 1)),
                            mutable,
                            structurePlacementData, random, 2));
        }



    }


    public Identifier threeWayDoor(Identifier roomId, String level, String shouldSky){

        if(!this.northDoor && !this.westDoor && !this.southDoor && !this.eastDoor){ //No Doors
            roomId = new Identifier(SPBRevamped.MOD_ID, level + "/broom" + shouldSky);
        }

        //ONE DOOR
        else if(!this.northDoor && !this.westDoor && !this.southDoor && this.eastDoor){ // East Door
            roomId = new Identifier(SPBRevamped.MOD_ID, level + "/broom_1door_east" + shouldSky);
        }
        else if(!this.northDoor && this.westDoor && !this.southDoor && !this.eastDoor){ // West Door
            roomId = new Identifier(SPBRevamped.MOD_ID, level + "/broom_1door_west" + shouldSky);
        }
        else if(this.northDoor && !this.westDoor && !this.southDoor && !this.eastDoor){ // North Door
            roomId = new Identifier(SPBRevamped.MOD_ID, level + "/broom_1door_north" + shouldSky);
        }

        //CORNER DOORS
        else if(this.northDoor && !this.westDoor && !this.southDoor && this.eastDoor){ // ╚
            roomId = new Identifier(SPBRevamped.MOD_ID, level + "/broom_2door_1" + shouldSky);
        }
        else if(this.northDoor && this.westDoor && !this.southDoor && !this.eastDoor){ // ╝
            roomId = new Identifier(SPBRevamped.MOD_ID, level + "/broom_2door_2" + shouldSky);
        }

        //HALLWAY DOORS
        else if(!this.northDoor && this.westDoor && !this.southDoor && this.eastDoor){
            roomId = new Identifier(SPBRevamped.MOD_ID, level + "/broom_2door" + shouldSky);
        }

        //ALL 3 DOORS
        else if(this.northDoor && this.westDoor && !this.southDoor && this.eastDoor){
            roomId = new Identifier(SPBRevamped.MOD_ID, level + "/broom_3door" + shouldSky);
        }
        return roomId;
    }


    public Identifier cornerDoor(Identifier roomId, String level, String shouldSky){

        if(!this.northDoor && !this.westDoor && !this.southDoor && !this.eastDoor){ //No Doors
            roomId = new Identifier(SPBRevamped.MOD_ID, level + "/croom" + shouldSky);
        }

        //ONE DOOR
        else if(!this.northDoor && !this.westDoor && !this.southDoor && this.eastDoor){ // East Door
            roomId = new Identifier(SPBRevamped.MOD_ID, level + "/croom_1door_1" + shouldSky);
        }
        else if(this.northDoor && !this.westDoor && !this.southDoor && !this.eastDoor){ // North Door
            roomId = new Identifier(SPBRevamped.MOD_ID, level + "/croom_1door_2" + shouldSky);
        }

        //ALL 4 DOORS
        else if(this.northDoor && !this.westDoor && !this.southDoor && this.eastDoor){
            roomId = new Identifier(SPBRevamped.MOD_ID, level + "/croom_2door" + shouldSky);
        }
        return roomId;
    }

    public Identifier HallwayDoor(Identifier roomId, String level, String shouldSky){

        if(!this.northDoor && !this.westDoor && !this.southDoor && !this.eastDoor){ //No Doors
            roomId = new Identifier(SPBRevamped.MOD_ID, level + "/droom" + shouldSky);
        }

        //ONE DOOR
        else if(!this.northDoor && !this.westDoor && this.southDoor && !this.eastDoor){ // South Door
            roomId = new Identifier(SPBRevamped.MOD_ID, level + "/droom_1door_1" + shouldSky);
        }
        else if(this.northDoor && !this.westDoor && !this.southDoor && !this.eastDoor){ // North Door
            roomId = new Identifier(SPBRevamped.MOD_ID, level + "/droom_1door_2" + shouldSky);
        }

        //ALL 4 DOORS
        else if(this.northDoor && !this.westDoor && this.southDoor && !this.eastDoor){
            roomId = new Identifier(SPBRevamped.MOD_ID, level + "/droom_2door" + shouldSky);
        }
        return roomId;
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

    public void setEastDoor(boolean eastDoor) {
        this.eastDoor = eastDoor;
    }

    public void setSouthDoor(boolean southDoor) {
        this.southDoor = southDoor;
    }

    public void setWestDoor(boolean westDoor) {
        this.westDoor = westDoor;
    }

    public void setNorthDoor(boolean northDoor) {
        this.northDoor = northDoor;
    }

}