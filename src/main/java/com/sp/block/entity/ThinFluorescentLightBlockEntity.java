package com.sp.block.entity;

import com.sp.ConfigStuff;
import com.sp.sounds.FluorescentLightSoundInstance;
import com.sp.sounds.ThinFluorescentLightSoundInstance;
import com.sp.world.levels.BackroomsLevels;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.deferred.light.PointLight;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import static com.sp.block.custom.ThinFluorescentLightBlock.FACE;
import static com.sp.block.custom.ThinFluorescentLightBlock.FACING;

public class ThinFluorescentLightBlockEntity extends BlockEntity {
    PointLight pointLight;
    BlockState currentState;
    private boolean playingSound;

    public ThinFluorescentLightBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.THIN_FLUORESCENT_LIGHT_BLOCK_ENTITY, pos, state);
        this.currentState = state;
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
                if (!this.isPlayingSound() && pos.isWithinDistance(playerPos, 15.0f)) {
                    MinecraftClient.getInstance().getSoundManager().play(new ThinFluorescentLightSoundInstance(this, player));
                    this.setPlayingSound(true);
                }


                if (pos.isWithinDistance(playerPos, ConfigStuff.lightRenderDistance * 1.3)) {
                    if (pointLight == null) {
                        pointLight = new PointLight();
                        VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().addLight(pointLight
                                .setRadius(18f)
                                .setColor(255, 255, 255)
                                .setBrightness(0.0024f)
                        );
                        switch ((WallMountLocation)state.get(FACE)) {
                            case FLOOR:
                                pointLight.setPosition(position.x, position.y, position.z);
                            case WALL:
                                switch ((Direction)state.get(FACING)) {
                                    case EAST:
                                        pointLight.setPosition(position.x, position.y, position.z + 0.5).setBrightness(0.0020f);
                                    case WEST:
                                        pointLight.setPosition(position.x, position.y, position.z - 0.5).setBrightness(0.0020f);
                                    case SOUTH:
                                        pointLight.setPosition(position.x + 0.5, position.y, position.z).setBrightness(0.0020f);
                                    case NORTH:
                                    default:
                                        pointLight.setPosition(position.x - 0.5, position.y, position.z).setBrightness(0.0020f);
                                }
                            case CEILING:
                            default:
                                pointLight.setPosition(position.x, position.y, position.z);

                        }
                        if(world.getRegistryKey() == BackroomsLevels.LEVEL2_WORLD_KEY){
                            pointLight.setColor(200, 200, 255);
                        }
                    }
                }
                else {
                    if (pointLight != null){
                        VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().removeLight(pointLight);
                        pointLight = null;
                    }
                }
            }
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
