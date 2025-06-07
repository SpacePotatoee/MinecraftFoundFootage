package com.sp.block.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class UtilityPole extends Block {
    private final int size;

    public UtilityPole(Settings settings, int size) {
        super(settings);
        this.size = Math.min(size, 8);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return Block.createCuboidShape(size, 0, size, 16-size, 16, 16-size);
    }
}
