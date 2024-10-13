package com.sp.world.events;

import com.sp.world.events.level0.Level0Blackout;
import net.minecraft.util.math.random.Random;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class EventHandler {
    private static int tick;
    private static boolean eventActive = false;
    private static AbstractEvent activeEvent;
    private static List<Supplier<AbstractEvent>> level0EventList;
//    private List<AbstractEvent> level1EventList = new ArrayList<>();
//    private List<AbstractEvent> level2EventList = new ArrayList<>();
//    private List<AbstractEvent> poolroomsEventList = new ArrayList<>();

    public static void ticks(){
        tick++;
        Random random = Random.create();
        if(!eventActive) {
            if (EventVariableStorage.inLevel0) {
                System.out.println("TICKED1");
                resetTick();
                if (!level0EventList.isEmpty()) {
                    int index = random.nextBetween(0, level0EventList.size() - 1);
                    activeEvent = level0EventList.get(index).get();

//                    activeEvent.init();
                    eventActive = true;
                }
            }
        }

//        if(activeEvent != null) {
//            if (tick == activeEvent.duration()) {
//                activeEvent.reset();
//                eventActive = false;
//            }
//        }

    }

    public static void clearEvents(){
        level0EventList = null;
        activeEvent = null;
        tick = 0;
    }

    private static void resetTick() {
        tick = 0;
    }

    public static void registerEvents(){
        level0EventList = new ArrayList<>();
        level0EventList.add(Level0Blackout::new);
    }

}
