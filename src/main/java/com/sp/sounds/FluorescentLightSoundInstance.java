package com.sp.sounds;

import com.sp.block.ModBlocks;
import com.sp.block.custom.FluorescentLightBlock;
import com.sp.block.entity.FluorescentLightBlockEntity;
import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.WorldEvents;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class FluorescentLightSoundInstance extends MovingSoundInstance {
    private BlockEntity entity;
    private PlayerEntity player;

    public FluorescentLightSoundInstance(BlockEntity entity, PlayerEntity player) {
        super(ModSounds.FLUORESCENT_LIGHT_HUM, SoundCategory.AMBIENT, SoundInstance.createRandom());
        this.x = (float) entity.getPos().toCenterPos().x;
        this.y = (float) entity.getPos().toCenterPos().y - 0.5;
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
            WorldEvents events = InitializeComponents.EVENTS.get(world);
            if (!this.entity.isRemoved() && entity.getPos().isWithinDistance(player.getPos(), 12.0f) && !events.isLevel0Blackout()) {
                this.pitch = 1.0F;
                this.volume = 0.2F;
            } else {
                this.setDone();
                ((FluorescentLightBlockEntity) entity).setPlayingSound(false);
            }
        }
    }
}
