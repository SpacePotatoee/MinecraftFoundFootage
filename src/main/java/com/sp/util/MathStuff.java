package com.sp.util;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

import java.util.Optional;

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
    public static float Lerp(float source, float destination, float smoothingFactor, float delta){
        return MathHelper.lerp(1.0f - (float) Math.pow(smoothingFactor, delta), source, destination);
    }

    public static int millisecToTick(long l){
        return (int) (l * 0.02);
    }

    public static float randomFloat(float min, float max, Random random){
        return min + random.nextFloat() * (max - min);
    }

    public static Vec2f get2DRelativeRotation(Vec3d vec3d, float degrees){
        float x = (float) vec3d.getX();
        float y = (float) vec3d.getZ();

        float radians = (float) Math.toRadians(degrees);
        float cos = MathHelper.cos(radians);
        float sin = MathHelper.sin(radians);

        return new Vec2f((x * cos) - (y * sin), (y * cos) + (x * sin));
    }

    /**
     * Swaps the bits in the given positions. <a href="https://www.geeksforgeeks.com/dsa/how-to-swap-two-bits-in-a-given-integer/">Learned from here</a>
     * @param byteIn The byte whose bits will be swapped
     * @param pos1 The first bit to swap
     * @param pos2 The second bit to swap
     * @return The original byte but with the bits in the two positions swapped
     */
    public static int swapBits(int byteIn, int pos1, int pos2) {
        // These comments are just to help me (and maybe you) learn
        // Example: 0110 Swap bits 0 and 2
        int bit1 = (byteIn >> pos1) & 1;  // (0110 >> 0) = 0110 & 0001 = 0 (Doing this tells us bit 0 is 0)
        int bit2 = (byteIn >> pos2) & 1;  // (0110 >> 2) = 0001 & 0001 = 1 (Doing this tells us bit 2 is 1)

        int xorResult = bit1 ^ bit2; // (0000 ^ 0001) = 0001 = 1

        int swapBits = byteIn ^ (xorResult << pos1) ^ (xorResult << pos2);  // (0110) ^ (0001 << 0) ^ (0001 << 2)
                                                                            // (0110) ^ (0001) ^ (0100)
                                                                            // (0111) ^ (0100)
                                                                            // (0011)
        return swapBits;
    }

    /**
     * @param byteIn The byte to shift
     * @param shift The amount to shift the byte by
     * @return The original byte but shifted by the shift amount. Any numbers that would have been lost are wrapped to the end
     */
    public static int rotateBits(int byteIn, int shift) {
        //Example 0110 shift by 3  Expected result: 1100
        shift = 4 - (int) mod(shift, 4);
        int mask = (byteIn << shift); // 0110 << 1 = 1100  This tells us what the first bits are and puts them at the end

        int result = byteIn >> shift; // 0110 >> 1 = 0011

        return result ^ mask; // 0001 ^ 1000 = 1111
    }

    public static boolean isEntityStaringAtEntity(LivingEntity entity1, LivingEntity entity2){
        Vec3d vec3d = entity1.getRotationVec(1.0F).normalize();
        Vec3d vec3d2 = new Vec3d(entity2.getX() - entity1.getX(), entity2.getEyeY() - entity1.getEyeY(), entity2.getZ() - entity1.getZ());
        double d = vec3d2.length();
        vec3d2 = vec3d2.normalize();
        double e = vec3d.dotProduct(vec3d2);
        return e > 1.0 - 0.025 / d && entity1.canSee(entity2);
    }

    public static Optional<Float> getTargetPitch(double x, double y, double z, double x2, double y2, double z2) {
        double d = x - x2;
        double e = y - y2;
        double f = z - z2;
        double g = Math.sqrt(d * d + f * f);
        return !(Math.abs(e) > 1.0E-5F) && !(Math.abs(g) > 1.0E-5F) ? Optional.empty() : Optional.of((float)(-(MathHelper.atan2(e, g) * 180.0F / (float)Math.PI)));
    }

    public static Optional<Float> getTargetYaw(float x, float z, float x2, float z2) {
        double d = x - x2;
        double e = z - z2;
        return !(Math.abs(e) > 1.0E-5F) && !(Math.abs(d) > 1.0E-5F)
                ? Optional.empty()
                : Optional.of((float)(MathHelper.atan2(e, d) * 180.0F / (float)Math.PI) - 90.0F);
    }

    public static float changeAngle(float from, float to, float max) {
        float f = MathHelper.subtractAngles(from, to);
        float g = MathHelper.clamp(f, -max, max);
        return from + g;
    }

}
