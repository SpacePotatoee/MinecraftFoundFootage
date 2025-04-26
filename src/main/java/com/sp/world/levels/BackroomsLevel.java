package com.sp.world.levels;

import com.mojang.serialization.Codec;
import com.sp.SPBRevamped;
import com.sp.cca_stuff.PlayerComponent;
import com.sp.world.events.AbstractEvent;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.gen.chunk.ChunkGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public abstract class BackroomsLevel {
    private final String levelId;
    private final String modId;
    private final Codec<? extends ChunkGenerator> chunkGeneratorCodec;
    private final RegistryKey<World> worldKey;
    private final Vec3d spawnPos;
    public Random random = new Random();
    private boolean shouldSync = false;
    protected List<Supplier<AbstractEvent>> events = new ArrayList<>();
    private HashMap<String, LevelTransition> transitions = new HashMap<>();

    public BackroomsLevel(String levelId, Codec<? extends ChunkGenerator> chunkGenerator, Vec3d spawnPos, RegistryKey<World> worldKey) {
        this.levelId = levelId;
        this.chunkGeneratorCodec = chunkGenerator;
        this.spawnPos = spawnPos;
        this.worldKey = worldKey;
        this.modId = SPBRevamped.MOD_ID;
    }

    public BackroomsLevel(String levelId, Codec<? extends ChunkGenerator> chunkGenerator, Vec3d spawnPos, RegistryKey<World> worldKey, String modId) {
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

    public Vec3d getSpawnPos() {
        return spawnPos;
    }

    public AbstractEvent getRandomEvent(World world) {
        return this.events.get(random.nextInt(this.events.size())).get();
    }

    public List<LevelTransition> checkForTransition(PlayerComponent playerComponent, World world) {
        List<LevelTransition> possibleTransitions = new ArrayList<>();
        this.transitions.forEach((key, value) -> {
            if (!value.predicate(world, playerComponent, this).isEmpty()) {
                possibleTransitions.add(value);
            }
        });

        return possibleTransitions;
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

    /**
     * Called when transitioning out of this level.
     * @return If the teleportation can happen now. Just return false if you want to delay the teleportation.
     */
    public abstract boolean transitionOut(BackroomsLevel to, PlayerComponent playerComponent, World world);

    public abstract void transitionIn(BackroomsLevel from, PlayerComponent playerComponent, World world);

    protected void registerTransition(LevelTransition transition, String name) {
        this.transitions.put(name, transition);
    }

    protected void unregisterTransition(String name) {
        this.transitions.remove(name);
    }

    public interface LevelTransition {
        List<CrossDimensionTeleport> predicate(World world, PlayerComponent playerComponent, BackroomsLevel from);
    }

    public record CrossDimensionTeleport(World world, PlayerComponent playerComponent, Vec3d pos, BackroomsLevel from, BackroomsLevel to) {}
}
