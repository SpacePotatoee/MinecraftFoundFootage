package com.sp.block.custom;

import net.minecraft.block.*;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecation")
public class PoolTileSlopeBlock extends Block implements Waterloggable {
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    public static final EnumProperty<BlockHalf> HALF = Properties.BLOCK_HALF;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    private static final VoxelShape SHAPE_NORTH = VoxelShapes.union(
            Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 4.0, 16.0),
            Block.createCuboidShape(0.0, 4.0, 4.0, 16.0, 8.0, 16.0),
            Block.createCuboidShape(0.0, 8.0, 8.0, 16.0, 12.0, 16.0),
            Block.createCuboidShape(0.0, 12.0, 12.0, 16.0, 16.0, 16.0)
    );
    private static final VoxelShape SHAPE_SOUTH = VoxelShapes.union(
            Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 4.0, 16.0),
            Block.createCuboidShape(0.0, 4.0, 0.0, 16.0, 8.0, 12.0),
            Block.createCuboidShape(0.0, 8.0, 0.0, 16.0, 12.0, 8.0),
            Block.createCuboidShape(0.0, 12.0, 0.0, 16.0, 16.0, 4.0)
    );
    private static final VoxelShape SHAPE_WEST = VoxelShapes.union(
            Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 4.0, 16.0),
            Block.createCuboidShape(4.0, 4.0, 0.0, 16.0, 8.0, 16.0),
            Block.createCuboidShape(8.0, 8.0, 0.0, 16.0, 12.0, 16.0),
            Block.createCuboidShape(12.0, 12.0, 0.0, 16.0, 16.0, 16.0)
    );
    private static final VoxelShape SHAPE_EAST = VoxelShapes.union(
            Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 4.0, 16.0),
            Block.createCuboidShape(0.0, 4.0, 0.0, 12.0, 8.0, 16.0),
            Block.createCuboidShape(0.0, 8.0, 0.0, 8.0, 12.0, 16.0),
            Block.createCuboidShape(0.0, 12.0, 0.0, 4.0, 16.0, 16.0)
    );

    private static final VoxelShape SHAPE_TOP_NORTH = VoxelShapes.union(
            Block.createCuboidShape(0.0, 12.0, 0.0, 16.0, 16.0, 16.0),
            Block.createCuboidShape(0.0, 8.0, 4.0, 16.0, 12.0, 16.0),
            Block.createCuboidShape(0.0, 4.0, 8.0, 16.0, 8.0, 16.0),
            Block.createCuboidShape(0.0, 0.0, 12.0, 16.0, 4.0, 16.0)
    );
    private static final VoxelShape SHAPE_TOP_SOUTH = VoxelShapes.union(
            Block.createCuboidShape(0.0, 12.0, 0.0, 16.0, 16.0, 16.0),
            Block.createCuboidShape(0.0, 8.0, 0.0, 16.0, 12.0, 12.0),
            Block.createCuboidShape(0.0, 4.0, 0.0, 16.0, 8.0, 8.0),
            Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 4.0, 4.0)
    );
    private static final VoxelShape SHAPE_TOP_WEST = VoxelShapes.union(
            Block.createCuboidShape(0.0, 12.0, 0.0, 16.0, 16.0, 16.0),
            Block.createCuboidShape(4.0, 8.0, 0.0, 16.0, 12.0, 16.0),
            Block.createCuboidShape(8.0, 4.0, 0.0, 16.0, 8.0, 16.0),
            Block.createCuboidShape(12.0, 0.0, 0.0, 16.0, 4.0, 16.0)
    );
    private static final VoxelShape SHAPE_TOP_EAST = VoxelShapes.union(
            Block.createCuboidShape(0.0, 12.0, 0.0, 16.0, 16.0, 16.0),
            Block.createCuboidShape(0.0, 8.0, 0.0, 12.0, 12.0, 16.0),
            Block.createCuboidShape(0.0, 4.0, 0.0, 8.0, 8.0, 16.0),
            Block.createCuboidShape(0.0, 0.0, 0.0, 4.0, 4.0, 16.0)
    );



    public PoolTileSlopeBlock(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        switch (state.get(HALF)){
            case TOP:{
                switch (state.get(FACING)) {
                    case UP:
                    case DOWN:
                    case SOUTH:
                    default: return SHAPE_TOP_SOUTH;
                    case NORTH: return SHAPE_TOP_NORTH;
                    case WEST : return SHAPE_TOP_WEST;
                    case EAST : return SHAPE_TOP_EAST;
                }
            }
            case BOTTOM:
            default: {
                switch (state.get(FACING)) {
                    case UP:
                    case DOWN:
                    case SOUTH:
                    default: return SHAPE_SOUTH;
                    case NORTH: return SHAPE_NORTH;
                    case WEST : return SHAPE_WEST;
                    case EAST : return SHAPE_EAST;
                }
            }
        }
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction direction = ctx.getSide();
        BlockPos blockPos = ctx.getBlockPos();
        return this.getDefaultState()
                .with(FACING, ctx.getHorizontalPlayerFacing().getOpposite())
                .with(HALF, direction != Direction.DOWN && (direction == Direction.UP || !(ctx.getHitPos().y - (double)blockPos.getY() > 0.5)) ? BlockHalf.BOTTOM : BlockHalf.TOP)
                .with(WATERLOGGED,ctx.getWorld().getFluidState(ctx.getBlockPos()).isOf(Fluids.WATER));
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (state.get(WATERLOGGED)) {
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }

        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
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
        builder.add(FACING, HALF, WATERLOGGED);
    }

}
