package com.sp.block.entity;

import com.sp.block.custom.WindowBlock;
import com.sp.init.ModBlockEntities;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.deferred.light.AreaLight;
import foundry.veil.api.client.render.deferred.light.PointLight;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.List;

public class WindowBlockEntity extends BlockEntity {
    private AreaLight areaLight;
    private List<PointLight> pointLightList;

    public WindowBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.WINDOW_BLOCK_ENTITY, pos, state);
    }

    @Override
    public void markRemoved() {;
        if (this.world.isClient) {
            if(areaLight != null) {
                VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().removeLight(areaLight);
                this.areaLight = null;
            }

            if(this.pointLightList != null){
                for (PointLight light : pointLightList){
                    VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().removeLight(light);
                }
            }
        }
        super.markRemoved();
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if(world.isClient){

            if(state.get(WindowBlock.point) > 0) {
                if (this.areaLight != null) {
                    VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().removeLight(areaLight);
                    this.areaLight = null;
                }
                if(state.get(WindowBlock.pointActive)) {

                    if (this.pointLightList == null) {
                        this.pointLightList = new ArrayList<>();
                        int numOfLights =
                                switch (state.get(WindowBlock.point)) {
                                    case 1 -> 1;
                                    case 2 -> 8;
                                    case 3 -> 16;
                                    default -> 0;
                                };

                        Vec3d position = pos.toCenterPos().add(0, -4, 0);

                        for (int i = 0; i < numOfLights; i++) {
                            PointLight pointLight = new PointLight();
                            pointLight
                                    .setPosition(position.x, position.y, position.z)
                                    .setBrightness(1.0f)
                                    .setRadius(15);

                            pointLightList.add(pointLight);
                            VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().addLight(pointLight);
                        }

                    }
                }
            } else {
                if (this.areaLight == null) {
                    areaLight = new AreaLight();
                    Vec3d position = pos.toCenterPos().add(0, 1.0, 0);
                    VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().addLight(areaLight
                            .setBrightness(2.5f)
                            .setSize(0, 0)
                            .setPosition(position.x, position.y, position.z)
                            .setOrientation(new Quaternionf().rotateXYZ((float) Math.toRadians(90), 0, 0))
                            .setDistance(15)
                    );

                } else {
                    switch (state.get(WindowBlock.color)) {
                        case 1:
                            areaLight.setColor(1, 0, 0);
                            break;
                        case 2:
                            areaLight.setColor(1, (float) 152 / 255, 0);
                            break;
                        case 3:
                            areaLight.setColor((float) 20 / 255, 1, 0);
                            break;
                        case 4:
                            areaLight.setColor(0, (float) 78 / 255, 1);
                            break;
                        case 5:
                            areaLight.setColor((float) 200 / 255, 0, 1);
                            break;
                    }
                }
            }

        }
    }
}
