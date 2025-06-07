package com.sp.world.levels.custom;

import com.sp.SPBRevamped;
import com.sp.init.BackroomsLevels;
import com.sp.world.events.infinite_grass.InfiniteGrassAmbience;
import com.sp.world.generation.InfGrassChunkGenerator;
import com.sp.world.levels.BackroomsLevel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InfiniteGrassBackroomsLevel extends BackroomsLevel {

    public InfiniteGrassBackroomsLevel() {
        super("inf_grass", InfGrassChunkGenerator.CODEC, new Vec3d(0, 31, 0), BackroomsLevels.INFINITE_FIELD_WORLD_KEY);
    }

    @Override
    public void register() {
        super.register();

        events.add(InfiniteGrassAmbience::new);


        /*
         * The transition is kinda ass and so it's not allow the transition out method to be called on the client.
         */
        this.registerTransition((world, playerComponent, from) -> {
            List<CrossDimensionTeleport> playerList = new ArrayList<>();

            if (playerComponent.player.getWorld().getRegistryKey() == BackroomsLevels.INFINITE_FIELD_WORLD_KEY && playerComponent.player.getPos().y > 57.5 && playerComponent.player.isOnGround()) {
                if (playerComponent.player instanceof ServerPlayerEntity) {
                    BlockPos blockPos = ((ServerPlayerEntity)playerComponent.player).getSpawnPointPosition();
                    float f = ((ServerPlayerEntity)playerComponent.player).getSpawnAngle();
                    boolean bl = ((ServerPlayerEntity)playerComponent.player).isSpawnForced();
                    ServerWorld serverWorld = playerComponent.player.getWorld().getServer().getWorld(World.OVERWORLD);
                    Optional<Vec3d> optional = Optional.empty();

                    if (serverWorld != null && blockPos != null) {
                        optional = PlayerEntity.findRespawnPosition(serverWorld, blockPos, f, bl, true);
                    }

                    ServerWorld overworld = playerComponent.player.getWorld().getServer().getWorld(World.OVERWORLD);
                    BlockPos blockPos1 = overworld.getSpawnPos();

                    playerList.add(new CrossDimensionTeleport(world, playerComponent, optional.orElse(blockPos1.toCenterPos()), from, BackroomsLevels.OVERWORLD_REPRESENTING_BACKROOMS_LEVEL));
                }
            }

            return playerList;
        }, this.getLevelId() + "->" + BackroomsLevels.OVERWORLD_REPRESENTING_BACKROOMS_LEVEL.getLevelId());

    }

    @Override
    public int nextEventDelay() {
        return random.nextInt(1000, 1200);
    }

    @Override
    public void writeToNbt(NbtCompound nbt) {

    }

    @Override
    public void readFromNbt(NbtCompound nbt) {

    }

    @Override
    public boolean transitionOut(CrossDimensionTeleport crossDimensionTeleport) {
        crossDimensionTeleport.playerComponent().loadPlayerSavedInventory();
        return true;
    }

    @Override
    public void transitionIn(CrossDimensionTeleport crossDimensionTeleport) {
        SPBRevamped.sendBlackScreenPacket((ServerPlayerEntity) crossDimensionTeleport.playerComponent().player, 60, true, false);
    }

    @Override
    public int getTransitionDuration() {
        return 0;
    }

    @Override
    public BoolTextPair allowsTorch() {
        return new BoolTextPair(false, Text.translatable("spb-revamped.flashlight.wet1").append(Text.translatable("spb-revamped.flashlight.wet2").formatted(Formatting.RED)));
    }

    @Override
    public boolean hasVanillaLighting() {
        return true;
    }
}
