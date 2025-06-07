package com.sp.world.levels.custom;

import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import com.sp.init.BackroomsLevels;
import com.sp.world.events.poolrooms.PoolroomsAmbience;
import com.sp.world.events.poolrooms.PoolroomsSunset;
import com.sp.world.generation.PoolroomsChunkGenerator;
import com.sp.world.levels.BackroomsLevel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class PoolroomsBackroomsLevel extends BackroomsLevel {
    public float timeOfDay = 0;
    public boolean sunsetTransitioning = false;

    public PoolroomsBackroomsLevel() {
        super("poolrooms", PoolroomsChunkGenerator.CODEC, new Vec3d(0, 32, 0), BackroomsLevels.POOLROOMS_WORLD_KEY);
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

        events.add(PoolroomsSunset::new);
        events.add(PoolroomsAmbience::new);


        this.registerTransition((world, playerComponent, from) -> {

            List<CrossDimensionTeleport> playerList = new ArrayList<>();
            if (from instanceof PoolroomsBackroomsLevel && playerComponent.player.getWorld().getLightLevel(playerComponent.player.getBlockPos()) == 0 && playerComponent.player.getPos().y < 60 && playerComponent.player.getPos().y > 52) {
                    if (playerComponent.player.getWorld().getRegistryKey() == BackroomsLevels.POOLROOMS_WORLD_KEY) {
                        playerList.add(new CrossDimensionTeleport(playerComponent.player.getWorld(), playerComponent, this.getSpawnPos(), BackroomsLevels.POOLROOMS_BACKROOMS_LEVEL, BackroomsLevels.INFINITE_FIELD_BACKROOMS_LEVEL));
                    }
            }

            return playerList;
        }, this.getLevelId() + "->" + BackroomsLevels.INFINITE_FIELD_BACKROOMS_LEVEL.getLevelId());
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
    public boolean transitionOut(CrossDimensionTeleport crossDimensionTeleport) {
        crossDimensionTeleport.playerComponent().player.fallDistance = 0;

        return true;
    }

    @Override
    public void transitionIn(CrossDimensionTeleport crossDimensionTeleport) {

    }

    @Override
    public int getTransitionDuration() {
        return 0;
    }
}
