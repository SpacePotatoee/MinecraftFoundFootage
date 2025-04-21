package com.sp.world.levels;

import com.mojang.serialization.Codec;
import com.sp.world.events.AbstractEvent;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.chunk.ChunkGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public abstract class BackroomsLevel {
    private String levelId;
    private String modId = "spb-revamped";
    private Codec<? extends ChunkGenerator> chunkGeneratorCodec;
    private RegistryKey<World> worldKey;
    private BlockPos spawnPos;
    public Random random = new Random();
    protected List<Supplier<AbstractEvent>> events = new ArrayList<>();

    public BackroomsLevel(String levelId, Codec<? extends ChunkGenerator> chunkGenerator, BlockPos spawnPos, RegistryKey<World> worldKey) {
        this.levelId = levelId;
        this.chunkGeneratorCodec = chunkGenerator;
        this.spawnPos = spawnPos;
        this.worldKey = worldKey;
    }

    public void register() {
        Registry.register(Registries.CHUNK_GENERATOR, new Identifier(modId, levelId + "_chunk_generator"), chunkGeneratorCodec);
    }

    public String getLevelId() {
        return levelId;
    }

    public String getModId() {
        return modId;
    }

    public RegistryKey<World> getWorldKey() {
        return worldKey;
    }

    public BlockPos getSpawnPos() {
        return spawnPos;
    }

    public AbstractEvent getRandomEvent(World world) {
        return this.events.get(random.nextInt(this.events.size())).get();
    }

    public abstract int nextEventDelay();
}
