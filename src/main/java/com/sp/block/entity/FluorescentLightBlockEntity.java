package com.sp.block.entity;

import com.sp.ConfigStuff;
import com.sp.block.ModBlocks;
import com.sp.block.custom.FluorescentLightBlock;
import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.WorldEvents;
import com.sp.sounds.instances.FluorescentLightSoundInstance;
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
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class FluorescentLightBlockEntity extends BlockEntity {
    BlockState currentState;
    private boolean playingSound;
    public PointLight pointLight;
    private boolean prevOn;
    private final int randInt;
    private int ticks = 0;

    public FluorescentLightBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FLUORESCENT_LIGHT_BLOCK_ENTITY, pos, state);
        java.util.Random random = new java.util.Random();

        this.playingSound = false;
        this.currentState = state;
        this.randInt = random.nextInt(2,9);
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
            prevOn = world.getBlockState(pos).get(FluorescentLightBlock.ON);
            ticks++;
            WorldEvents events = InitializeComponents.EVENTS.get(world);
            Random random = Random.create();
            java.util.Random random1 = new java.util.Random();
            Vec3d position = pos.toCenterPos();
            this.currentState = state;

            if (!world.isClient) {
                //Set to ceiling tile if it can't be seen
                if (world.getRegistryKey() == BackroomsLevels.LEVEL0_WORLD_KEY) {
                    if (world.getBlockState(pos.down()) != Blocks.AIR.getDefaultState()) {
                        world.setBlockState(pos, ModBlocks.CeilingTile.getDefaultState());
                        this.markRemoved();
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
                    } else if (northOWest == 2) {
                        world.setBlockState(pos, westState.with(FluorescentLightBlock.COPY, true));
                    }
                } else {
                    if (world.getBlockState(pos) == ModBlocks.FluorescentLight.getDefaultState().with(FluorescentLightBlock.COPY, true)) {
                        world.setBlockState(pos, ModBlocks.FluorescentLight.getDefaultState().with(FluorescentLightBlock.COPY, false));
                    }

                    //Turn off if Blackout Event is active
                    if (events.isLevel0Blackout()) {
                        if (world.getBlockState(pos) == ModBlocks.FluorescentLight.getDefaultState()) {
                            world.setBlockState(pos, world.getBlockState(pos).with(FluorescentLightBlock.BLACKOUT, true));
                            this.setPlayingSound(false);
                        }
                    }

                    if (!events.isLevel0On() && world.getBlockState(pos) == ModBlocks.FluorescentLight.getDefaultState().with(FluorescentLightBlock.ON, true)) {
                        world.setBlockState(pos, world.getBlockState(pos).with(FluorescentLightBlock.ON, false));
                    }

                    if (events.isLevel0Flicker() && world.getBlockState(pos) == ModBlocks.FluorescentLight.getDefaultState().with(FluorescentLightBlock.BLACKOUT, false)) {
                        if (ticks % randInt == 0) {
                            int i = random.nextBetween(1, 2);
                            if (i == 1) {
                                world.setBlockState(pos, world.getBlockState(pos).with(FluorescentLightBlock.ON, true));
                            } else {
                                world.setBlockState(pos, world.getBlockState(pos).with(FluorescentLightBlock.ON, false));
                            }
                        }
                    } else {
                        if (world.getBlockState(pos) == ModBlocks.FluorescentLight.getDefaultState().with(FluorescentLightBlock.ON, false) && events.isLevel0On()) {
                            world.setBlockState(pos, world.getBlockState(pos).with(FluorescentLightBlock.ON, true));
                        }
                    }


                }
            }

            if (world.getBlockState(pos).getBlock() == ModBlocks.FluorescentLight) {
                if (prevOn != world.getBlockState(pos).get(FluorescentLightBlock.ON)) {
                    if (!world.isClient)
                        world.playSound(null, pos, ModSounds.LIGHT_BLINK, SoundCategory.AMBIENT, 1000.0F, random1.nextFloat(0.9f, 1.1f));
                }
            }


            if (world.isClient) {
                PlayerEntity player = MinecraftClient.getInstance().player;
                if (player != null) {
                    Vec3d playerPos = player.getPos();
                    boolean withinDistance = pos.isWithinDistance(playerPos, ConfigStuff.lightRenderDistance);
                    if (withinDistance) {
                        if (world.getBlockState(pos) == ModBlocks.FluorescentLight.getDefaultState()
                                .with(FluorescentLightBlock.COPY, false)
                                .with(FluorescentLightBlock.ON, true)
                                .with(FluorescentLightBlock.BLACKOUT, false)) {
                            if (!this.isPlayingSound() && pos.isWithinDistance(playerPos, 12.0f) && world.getBlockState(pos) != ModBlocks.FluorescentLight.getDefaultState().with(FluorescentLightBlock.BLACKOUT, true)) {
                                MinecraftClient.getInstance().getSoundManager().play(new FluorescentLightSoundInstance(this, player));
                                this.setPlayingSound(true);
                            }

                            if (this.pointLight == null) {
                                this.pointLight = new PointLight();
                                VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().addLight(this.pointLight
                                        .setRadius(13f)
                                        .setColor(255, 240, 130)
                                        .setPosition(position.x, position.y - 0.5, position.z)
                                        .setBrightness(0.003f)
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
