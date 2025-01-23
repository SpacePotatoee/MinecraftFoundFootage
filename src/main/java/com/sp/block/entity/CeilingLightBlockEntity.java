package com.sp.block.entity;

import com.sp.init.ModBlockEntities;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.deferred.light.AreaLight;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.joml.Quaternionf;
import org.joml.Vector3d;

public class CeilingLightBlockEntity extends BlockEntity {
    private AreaLight light;
    private float brightness;
    private float angle;
    private int ticks;
    private Random random = Random.create();
    private int randomInt;
    private boolean on;

    public CeilingLightBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CEILING_LIGHT_BLOCK_ENTITY, pos, state);
        this.randomInt = random.nextBetween(1, 4);
        this.on = true;
    }

    @Override
    public void markRemoved() {
        if (this.light != null) {
            VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().removeLight(this.light);
            this.light = null;
        }
        super.markRemoved();
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if(this.world.isClient) {
            if (this.light == null) {
                Vec3d position = pos.toCenterPos().add(-0.5, -0.06, 0);
                this.brightness = 2.58f;
                this.angle = 60.4f;
                this.light = new AreaLight();
                VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().addLight(this.light
                        .setBrightness(this.brightness)
                        .setSize(0.9, 0.0)
                        .setAngle((float) Math.toRadians(this.angle))
                        .setOrientation(new Quaternionf().rotateXYZ((float) Math.toRadians(-90d), 0, 0))
                        .setPosition(new Vector3d(position.x, position.y, position.z))
                        .setDistance(15)
                );
            }

//            if(!state.get(CeilingLight.STOPPED)) {
//                ticks++;
//
//                if (this.light == null && this.on) {
//                    Vec3d position = pos.toCenterPos().add(-0.5, -0.06, 0);
//                    this.brightness = 2.58f;
//                    this.angle = 60.4f;
//                    this.light = new AreaLight();
//                    VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().addLight(this.light
//                            .setBrightness(this.brightness)
//                            .setSize(0.9, 0.0)
//                            .setAngle((float) Math.toRadians(this.angle))
//                            .setOrientation(new Quaternionf().rotateXYZ((float) Math.toRadians(-90d), 0, 0))
//                            .setPosition(new Vector3d(position.x, position.y, position.z))
//                            .setDistance(15)
//                    );
//                } else if (this.light != null) {
//                    if (ticks % this.randomInt == 0) {
//                        if (random.nextBoolean()) {
//                            this.on = false;
//                        } else {
//                            this.on = true;
//                        }
//                    }
//                }
//
//                if (this.light != null) {
//                    if (!this.on) {
//                        this.brightness = Math.max(this.brightness - 0.5f, 0.0f);
//                        this.angle = Math.max(this.angle - 4.0f, 0.0f);
//                    } else {
//                        this.brightness = 2.58f;
//                        this.angle = 60.4f;
//                    }
//
//                    this.light.setBrightness(this.brightness).setAngle((float) Math.toRadians(this.angle));
//                }
//            } else

        }
    }
}
