package com.sp.render.bird;

import com.sp.compat.modmenu.ConfigStuff;
import com.sp.entity.ik.util.MathUtil;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FlockManager {
    private static final List<Vec3d> FLOCK_CENTERS = new ArrayList<>();
    private static final List<Vec3d> FLOCK_VELOCITIES = new ArrayList<>();

    private static int lastFlockCount = 0;

    private static final double MAX_HORIZONTAL_DISTANCE = 100;

    public static void init() {
        Random random = new Random();

        lastFlockCount = ConfigStuff.birdQuality.getFlockCount();

        FLOCK_CENTERS.clear();
        FLOCK_VELOCITIES.clear();
        for (int i = 0; i < lastFlockCount; i++) {
            FLOCK_CENTERS.add(getCheckCoord().add(new Vec3d((random.nextFloat() * MAX_HORIZONTAL_DISTANCE * 2) - MAX_HORIZONTAL_DISTANCE, 0, (random.nextFloat() * MAX_HORIZONTAL_DISTANCE * 2) - MAX_HORIZONTAL_DISTANCE)));
            FLOCK_VELOCITIES.add(new Vec3d(random.nextFloat() * 2 - 1, random.nextFloat() * 2 - 1, random.nextFloat() * 2 - 1).normalize());
        }
    }

    public static void moveFlockCenterTowards(Vec3d target, int flockIndex, boolean shouldLerp) {
        if (FLOCK_CENTERS.size() <= flockIndex) {
            FLOCK_CENTERS.set(flockIndex, new Vec3d(getCheckCoord().x, 60, getCheckCoord().z));
        }

        if (shouldLerp) {
            FLOCK_CENTERS.set(flockIndex, MathUtil.lerpVec3(10, FLOCK_CENTERS.get(flockIndex), target));
        } else {
            FLOCK_CENTERS.set(flockIndex, target);
        }

    }

    public static Vec3d getFlockCenter(int flockIndex) {
        if (FLOCK_CENTERS.size() <= flockIndex) {
            FLOCK_CENTERS.set(flockIndex, new Vec3d(getCheckCoord().x, 60, getCheckCoord().z));
        }
        return FLOCK_CENTERS.get(flockIndex);
    }

    public static List<Vec3d> getFlockCenters() {
        return FLOCK_CENTERS;
    }

    public static void tick() {
        if (lastFlockCount != ConfigStuff.birdQuality.getFlockCount()) {
            init();
        }

        boolean shouldLerp = true;
        Random random = new Random();
        for (int i = 0; i < FLOCK_CENTERS.size(); i++) {
            Vec3d flockCenter = getFlockCenter(i);
            Vec3d velocity = FLOCK_VELOCITIES.size() > i ? FLOCK_VELOCITIES.get(i) : new Vec3d(random.nextFloat() * 2 - 1, random.nextFloat() * 2 - 1, random.nextFloat() * 2 - 1).normalize();
            Vec3d flockingTarget = getCheckCoord();

            Vec3d newPos = flockCenter.add(velocity.multiply(0.3));

            Vec3d localPos = flockingTarget.subtract(newPos);

            if (newPos.y > 60 || newPos.y < 50) {
                velocity = new Vec3d(velocity.x, -velocity.y, velocity.z);
                newPos = new Vec3d(newPos.x, newPos.y, newPos.z);
            }

            if (Math.abs(localPos.x) > MAX_HORIZONTAL_DISTANCE) {
                newPos = new Vec3d(flockingTarget.x - (localPos.x > 0 ? -MAX_HORIZONTAL_DISTANCE : MAX_HORIZONTAL_DISTANCE), newPos.y, newPos.z);
                shouldLerp = false;
            }

            if (Math.abs(localPos.z) > MAX_HORIZONTAL_DISTANCE) {
                newPos = new Vec3d(newPos.x, newPos.y, flockingTarget.z - (localPos.z > 0 ? -MAX_HORIZONTAL_DISTANCE : MAX_HORIZONTAL_DISTANCE));
                shouldLerp = false;
            }

            if (FabricLoader.getInstance().isDevelopmentEnvironment() && MinecraftClient.getInstance().world != null) {
                MinecraftClient.getInstance().world.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, newPos.x, newPos.y, newPos.z, 0, 0, 0);
            }

            FLOCK_VELOCITIES.set(i, velocity.normalize());

            moveFlockCenterTowards(newPos, i, shouldLerp);
        }
    }

    private static Vec3d getCheckCoord() {
        if (MinecraftClient.getInstance() == null || MinecraftClient.getInstance().player == null) {
            return new Vec3d(0, 60, 0);
        }
        return MinecraftClient.getInstance().player.getPos();
    }
}
