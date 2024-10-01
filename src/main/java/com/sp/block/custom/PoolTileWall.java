package com.sp.block.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class PoolTileWall extends HorizontalFacingBlock {
    public static final IntProperty TYPE = IntProperty.of("type", 0, 1);

    public PoolTileWall(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        switch (state.get(FACING)) {
            case UP:
            case DOWN:
            case SOUTH:
            default: {
                switch (state.get(TYPE)){
                    case 1: return VoxelShapes.union(
                            Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 6.4),
                            Block.createCuboidShape(0.0, 0.0, 0.0, 6.4, 16.0, 16.0)
                    );
                    default: return Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 6.4);
                }
            }
            case NORTH: {
                switch (state.get(TYPE)){
                    case 1: return VoxelShapes.union(
                            Block.createCuboidShape(0.0, 0.0, 9.6, 16.0, 16.0, 16.0),
                            Block.createCuboidShape(9.6, 0.0, 0.0, 16.0, 16.0, 16.0)
                    );
                    default: return Block.createCuboidShape(0.0, 0.0, 9.6, 16.0, 16.0, 16.0);
                }

            }
            case WEST : {
                switch (state.get(TYPE)){
                    case 1: return VoxelShapes.union(
                            Block.createCuboidShape(9.6, 0.0, 0.0, 16.0, 16.0, 16.0),
                            Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 6.4)
                    );
                    default: return Block.createCuboidShape(9.6, 0.0, 0.0, 16.0, 16.0, 16.0);
                }
            }
            case EAST : {
                switch (state.get(TYPE)){
                    case 1: return VoxelShapes.union(
                            Block.createCuboidShape(0.0, 0.0, 0.0, 6.4, 16.0, 16.0),
                            Block.createCuboidShape(0.0, 0.0, 9.6, 16.0, 16.0, 16.0)
                    );
                    default: return Block.createCuboidShape(0.0, 0.0, 0.0, 6.4, 16.0, 16.0);
                }
            }
        }
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState blockState = ctx.getWorld().getBlockState(ctx.getBlockPos());
        if (blockState.isOf(this)) {
            return blockState.with(TYPE, Math.min(1, blockState.get(TYPE) + 1)).with(FACING, blockState.get(FACING));
        } else {
            return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
        }
    }

    @Override
    public boolean canReplace(BlockState state, ItemPlacementContext context) {
        return !context.shouldCancelInteraction() && context.getStack().isOf(this.asItem()) && state.get(TYPE) < 1 || super.canReplace(state, context);
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, TYPE);
    }
}
