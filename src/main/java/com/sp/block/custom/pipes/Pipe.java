package com.sp.block.custom.pipes;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

@SuppressWarnings("deprecation")
public class Pipe extends FacingBlock {

    private static final VoxelShape SHAPE_EAST = Block.createCuboidShape(0.0, 2.0, 2.0, 16.0, 16.0, 14.0);
    private static final VoxelShape SHAPE_WEST = Block.createCuboidShape(0.0, 2.0, 2.0, 16.0, 16.0, 14.0);
    private static final VoxelShape SHAPE_NORTH = Block.createCuboidShape(2.0, 2.0, 0.0, 14.0, 14.0, 16.0);
    private static final VoxelShape SHAPE_SOUTH = Block.createCuboidShape(2.0, 2.0, 0.0, 14.0, 14.0, 16.0);
    private static final VoxelShape VERTICAL = Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 16.0, 14.0);

    public Pipe(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        switch (state.get(FACING)) {
            case UP, DOWN -> {
                return VERTICAL;
            }
            case NORTH -> {
                return SHAPE_NORTH;
            }
            case WEST -> {
                return SHAPE_WEST;
            }
            case EAST -> {
                return SHAPE_EAST;
            }
            default -> {
                return SHAPE_SOUTH;
            }
        }
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
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getPlayerLookDirection().getOpposite());
    }
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
}
