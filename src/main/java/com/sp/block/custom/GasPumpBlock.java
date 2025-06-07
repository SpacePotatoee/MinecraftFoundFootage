package com.sp.block.custom;

import com.sp.block.entity.GasPumpBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GasPumpBlock extends BlockWithEntity {
    public GasPumpBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new GasPumpBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return makeShape();
    }


    public VoxelShape makeShape(){
        VoxelShape shape = VoxelShapes.empty();

        List<Vec3d> steps = List.of(
                new Vec3d(0.05, 0.05, 0.3125),

                new Vec3d(0.05, 0.25, 0.6),
                new Vec3d(0.2380, 0.25, 0.64),
                new Vec3d(0.4755, 0.25, 0.68),
                new Vec3d(0.7130, 0.25, 0.725)
        );

        List<Double> sizes = List.of(
                0.95,

                0.2375,
                0.2375,
                0.2375,
                0.2375
        );

        for (int i = 0; i < steps.size(); i++) {
            Vec3d step = steps.get(i);
            double size = sizes.get(i);

            shape = VoxelShapes.combine(shape, VoxelShapes.cuboid(0 + step.x, 0 + step.y, 0 + step.z, size + step.x, 0.13 + step.y, 0.375 + step.z), BooleanBiFunction.OR);
        }

        return shape;
    }
}