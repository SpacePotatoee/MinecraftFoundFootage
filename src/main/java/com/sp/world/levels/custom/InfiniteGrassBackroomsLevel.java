package com.sp.world.levels.custom;

import com.sp.cca_stuff.PlayerComponent;
import com.sp.init.BackroomsLevels;
import com.sp.world.events.infinite_grass.InfiniteGrassAmbience;
import com.sp.world.generation.InfGrassChunkGenerator;
import com.sp.world.levels.BackroomsLevel;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class InfiniteGrassBackroomsLevel extends BackroomsLevel {
    public InfiniteGrassBackroomsLevel() {
        super("inf_grass", InfGrassChunkGenerator.CODEC, new Vec3d(0, 31, 0), BackroomsLevels.INFINITE_FIELD_WORLD_KEY);
    }

    @Override
    public void register() {
        super.register();

        events.add(InfiniteGrassAmbience::new);
    }

    @Override
    public int nextEventDelay() {
        return random.nextInt(1000, 1200);
    }

    @Override
    public void writeToNbt(NbtCompound nbt) {

    }

    @Override
    public void readFromNbt(NbtCompound nbt) {

    }

    @Override
    public boolean transitionOut(BackroomsLevel to, PlayerComponent playerComponent, World world) {
        return true;
    }

    @Override
    public void transitionIn(BackroomsLevel from, PlayerComponent playerComponent, World world) {

    }
}
