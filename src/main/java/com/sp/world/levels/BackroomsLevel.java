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

    /**
     * Called when the level needs to be synced or saved to disk.
     * Here you can put anything you want to save to the NBT.
     * You do <b>not</b> needing to make a NbtCompound first. That is handled by the WorldEvents class.
     * <b>NOTE</b>: You should not only save data here which you want to <b>save</b> to disk but also which you want to <b>sync</b>.
     * @param nbt the NbtCompound to save in, assigned by the WorldEvents class.
     */
    public abstract void writeToNbt(NbtCompound nbt);

    /**
     * Called when the level is loaded from disk.
     * Here you can read anything you want to load from the NBT into your level.
     * You do <b>not</b> needing to step down into an NbtCompound first. That is handled by the WorldEvents class.
     * @param nbt the NbtCompound to load in, assigned by the WorldEvents class.
     */
    public abstract void readFromNbt(NbtCompound nbt);

    public boolean shouldSync() {
        boolean shouldSync = this.shouldSync;
        this.shouldSync = false;
        return shouldSync;
    }

    /**
     * Called when transitioning out of this level.
     * This method is called on the server first and then on the next frame on the client,
     * which will then skip the transitionTime one tick ahead to avoid being called twice.
     * <b>Note:</b> this will be called once on the server and every tick on the server. If you only want to have this run once then just check if <code>the teleportingTimer == -1</code>
     * @return If the teleportation can happen now. Just return false if you can't teleport the player. This should be used carefully, since it can lead a failed teleport.
     */
    public abstract boolean transitionOut(CrossDimensionTeleport crossDimensionTeleport);

    /**
     * Called when transitioning in to this level.
     * It is called first on client at <code>teleportingTimer == 1</code>,
     * then the method will be called on the server later when <code>teleportingTimer == 0</code>.
     * <b>Note:</b> this may lead to the method being called multiple times, so be careful with the code you put in here.
     */
    public abstract void transitionIn(CrossDimensionTeleport crossDimensionTeleport);

    public void registerTransition(LevelTransition transition, String name) {
        this.transitions.put(name, transition);
    }

    public void unregisterTransition(String name) {
        this.transitions.remove(name);
    }

    /**
     * the time it takes form the first trigger of the transition to the teleportation (if transitionOut returns true)
     */
    public abstract int getTransitionDuration();

    public interface LevelTransition {
        List<CrossDimensionTeleport> predicate(World world, PlayerComponent playerComponent, BackroomsLevel from);
    }

    public record CrossDimensionTeleport(World world, PlayerComponent playerComponent, Vec3d pos, BackroomsLevel from, BackroomsLevel to) {}
}
