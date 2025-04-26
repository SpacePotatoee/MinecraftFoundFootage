package com.sp.world.levels.custom;

import com.sp.SPBRevampedClient;
import com.sp.cca_stuff.PlayerComponent;
import com.sp.init.BackroomsLevels;
import com.sp.init.ModSounds;
import com.sp.world.events.level1.Level1Ambience;
import com.sp.world.events.level1.Level1Blackout;
import com.sp.world.events.level1.Level1Flicker;
import com.sp.world.generation.Level1ChunkGenerator;
import com.sp.world.levels.BackroomsLevel;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Level1BackroomsLevel extends BackroomsLevel {
    private Level0BackroomsLevel.LightState lightState = Level0BackroomsLevel.LightState.ON;

    public Level1BackroomsLevel() {
        super("level1", Level1ChunkGenerator.CODEC, new Vec3d(6, 22, 3), BackroomsLevels.LEVEL1_WORLD_KEY);
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

    @Override
    public boolean transitionOut(BackroomsLevel to, PlayerComponent playerComponent, World world) {
        if (world.isClient()) {
            ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

            //Turn off the lights
            playerComponent.player.playSound(ModSounds.LIGHTS_OUT, SoundCategory.AMBIENT, 1, 1);
            SPBRevampedClient.getCutsceneManager().blackScreen.showBlackScreen(80, false, false);

            //PlaySound after black screen is over
            executorService.schedule(() -> {
                playerComponent.player.playSound(ModSounds.LIGHTS_ON, SoundCategory.AMBIENT, 1, 1);
                executorService.shutdown();
            }, 4000, TimeUnit.MILLISECONDS);
        }

        return true;
    }

    @Override
    public void transitionIn(BackroomsLevel from, PlayerComponent playerComponent, World world) {

    }

    public void setLightState(Level0BackroomsLevel.LightState lightState) {
        this.justChanged();
        this.lightState = lightState;
    }

    public Level0BackroomsLevel.LightState getLightState() {
        return this.lightState;
    }
}
