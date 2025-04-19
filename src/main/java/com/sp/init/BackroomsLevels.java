package com.sp.init;

import com.sp.SPBRevamped;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

import java.util.HashMap;
import java.util.Map;

public class BackroomsLevels {
    public static final RegistryKey<World> LEVEL0_WORLD_KEY = RegistryKey.of(RegistryKeys.WORLD, new Identifier(SPBRevamped.MOD_ID, "level0"));
    public static final RegistryKey<DimensionType> LEVEL0_DIM_TYPE = RegistryKey.of(RegistryKeys.DIMENSION_TYPE, new Identifier(SPBRevamped.MOD_ID, "level0_type"));
    public static final RegistryKey<World> LEVEL1_WORLD_KEY = RegistryKey.of(RegistryKeys.WORLD, new Identifier(SPBRevamped.MOD_ID, "level1"));
    public static final RegistryKey<World> LEVEL2_WORLD_KEY = RegistryKey.of(RegistryKeys.WORLD, new Identifier(SPBRevamped.MOD_ID, "level2"));
    public static final RegistryKey<World> POOLROOMS_WORLD_KEY = RegistryKey.of(RegistryKeys.WORLD, new Identifier(SPBRevamped.MOD_ID, "poolrooms"));
    public static final RegistryKey<World> INFINITE_FIELD_WORLD_KEY = RegistryKey.of(RegistryKeys.WORLD, new Identifier(SPBRevamped.MOD_ID, "infinite_field"));

    public static HashMap<RegistryKey<World>, BlockPos> BACKROOMS_LEVEL = new HashMap<>();

    public static void init() {
        BACKROOMS_LEVEL.put(LEVEL0_WORLD_KEY, new BlockPos(1, 22, 1));
        BACKROOMS_LEVEL.put(LEVEL1_WORLD_KEY, new BlockPos(6, 22, 3));
        BACKROOMS_LEVEL.put(LEVEL2_WORLD_KEY, new BlockPos(0, 21, 8));
        BACKROOMS_LEVEL.put(POOLROOMS_WORLD_KEY, new BlockPos(15, 104, 16));
        BACKROOMS_LEVEL.put(INFINITE_FIELD_WORLD_KEY, new BlockPos(0, 31, 0));
    }

    public static boolean isInBackrooms(RegistryKey<World> world){
        return BACKROOMS_LEVEL.containsKey(world);
    }

    public static BlockPos getCurrentLevelsOrigin(RegistryKey<World> world){
        if (BACKROOMS_LEVEL.containsKey(world)) {
            return BACKROOMS_LEVEL.get(world);
        }

        return null;
    }

    public static Map<String, RegistryKey<World>> definitions = Map.of(
            "LEVEL0",         LEVEL0_WORLD_KEY,
            "LEVEL1",         LEVEL1_WORLD_KEY,
            "LEVEL2",         LEVEL2_WORLD_KEY,
            "POOLROOMS",      POOLROOMS_WORLD_KEY,
            "INFINITE_FIELD", INFINITE_FIELD_WORLD_KEY
    );
}
