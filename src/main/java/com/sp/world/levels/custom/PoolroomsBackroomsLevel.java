package com.sp.world.levels.custom;

import com.sp.init.BackroomsLevels;
import com.sp.world.events.poolrooms.PoolroomsAmbience;
import com.sp.world.events.poolrooms.PoolroomsSunset;
import com.sp.world.generation.PoolroomsChunkGenerator;
import com.sp.world.levels.BackroomsLevel;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

public class PoolroomsBackroomsLevel extends BackroomsLevel {
    public float timeOfDay = 0;
    public boolean sunsetTransitioning = false;

    public PoolroomsBackroomsLevel() {
        super("poolrooms", PoolroomsChunkGenerator.CODEC, new BlockPos(15, 104, 16), BackroomsLevels.POOLROOMS_WORLD_KEY);
    }

    @Override
    public void register() {
        super.register();

        events.add(PoolroomsSunset::new);
        events.add(PoolroomsAmbience::new);
    }

    @Override
    public int nextEventDelay() {
        return random.nextInt(800, 1000);
    }

    public boolean isNoon() {
        return timeOfDay != 0.25 && timeOfDay != 0.75;
    }

    public float getTimeOfDay() {
        return timeOfDay;
    }

    public void setTimeOfDay(float timeOfDay) {
        this.justChanged();
        this.timeOfDay = timeOfDay;
    }

    public boolean isSunsetTransitioning() {
        return sunsetTransitioning;
    }

    public void setSunsetTransitioning(boolean sunsetTransitioning) {
        this.justChanged();
        this.sunsetTransitioning = sunsetTransitioning;
    }

    @Override
    public void writeToNbt(NbtCompound nbt) {
        nbt.putFloat("timeOfDay", timeOfDay);
        nbt.putBoolean("sunsetTransitioning", sunsetTransitioning);
    }

    @Override
    public void readFromNbt(NbtCompound nbt) {
        this.timeOfDay = nbt.getFloat("timeOfDay");
        this.sunsetTransitioning = nbt.getBoolean("sunsetTransitioning");
    }
}
