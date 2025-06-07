package com.sp.block.entity;

import com.sp.block.custom.FluorescentLightBlock;
import com.sp.block.custom.ThinFluorescentLightBlock;
import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.WorldEvents;
import com.sp.init.BackroomsLevels;
import com.sp.init.ModBlockEntities;
import com.sp.init.ModBlocks;
import com.sp.world.levels.custom.Level0BackroomsLevel;
import com.sp.world.levels.custom.Level1BackroomsLevel;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.deferred.light.PointLight;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import static com.sp.clientWrapper.ClientWrapper.doClientSideThinFluorescentsTick;


public class ThinFluorescentLightBlockEntity extends BlockEntity {
    BlockState currentState;
    public boolean playingSound;
    public PointLight pointLight;
    public boolean prevOn;
    public final int randInt;
    public int ticks = 0;
    public final Random random = Random.create();

    public ThinFluorescentLightBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.THIN_FLUORESCENT_LIGHT_BLOCK_ENTITY, pos, state);
        java.util.Random random = new java.util.Random();

        this.currentState = state;
        this.playingSound = false;
        this.randInt = random.nextInt(1,8);
    }

    @Override
    public void markRemoved() {
        if (this.getWorld().isClient){
            if(pointLight != null) {
                VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().removeLight(pointLight);
                pointLight = null;
            }
        }

        super.markRemoved();
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (world.getBlockState(pos).getBlock() != ModBlocks.THIN_FLUORESCENT_LIGHT) {
            return;
        }

        Vec3d position = pos.toCenterPos();
        WorldEvents events = InitializeComponents.EVENTS.get(world);
        Random random = Random.create();
        java.util.Random random1 = new java.util.Random();
        this.currentState = state;
        ticks++;

        if (!world.isClient) {
            BlockState northState = world.getBlockState(pos.north());
            BlockState westState = world.getBlockState(pos.west());
            BlockState downState = world.getBlockState(pos.down());
            int northOWest = 0;

            if (northState.getBlock() == ModBlocks.THIN_FLUORESCENT_LIGHT) {
                northOWest = 1;
            } else if (westState.getBlock() == ModBlocks.THIN_FLUORESCENT_LIGHT) {
                northOWest = 2;
            } else if (downState.getBlock() == ModBlocks.THIN_FLUORESCENT_LIGHT) {
                northOWest = 3;
            }

            if (northOWest != 0) {
                if (northOWest == 1) {
                    world.setBlockState(pos, northState.with(ThinFluorescentLightBlock.COPY, true));
                } else if (northOWest == 2) {
                    world.setBlockState(pos, westState.with(ThinFluorescentLightBlock.COPY, true));
                } else {
                    world.setBlockState(pos, downState.with(ThinFluorescentLightBlock.COPY, true));
                }
            } else {
                if (state.get(ThinFluorescentLightBlock.COPY)) {
                    world.setBlockState(pos, world.getBlockState(pos).with(ThinFluorescentLightBlock.COPY, false));
                }

                //Turn off if Blackout Event is active
                boolean blackouted = false;

                if ((BackroomsLevels.getLevel(world)) instanceof Level1BackroomsLevel level) {
                    blackouted = level.getLightState() == Level0BackroomsLevel.LightState.BLACKOUT;
                }

                if ((BackroomsLevels.getLevel(world)) instanceof Level0BackroomsLevel level) {
                    if (level.getLightState() == Level0BackroomsLevel.LightState.BLACKOUT) {
                        blackouted = true;
                    }
                }

                if (blackouted) {
                    world.setBlockState(pos, world.getBlockState(pos).with(ThinFluorescentLightBlock.BLACKOUT, true));
                    this.setPlayingSound(false);

                } else {
                    world.setBlockState(pos, world.getBlockState(pos).with(ThinFluorescentLightBlock.BLACKOUT, false));
                }

                if ((BackroomsLevels.getLevel(world)) instanceof Level1BackroomsLevel level && level.getLightState() == Level0BackroomsLevel.LightState.FLICKER && !state.get(ThinFluorescentLightBlock.BLACKOUT)) {
                    if (ticks % randInt == 0) {
                        int i = random.nextBetween(1, 2);
                        if (i == 1) {
                            world.setBlockState(pos, world.getBlockState(pos).with(ThinFluorescentLightBlock.ON, true));
                        } else {
                            world.setBlockState(pos, world.getBlockState(pos).with(ThinFluorescentLightBlock.ON, false));
                        }
                    }
                } else {
                    if (!state.get(ThinFluorescentLightBlock.ON)) {
                        world.setBlockState(pos, world.getBlockState(pos).with(ThinFluorescentLightBlock.ON, true));
                    }
                }
            }


        }

        if (world.isClient) {
            doClientSideThinFluorescentsTick(world, pos, state, random1, position, this);
        }

        if (ticks > 100) {
            ticks = 1;
        }

        prevOn = world.getBlockState(pos).get(FluorescentLightBlock.ON);
    }

    public BlockState getCurrentState(){
        return this.currentState;
    }

    public boolean isPlayingSound() {
        return playingSound;
    }

    public void setPlayingSound(boolean playingSound) {
        this.playingSound = playingSound;
    }

}
