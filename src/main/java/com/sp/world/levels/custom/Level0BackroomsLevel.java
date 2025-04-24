package com.sp.world.levels.custom;

import com.sp.init.BackroomsLevels;
import com.sp.world.events.AbstractEvent;
import com.sp.world.events.level0.Level0Blackout;
import com.sp.world.events.level0.Level0Flicker;
import com.sp.world.events.level0.Level0IntercomBasic;
import com.sp.world.events.level0.Level0Music;
import com.sp.world.generation.Level0ChunkGenerator;
import com.sp.world.levels.BackroomsLevel;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Level0BackroomsLevel extends BackroomsLevel {
    private int blackoutCount = 0;
    private int intercomCount = 0;
    private LightState lightState = LightState.ON;

    public Level0BackroomsLevel() {
        super("level0", Level0ChunkGenerator.CODEC, new BlockPos(1, 22, 1), BackroomsLevels.LEVEL0_WORLD_KEY);
    }

    @Override
    public void register() {
        super.register();
        events.add(Level0Blackout::new);
        events.add(Level0Flicker::new);
        events.add(Level0IntercomBasic::new);
        events.add(Level0Music::new);
    }

    @Override
    public AbstractEvent getRandomEvent(World world) {
        AbstractEvent activeEvent = super.getRandomEvent(world);

        if (activeEvent instanceof Level0Blackout) {
            this.blackoutCount++;
            if (this.blackoutCount > 2) {
                while (activeEvent instanceof Level0Blackout) {
                    activeEvent = super.getRandomEvent(world);
                }
            }
        }

        return activeEvent;
    }

    @Override
    public int nextEventDelay() {
        return random.nextInt(1000, 1500);
    }

    @Override
    public void writeToNbt(NbtCompound nbt) {
        nbt.putInt("blackoutCount", blackoutCount);
        nbt.putInt("intercomCount", intercomCount);
        nbt.putString("lightState", lightState.name());
    }

    @Override
    public void readFromNbt(NbtCompound nbt) {
        this.blackoutCount = nbt.getInt("blackoutCount");
        this.intercomCount = nbt.getInt("intercomCount");
        this.lightState = LightState.valueOf(nbt.getString("lightState"));
    }

    public int getIntercomCount() {
        return intercomCount;
    }

    public void setIntercomCount(int intercomCount) {
        this.justChanged();
        this.intercomCount = intercomCount;
    }

    public void addIntercomCount() {
        this.justChanged();
        this.intercomCount++;
    }

    public void setLightState(LightState lightState) {
        this.justChanged();
        this.lightState = lightState;
    }

    public LightState getLightState() {
        return this.lightState;
    }

    public enum LightState {
        ON,
        OFF,
        FLICKER,
        BLACKOUT
    }
}
