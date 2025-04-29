package com.sp.world.levels.custom;

import com.sp.world.levels.WorldRepresentingBackroomsLevel;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class OverworldRepresentingBackroomsLevel extends WorldRepresentingBackroomsLevel {
    public OverworldRepresentingBackroomsLevel() {
        super("overworld", new Vec3d(1.5, 22, 1.5), World.OVERWORLD);
    }

    @Override
    public void register() {
        /*
        this.registerTransition((world, playerComponent, from) -> {
            PlayerEntity player = playerComponent.player;

            if (player.isInsideWall()) {
                if (player.getWorld().getRegistryKey() == World.OVERWORLD && !playerComponent.isDoingCutscene()) {
                    playerComponent.suffocationTimer++;
                    if (playerComponent.suffocationTimer == 1) {
                        if (!world.isClient()) {
                            SPBRevamped.sendPersonalPlaySoundPacket((ServerPlayerEntity) player, ModSounds.GLITCH, 1.0f, 1.0f);
                        }
                        playerComponent.setShouldGlitch(true);
                    }

                    if (playerComponent.suffocationTimer == 40) {
                        playerComponent.savePlayerInventory();
                        playerComponent.player.getInventory().clear();

                        playerComponent.setDoingCutscene(true);
                        playerComponent.sync();
                        playerComponent.suffocationTimer = 0;

                        return List.of(new CrossDimensionTeleport(world, playerComponent, from.getSpawnPos(), from, BackroomsLevels.LEVEL0_BACKROOMS_LEVEL));
                    }
                }
            } else {
                if (playerComponent.shouldGlitch()) {
                    StopSoundS2CPacket stopSoundS2CPacket = new StopSoundS2CPacket(new Identifier(SPBRevamped.MOD_ID, "glitch"), null);
                    ((ServerPlayerEntity) player).networkHandler.sendPacket(stopSoundS2CPacket);
                }
                playerComponent.setShouldGlitch(false);
                playerComponent.suffocationTimer = 0;
            }

            return List.of();
        }, this.getLevelId());

         */
    }

    @Override
    public int nextEventDelay() {
        return 0;
    }

    @Override
    public void writeToNbt(NbtCompound nbt) {

    }

    @Override
    public void readFromNbt(NbtCompound nbt) {

    }

    @Override
    public boolean transitionOut(CrossDimensionTeleport crossDimensionTeleport) {
        return true;
    }

    @Override
    public void transitionIn(CrossDimensionTeleport crossDimensionTeleport) {
        crossDimensionTeleport.playerComponent().loadPlayerSavedInventory();
    }

    @Override
    public int getTransitionDuration() {
        return 0;
    }
}
