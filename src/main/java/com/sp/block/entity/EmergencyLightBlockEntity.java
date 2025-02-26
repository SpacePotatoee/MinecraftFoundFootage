package com.sp.block.entity;

import com.sp.block.custom.EmergencyLightBlock;
import com.sp.init.ModBlockEntities;
import com.sp.sounds.EmergencyAlarmSoundInstance;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.deferred.light.AreaLight;
import foundry.veil.api.client.render.deferred.light.PointLight;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3d;

public class EmergencyLightBlockEntity extends BlockEntity {
    final float randomOffset;
    boolean initEmergencyLights = false;
    boolean playingEmergencyAlarm = false;
    EmergencyAlarmSoundInstance emergencyAlarmSoundInstance;
    boolean initNormalLights = false;
    AreaLight areaLight1;
    AreaLight areaLight2;
    PointLight pointLight;

    public EmergencyLightBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.EMERGENCY_LIGHT_BLOCK_ENTITY, pos, state);
        this.randomOffset = Random.create().nextFloat() * 180;
    }

    @Override
    public void markRemoved() {
        if(this.initEmergencyLights){
            this.removeEmergencyLights();
        }
        if(this.initNormalLights){
            this.removeNormalLights();
        }
        super.markRemoved();
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if(world.isClient){
            MinecraftClient client = MinecraftClient.getInstance();
            PlayerEntity player = client.player;

            if(player != null) {
                if (state.get(EmergencyLightBlock.RED_LIGHT)) {
                    if (!this.playingEmergencyAlarm) {
                        this.emergencyAlarmSoundInstance = new EmergencyAlarmSoundInstance(this, player);
                        client.getSoundManager().play(this.emergencyAlarmSoundInstance);
                        this.setEmergencyAlarm(true);
                    }

                    AxisAngle4f axisAngle4d = new AxisAngle4f();
                    Quaternionf quaternionf = new Quaternionf();
                    Vec3d centerPos = pos.toCenterPos();
                    switch (state.get(EmergencyLightBlock.FACE)) {
                        case WALL -> {
                            switch (state.get(EmergencyLightBlock.FACING)) {
                                case EAST -> {
                                    axisAngle4d.set(0.0f, 1, 0, 0);
                                    quaternionf.rotateXYZ(0.0f, 0.0f, (float) Math.toRadians(90.0f));
                                    centerPos = centerPos.add(-0.28125, 0.0f, 0.0f);
                                }
                                case WEST -> {
                                    axisAngle4d.set(0.0f, -1, 0, 0);
                                    quaternionf.rotateXYZ(0.0f, 0.0f, (float) Math.toRadians(90.0f));
                                    centerPos = centerPos.add(0.28125, 0.0f, 0.0f);
                                }
                                case NORTH -> {
                                    axisAngle4d.set(0.0f, 0, 0, -1);
                                    quaternionf.rotateXYZ((float) Math.toRadians(90.0f), 0.0f, 0.0f);
                                    centerPos = centerPos.add(0.0f, 0.0f, 0.28125);
                                }
                                case SOUTH -> {
                                    axisAngle4d.set(0.0f, 0, 0, 1);
                                    quaternionf.rotateXYZ((float) Math.toRadians(90.0f), 0.0f, 0.0f);
                                    centerPos = centerPos.add(0.0f, 0.0f, -0.28125);
                                }
                            }

                        }
                        case FLOOR -> {
                            axisAngle4d.set(0.0f, 0, 1, 0);
                            centerPos = centerPos.add(0.0f, -0.28125, 0.0f);
                        }
                        default -> {
                            axisAngle4d.set(0.0f, 0, -1, 0);
                            centerPos = centerPos.add(0.0f, 0.28125, 0.0f);
                        }
                    }

                    this.removeNormalLights();

                    if (!this.initEmergencyLights) {
                        this.areaLight1 = new AreaLight();
                        this.areaLight2 = new AreaLight();
                        this.pointLight = new PointLight();


                        VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().addLight(this.areaLight1
                                .setBrightness(1.0f)
                                .setColor(1.0f, 0.0f, 0.0f)
                                .setSize(0.0, 0.0)
                                .setAngle((float) Math.toRadians(50.0f))
                                .setOrientation(new Quaternionf().rotateXYZ(0, 0, 0))
                                .setPosition(new Vector3d(centerPos.x, centerPos.y, centerPos.z))
                                .setDistance(15)
                        );
                        VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().addLight(this.areaLight2
                                .setBrightness(1.0f)
                                .setColor(1.0f, 0.0f, 0.0f)
                                .setSize(0.0, 0.0)
                                .setAngle((float) Math.toRadians(50.0f))
                                .setOrientation(new Quaternionf().rotateXYZ(0, 0, 0))
                                .setPosition(new Vector3d(centerPos.x, centerPos.y, centerPos.z))
                                .setDistance(15)
                        );
                        VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().addLight(this.pointLight
                                .setBrightness(0.5f)
                                .setColor(1.0f, 0.0f, 0.0f)
                                .setPosition(new Vector3d(centerPos.x, centerPos.y, centerPos.z))
                                .setRadius(15.0f)
                        );
                        this.initEmergencyLights = true;
                    }

                    Quaternionf quaternionf1 = new Quaternionf(quaternionf);
                    this.areaLight1.setOrientation(quaternionf1.rotateLocalY((float) Math.toRadians(this.randomOffset + world.getTime() * 20)));

                    Quaternionf quaternionf2 = new Quaternionf(quaternionf);
                    this.areaLight2.setOrientation(quaternionf2.rotateLocalY((float) Math.toRadians(this.randomOffset + 180.0f + world.getTime() * 20)));

                } else {
                    if(this.playingEmergencyAlarm){
                        client.getSoundManager().stop(this.emergencyAlarmSoundInstance);
                        this.emergencyAlarmSoundInstance = null;
                        this.setEmergencyAlarm(false);
                    }
                    this.removeEmergencyLights();
                    if (!initNormalLights) {
                        this.pointLight = new PointLight();
                        Vec3d centerPos = pos.toCenterPos().add(0.0f, -0.15625f, 0.0f);
                        VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().addLight(this.pointLight
                                .setBrightness(1.0f)
                                .setPosition(new Vector3d(centerPos.x, centerPos.y, centerPos.z))
                                .setRadius(15.0f)
                        );
                        this.initNormalLights = true;
                    }
                }
            }
        }

    }

    public void setEmergencyAlarm(boolean b) {
        this.playingEmergencyAlarm = b;
    }

    private void removeEmergencyLights() {
        if(this.initEmergencyLights) {
            VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().removeLight(this.areaLight1);
            this.areaLight1 = null;
            VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().removeLight(this.areaLight2);
            this.areaLight2 = null;
            VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().removeLight(this.pointLight);
            this.pointLight = null;
            this.initEmergencyLights = false;
        }
    }

    private void removeNormalLights() {
        if(this.initNormalLights) {
            VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().removeLight(this.pointLight);
            this.pointLight = null;
            this.initNormalLights = false;
        }
    }
}
