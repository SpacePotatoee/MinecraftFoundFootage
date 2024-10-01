package com.sp.block.entity;

import com.sp.ConfigStuff;
import com.sp.block.custom.PoolroomsWindowBlock;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.deferred.light.AreaLight;
import net.minecraft.block.BlockState;
import net.minecraft.block.FacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Quaternionf;

import static com.sp.block.custom.PoolroomsWindowBlock.FACING;

public class PoolroomsWindowBlockEntity extends BlockEntity {
    AreaLight areaLight1;
    AreaLight areaLight2;

    public PoolroomsWindowBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.POOLROOMS_WINDOW_BLOCK_ENTITY, pos, state);
    }

    @Override
    public void markRemoved() {
        world = this.getWorld();
        if (world.isClient){
            if(areaLight1 != null) {
                VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().removeLight(this.areaLight1);
                areaLight1 = null;
            }
            if(areaLight2 != null) {
                VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().removeLight(this.areaLight2);
                areaLight2 = null;
            }
        }
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        BlockState state1 = world.getBlockState(pos);
        if(this.getWorld() != null && this.getPos() != null && !this.isRemoved()) {
            if (world.isClient) {
                PlayerEntity player = MinecraftClient.getInstance().player;
                if (player != null) {
                    Vec3d playerPos = player.getPos();

                    if (this.getPos().isWithinDistance(playerPos, ConfigStuff.lightRenderDistance)) {
                        if (state1.get(PoolroomsWindowBlock.TYPE)) {
                            if (state1.get(PoolroomsWindowBlock.FACING) == Direction.SOUTH) {
                                if (areaLight1 == null) {
                                    this.areaLight1 = new AreaLight();
                                    VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().addLight(this.areaLight1
                                            .setColor(255, 255, 220)
                                            .setPosition(pos.getX() + 1, pos.getY(), pos.getZ() + 1)
                                            .setBrightness(0.003f)
                                            .setSize(1, 1)
                                            .setAngle((float) Math.toRadians(0.5))
                                            .setDistance(38)
                                            .setOrientation(new Quaternionf().rotateXYZ((float) Math.toRadians(-90), (float) Math.toRadians(0), 0.0f))
                                    );
                                }
                                if (areaLight2 == null) {
                                    this.areaLight2 = new AreaLight();
                                    VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().addLight(this.areaLight2
                                            .setColor(255, 255, 255)
                                            .setPosition(pos.getX() + 1, pos.getY() + 0.5, pos.getZ() + 1)
                                            .setBrightness(0.005f)
                                            .setSize(0, 0)
                                            .setAngle((float) Math.toRadians(120))
                                            .setDistance(20)
                                            .setOrientation(new Quaternionf().rotateXYZ((float) Math.toRadians(-90), (float) Math.toRadians(0), 0.0f))
                                    );
                                }
                            } else if (state1.get(PoolroomsWindowBlock.FACING) == Direction.NORTH) {
                                if (areaLight1 == null) {
                                    this.areaLight1 = new AreaLight();
                                    VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().addLight(this.areaLight1
                                            .setColor(255, 255, 220)
                                            .setPosition(pos.getX(), pos.getY(), pos.getZ())
                                            .setBrightness(0.003f)
                                            .setSize(1, 1)
                                            .setAngle((float) Math.toRadians(0.5))
                                            .setDistance(38)
                                            .setOrientation(new Quaternionf().rotateXYZ((float) Math.toRadians(-90), (float) Math.toRadians(0), 0.0f))
                                    );
                                }
                                if (areaLight2 == null) {
                                    this.areaLight2 = new AreaLight();
                                    VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().addLight(this.areaLight2
                                            .setColor(255, 255, 255)
                                            .setPosition(pos.getX(), pos.getY() + 0.5, pos.getZ())
                                            .setBrightness(0.005f)
                                            .setSize(0, 0)
                                            .setAngle((float) Math.toRadians(120))
                                            .setDistance(20)
                                            .setOrientation(new Quaternionf().rotateXYZ((float) Math.toRadians(-90), (float) Math.toRadians(0), 0.0f))
                                    );
                                }
                            } else if (state1.get(PoolroomsWindowBlock.FACING) == Direction.EAST) {
                                if (areaLight1 == null) {
                                    this.areaLight1 = new AreaLight();
                                    VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().addLight(this.areaLight1
                                            .setColor(255, 255, 220)
                                            .setPosition(pos.getX() + 1, pos.getY(), pos.getZ())
                                            .setBrightness(0.003f)
                                            .setSize(1, 1)
                                            .setAngle((float) Math.toRadians(0.5))
                                            .setDistance(38)
                                            .setOrientation(new Quaternionf().rotateXYZ((float) Math.toRadians(-90), (float) Math.toRadians(0), 0.0f))
                                    );
                                }
                                if (areaLight2 == null) {
                                    this.areaLight2 = new AreaLight();
                                    VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().addLight(this.areaLight2
                                            .setColor(255, 255, 255)
                                            .setPosition(pos.getX() + 1, pos.getY() + 0.5, pos.getZ())
                                            .setBrightness(0.005f)
                                            .setSize(0, 0)
                                            .setAngle((float) Math.toRadians(120))
                                            .setDistance(20)
                                            .setOrientation(new Quaternionf().rotateXYZ((float) Math.toRadians(-90), (float) Math.toRadians(0), 0.0f))
                                    );
                                }
                            } else if (state1.get(PoolroomsWindowBlock.FACING) == Direction.WEST) {
                                if (areaLight1 == null) {
                                    this.areaLight1 = new AreaLight();
                                    VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().addLight(this.areaLight1
                                            .setColor(255, 255, 220)
                                            .setPosition(pos.getX(), pos.getY(), pos.getZ() + 1)
                                            .setBrightness(0.003f)
                                            .setSize(1, 1)
                                            .setAngle((float) Math.toRadians(0.5))
                                            .setDistance(38)
                                            .setOrientation(new Quaternionf().rotateXYZ((float) Math.toRadians(-90), (float) Math.toRadians(0), 0.0f))
                                    );
                                }
                                if (areaLight2 == null) {
                                    this.areaLight2 = new AreaLight();
                                    VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().addLight(this.areaLight2
                                            .setColor(255, 255, 255)
                                            .setPosition(pos.getX(), pos.getY() + 0.5, pos.getZ() + 1)
                                            .setBrightness(0.005f)
                                            .setSize(0, 0)
                                            .setAngle((float) Math.toRadians(120))
                                            .setDistance(20)
                                            .setOrientation(new Quaternionf().rotateXYZ((float) Math.toRadians(-90), (float) Math.toRadians(0), 0.0f))
                                    );
                                }
                            }
                        } else if (state1.get(PoolroomsWindowBlock.FACING) == Direction.SOUTH) {
                            if (areaLight1 == null) {
                                this.areaLight1 = new AreaLight();
                                VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().addLight(this.areaLight1
                                        .setColor(255, 255, 220)
                                        .setPosition(pos.getX() + 1, pos.getY() - 0.56, pos.getZ() - 0.1)
                                        .setBrightness(0.003f)
                                        .setSize(1, 1.84)
                                        .setAngle((float) Math.toRadians(0.5))
                                        .setDistance(38)
                                        .setOrientation(new Quaternionf().rotateXYZ((float) Math.toRadians(-22), (float) Math.toRadians(0), 0.0f))
                                );
                            }
                            if (areaLight2 == null) {
                                this.areaLight2 = new AreaLight();
                                VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().addLight(this.areaLight2
                                        .setColor(255, 255, 255)
                                        .setPosition(pos.getX() + 1, pos.getY() - 1, pos.getZ() + 0.52)
                                        .setBrightness(0.0035f)
                                        .setSize(0, 0)
                                        .setAngle((float) Math.toRadians(120))
                                        .setDistance(40)
                                        .setOrientation(new Quaternionf().rotateXYZ((float) Math.toRadians(0), (float) Math.toRadians(0), 0.0f))
                                );
                            }
                        } else if (state1.get(PoolroomsWindowBlock.FACING) == Direction.NORTH) {
                            if (areaLight1 == null) {
                                this.areaLight1 = new AreaLight();
                                VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().addLight(this.areaLight1
                                        .setColor(255, 255, 220)
                                        .setPosition(pos.getX(), pos.getY() - 0.72, pos.getZ() + 0.68)
                                        .setBrightness(0.003f)
                                        .setSize(1, 1.84)
                                        .setAngle((float) Math.toRadians(0.5))
                                        .setDistance(38)
                                        .setOrientation(new Quaternionf().rotateXYZ((float) Math.toRadians(-22), (float) Math.toRadians(180), 0.0f))
                                );
                            }
                            if (areaLight2 == null) {
                                this.areaLight2 = new AreaLight();
                                VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().addLight(this.areaLight2
                                        .setColor(255, 255, 255)
                                        .setPosition(pos.getX(), pos.getY() - 1, pos.getZ() - 0.52)
                                        .setBrightness(0.0035f)
                                        .setSize(0, 0)
                                        .setAngle((float) Math.toRadians(120))
                                        .setDistance(40)
                                        .setOrientation(new Quaternionf().rotateXYZ((float) Math.toRadians(0), (float) Math.toRadians(180), 0.0f))
                                );
                            }
                        } else if (state1.get(PoolroomsWindowBlock.FACING) == Direction.EAST) {
                            if (areaLight1 == null) {
                                this.areaLight1 = new AreaLight();
                                VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().addLight(this.areaLight1
                                        .setColor(255, 255, 220)
                                        .setPosition(pos.getX() + 0.32, pos.getY() - 0.72, pos.getZ())
                                        .setBrightness(0.003f)
                                        .setSize(1, 1.84)
                                        .setAngle((float) Math.toRadians(0.5))
                                        .setDistance(38)
                                        .setOrientation(new Quaternionf().rotateXYZ((float) Math.toRadians(-22), (float) Math.toRadians(-90), 0.0f))
                                );
                            }
                            if (areaLight2 == null) {
                                this.areaLight2 = new AreaLight();
                                VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().addLight(this.areaLight2
                                        .setColor(255, 255, 255)
                                        .setPosition(pos.getX(), pos.getY() - 1, pos.getZ())
                                        .setBrightness(0.0035f)
                                        .setSize(0, 0)
                                        .setAngle((float) Math.toRadians(120))
                                        .setDistance(40)
                                        .setOrientation(new Quaternionf().rotateXYZ((float) Math.toRadians(0), (float) Math.toRadians(-90), 0.0f))
                                );
                            }
                        } else if (state1.get(PoolroomsWindowBlock.FACING) == Direction.WEST) {
                            if (areaLight1 == null) {
                                this.areaLight1 = new AreaLight();
                                VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().addLight(this.areaLight1
                                        .setColor(255, 255, 220)
                                        .setPosition(pos.getX() + 0.7, pos.getY() - 0.72, pos.getZ() + 1)
                                        .setBrightness(0.003f)
                                        .setSize(1, 1.86)
                                        .setAngle((float) Math.toRadians(0.5))
                                        .setDistance(38)
                                        .setOrientation(new Quaternionf().rotateXYZ((float) Math.toRadians(-22), (float) Math.toRadians(90), 0.0f))
                                );
                            }
                            if (areaLight2 == null) {
                                this.areaLight2 = new AreaLight();
                                VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().addLight(this.areaLight2
                                        .setColor(255, 255, 255)
                                        .setPosition(pos.getX() + 1, pos.getY() - 1, pos.getZ())
                                        .setBrightness(0.0035f)
                                        .setSize(0, 0)
                                        .setAngle((float) Math.toRadians(120))
                                        .setDistance(40)
                                        .setOrientation(new Quaternionf().rotateXYZ((float) Math.toRadians(0), (float) Math.toRadians(90), 0.0f))
                                );
                            }
                        }
                    } else {
                        if (this.areaLight1 != null) {
                            VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().removeLight(this.areaLight1);
                            this.areaLight1 = null;
                        }
                        if (this.areaLight2 != null) {
                            VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().removeLight(this.areaLight2);
                            this.areaLight2 = null;
                        }
                    }
                }
            }
        }
    }
}
