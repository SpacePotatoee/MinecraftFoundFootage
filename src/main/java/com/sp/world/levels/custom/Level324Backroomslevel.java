package com.sp.world.levels.custom;

import com.sp.init.BackroomsLevels;
import com.sp.world.generation.Level324ChunkGenerator;
import com.sp.world.levels.BackroomsLevel;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Vec3d;

public class Level324Backroomslevel extends BackroomsLevel {
    public Level324Backroomslevel() {
        super("level324", Level324ChunkGenerator.CODEC, new Vec3d(0,65,0), BackroomsLevels.LEVEL324_WORLD_KEY);
    }

    @Override
    public boolean rendersClouds() {
        return false;
    }

    @Override
    public boolean rendersSky() {
        return false;
    }

    @Override
    public int nextEventDelay() {
        return 0;
    }

    @Override
    public void writeToNbt(NbtCompound nbt) {

    }

    @Override
    public void readFromNbt(NbtCompound nbt) {

    }

    @Override
    public boolean transitionOut(CrossDimensionTeleport crossDimensionTeleport) {
        return true;
    }

    @Override
    public void transitionIn(CrossDimensionTeleport crossDimensionTeleport) {

    }

    @Override
    public int getTransitionDuration() {
        return 0;
    }
}
