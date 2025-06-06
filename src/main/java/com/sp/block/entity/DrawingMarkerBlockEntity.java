package com.sp.block.entity;

import com.sp.block.custom.DrawingMarker;
import com.sp.block.custom.WallText;
import com.sp.init.BackroomsLevels;
import com.sp.init.ModBlockEntities;
import com.sp.init.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class DrawingMarkerBlockEntity extends BlockEntity {
    Random random = Random.create();

    public DrawingMarkerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DRAWING_MARKER_BLOCK_ENTITY, pos, state);
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        BlockState blockState = world.getBlockState(pos);
        int rand = random.nextBetween(1, 4);
        int rand2 = random.nextBetween(1, 10);

        if (world.isClient) {
            return;
        }

        if (world.getRegistryKey() != BackroomsLevels.LEVEL0_WORLD_KEY) {
            return;
        }

        world.removeBlockEntity(pos);
        world.getWorldChunk(pos).blockEntityNbts.remove(pos);
        if (rand2 == 1 && !blockState.get(DrawingMarker.TYPE)) {
            switch (rand) {
                case 2:
                    world.setBlockState(pos, ModBlocks.WALL_ARROW_2.getDefaultState().with(WallText.FACING, blockState.get(DrawingMarker.FACING)));
                    break;
                case 3:
                    world.setBlockState(pos, ModBlocks.WALL_ARROW_3.getDefaultState().with(WallText.FACING, blockState.get(DrawingMarker.FACING)));
                    break;
                case 4:
                    world.setBlockState(pos, ModBlocks.WALL_ARROW_4.getDefaultState().with(WallText.FACING, blockState.get(DrawingMarker.FACING)));
                    break;
                default:
                    world.setBlockState(pos, ModBlocks.WALL_ARROW_1.getDefaultState().with(WallText.FACING, blockState.get(DrawingMarker.FACING)));
                    break;
            }
        } else if (rand2 == 1 && blockState.get(DrawingMarker.TYPE)) {
            switch (rand) {
                case 2, 4:
                    world.setBlockState(pos, ModBlocks.WALL_SMALL_1.getDefaultState().with(WallText.FACING, blockState.get(DrawingMarker.FACING)));
                    break;
                default:
                    world.setBlockState(pos, ModBlocks.WALL_SMALL_2.getDefaultState().with(WallText.FACING, blockState.get(DrawingMarker.FACING)));
                    break;
            }
        } else {
            world.setBlockState(pos, Blocks.AIR.getDefaultState());
        }
    }
}
