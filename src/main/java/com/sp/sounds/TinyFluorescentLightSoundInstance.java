package com.sp.sounds;

import com.sp.SPBRevampedClient;
import com.sp.block.custom.ThinFluorescentLightBlock;
import com.sp.block.entity.TinyFluorescentLightBlockEntity;
import com.sp.init.BackroomsLevels;
import com.sp.init.ModSounds;
import com.sp.world.levels.custom.Level0BackroomsLevel;
import com.sp.world.levels.custom.Level1BackroomsLevel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class TinyFluorescentLightSoundInstance extends MovingSoundInstance {
    private final BlockEntity entity;
    private final PlayerEntity player;

    public TinyFluorescentLightSoundInstance(BlockEntity entity, PlayerEntity player) {
        super(ModSounds.FLUORESCENT_LIGHT_HUM2, SoundCategory.BLOCKS, SoundInstance.createRandom());
        this.x = (float) entity.getPos().toCenterPos().x;
        this.y = (float) entity.getPos().toCenterPos().y;
        this.z = (float) entity.getPos().toCenterPos().z;
        this.entity = entity;
        this.player = player;
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

        if(world != null) {
            boolean blackedOut = false;

            if ((BackroomsLevels.getLevel(world).orElse(BackroomsLevels.OVERWORLD_REPRESENTING_BACKROOMS_LEVEL)) instanceof Level0BackroomsLevel level) {
                blackedOut = level.getLightState() == Level0BackroomsLevel.LightState.BLACKOUT;
            }

            if ((BackroomsLevels.getLevel(world).orElse(BackroomsLevels.OVERWORLD_REPRESENTING_BACKROOMS_LEVEL)) instanceof Level1BackroomsLevel level) {
                blackedOut = level.getLightState() == Level0BackroomsLevel.LightState.BLACKOUT;
            }

            if (!this.entity.isRemoved() &&
                    entity.getPos().isWithinDistance(player.getPos(), 15.0f) &&
                    !blackedOut &&
                    ((TinyFluorescentLightBlockEntity) entity).getCurrentState().get(ThinFluorescentLightBlock.ON) &&
                    !((TinyFluorescentLightBlockEntity) entity).getCurrentState().get(ThinFluorescentLightBlock.BLACKOUT) &&
                    !SPBRevampedClient.blackScreen) {
                this.pitch = 1.0F;
                this.volume = 0.2F;
            } else {
                this.setDone();
                ((TinyFluorescentLightBlockEntity) entity).setPlayingSound(false);
            }
        }
    }
}
