package com.sp.world.events.level1;

import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.WorldEvents;
import com.sp.entity.custom.SmilerEntity;
import com.sp.init.BackroomsLevels;
import com.sp.init.ModEntities;
import com.sp.init.ModSounds;
import com.sp.world.events.AbstractEvent;
import com.sp.world.levels.custom.Level0BackroomsLevel;
import com.sp.world.levels.custom.Level1BackroomsLevel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.List;

public class Level1Blackout extends AbstractEvent {
    private int smilerSpawnDelay = 80;

    @Override
    public void init(World world) {
        if (!((BackroomsLevels.getLevel(world)) instanceof Level1BackroomsLevel level)) {
            return;
        }

        WorldEvents events = InitializeComponents.EVENTS.get(world);
        if(level.getLightState() != Level0BackroomsLevel.LightState.BLACKOUT) {
            level.setLightState(Level0BackroomsLevel.LightState.BLACKOUT);
            playSound(world, ModSounds.LIGHTS_OUT);
        }
    }

    @Override
    public void ticks(int ticks, World world) {
        if (world.getRegistryKey() != BackroomsLevels.LEVEL1_WORLD_KEY) {
            return;
        }

        Random random = Random.create();

        List<? extends PlayerEntity> playerList = world.getPlayers();

        this.smilerSpawnDelay--;

        if (this.smilerSpawnDelay >= 0) {
            return;
        }
        for (PlayerEntity player : playerList) {
            int rand = player.getRandom().nextBetween(1, 10);

            if (rand != 1) {
                continue;
            }

            SmilerEntity smiler = ModEntities.SMILER_ENTITY.create(world);

            if (smiler == null) {
                continue;
            }

            BlockPos.Mutable mutable = new BlockPos.Mutable();
            float randomAngle = random.nextFloat() * 360.0f;
            Vec3d spawnPos = new Vec3d(0, 0, 15).rotateY(randomAngle).add(player.getPos());
            if (!world.getBlockState(mutable.set(spawnPos.x, spawnPos.y, spawnPos.z)).blocksMovement()) {
                smiler.refreshPositionAndAngles(Math.floor(spawnPos.x) + 0.5f, spawnPos.y, Math.floor(spawnPos.z) + 0.5f, 0.0f, 0.0f);
                world.spawnEntity(smiler);
                smilerSpawnDelay = 80;
            }
        }
    }

    @Override
    public void reset(World world) {
        super.reset(world);

        if (!((BackroomsLevels.getLevel(world)) instanceof Level1BackroomsLevel level)) {
            return;
        }

        level.setLightState(Level0BackroomsLevel.LightState.ON);
        playSound(world, ModSounds.LIGHTS_ON);
    }


    @Override
    public int duration() {
        return 600;
    }
}
