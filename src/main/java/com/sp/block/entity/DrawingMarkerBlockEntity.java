package com.sp.block.entity;

import com.sp.ConfigStuff;
import com.sp.block.ModBlocks;
import com.sp.block.custom.DrawingMarker;
import com.sp.block.custom.WallText;
import com.sp.world.levels.BackroomsLevels;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.deferred.light.PointLight;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class DrawingMarkerBlockEntity extends BlockEntity {
    Random random = Random.create();
    java.util.Random random2 = new java.util.Random();

    public DrawingMarkerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DRAWING_MARKER_BLOCK_ENTITY, pos, state);
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        BlockState blockState = world.getBlockState(pos);
        int rand = random.nextBetween(1, 4);
        int rand2 = random.nextBetween(1, 10);
        if(!world.isClient){
            if(world.getRegistryKey() == BackroomsLevels.LEVEL0_WORLD_KEY) {
                if(rand2 == 1 && !blockState.get(DrawingMarker.TYPE)) {
                    switch (rand) {
                        case 2:
                            world.setBlockState(pos, ModBlocks.WallArrow2.getDefaultState().with(WallText.FACING, blockState.get(DrawingMarker.FACING)));
                            break;
                        case 3:
                            world.setBlockState(pos, ModBlocks.WallArrow3.getDefaultState().with(WallText.FACING, blockState.get(DrawingMarker.FACING)));
                            break;
                        case 4:
                            world.setBlockState(pos, ModBlocks.WallArrow4.getDefaultState().with(WallText.FACING, blockState.get(DrawingMarker.FACING)));
                            break;
                        default:
                            world.setBlockState(pos, ModBlocks.WallArrow1.getDefaultState().with(WallText.FACING, blockState.get(DrawingMarker.FACING)));
                            break;
                    }
                }
                else if(rand2 == 1 && blockState.get(DrawingMarker.TYPE)){
                    switch (rand) {
                        case 2, 4:
                            world.setBlockState(pos, ModBlocks.WallSmall1.getDefaultState().with(WallText.FACING, blockState.get(DrawingMarker.FACING)));
                            break;
                        default:
                            world.setBlockState(pos, ModBlocks.WallSmall2.getDefaultState().with(WallText.FACING, blockState.get(DrawingMarker.FACING)));
                            break;
                    }
                }
                else{
                    world.setBlockState(pos, Blocks.AIR.getDefaultState());
                }
            }
        }
    }
}
