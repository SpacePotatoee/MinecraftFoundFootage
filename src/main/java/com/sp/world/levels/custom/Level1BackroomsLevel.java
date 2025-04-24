package com.sp.world.levels.custom;

import com.sp.init.BackroomsLevels;
import com.sp.world.events.level1.Level1Ambience;
import com.sp.world.events.level1.Level1Blackout;
import com.sp.world.events.level1.Level1Flicker;
import com.sp.world.generation.Level1ChunkGenerator;
import com.sp.world.levels.BackroomsLevel;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

public class Level1BackroomsLevel extends BackroomsLevel {
    private Level0BackroomsLevel.LightState lightState = Level0BackroomsLevel.LightState.ON;

    public Level1BackroomsLevel() {
        super("level1", Level1ChunkGenerator.CODEC, new BlockPos(6, 22, 3), BackroomsLevels.LEVEL1_WORLD_KEY);
    }

    @Override
    public void register() {
        super.register();

        events.add(Level1Blackout::new);
        events.add(Level1Flicker::new);
        events.add(Level1Ambience::new);
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

    public void setLightState(Level0BackroomsLevel.LightState lightState) {
        this.justChanged();
        this.lightState = lightState;
    }

    public Level0BackroomsLevel.LightState getLightState() {
        return this.lightState;
    }
}
