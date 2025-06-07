package com.sp.block.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class RedMetalCasingBlock extends Block {
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;

    public RedMetalCasingBlock(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return switch (state.get(FACING)) {
            case NORTH -> VoxelShapes.cuboid(-0.15, -0.15, 0, 1.15, 1.15, 0.1);
            case SOUTH -> VoxelShapes.cuboid(-0.15, -0.15, 0.9, 1.15, 1.15, 1);
            case WEST -> VoxelShapes.cuboid(0, -0.15, -0.15, 0.1, 1.15, 1.15);
            case EAST -> VoxelShapes.cuboid(0.9, -0.15, -0.15, 1, 1.15, 1.15);
            default -> null;
        };
    }

    @Override
    public boolean isTransparent(BlockState state, BlockView world, BlockPos pos) {
        return true;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing());
    }
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
}
