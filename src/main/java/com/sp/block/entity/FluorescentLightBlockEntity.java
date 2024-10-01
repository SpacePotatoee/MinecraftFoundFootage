package com.sp.block.entity;

import com.sp.ConfigStuff;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.deferred.light.PointLight;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class FluorescentLightBlockEntity extends BlockEntity {
    PointLight pointLight;

    public FluorescentLightBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FLUORESCENT_LIGHT_BLOCK_ENTITY, pos, state);
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
        Vec3d position = pos.toCenterPos();

        if(world.isClient) {
            PlayerEntity player = MinecraftClient.getInstance().player;
            if (player != null) {
                Vec3d playerPos = player.getPos();

                if (pos.isWithinDistance(playerPos, ConfigStuff.lightRenderDistance)) {
                    if (pointLight == null) {
                        this.pointLight = new PointLight();
                        VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().addLight(this.pointLight
                                .setRadius(15f)
                                .setColor(220, 220, 100)
                                .setPosition(position.x, position.y - 0.5, position.z)
                                .setBrightness(0.001f)//
                        );
                    }
                }
                else {
                    if (this.pointLight != null){
                        VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().removeLight(this.pointLight);
                        this.pointLight = null;
                    }
                }
            }
        }
    }
}
