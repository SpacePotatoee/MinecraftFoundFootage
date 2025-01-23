package com.sp.block.entity;

import com.sp.ConfigStuff;
import com.sp.SPBRevampedClient;
import com.sp.init.ModBlockEntities;
import com.sp.init.ModBlocks;
import com.sp.block.custom.FluorescentLightBlock;
import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.WorldEvents;
import com.sp.sounds.FluorescentLightSoundInstance;
import com.sp.init.ModSounds;
import com.sp.init.BackroomsLevels;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.deferred.light.PointLight;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class FluorescentLightBlockEntity extends BlockEntity {
    BlockState currentState;
    private Random random = Random.create();
    private java.util.Random random1 = new java.util.Random();
    private boolean playingSound;
    public PointLight pointLight;
    private boolean prevOn;
    private final int randInt;
    private int ticks = 0;

    public FluorescentLightBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FLUORESCENT_LIGHT_BLOCK_ENTITY, pos, state);

        this.playingSound = false;
        this.currentState = state;
        this.randInt = this.random.nextBetween(1,5);
    }

    @Override
    public void markRemoved() {
        if(world != null) {
            if (world.isClient) {
                this.setPlayingSound(false);
                if (this.pointLight != null) {
                    VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().removeLight(this.pointLight);
                    this.pointLight = null;
                }
            }
        }
        super.markRemoved();
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if(world.getBlockState(pos).getBlock() == ModBlocks.FluorescentLight) {
            ticks++;
            WorldEvents events = InitializeComponents.EVENTS.get(world);
            Vec3d position = pos.toCenterPos();
            this.currentState = state;

            if (!world.isClient) {
                //Set to ceiling tile if it can't be seen
                if (world.getRegistryKey() == BackroomsLevels.LEVEL0_WORLD_KEY) {
                    if (world.getBlockState(pos.down()) != Blocks.AIR.getDefaultState()) {
                        world.removeBlockEntity(pos);
                        world.getWorldChunk(pos).blockEntityNbts.remove(pos);
                        world.setBlockState(pos, ModBlocks.CeilingTile.getDefaultState());
                        return;
                    }
                }


                BlockState northState = world.getBlockState(pos.north());
                BlockState westState = world.getBlockState(pos.west());
                int northOWest = 0;

                if (northState.getBlock() == ModBlocks.FluorescentLight) {
                    northOWest = 1;
                } else if (westState.getBlock() == ModBlocks.FluorescentLight) {
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
                        world.setBlockState(pos, ModBlocks.FluorescentLight.getDefaultState().with(FluorescentLightBlock.COPY, false));
                    }

                    //Turn off if Blackout Event is active
                    if (events.isLevel0Blackout()) {
                        world.setBlockState(pos, world.getBlockState(pos).with(FluorescentLightBlock.BLACKOUT, true));
                    }

                    if (!events.isLevel0On() && state.get(FluorescentLightBlock.ON)) {
                        world.setBlockState(pos, world.getBlockState(pos).with(FluorescentLightBlock.ON, false));
                    }

                    if (events.isLevel0Flicker() && !state.get(FluorescentLightBlock.BLACKOUT)) {
                        if (ticks % randInt == 0) {
                            boolean i = this.random.nextBoolean();
                            if (i) {
                                world.setBlockState(pos, world.getBlockState(pos).with(FluorescentLightBlock.ON, true));
                            } else {
                                world.setBlockState(pos, world.getBlockState(pos).with(FluorescentLightBlock.ON, false));
                            }
                        }
                    } else {
                        if (!state.get(FluorescentLightBlock.ON) && events.isLevel0On()) {
                            world.setBlockState(pos, world.getBlockState(pos).with(FluorescentLightBlock.ON, true));
                        }
                    }


                }


            }




            if (world.isClient) {
                MinecraftClient client = MinecraftClient.getInstance();
                PlayerEntity player = client.player;

                if (player != null) {

                    if(!state.get(FluorescentLightBlock.COPY)) {
                        if (pos.isWithinDistance(player.getPos(), 20)) {
                            if (prevOn != world.getBlockState(pos).get(FluorescentLightBlock.ON)) {
                                client.getSoundManager().play(new PositionedSoundInstance(ModSounds.LIGHT_BLINK, SoundCategory.AMBIENT, 0.1F, random1.nextFloat(0.9f, 1.1f), this.random, pos));
                            }
                        }
                    }

                    Vec3d playerPos = player.getPos();
                    boolean withinDistance = pos.isWithinDistance(playerPos, ConfigStuff.lightRenderDistance);
                    if (withinDistance) {
                        if (!state.get(FluorescentLightBlock.COPY) &&
                                state.get(FluorescentLightBlock.ON) &&
                                !state.get(FluorescentLightBlock.BLACKOUT)) {
                            if (!this.isPlayingSound() && pos.isWithinDistance(playerPos, 16.0f) && !state.get(FluorescentLightBlock.BLACKOUT)  && !SPBRevampedClient.blackScreen) {
                                MinecraftClient.getInstance().getSoundManager().play(new FluorescentLightSoundInstance(this, player));
                                this.setPlayingSound(true);
                            }

                            if (this.pointLight == null) {
                                this.pointLight = new PointLight();
                                VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().addLight(this.pointLight
                                        .setRadius(13f)
                                        .setColor((float) 255 /255, (float) 240 /255, (float) 100 /255)
                                        .setPosition(position.x, position.y - 1, position.z)
                                        .setBrightness(1.0f)
                                );
                            }
                        } else {
                            if (this.pointLight != null) {
                                VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().removeLight(this.pointLight);
                                this.pointLight = null;
                            }
                            this.setPlayingSound(false);
                        }

                    } else {
                        if (this.pointLight != null) {
                            VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().removeLight(this.pointLight);
                            this.pointLight = null;
                        }
                    }
                }
            }

            if (ticks > 100) {
                ticks = 1;
            }
            prevOn = world.getBlockState(pos).get(FluorescentLightBlock.ON);
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
