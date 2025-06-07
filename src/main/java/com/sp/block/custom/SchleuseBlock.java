package com.sp.block.custom;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

//Yes I know that that is a german name, but I did not know the english word. They are called Strip Doors btw.
public class SchleuseBlock extends Block {
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;

    private static final double[] SHAPE = new double[] {0, 0, 0.625, 1, 2, 0.75};

    public SchleuseBlock(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return switch (state.get(FACING)) {
            case NORTH -> VoxelShapes.cuboid(SHAPE[0], SHAPE[1], SHAPE[2], SHAPE[3], SHAPE[4], SHAPE[5]);
            case SOUTH -> VoxelShapes.cuboid(SHAPE[0], SHAPE[1], 1 - SHAPE[5], SHAPE[3], SHAPE[4], 1 - SHAPE[2]);
            case WEST -> VoxelShapes.cuboid(SHAPE[2], SHAPE[1], SHAPE[0], SHAPE[5], SHAPE[4], SHAPE[3]);
            case EAST -> VoxelShapes.cuboid(1 - SHAPE[5], SHAPE[1], SHAPE[0], 1 - SHAPE[2], SHAPE[4], SHAPE[3]);
            default -> VoxelShapes.fullCube();
        };
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.empty(); // Makes the block non-solid
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        entity.teleport(entity.getX(), entity.getY() + (state.get(FACING) == Direction.NORTH ? - 60 : 60), entity.getZ());
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
