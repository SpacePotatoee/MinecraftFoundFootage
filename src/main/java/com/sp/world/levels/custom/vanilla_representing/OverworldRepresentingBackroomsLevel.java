package com.sp.world.levels.custom.vanilla_representing;

import com.sp.world.levels.WorldRepresentingBackroomsLevel;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class OverworldRepresentingBackroomsLevel extends WorldRepresentingBackroomsLevel {
    public OverworldRepresentingBackroomsLevel() {
        super("overworld", new Vec3d(0,200,0), World.OVERWORLD);
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
    public void transitionOut(CrossDimensionTeleport crossDimensionTeleport) {

    }

    @Override
    public void transitionIn(CrossDimensionTeleport crossDimensionTeleport) {
        crossDimensionTeleport.playerComponent().loadPlayerSavedInventory();
    }
}
