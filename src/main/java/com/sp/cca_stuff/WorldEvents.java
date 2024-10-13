package com.sp.cca_stuff;

import com.sp.world.events.AbstractEvent;
import com.sp.world.events.EventVariableStorage;
import com.sp.world.events.level0.Level0Blackout;
import com.sp.world.levels.BackroomsLevels;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class WorldEvents implements AutoSyncedComponent, ServerTickingComponent {
    private World world;
    private boolean level0Blackout = false;
    private boolean inLevel0;
    private boolean inLevel1;
    private boolean inLevel2;
    private boolean inPoolRooms;

    private boolean prevLevel0Blackout;
    private boolean prevInLevel0;
    private boolean prevInLevel1;
    private boolean prevInLevel2;
    private boolean prevInPoolRooms;

    private static List<Supplier<AbstractEvent>> level0EventList;
    private List<AbstractEvent> level1EventList;
    private List<AbstractEvent> level2EventList;
    private List<AbstractEvent> poolroomsEventList;
    boolean registered;
    private AbstractEvent activeEvent;
    private boolean eventActive;
    private int ticks;
    private int times = 0;

    public WorldEvents(World world){
        this.world = world;
        this.level0Blackout = false;
        this.inLevel0 = false;
        this.inLevel1 = false;
        this.inLevel2 = false;
        this.inPoolRooms = false;
        registered = false;
        eventActive = false;
        ticks = 0;
    }

    public boolean isInPoolRooms() {
        return inPoolRooms;
    }

    public void setInPoolRooms(boolean inPoolRooms) {
        this.inPoolRooms = inPoolRooms;
    }

    public boolean isInLevel2() {
        return inLevel2;
    }

    public void setInLevel2(boolean inLevel2) {
        this.inLevel2 = inLevel2;
    }

    public boolean isInLevel1() {
        return inLevel1;
    }

    public void setInLevel1(boolean inLevel1) {
        this.inLevel1 = inLevel1;
    }

    public boolean isInLevel0() {
        return inLevel0;
    }

    public void setInLevel0(boolean inLevel0) {
        this.inLevel0 = inLevel0;
    }

    public boolean isLevel0Blackout() {
        return level0Blackout;
    }

    public void setLevel0Blackout(boolean level0Blackout) {
        this.level0Blackout = level0Blackout;
    }

    public void sync(){
        InitializeComponents.EVENTS.sync(this.world);
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        this.level0Blackout = tag.getBoolean("level0Blackout");
        this.inLevel0 = tag.getBoolean("inLevel0");
        this.inLevel1 = tag.getBoolean("inLevel1");
        this.inLevel2 = tag.getBoolean("inLevel2");
        this.inPoolRooms = tag.getBoolean("inPoolRooms");
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putBoolean("level0Blackout", this.level0Blackout);
        tag.putBoolean("inLevel0", this.inLevel0);
        tag.putBoolean("inLevel1", this.inLevel1);
        tag.putBoolean("inLevel2", this.inLevel2);
        tag.putBoolean("inPoolRooms", this.inPoolRooms);
    }

    @Override
    public void serverTick() {
        MinecraftClient client = MinecraftClient.getInstance();
        Random random = Random.create();
        getPrevSettings();

        if (!registered) {
            registerEvents();
            registered = true;
        }

        if(client.world != null && client.player != null) {
            //Only tick for the current Dimension instead of all of them
            if (this.world.getRegistryKey() == client.world.getRegistryKey()){
                ticks++;



                checkDimension(client);

                if (!eventActive) {
                    if (client.player.isSneaking()) {
                        if (!level0EventList.isEmpty()) {
                            int currentDimension = getCurrentDimension();

                            switch (currentDimension) {
                                case 1: {
                                    times++;
                                    int index = random.nextBetween(0, level0EventList.size() - 1);
                                    activeEvent = level0EventList.get(index).get();

                                    activeEvent.init(this.world);
                                    eventActive = true;
                                    ticks = 0;                                 //Reset Ticks
                                }
                                break;
                                case 2: {

                                }
                                break;
                                case 3: {

                                }
                                break;
                                case 4: {

                                }
                                break;
                                default:
                                    return;
                            }
                        }
                    }
                } else {
                    activeEvent.ticks();
                    if (activeEvent.duration() == ticks) {
                        activeEvent.reset(this.world);
                        eventActive = false;
                    }
                }
            }
        }
        shouldSync();
    }

    private void registerEvents(){
        level0EventList = new ArrayList<>();
        level0EventList.add(Level0Blackout::new);
    }

    private void shouldSync() {
        boolean sync = false;

        if(this.prevLevel0Blackout != this.level0Blackout){
            sync = true;
        }

        if(this.prevInLevel0 != this.inLevel0){
            sync = true;
        }

        if(this.prevInLevel1 != this.inLevel1){
            sync = true;
        }

        if(this.prevInLevel2 != this.inLevel2){
            sync = true;
        }

        if(this.prevInPoolRooms != this.inPoolRooms){
            sync = true;
        }

        if(sync){
            this.sync();
        }

    }

    private void getPrevSettings(){
        this.prevLevel0Blackout = this.level0Blackout;
        this.prevInLevel0 = this.inLevel0;
        this.prevInLevel1 = this.inLevel1;
        this.prevInLevel2 = this.inLevel2;
        this.prevInPoolRooms = this.inPoolRooms;
    }

    private int getCurrentDimension(){
        if(this.inLevel0){
            return 1;
        }
        else if(this.inLevel1){
            return 2;
        }
        else if(this.inLevel2){
            return 3;
        }
        else if(this.inPoolRooms){
            return 4;
        }
        else {
            return 0;
        }
    }

    private void checkDimension(MinecraftClient client){
        if(client.world != null) {
            if (client.world.getRegistryKey() == BackroomsLevels.LEVEL0_WORLD_KEY) {
                this.inLevel0 = true;
                this.inLevel1 = false;
                this.inLevel2 = false;
                this.inPoolRooms = false;
            }
            else if (client.world.getRegistryKey() == BackroomsLevels.LEVEL1_WORLD_KEY) {
                this.inLevel0 = false;
                this.inLevel1 = true;
                this.inLevel2 = false;
                this.inPoolRooms = false;
            }
            else if (client.world.getRegistryKey() == BackroomsLevels.LEVEL2_WORLD_KEY) {
                this.inLevel0 = false;
                this.inLevel1 = false;
                this.inLevel2 = true;
                this.inPoolRooms = false;
            }
            else if (client.world.getRegistryKey() == BackroomsLevels.POOLROOMS_WORLD_KEY) {
                this.inLevel0 = false;
                this.inLevel1 = false;
                this.inLevel2 = false;
                this.inPoolRooms = true;
            }
            else{
                this.inLevel0 = false;
                this.inLevel1 = false;
                this.inLevel2 = false;
                this.inPoolRooms = false;
            }
        }
    }
}
