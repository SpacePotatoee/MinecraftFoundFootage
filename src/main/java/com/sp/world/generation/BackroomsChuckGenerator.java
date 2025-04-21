package com.sp.world.generation;

import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.chunk.ChunkGenerator;

import java.util.function.Function;

public abstract class BackroomsChuckGenerator extends ChunkGenerator {
    public BackroomsChuckGenerator(BiomeSource biomeSource) {
        super(biomeSource);
    }

    public BackroomsChuckGenerator(BiomeSource biomeSource, Function<RegistryEntry<Biome>, GenerationSettings> generationSettingsGetter) {
        super(biomeSource, generationSettingsGetter);
    }

    public abstract void generate(StructureWorldAccess world, Chunk chunk);
}
