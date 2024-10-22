package com.sp.sounds.instances;

import com.sp.block.ModBlocks;
import com.sp.block.custom.FluorescentLightBlock;
import com.sp.block.custom.ThinFluorescentLightBlock;
import com.sp.block.entity.FluorescentLightBlockEntity;
import com.sp.block.entity.ThinFluorescentLightBlockEntity;
import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.WorldEvents;
import com.sp.sounds.ModSounds;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.world.World;

public class ThinFluorescentLightSoundInstance extends MovingSoundInstance {
    private BlockEntity entity;
    private PlayerEntity player;

    public ThinFluorescentLightSoundInstance(BlockEntity entity, PlayerEntity player) {
        super(ModSounds.FLUORESCENT_LIGHT_HUM2, SoundCategory.AMBIENT, SoundInstance.createRandom());
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
            WorldEvents events = InitializeComponents.EVENTS.get(world);
            if (!this.entity.isRemoved() &&
                    entity.getPos().isWithinDistance(player.getPos(), 15.0f) &&
                    !events.isLevel1Blackout() &&
                    !events.isLevel2Blackout() &&
                    ((ThinFluorescentLightBlockEntity) entity).getCurrentState().get(ThinFluorescentLightBlock.ON) &&
                    !((ThinFluorescentLightBlockEntity) entity).getCurrentState().get(ThinFluorescentLightBlock.BLACKOUT)) {
                this.pitch = 1.0F;
                this.volume = 0.2F;
            } else {
                this.setDone();
                ((ThinFluorescentLightBlockEntity) entity).setPlayingSound(false);
            }
        }
    }
}
