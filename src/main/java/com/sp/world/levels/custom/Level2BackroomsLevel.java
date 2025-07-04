package com.sp.world.levels.custom;

import com.sp.SPBRevamped;
import com.sp.compat.modmenu.ConfigStuff;
import com.sp.init.BackroomsLevels;
import com.sp.mixininterfaces.NewServerProperties;
import com.sp.world.events.level2.Level2Ambience;
import com.sp.world.events.level2.Level2Warp;
import com.sp.world.generation.chunk_generator.Level2ChunkGenerator;
import com.sp.world.levels.BackroomsLevel;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Level2BackroomsLevel extends BackroomsLevel {
    private boolean isWarping = false;

    public Level2BackroomsLevel() {
        super("level2", Level2ChunkGenerator.CODEC, new Vec3d(0.5, 20, 8), BackroomsLevels.LEVEL2_WORLD_KEY);

        this.registerEvents("warp", Level2Warp::new);
        this.registerEvents("abience", Level2Ambience::new);

        this.registerTransition(new LevelTransition(110, (world, playerComponent, from) -> {

            List<CrossDimensionTeleport> playerList = new ArrayList<>();

            int exitRadius = ConfigStuff.exitSpawnRadius;
            if(world.getServer() != null) {
                if (world.getServer().isDedicated()) {
                    exitRadius = ((NewServerProperties) ((MinecraftDedicatedServer) world.getServer()).getProperties()).getExitSpawnRadius();
                }
            }

            if (from instanceof Level2BackroomsLevel && Math.abs(playerComponent.player.getPos().getZ()) >= exitRadius) {
                    if (playerComponent.player.getWorld().getRegistryKey() == BackroomsLevels.LEVEL2_WORLD_KEY) {
                        playerList.add(new CrossDimensionTeleport(playerComponent.player.getWorld(), playerComponent, BackroomsLevels.POOLROOMS_BACKROOMS_LEVEL.getSpawnPos(), BackroomsLevels.LEVEL2_BACKROOMS_LEVEL, BackroomsLevels.POOLROOMS_BACKROOMS_LEVEL));
                    }
            }

            return playerList;
        }), "level2 -> poolrooms");
    }

    @Override
    public int nextEventDelay() {
        return random.nextInt(500, 800);
    }

    public boolean isWarping() {
        return isWarping;
    }

    public void setWarping(boolean warping) {
        isWarping = warping;
    }

    @Override
    public void writeToNbt(NbtCompound nbt) {
        nbt.putBoolean("isWarping", isWarping);
    }

    @Override
    public void readFromNbt(NbtCompound nbt) {
        this.isWarping = nbt.getBoolean("isWarping");
    }

    @Override
    public boolean transitionOut(CrossDimensionTeleport crossDimensionTeleport) {
        if (crossDimensionTeleport.world().isClient()) {
            return true;
        }
        int exitRadius = ConfigStuff.exitSpawnRadius;

        if (crossDimensionTeleport.playerComponent().getTeleportingTimer() == -1) {
            ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

            executorService.schedule(() -> {
                if (Math.abs(crossDimensionTeleport.playerComponent().player.getPos().getZ()) >= exitRadius) {
                    crossDimensionTeleport.playerComponent().setShouldNoClip(true);
                    crossDimensionTeleport.playerComponent().sync();
                }
                executorService.shutdown();
            }, 4500, TimeUnit.MILLISECONDS);

            //Turn Player screen to Black
            executorService.schedule(() -> {
                if (Math.abs(crossDimensionTeleport.playerComponent().player.getPos().getZ()) >= exitRadius) {
                    SPBRevamped.sendBlackScreenPacket((ServerPlayerEntity) crossDimensionTeleport.playerComponent().player, 20, true, false);
                }
                executorService.shutdown();
            }, 4800, TimeUnit.MILLISECONDS);

            //After the screen turns black THEN teleport
            executorService.schedule(() -> {
                if (Math.abs(crossDimensionTeleport.playerComponent().player.getPos().getZ()) >= exitRadius) {
                    crossDimensionTeleport.playerComponent().setShouldNoClip(false);
                    crossDimensionTeleport.playerComponent().setTeleportingTimer(0);
                    crossDimensionTeleport.playerComponent().sync();
                }
                executorService.shutdown();
            }, 5500, TimeUnit.MILLISECONDS);
        }


        return true;
    }

    @Override
    public void transitionIn(CrossDimensionTeleport crossDimensionTeleport) {

    }
}
