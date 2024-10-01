package com.sp.block.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class BottomTrim extends HorizontalFacingBlock {
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final IntProperty SIDES = IntProperty.of("sides", 1, 4);

    private static final VoxelShape SHAPE_SOUTH = Block.createCuboidShape(0.0, 3.0, 0.0, 16.0, 12.0, 1.0);
    private static final VoxelShape SHAPE_NORTH = Block.createCuboidShape(0.0, 3.0, 15.0, 16.0, 12.0, 16.0);
    private static final VoxelShape SHAPE_WEST = Block.createCuboidShape(15.0, 3.0, 0.0, 16.0, 12.0, 16.0);
    private static final VoxelShape SHAPE_EAST = Block.createCuboidShape(0.0, 3.0, 0.0, 1.0, 12.0, 16.0);


    public BottomTrim(Settings settings) {
        super(settings);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState blockState = ctx.getWorld().getBlockState(ctx.getBlockPos());
        if (blockState.isOf(this)) {
            return blockState.with(SIDES, Math.min(4, blockState.get(SIDES) + 1)).with(FACING, blockState.get(FACING));
        } else {
            return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
        }
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        switch (state.get(FACING)) {
            case UP:
            case DOWN:
            case SOUTH:
            default: {
                return Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 2.0, 1.0);
            }
            case NORTH: {
                return Block.createCuboidShape(0.0, 0.0, 15.0, 16.0, 2.0, 16.0);
            }
            case WEST : {
                return Block.createCuboidShape(15.0, 0.0, 0.0, 16.0, 2.0, 16.0);
            }
            case EAST : {
                return Block.createCuboidShape(0.0, 0.0, 0.0, 1.0, 2.0, 16.0);
            }
        }
    }

    @Override
    public boolean canReplace(BlockState state, ItemPlacementContext context) {
        return !context.shouldCancelInteraction() && context.getStack().isOf(this.asItem()) && state.get(SIDES) < 4 || super.canReplace(state, context);
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
        builder.add(SIDES);
    }

}
