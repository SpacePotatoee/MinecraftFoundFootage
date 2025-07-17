package com.sp.world.levels;

import com.mojang.serialization.Codec;
import com.sp.SPBRevamped;
import com.sp.cca_stuff.PlayerComponent;
import com.sp.world.events.AbstractEvent;
import com.sp.world.events.EmptyEvent;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;

public abstract class BackroomsLevel {
    private final String levelId;
    private final String modId;
    private final RoomCount roomCount;
    private final Codec<? extends ChunkGenerator> chunkGeneratorCodec;
    private final RegistryKey<World> worldKey;
    private final Vec3d spawnPos;
    public Random random = new Random();
    private boolean shouldSync = false;
    private final HashMap<String, Supplier<AbstractEvent>> events = new HashMap<>();
    private final HashMap<String, LevelTransitionCriteriaCallback> transitions = new HashMap<>();

    public BackroomsLevel(String levelId, Codec<? extends ChunkGenerator> chunkGenerator, Vec3d spawnPos, RegistryKey<World> worldKey) {
        this(levelId, chunkGenerator, null, spawnPos, worldKey, SPBRevamped.MOD_ID);
    }

    public BackroomsLevel(String levelId, Codec<? extends ChunkGenerator> chunkGenerator, RoomCount roomCount, Vec3d spawnPos, RegistryKey<World> worldKey) {
        this(levelId, chunkGenerator, roomCount, spawnPos, worldKey, SPBRevamped.MOD_ID);
    }

    public BackroomsLevel(String levelId, Codec<? extends ChunkGenerator> chunkGenerator, Vec3d spawnPos, RegistryKey<World> worldKey, String modId) {
        this(levelId, chunkGenerator, null, spawnPos, worldKey, modId);
    }

    public BackroomsLevel(String levelId, Codec<? extends ChunkGenerator> chunkGenerator, @Nullable RoomCount roomCount, Vec3d spawnPos, RegistryKey<World> worldKey, String modId) {
        this.levelId = levelId;
        this.chunkGeneratorCodec = chunkGenerator;
        this.spawnPos = spawnPos;
        this.worldKey = worldKey;
        this.modId = modId;

        this.roomCount = roomCount;
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

    public Optional<RoomCount> getRoomCount() {
        return Optional.ofNullable(roomCount);
    }

    /**
     * If the level allows the torch to be used.
     * @return a BoolTextPair containing the value and a message to display to the player.
     */
    public BoolTextPair allowsTorch() {
        return new BoolTextPair(true, Text.literal("Flashlight is allowed in this level."));
    }

    /**
     * If the level renders the sky.
     * Also look at {@link #rendersClouds()}.
     * @return if the sky renders.
     */
    public boolean rendersSky() {
        return true;
    }

    /**
     * If the level renders the sky.
     * Also look at {@link #rendersSky()}.
     * @return if the sky renders.
     */
    public boolean rendersClouds() {
        return true;
    }

    /**
     * @return If the level has vanilla lighting.
     * Without vanilla lighting, the level will be completely dark without light sources like the torch.
     */
    public boolean hasVanillaLighting() {
        return false;
    }

    public record BoolTextPair(boolean value, MutableText string) {}

    public AbstractEvent getRandomEvent(World world) {
        if (this.events.isEmpty()) {
            return new EmptyEvent();
        }

        List<AbstractEvent> eventList = new ArrayList<>();

        this.events.forEach((key, value) -> {
            AbstractEvent event = value.get();
            eventList.add(event);
        });

        return eventList.get(random.nextInt(eventList.size()));
    }

    public List<LevelTransition> checkForTransition(PlayerComponent playerComponent, World world) {
        List<LevelTransition> possibleTransitions = new ArrayList<>();
        this.transitions.forEach((key, value) -> {
            if (!value.predicate(world, playerComponent, this).isEmpty()) {
                possibleTransitions.add(value.predicate(world, playerComponent, this).get(0));
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
     * This method is called on the same tick the player gets teleported. (Save on the client and server)
     */
    public abstract void transitionOut(CrossDimensionTeleport crossDimensionTeleport);

    /**
     * Called when transitioning in to this level.
     * It is called first on client at <code>teleportingTimer == 1</code>,
     * then the method will be called on the server later when <code>teleportingTimer == 0</code>.
     * <b>Note:</b> this may lead to the method being called multiple times, so be careful with the code you put in here.
     */
    public abstract void transitionIn(CrossDimensionTeleport crossDimensionTeleport);

    public void registerTransition(LevelTransitionCriteriaCallback transition, String name) {
        this.transitions.put(name, transition);
    }

    public void unregisterTransition(String name) {
        this.transitions.remove(name);
    }


    public void registerEvents(String name, Supplier<AbstractEvent> event) {
        this.events.put(name, event);
    }

    public void unregisterEvents(String name) {
        this.events.remove(name);
    }


    /**
     * The Transition Duration is now registered alongside the transition.
     */
    @Deprecated
    public int getTransitionDuration() {return 0;}

    /**
     * @param duration How many thicks the transition should take.
     * @param callback The callback to call every tick during the transition. <b>Note:</b> The last tick which is called is 1. Not 0. The teleport happens on 0.
     * @param teleport The teleport to perform when the transition is done.
     * @param cancel The callback to call when the transition is cancelled. <b>Note:</b> This is currently not used as canceling transitions has proven very buggy.
     */
    public record LevelTransition(int duration, TransitionTickCallback callback, CrossDimensionTeleport teleport, TransitionCancelCallback cancel) {}

    public interface TransitionTickCallback {
        void tick(CrossDimensionTeleport teleport, int tick);
    }

    public interface TransitionCancelCallback {
        void cancel(CrossDimensionTeleport teleport, int tick);
    }

    public record CrossDimensionTeleport(PlayerComponent playerComponent, Vec3d pos, BackroomsLevel from, BackroomsLevel to) {}

    public interface LevelTransitionCriteriaCallback {
        List<LevelTransition> predicate(World world, PlayerComponent playerComponent, BackroomsLevel from);
    }

    public record RoomCount(int aRoomCount, int bRoomCount, int cRoomCount, int dRoomCount, int eRoomCount) {
        public RoomCount(int i) {
            this(i, i, i, i, i);
        }
    }
}
