package com.sp.block.entity;

import com.sp.block.custom.FluorescentLightBlock;
import com.sp.init.BackroomsLevels;
import com.sp.init.ModBlockEntities;
import com.sp.init.ModBlocks;
import com.sp.world.levels.custom.Level0BackroomsLevel;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.deferred.light.PointLight;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import static com.sp.clientWrapper.ClientWrapper.doClientSideTick;

public class FluorescentLightBlockEntity extends BlockEntity {
    public BlockState currentState;
    public Random random = Random.create();
    public java.util.Random random1 = new java.util.Random();
    public boolean playingSound;
    public PointLight pointLight;
    public boolean prevOn;
    public final int randInt;
    public int ticks = 0;

    public FluorescentLightBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FLUORESCENT_LIGHT_BLOCK_ENTITY, pos, state);

        this.playingSound = false;
        this.currentState = state;
        this.randInt = this.random.nextBetween(1, 5);
    }

    @Override
    public void markRemoved() {
        super.markRemoved();

        if (world == null) {
            return;
        }

        if (!world.isClient) {
            return;
        }

        this.setPlayingSound(false);

        if (this.pointLight == null) {
            return;
        }

        VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().removeLight(this.pointLight);
        this.pointLight = null;
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (world.getBlockState(pos).getBlock() != ModBlocks.FLUORESCENT_LIGHT) {
            return;
        }

        ticks++;
        this.currentState = state;

        if (!world.isClient) {
            //Set to ceiling tile if it can't be seen
            if (world.getRegistryKey() == BackroomsLevels.LEVEL0_WORLD_KEY) {
                if (world.getBlockState(pos.down()) != Blocks.AIR.getDefaultState()) {
                    world.removeBlockEntity(pos);
                    world.getWorldChunk(pos).blockEntityNbts.remove(pos);
                    world.setBlockState(pos, ModBlocks.CEILING_TILE.getDefaultState());
                    return;
                }
            }


            BlockState northState = world.getBlockState(pos.north());
            BlockState westState = world.getBlockState(pos.west());
            int northOWest = 0;

            if (northState.getBlock() == ModBlocks.FLUORESCENT_LIGHT) {
                northOWest = 1;
            } else if (westState.getBlock() == ModBlocks.FLUORESCENT_LIGHT) {
                northOWest = 2;
            }

            if (northOWest != 0) {
                if (northOWest == 1) {
                    world.setBlockState(pos, northState.with(FluorescentLightBlock.COPY, true));
                } else {
                    world.setBlockState(pos, westState.with(FluorescentLightBlock.COPY, true));
                }
            } else {
                if (state.get(FluorescentLightBlock.COPY)) {
                    world.setBlockState(pos, ModBlocks.FLUORESCENT_LIGHT.getDefaultState().with(FluorescentLightBlock.COPY, false));
                }

                if (!((BackroomsLevels.getLevel(this.getWorld()).orElse(BackroomsLevels.OVERWORLD_REPRESENTING_BACKROOMS_LEVEL)) instanceof Level0BackroomsLevel level)) {
                    return;
                }
                //Turn off if Blackout Event is active
                if (level.getLightState() == Level0BackroomsLevel.LightState.BLACKOUT) {
                    world.setBlockState(pos, world.getBlockState(pos).with(FluorescentLightBlock.BLACKOUT, true));
                }

                if (level.getLightState() != Level0BackroomsLevel.LightState.ON && state.get(FluorescentLightBlock.ON)) {
                    world.setBlockState(pos, world.getBlockState(pos).with(FluorescentLightBlock.ON, false));
                }

                if (level.getLightState() == Level0BackroomsLevel.LightState.FLICKER && !state.get(FluorescentLightBlock.BLACKOUT)) {
                    if (ticks % randInt == 0) {
                        boolean i = this.random.nextBoolean();
                        if (i) {
                            world.setBlockState(pos, world.getBlockState(pos).with(FluorescentLightBlock.ON, true));
                        } else {
                            world.setBlockState(pos, world.getBlockState(pos).with(FluorescentLightBlock.ON, false));
                        }
                    }
                } else {
                    if (!state.get(FluorescentLightBlock.ON) && level.getLightState() == Level0BackroomsLevel.LightState.ON) {
                        world.setBlockState(pos, world.getBlockState(pos).with(FluorescentLightBlock.ON, true));
                    }
                }
            }
        }


        if (world.isClient) {
            doClientSideTick(world, pos, state, this);
        }

        if (ticks > 100) {
            ticks = 1;
        }

        prevOn = world.getBlockState(pos).get(FluorescentLightBlock.ON);
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
