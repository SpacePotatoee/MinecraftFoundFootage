package com.sp.sounds;

import com.sp.SPBRevampedClient;
import com.sp.block.entity.EmergencyLightBlockEntity;
import com.sp.init.BackroomsLevels;
import com.sp.init.ModSounds;
import com.sp.world.levels.custom.Level0BackroomsLevel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class EmergencyAlarmSoundInstance extends MovingSoundInstance {
    private BlockEntity entity;
    private PlayerEntity player;

    public EmergencyAlarmSoundInstance(BlockEntity entity, PlayerEntity player) {
        super(ModSounds.EMERGENCY_LIGHT_ALARM, SoundCategory.BLOCKS, SoundInstance.createRandom());
        this.x = (float) entity.getPos().toCenterPos().x;
        this.y = (float) entity.getPos().toCenterPos().y;
        this.z = (float) entity.getPos().toCenterPos().z;
        this.entity = entity;
        this.player = player;
        this.pitch = 1.0F;
        this.volume = 3.0F;
    }

    @Override
    public boolean shouldAlwaysPlay() {
        return true;
    }

    @Override
    public boolean isRepeatable() {
        return true;
    }

    @Override
    public void tick() {
        World world = this.entity.getWorld();

        if (!((BackroomsLevels.getLevel(world)) instanceof Level0BackroomsLevel level)) {
            return;
        }

        if(world != null) {
            if (!this.entity.isRemoved() &&
                    this.entity.getPos().isWithinDistance(player.getPos(), 80.0f) &&
                    level.getLightState() != Level0BackroomsLevel.LightState.BLACKOUT &&
                    !SPBRevampedClient.blackScreen)
            {
                this.pitch = 1.0F;
                this.volume = 10.0F;
            } else {
                this.setDone();
                ((EmergencyLightBlockEntity) entity).setEmergencyAlarm(false);
            }
        }
    }
}
