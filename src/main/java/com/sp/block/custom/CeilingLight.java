package com.sp.block.custom;

import com.sp.block.entity.CeilingLightBlockEntity;
import com.sp.init.ModBlockEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class CeilingLight extends BlockWithEntity {
    public static final BooleanProperty STOPPED = BooleanProperty.of("stopped");

    public CeilingLight(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(STOPPED, false));
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CeilingLightBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntities.CEILING_LIGHT_BLOCK_ENTITY, (world1, pos, state1, blockEntity) -> blockEntity.tick(world, pos, state));
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(STOPPED);
    }
}
