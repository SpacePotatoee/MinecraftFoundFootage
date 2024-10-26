package com.sp.world;

import com.sp.SPBRevamped;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

public class BackroomsLevels {
    public static final RegistryKey<World> LEVEL0_WORLD_KEY = RegistryKey.of(RegistryKeys.WORLD, new Identifier(SPBRevamped.MOD_ID, "level0"));
    public static final RegistryKey<DimensionType> LEVEL0_DIM_TYPE = RegistryKey.of(RegistryKeys.DIMENSION_TYPE, new Identifier(SPBRevamped.MOD_ID, "level0_type"));

    public static final RegistryKey<World> LEVEL1_WORLD_KEY = RegistryKey.of(RegistryKeys.WORLD, new Identifier(SPBRevamped.MOD_ID, "level1"));

    public static final RegistryKey<World> LEVEL2_WORLD_KEY = RegistryKey.of(RegistryKeys.WORLD, new Identifier(SPBRevamped.MOD_ID, "level2"));

    public static final RegistryKey<World> POOLROOMS_WORLD_KEY = RegistryKey.of(RegistryKeys.WORLD, new Identifier(SPBRevamped.MOD_ID, "poolrooms"));


}
