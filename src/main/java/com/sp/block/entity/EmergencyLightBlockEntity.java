package com.sp.block.entity;

import com.sp.clientWrapper.ClientWrapper;
import com.sp.init.ModBlockEntities;
import com.sp.sounds.EmergencyAlarmSoundInstance;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.deferred.light.AreaLight;
import foundry.veil.api.client.render.deferred.light.PointLight;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class EmergencyLightBlockEntity extends BlockEntity {
    public final float randomOffset;
    public boolean initEmergencyLights = false;
    public boolean playingEmergencyAlarm = false;
    public boolean initNormalLights = false;

    public EmergencyAlarmSoundInstance emergencyAlarmSoundInstance;
    public AreaLight areaLight1;
    public AreaLight areaLight2;
    public PointLight pointLight;

    public EmergencyLightBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.EMERGENCY_LIGHT_BLOCK_ENTITY, pos, state);
        this.randomOffset = Random.create().nextFloat() * 180;
    }

    @Override
    public void markRemoved() {
        if (this.initEmergencyLights){
            this.removeEmergencyLights();
        }

        if(this.initNormalLights){
            this.removeNormalLights();
        }

        super.markRemoved();
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (!world.isClient) {
            return;
        }

        ClientWrapper.tickEmergencyLight(world, pos, state, this);
    }

    public void setEmergencyAlarm(boolean b) {
        this.playingEmergencyAlarm = b;
    }

    public void removeEmergencyLights() {
        if (this.initEmergencyLights && this.world.isClient) {
            VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().removeLight(this.areaLight1);
            this.areaLight1 = null;
            VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().removeLight(this.areaLight2);
            this.areaLight2 = null;
            VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().removeLight(this.pointLight);
            this.pointLight = null;
            this.initEmergencyLights = false;
        }
    }

    public void removeNormalLights() {
        if(this.initNormalLights && this.world.isClient) {
            VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().removeLight(this.pointLight);
            this.pointLight = null;
            this.initNormalLights = false;
        }
    }
}
