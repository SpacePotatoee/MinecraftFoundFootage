package com.sp.block.entity;

import com.sp.ConfigStuff;
import com.sp.init.ModBlockEntities;
import com.sp.init.ModBlocks;
import com.sp.block.custom.ThinFluorescentLightBlock;
import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.WorldEvents;
import com.sp.init.ModSounds;
import com.sp.sounds.ThinFluorescentLightSoundInstance;
import com.sp.world.BackroomsLevels;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.deferred.light.PointLight;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import static com.sp.block.custom.ThinFluorescentLightBlock.*;

public class ThinFluorescentLightBlockEntity extends BlockEntity {
    BlockState currentState;
    private boolean playingSound;
    public PointLight pointLight;
    private boolean prevOn;
    private final int randInt;
    private int ticks = 0;

    public ThinFluorescentLightBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.THIN_FLUORESCENT_LIGHT_BLOCK_ENTITY, pos, state);
        java.util.Random random = new java.util.Random();

        this.currentState = state;
        this.playingSound = false;
        this.randInt = random.nextInt(1,8);
    }

    @Override
    public void markRemoved() {
        world = this.getWorld();
        if (world.isClient){
            if(pointLight != null) {
                VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().removeLight(pointLight);
                pointLight = null;
            }
        }

        super.markRemoved();
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        prevOn = world.getBlockState(pos).get(ThinFluorescentLightBlock.ON);
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

            if (northState.getBlock() == ModBlocks.ThinFluorescentLight) {
                northOWest = 1;
            } else if (westState.getBlock() == ModBlocks.ThinFluorescentLight) {
                northOWest = 2;
            } else if (downState.getBlock() == ModBlocks.ThinFluorescentLight) {
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
                if(state.get(ThinFluorescentLightBlock.COPY)) {
                    world.setBlockState(pos, ModBlocks.ThinFluorescentLight.getDefaultState().with(ThinFluorescentLightBlock.COPY, false));
                }

                //Turn off if Blackout Event is active
                if (events.isLevel1Blackout() || events.isLevel2Blackout()) {
                    if (world.getBlockState(pos).getBlock() == ModBlocks.ThinFluorescentLight) {
                        world.setBlockState(pos, world.getBlockState(pos).with(ThinFluorescentLightBlock.BLACKOUT, true));
                        this.setPlayingSound(false);
                    }
                }

                if (events.isLevel1Flicker() && !state.get(ThinFluorescentLightBlock.BLACKOUT)) {
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

        if (world.getBlockState(pos).getBlock() == ModBlocks.ThinFluorescentLight) {
            if (prevOn != world.getBlockState(pos).get(ThinFluorescentLightBlock.ON)) {
                if (!world.isClient)
                    world.playSound(null, pos, ModSounds.LIGHT_BLINK, SoundCategory.AMBIENT, 0.2F, random1.nextFloat(0.9f, 1.1f));
            }
        }

        if(world.isClient) {
            PlayerEntity player = MinecraftClient.getInstance().player;
            if (player != null) {
                Vec3d playerPos = player.getPos();
                boolean withinDistance = pos.isWithinDistance(playerPos, ConfigStuff.lightRenderDistance);
                if(withinDistance) {
                    if (!state.get(ThinFluorescentLightBlock.COPY) && state.get(ThinFluorescentLightBlock.ON) && !state.get(ThinFluorescentLightBlock.BLACKOUT))
                    {
                        if (!this.isPlayingSound() && pos.isWithinDistance(playerPos, 15.0f)) {
                            MinecraftClient.getInstance().getSoundManager().play(new ThinFluorescentLightSoundInstance(this, player));
                            this.setPlayingSound(true);
                        }

                        if (pointLight == null) {
                            pointLight = new PointLight();
                            VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().addLight(pointLight
                                    .setRadius(18f)
                                    .setColor(255, 255, 255)
                                    .setBrightness(0.0024f)
                            );
                            switch ((WallMountLocation) state.get(FACE)) {
                                case FLOOR:
                                    pointLight.setPosition(position.x, position.y, position.z);
                                case WALL:
                                    switch ((Direction) state.get(FACING)) {
                                        case EAST:
                                            pointLight.setPosition(position.x, position.y, position.z + 0.5);
                                        case WEST:
                                            pointLight.setPosition(position.x, position.y, position.z - 0.5);
                                        case SOUTH:
                                            pointLight.setPosition(position.x + 0.5, position.y, position.z);
                                        case NORTH:
                                        default:
                                            pointLight.setPosition(position.x - 0.5, position.y, position.z);
                                    }
                                case CEILING:
                                default:
                                    pointLight.setPosition(position.x, position.y, position.z);

                            }
                            if (world.getRegistryKey() == BackroomsLevels.LEVEL2_WORLD_KEY) {
                                pointLight
                                        .setColor(200, 200, 255)
                                        .setBrightness(0.005f);
                            }
                        }
                    } else {
                        if (pointLight != null) {
                            VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().removeLight(pointLight);
                            pointLight = null;
                        }
                    }
                } else {
                    if (pointLight != null) {
                        VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().removeLight(pointLight);
                        pointLight = null;
                    }
                }
            }
        }

        if (ticks > 100) {
            ticks = 1;
        }

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
