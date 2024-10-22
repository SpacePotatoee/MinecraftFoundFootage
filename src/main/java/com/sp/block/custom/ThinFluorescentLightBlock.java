package com.sp.block.custom;

import com.sp.block.entity.ModBlockEntities;
import com.sp.block.entity.ThinFluorescentLightBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.WallMountLocation;
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
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecation")
public class ThinFluorescentLightBlock extends BlockWithEntity {
    public static final BooleanProperty ON = BooleanProperty.of("on");
    public static final BooleanProperty COPY = BooleanProperty.of("copy");
    public static final BooleanProperty BLACKOUT = BooleanProperty.of("blackout");

    public static final DirectionProperty FACING = Properties.FACING;
    public static final EnumProperty<WallMountLocation> FACE = Properties.WALL_MOUNT_LOCATION;

    private static final VoxelShape FLOOR_X_AXIS_SHAPE = Block.createCuboidShape(0.0, 0.0, 6.0, 16.0, 2.0, 10.0);
    private static final VoxelShape FLOOR_Z_AXIS_SHAPE = Block.createCuboidShape(6.0, 0.0, 0.0, 10.0, 2.0, 16.0);
    private static final VoxelShape EAST_WALL_SHAPE = Block.createCuboidShape(0.0, 0.0, 6.0, 2.0, 16.0, 10.0);
    private static final VoxelShape WEST_WALL_SHAPE = Block.createCuboidShape(14.0, 0.0, 6.0, 16.0, 16.0, 10.0);
    private static final VoxelShape NORTH_WALL_SHAPE = Block.createCuboidShape(6.0, 0.0, 14.0, 10.0, 16.0, 16.0);
    private static final VoxelShape SOUTH_WALL_SHAPE = Block.createCuboidShape(6.0, 0.0, 0.0, 10.0, 16.0, 2.0);
    private static final VoxelShape CEILING_X_AXIS_SHAPE = Block.createCuboidShape(0.0, 14.0, 6.0, 16.0, 16.0, 10.0);
    private static final VoxelShape CEILING_Z_AXIS_SHAPE = Block.createCuboidShape(6.0, 14.0, 0.0, 10.0, 16.0, 16.0);

    public ThinFluorescentLightBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(BLACKOUT, false).with(ON, true).with(COPY, false));
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        for (Direction direction : ctx.getPlacementDirections()) {
            BlockState blockState;
            if (direction.getAxis() == Direction.Axis.Y) {
                blockState = this.getDefaultState()
                        .with(FACE, direction == Direction.UP ? WallMountLocation.CEILING : WallMountLocation.FLOOR)
                        .with(FACING, ctx.getHorizontalPlayerFacing())
                        .with(ON, true)
                        .with(BLACKOUT, false)
                        .with(COPY, false);
            } else {
                blockState = this.getDefaultState().with(FACE, WallMountLocation.WALL).with(FACING, direction.getOpposite()).with(ON, true).with(BLACKOUT, false).with(COPY, false);
            }

            if (blockState.canPlaceAt(ctx.getWorld(), ctx.getBlockPos())) {
                return blockState;
            }
        }

        return null;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        switch ((WallMountLocation)state.get(FACE)) {
            case FLOOR:
                switch (((Direction)state.get(FACING)).getAxis()) {
                    case X:
                        return FLOOR_X_AXIS_SHAPE;
                    case Z:
                    default:
                        return FLOOR_Z_AXIS_SHAPE;
                }
            case WALL:
                switch ((Direction)state.get(FACING)) {
                    case EAST:
                        return EAST_WALL_SHAPE;
                    case WEST:
                        return WEST_WALL_SHAPE;
                    case SOUTH:
                        return SOUTH_WALL_SHAPE;
                    case NORTH:
                    default:
                        return NORTH_WALL_SHAPE;
                }
            case CEILING:
            default:
                switch (((Direction)state.get(FACING)).getAxis()) {
                    case X:
                        return CEILING_X_AXIS_SHAPE;
                    case Z:
                    default:
                        return CEILING_Z_AXIS_SHAPE;
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
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, FACE, ON, COPY, BLACKOUT);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ThinFluorescentLightBlockEntity(pos,state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        if(state.get(BLACKOUT) || !state.get(ON)){
            return BlockRenderType.MODEL;
        }
        else {
            return BlockRenderType.INVISIBLE;
        }
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntities.THIN_FLUORESCENT_LIGHT_BLOCK_ENTITY, (world1, pos, state1, blockEntity) -> blockEntity.tick(world1, pos, state1));
    }

}
