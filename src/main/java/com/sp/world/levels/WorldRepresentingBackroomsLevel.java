package com.sp.world.levels;

import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public abstract class WorldRepresentingBackroomsLevel extends BackroomsLevel {
    public WorldRepresentingBackroomsLevel(String levelId, Vec3d spawnPos, RegistryKey<World> worldKey) {
        super(levelId, null, spawnPos, worldKey, "minecraft");
    }


    public WorldRepresentingBackroomsLevel(String levelId, Vec3d spawnPos, RegistryKey<World> worldKey, String modId) {
        super(levelId, null, spawnPos, worldKey, modId);
    }

    @Override
    public void register() {

    }
}
