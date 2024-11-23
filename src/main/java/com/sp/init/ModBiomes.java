package com.sp.init;

import com.sp.SPBRevamped;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;

public class ModBiomes {
    public static final RegistryKey<Biome> BASE_BACKROOMS_BIOME = RegistryKey.of(RegistryKeys.BIOME, new Identifier(SPBRevamped.MOD_ID, "base_backrooms_biome"));
}
