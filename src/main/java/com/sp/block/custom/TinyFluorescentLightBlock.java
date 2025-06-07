package com.sp.block.custom;

import com.sp.block.entity.TinyFluorescentLightBlockEntity;
import com.sp.init.ModBlockEntities;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecation")
public class TinyFluorescentLightBlock extends BlockWithEntity {
    public static final BooleanProperty ON = BooleanProperty.of("on");
    public static final BooleanProperty COPY = BooleanProperty.of("copy");
    public static final BooleanProperty BLACKOUT = BooleanProperty.of("blackout");

    private static final VoxelShape FLOOR_X_AXIS_SHAPE = Block.createCuboidShape(6.0, 14.0, 6.0, 10.0, 16.0, 10.0);

    public TinyFluorescentLightBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(BLACKOUT, false).with(ON, true).with(COPY, false));
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState blockState;
        blockState = this.getDefaultState()
                .with(ON, true)
                .with(BLACKOUT, false)
                .with(COPY, false);
        if (blockState.canPlaceAt(ctx.getWorld(), ctx.getBlockPos())) {
            return blockState;
        }

        return null;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return FLOOR_X_AXIS_SHAPE;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(ON, COPY, BLACKOUT);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new TinyFluorescentLightBlockEntity(pos,state);
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
        return checkType(type, ModBlockEntities.TINY_FLUORESCENT_LIGHT_BLOCK_ENTITY, (world1, pos, state1, blockEntity) -> blockEntity.tick(world1, pos, state1));
    }
}
