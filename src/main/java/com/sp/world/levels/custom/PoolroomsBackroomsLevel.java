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
    public void register() {
        super.register();

        events.add(PoolroomsSunset::new);
        events.add(PoolroomsAmbience::new);


        this.registerTransition((world, playerComponent, from) -> {

            List<CrossDimensionTeleport> playerList = new ArrayList<>();
            if (from instanceof PoolroomsBackroomsLevel && playerComponent.player.getWorld().getLightLevel(playerComponent.player.getBlockPos()) == 0 && playerComponent.player.getPos().y < 60 && playerComponent.player.getPos().y > 52) {
                for (PlayerEntity player : playerComponent.player.getWorld().getPlayers()) {
                    PlayerComponent otherPlayerComponent = InitializeComponents.PLAYER.get(player);
                    if (player.getWorld().getRegistryKey() == BackroomsLevels.POOLROOMS_WORLD_KEY) {
                        playerList.add(new CrossDimensionTeleport(player.getWorld(), otherPlayerComponent, this.getSpawnPos(), BackroomsLevels.POOLROOMS_BACKROOMS_LEVEL, BackroomsLevels.INFINITE_FIELD_BACKROOMS_LEVEL));
                    }
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
