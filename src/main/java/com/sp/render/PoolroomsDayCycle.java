package com.sp.render;

import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.WorldEvents;
import foundry.veil.api.client.util.Easings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.joml.Vector3f;

public class PoolroomsDayCycle {
    public static float dayTime = 0.0f;
    private static float prevDayTime = 0.0f;
    private static float targetDayTime = 0.0f;
    private static long startTime;
    private static boolean done;

    static float noonAngle = 85.0f;
    static float sunSetAngle = 160.0f;
    static float midnightAngle = 70.0f;
    static float sunriseAngle = 20.0f;

    public static float advanceDayTime() {
        World world = MinecraftClient.getInstance().world;
        System.out.println(dayTime);

        if(world != null) {
            WorldEvents events = InitializeComponents.EVENTS.get(world);
            if(events.isSunsetTransition()) {
                if(targetDayTime == dayTime && !done) {
                    prevDayTime = dayTime;
                    targetDayTime = events.getCurrentPoolroomsTime();
                    startTime = System.currentTimeMillis();
                }

                float timer = (float) (System.currentTimeMillis() - startTime) / 8000;
                dayTime = MathHelper.lerp(Easings.Easing.easeInOutQuad.ease(timer), prevDayTime, targetDayTime);


                if(timer >= 1.0) {
                    dayTime = targetDayTime == 1.0f ? 0.0f : targetDayTime;
                    targetDayTime = dayTime;
                    done = true;
                }

                return dayTime;

            }
        }

        done = false;

        return dayTime;
    }

    public static float getSunAngle() {
//        dayTime = MathStuff.mod(RenderSystem.getShaderGameTime()*40, 1);
//        System.out.println(dayTime);

        if(dayTime <= 0.25) {
            return MathHelper.lerp((dayTime - 0.0f) / 0.25f, noonAngle, sunSetAngle);
        }
        else if(dayTime <= 0.35) {
            return MathHelper.lerp((dayTime-0.25f) / 0.15f, sunSetAngle, 220.0f);
        }
        else if(dayTime <= 0.5) {
            return MathHelper.lerp((dayTime-0.35f) / 0.15f, -40.0f, midnightAngle);
        }
        else if(dayTime <= 0.65) {
            return MathHelper.lerp((dayTime-0.5f) / 0.15f, midnightAngle, 220.0f);
        }
        else if(dayTime <= 0.75){
            return MathHelper.lerp((dayTime-0.65f) / 0.1f, -40.0f, sunriseAngle);
        }
        else if(dayTime <= 1.0){
            return MathHelper.lerp((dayTime-0.75f) / 0.25f, sunriseAngle, noonAngle);
        }

        return 90.0f;

    }

    public static Vector3f getLightColor(){
        Vector3f orangeLightColor = new Vector3f(0.9411f, 0.8156f, 0.5803f);
        Vector3f darkColor = new Vector3f(0.3f, 0.3f, 0.5803f);
        Vector3f whiteColor = new Vector3f(1.0f);

        if(dayTime <= 0.25) {
            return whiteColor.lerp(orangeLightColor, (dayTime - 0.0f) / 0.25f);
        }
        else if(dayTime <= 0.35) {
            return orangeLightColor.lerp(darkColor, (dayTime-0.25f) / 0.1f);
        }
        else if(dayTime <= 0.5) {
            return darkColor;
        }
        else if(dayTime <= 0.65) {
            return darkColor;
        }
        else if(dayTime <= 0.75){
            return darkColor.lerp(orangeLightColor, (dayTime-0.65f) / 0.1f);
        }
        else if(dayTime <= 1.0){
            return orangeLightColor.lerp(whiteColor, (dayTime-0.75f) / 0.25f);
        }


        return whiteColor;
    }

    public static float getDayTime(){
        return advanceDayTime();
    }


}
