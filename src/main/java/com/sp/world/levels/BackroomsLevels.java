package com.sp.world.levels;

import com.sp.SPBRevamped;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;

import java.util.OptionalLong;

public class BackroomsLevels {
    public static final RegistryKey<DimensionOptions> LEVEL0_OPTIONS = RegistryKey.of(RegistryKeys.DIMENSION, new Identifier(SPBRevamped.MOD_ID, "level0"));
    public static final RegistryKey<World> LEVEL0_WORLD_KEY = RegistryKey.of(RegistryKeys.WORLD, new Identifier(SPBRevamped.MOD_ID, "level0"));
    public static final RegistryKey<DimensionType> LEVEL0_DIM_TYPE = RegistryKey.of(RegistryKeys.DIMENSION_TYPE, new Identifier(SPBRevamped.MOD_ID, "level0_type"));

    public static final RegistryKey<DimensionOptions> LEVEL1_OPTIONS = RegistryKey.of(RegistryKeys.DIMENSION, new Identifier(SPBRevamped.MOD_ID, "level1"));
    public static final RegistryKey<World> LEVEL1_WORLD_KEY = RegistryKey.of(RegistryKeys.WORLD, new Identifier(SPBRevamped.MOD_ID, "level1"));
    public static final RegistryKey<DimensionType> LEVEL1_DIM_TYPE = RegistryKey.of(RegistryKeys.DIMENSION_TYPE, new Identifier(SPBRevamped.MOD_ID, "level1_type"));

    public static final RegistryKey<DimensionOptions> LEVEL2_OPTIONS = RegistryKey.of(RegistryKeys.DIMENSION, new Identifier(SPBRevamped.MOD_ID, "level2"));
    public static final RegistryKey<World> LEVEL2_WORLD_KEY = RegistryKey.of(RegistryKeys.WORLD, new Identifier(SPBRevamped.MOD_ID, "level2"));
    public static final RegistryKey<DimensionType> LEVEL2_DIM_TYPE = RegistryKey.of(RegistryKeys.DIMENSION_TYPE, new Identifier(SPBRevamped.MOD_ID, "level2_type"));

    public static final RegistryKey<DimensionOptions> POOLROOMS_OPTIONS = RegistryKey.of(RegistryKeys.DIMENSION, new Identifier(SPBRevamped.MOD_ID, "poolrooms"));
    public static final RegistryKey<World> POOLROOMS_WORLD_KEY = RegistryKey.of(RegistryKeys.WORLD, new Identifier(SPBRevamped.MOD_ID, "poolrooms"));
    public static final RegistryKey<DimensionType> POOLROOMS_DIM_TYPE = RegistryKey.of(RegistryKeys.DIMENSION_TYPE, new Identifier(SPBRevamped.MOD_ID, "poolrooms_type"));


}
