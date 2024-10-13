package com.sp.block.entity;

import com.sp.ConfigStuff;
import com.sp.block.ModBlocks;
import com.sp.block.custom.FluorescentLightBlock;
import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.WorldEvents;
import com.sp.sounds.FluorescentLightSoundInstance;
import com.sp.sounds.ModSounds;
import com.sp.world.levels.BackroomsLevels;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.deferred.light.PointLight;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class FluorescentLightBlockEntity extends BlockEntity {
    BlockState currentState;
    private boolean playingSound;
    public PointLight pointLight;
    private boolean on;

    public FluorescentLightBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FLUORESCENT_LIGHT_BLOCK_ENTITY, pos, state);
        this.playingSound = false;
        this.on = true;
        this.currentState = state;
    }

    @Override
    public void markRemoved() {
        if(world != null) {
            if (world.isClient) {
                this.setPlayingSound(false);
                on = false;
                if (this.pointLight != null) {
                    VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().removeLight(this.pointLight);
                    this.pointLight = null;
                }
            }
        }
        super.markRemoved();
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        WorldEvents events = InitializeComponents.EVENTS.get(world);
        Vec3d position = pos.toCenterPos();
        this.currentState = state;

        if (!world.isClient){
            //Set to ceiling tile if it can't be seen
            if (world.getRegistryKey() == BackroomsLevels.LEVEL0_WORLD_KEY) {
                if (world.getBlockState(pos.down()) != Blocks.AIR.getDefaultState()) {
                    world.setBlockState(pos, ModBlocks.CeilingTile.getDefaultState());
                    on = false;
                }
            }

            //Turn off if Blackout Event is active
            if(events.isLevel0Blackout()) {
                if (world.getBlockState(pos) == ModBlocks.FluorescentLight.getDefaultState()) {
                    world.setBlockState(pos, world.getBlockState(pos).with(FluorescentLightBlock.BLACKOUT, true));
                    this.setPlayingSound(false);
                    on = false;
                }
            }

        }


        if (on && world.getBlockState(pos) == ModBlocks.FluorescentLight.getDefaultState().with(FluorescentLightBlock.BLACKOUT, false)) {
            if (world.isClient) {
                PlayerEntity player = MinecraftClient.getInstance().player;
                if (player != null) {
                    Vec3d playerPos = player.getPos();
                    if (!this.isPlayingSound() && pos.isWithinDistance(playerPos, 12.0f) && world.getBlockState(pos) != ModBlocks.FluorescentLight.getDefaultState().with(FluorescentLightBlock.BLACKOUT, true)) {
                        MinecraftClient.getInstance().getSoundManager().play(new FluorescentLightSoundInstance(this, player));
                        this.setPlayingSound(true);
                    }

                    if (pos.isWithinDistance(playerPos, ConfigStuff.lightRenderDistance)) {
                        if (this.pointLight == null) {
                            this.pointLight = new PointLight();
                            VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().addLight(this.pointLight
                                    .setRadius(13f)
                                    .setColor(255, 240, 130)
                                    .setPosition(position.x, position.y - 0.5, position.z)
                                    .setBrightness(0.0015f)
                            );
                        }
                    }
                    else {
                        if (this.pointLight != null) {
                            VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().removeLight(this.pointLight);
                            this.pointLight = null;
                        }
                    }
                }
            }
        }
        else {
            if (this.pointLight != null) {
                VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().removeLight(this.pointLight);
                this.pointLight = null;
            }
            this.setPlayingSound(false);
        }



    }

    public boolean isPlayingSound() {
        return playingSound;
    }

    public void setPlayingSound(boolean playingSound) {
        this.playingSound = playingSound;
    }

    public BlockState getCurrentState(){
        return this.currentState;
    }

}
