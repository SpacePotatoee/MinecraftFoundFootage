package com.sp.world.levels.custom.vanilla_representing;

import com.sp.world.levels.WorldRepresentingBackroomsLevel;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class NetherRepresentingBackroomsLevel extends WorldRepresentingBackroomsLevel {
    public NetherRepresentingBackroomsLevel() {
        super("nether", new Vec3d(0,0,0), World.NETHER);
    }

    @Override
    public void register() {

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
        crossDimensionTeleport.playerComponent().loadPlayerSavedInventory();
    }

    @Override
    public int getTransitionDuration() {
        return 0;
    }
}
