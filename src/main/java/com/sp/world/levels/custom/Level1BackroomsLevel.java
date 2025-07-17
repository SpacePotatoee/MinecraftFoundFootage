package com.sp.world.levels.custom;

import com.sp.SPBRevamped;
import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import com.sp.init.BackroomsLevels;
import com.sp.world.events.level1.Level1Ambience;
import com.sp.world.events.level1.Level1Blackout;
import com.sp.world.events.level1.Level1Flicker;
import com.sp.world.generation.chunk_generator.Level1ChunkGenerator;
import com.sp.world.levels.BackroomsLevel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class Level1BackroomsLevel extends BackroomsLevel {
    private Level0BackroomsLevel.LightState lightState = Level0BackroomsLevel.LightState.ON;

    public Level1BackroomsLevel() {
        super("level1", Level1ChunkGenerator.CODEC, new RoomCount(6, 24, 24, 12, 24), new Vec3d(6, 22, 3), BackroomsLevels.LEVEL1_WORLD_KEY);
    }

    @Override
    public void register() {
        super.register();

        this.registerEvents("blackout", Level1Blackout::new);
        this.registerEvents("flicker", Level1Flicker::new);
        this.registerEvents("ambience", Level1Ambience::new);

        this.registerTransition((world, playerComponent, from) -> {
            List<LevelTransition> playerList = new ArrayList<>();


            if (from instanceof Level1BackroomsLevel && playerComponent.player.getPos().getY() <= 12 && playerComponent.player.isOnGround()) {
                for (PlayerEntity player : playerComponent.player.getWorld().getPlayers()) {
                    PlayerComponent otherPlayerComponent = InitializeComponents.PLAYER.get(player);
                    playerList.add(getLevel2Transition(otherPlayerComponent));
                }
            }

            return playerList;
        }, this.getLevelId() + "-> level2");
    }


    private LevelTransition getLevel2Transition(PlayerComponent playerComponent) {
        return new LevelTransition(
                30,
                (teleport, tick) -> {
                    if (tick == 30) {
                        if (!playerComponent.player.getWorld().isClient()) {
                            if(!playerComponent.isTeleporting()) {
                                SPBRevamped.sendLevelTransitionLightsOutPacket((ServerPlayerEntity) playerComponent.player, 80);
                            }
                        }
                    }
                }, // Tick
                new CrossDimensionTeleport(
                        playerComponent,
                        calculateLevel2TeleportCoords(playerComponent.player,
                        playerComponent.player.getChunkPos()),
                        this,
                        BackroomsLevels.LEVEL2_BACKROOMS_LEVEL),
                (teleport, tick) -> {}); // Cancel
    }

    private Vec3d calculateLevel2TeleportCoords(PlayerEntity player, ChunkPos chunkPos) {
        if(chunkPos.x == player.getChunkPos().x && chunkPos.z == player.getChunkPos().z) {
            int chunkX = chunkPos.getStartX();
            int chunkZ = chunkPos.getStartZ();

            double playerX = player.getPos().x;
            double playerZ = player.getPos().z;

            return new Vec3d((playerX - chunkX) - 1, player.getPos().y + 8, playerZ - chunkZ);
        } else {
            return this.getSpawnPos();
        }
    }

    @Override
    public int nextEventDelay() {
        return random.nextInt(1000, 1600);
    }

    @Override
    public void writeToNbt(NbtCompound nbt) {
        nbt.putString("lightState", lightState.name());
    }

    @Override
    public void readFromNbt(NbtCompound nbt) {
        this.lightState = Level0BackroomsLevel.LightState.valueOf(nbt.getString("lightState"));

    }

    @Override
    public void transitionOut(CrossDimensionTeleport crossDimensionTeleport) {

    }

    @Override
    public void transitionIn(CrossDimensionTeleport crossDimensionTeleport) {

    }

    public void setLightState(Level0BackroomsLevel.LightState lightState) {
        this.justChanged();
        this.lightState = lightState;
    }

    public Level0BackroomsLevel.LightState getLightState() {
        return this.lightState;
    }
}
