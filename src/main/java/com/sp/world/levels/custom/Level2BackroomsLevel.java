package com.sp.world.levels.custom;

import com.sp.cca_stuff.PlayerComponent;
import com.sp.init.BackroomsLevels;
import com.sp.world.events.level2.Level2Ambience;
import com.sp.world.events.level2.Level2Warp;
import com.sp.world.generation.Level2ChunkGenerator;
import com.sp.world.levels.BackroomsLevel;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class Level2BackroomsLevel extends BackroomsLevel {
    private boolean isWarping = false;

    public Level2BackroomsLevel() {
        super("level2", Level2ChunkGenerator.CODEC, new Vec3d(0, 21, 8), BackroomsLevels.LEVEL2_WORLD_KEY);
    }

    @Override
    public void register() {
        super.register();

        events.add(Level2Warp::new);
        events.add(Level2Ambience::new);
    }

    @Override
    public int nextEventDelay() {
        return random.nextInt(500, 800);
    }

    public boolean isWarping() {
        return isWarping;
    }

    public void setWarping(boolean warping) {
        isWarping = warping;
    }

    @Override
    public void writeToNbt(NbtCompound nbt) {
        nbt.putBoolean("isWarping", isWarping);
    }

    @Override
    public void readFromNbt(NbtCompound nbt) {
        this.isWarping = nbt.getBoolean("isWarping");
    }

    @Override
    public boolean transitionOut(BackroomsLevel to, PlayerComponent playerComponent, World world) {
        return true;
    }

    @Override
    public void transitionIn(BackroomsLevel from, PlayerComponent playerComponent, World world) {

    }
}
