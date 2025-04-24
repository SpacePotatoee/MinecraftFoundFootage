package com.sp.world.levels;

import com.mojang.serialization.Codec;
import com.sp.SPBRevamped;
import com.sp.world.events.AbstractEvent;
import net.minecraft.nbt.NbtCompound;
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
    private final String levelId;
    private final String modId;
    private final Codec<? extends ChunkGenerator> chunkGeneratorCodec;
    private final RegistryKey<World> worldKey;
    private final BlockPos spawnPos;
    public Random random = new Random();
    private boolean shouldSync = false;
    protected List<Supplier<AbstractEvent>> events = new ArrayList<>();

    public BackroomsLevel(String levelId, Codec<? extends ChunkGenerator> chunkGenerator, BlockPos spawnPos, RegistryKey<World> worldKey) {
        this.levelId = levelId;
        this.chunkGeneratorCodec = chunkGenerator;
        this.spawnPos = spawnPos;
        this.worldKey = worldKey;
        this.modId = SPBRevamped.MOD_ID;
    }

    public BackroomsLevel(String levelId, Codec<? extends ChunkGenerator> chunkGenerator, BlockPos spawnPos, RegistryKey<World> worldKey, String modId) {
        this.levelId = levelId;
        this.chunkGeneratorCodec = chunkGenerator;
        this.spawnPos = spawnPos;
        this.worldKey = worldKey;
        this.modId = modId;
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

    public void justChanged() {
        this.shouldSync = true;
    }

    public abstract int nextEventDelay();

    public abstract void writeToNbt(NbtCompound nbt);

    public abstract void readFromNbt(NbtCompound nbt);

    public boolean shouldSync() {
        boolean shouldSync = this.shouldSync;
        this.shouldSync = false;
        return shouldSync;
    }
}
