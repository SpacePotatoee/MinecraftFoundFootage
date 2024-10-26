package com.sp.util;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class MathStuff {

    /**
     * Repeating function between 0 - y
     */
    public static float mod(float x, float y){
        return x - y * (float) Math.floor((double) x/y);
    }

    public static double mod(double x, double y){
        return x - y * Math.floor(x/y);
    }

    /**
     * Framerate Independent lerp
     * <a href="https://www.youtube.com/watch?v=LSNQuFEDOyQ">Learned from here</a>
     */
    public static float Lerp(float source, float destination, float smoothingFactor, float tickDelta){
        return MathHelper.lerp(1.0f - (float) Math.pow(smoothingFactor, tickDelta), source, destination);
    }

    public static Vec2f get2DRelativeRotation(Vec3d vec3d, float degrees){
        float x = (float) vec3d.getX();
        float y = (float) vec3d.getZ();

        double radians = Math.toRadians(degrees);
        float cos = (float) Math.cos(radians);
        float sin = (float) Math.sin(radians);

        return new Vec2f((x * cos) - (y * sin), (y * cos) + (x * sin));
    }

}
