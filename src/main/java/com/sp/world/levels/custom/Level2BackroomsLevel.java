package com.sp.world.levels.custom;

import com.sp.SPBRevamped;
import com.sp.cca_stuff.PlayerComponent;
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
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class Level2BackroomsLevel extends BackroomsLevel {
    private boolean isWarping = false;

    public Level2BackroomsLevel() {
        super("level2", Level2ChunkGenerator.CODEC, new Vec3d(0.5, 20, 8), BackroomsLevels.LEVEL2_WORLD_KEY);

        this.registerEvents("warp", Level2Warp::new);
        this.registerEvents("abience", Level2Ambience::new);

        this.registerTransition((world, playerComponent, from) -> {
            List<LevelTransition> playerList = new ArrayList<>();

            int exitRadius = ConfigStuff.exitSpawnRadius;

            if (world.getServer() != null) {
                if (world.getServer().isDedicated()) {
                    exitRadius = ((NewServerProperties) ((MinecraftDedicatedServer) world.getServer()).getProperties()).getExitSpawnRadius();
                }
            }

            if (from instanceof Level2BackroomsLevel && Math.abs(playerComponent.player.getPos().getZ()) >= exitRadius) {
                playerList.add(getPoolRoomsTransition(playerComponent));
            }

            return playerList;
        }, "level2 -> poolrooms");
    }

    private LevelTransition getPoolRoomsTransition(PlayerComponent playerComponent) {
        return new LevelTransition(
                110,
                (teleport, tick) -> {
                    World world = teleport.playerComponent().player.getWorld();

                    if (world.isClient()) {
                        return;
                    }

                    if (tick == 20) {
                        teleport.playerComponent().setShouldNoClip(true);
                        teleport.playerComponent().sync();
                    }

                    if (tick == 14) {
                        SPBRevamped.sendBlackScreenPacket((ServerPlayerEntity) teleport.playerComponent().player, 20, true, false);
                    }

                    //After the screen turns black THEN teleport
                    if (tick == 1) {
                        teleport.playerComponent().setShouldNoClip(false);
                        teleport.playerComponent().sync();
                    }
                }, // Tick
                new CrossDimensionTeleport(
                        playerComponent,
                        BackroomsLevels.POOLROOMS_BACKROOMS_LEVEL.getSpawnPos(),
                        this,
                        BackroomsLevels.POOLROOMS_BACKROOMS_LEVEL
                ),
                (teleport, tick) -> {
                    teleport.playerComponent().setShouldNoClip(false);
                    teleport.playerComponent().sync();
                }); // Cancel
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
    public void transitionOut(CrossDimensionTeleport crossDimensionTeleport) {

    }

    @Override
    public void transitionIn(CrossDimensionTeleport crossDimensionTeleport) {

    }
}
