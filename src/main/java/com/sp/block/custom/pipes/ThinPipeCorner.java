package com.sp.block.custom.pipes;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

@SuppressWarnings("deprecation")
public class ThinPipeCorner extends HorizontalFacingBlock {
    public static final DirectionProperty FACING = Properties.FACING;
    public static final IntProperty TYPE = IntProperty.of("type", 0, 1);

    private static final VoxelShape SHAPE_SOUTH = Block.createCuboidShape(0.0, 0.0, 0.0, 15.0, 16.0, 1.0);
    private static final VoxelShape SHAPE_NORTH = Block.createCuboidShape(0.0, 0.0, 15.0, 15.0, 16.0, 16.0);
    private static final VoxelShape SHAPE_EAST = Block.createCuboidShape(0.0, 0.0, 0.0, 1.0, 16.0, 15.0);
    private static final VoxelShape SHAPE_WEST = Block.createCuboidShape(15.0, 0.0, 0.0, 16.0, 16.0, 15.0);
    private static final VoxelShape CEILING = Block.createCuboidShape(0.0, 15.0, 0.0, 15.0, 16.0, 15.0);

    public ThinPipeCorner(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return CEILING;
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
