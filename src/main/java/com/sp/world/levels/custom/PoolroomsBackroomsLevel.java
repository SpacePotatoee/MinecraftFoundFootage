package com.sp.world.levels.custom;

import com.sp.SPBRevamped;
import com.sp.cca_stuff.PlayerComponent;
import com.sp.init.BackroomsLevels;
import com.sp.world.events.poolrooms.PoolroomsAmbience;
import com.sp.world.events.poolrooms.PoolroomsSunset;
import com.sp.world.generation.chunk_generator.PoolroomsChunkGenerator;
import com.sp.world.levels.BackroomsLevel;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class PoolroomsBackroomsLevel extends BackroomsLevel {
    public float timeOfDay = 0;
    public boolean sunsetTransitioning = false;

    public PoolroomsBackroomsLevel() {
        super("poolrooms", PoolroomsChunkGenerator.CODEC, new RoomCount(1), new Vec3d(16, 106, 16), BackroomsLevels.POOLROOMS_WORLD_KEY);
    }

    @Override
    public boolean rendersClouds() {
        return false;
    }

    @Override
    public boolean rendersSky() {
        return false;
    }

    @Override
    public void register() {
        super.register();

        this.registerEvents("sunset", PoolroomsSunset::new);
        this.registerEvents("abience", PoolroomsAmbience::new);

        this.registerTransition((world, playerComponent, from) -> {
            List<LevelTransition> playerList = new ArrayList<>();

            if (from instanceof PoolroomsBackroomsLevel && playerComponent.player.getWorld().getLightLevel(playerComponent.player.getBlockPos()) == 0 && playerComponent.player.getPos().y < 60 && playerComponent.player.getPos().y > 52) {
                playerList.add(getInfiniteFieldTransition(playerComponent));
            }

            return playerList;

        }, this.getLevelId() + "->" + BackroomsLevels.INFINITE_FIELD_BACKROOMS_LEVEL.getLevelId());
    }

    private LevelTransition getInfiniteFieldTransition(PlayerComponent playerComponent) {
        return new LevelTransition(
                1,
                (teleport, tick) -> {
                    if (!teleport.playerComponent().player.getWorld().isClient()) {
                        SPBRevamped.sendBlackScreenPacket((ServerPlayerEntity) teleport.playerComponent().player, 60, true, false);
                    }
                },
                new CrossDimensionTeleport(
                        playerComponent,
                        BackroomsLevels.INFINITE_FIELD_BACKROOMS_LEVEL.getSpawnPos(),
                        this,
                        BackroomsLevels.INFINITE_FIELD_BACKROOMS_LEVEL
                ),
                (teleport, tick) -> {});
    }

    @Override
    public int nextEventDelay() {
        return random.nextInt(800, 1000);
    }

    public boolean isNoon() {
        return timeOfDay != 0.25 && timeOfDay != 0.75;
    }

    public float getTimeOfDay() {
        return timeOfDay;
    }

    @Override
    public BoolTextPair allowsTorch() {
        return new BoolTextPair(false, Text.translatable("spb-revamped.flashlight.wet1").append(Text.translatable("spb-revamped.flashlight.wet2").formatted(Formatting.RED)));
    }

    @Override
    public boolean hasVanillaLighting() {
        return true;
    }

    public void setTimeOfDay(float timeOfDay) {
        this.justChanged();
        this.timeOfDay = timeOfDay;
    }

    public boolean isSunsetTransitioning() {
        return sunsetTransitioning;
    }

    public void setSunsetTransitioning(boolean sunsetTransitioning) {
        this.justChanged();
        this.sunsetTransitioning = sunsetTransitioning;
    }

    @Override
    public void writeToNbt(NbtCompound nbt) {
        nbt.putFloat("timeOfDay", timeOfDay);
        nbt.putBoolean("sunsetTransitioning", sunsetTransitioning);
    }

    @Override
    public void readFromNbt(NbtCompound nbt) {
        this.timeOfDay = nbt.getFloat("timeOfDay");
        this.sunsetTransitioning = nbt.getBoolean("sunsetTransitioning");
    }

    @Override
    public void transitionOut(CrossDimensionTeleport crossDimensionTeleport) {
        crossDimensionTeleport.playerComponent().player.fallDistance = 0;
    }

    @Override
    public void transitionIn(CrossDimensionTeleport crossDimensionTeleport) {

    }
}
