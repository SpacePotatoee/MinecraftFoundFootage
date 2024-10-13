package com.sp.block.custom;

import com.sp.block.ModBlocks;
import com.sp.block.entity.FluorescentLightBlockEntity;
import com.sp.block.entity.ModBlockEntities;
import com.sp.world.events.EventVariableStorage;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.deferred.light.PointLight;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class FluorescentLightBlock extends BlockWithEntity {
    public static final BooleanProperty BLACKOUT = BooleanProperty.of("blackout");

    public FluorescentLightBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(BLACKOUT, false));
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(BLACKOUT, false);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        if(state.get(BLACKOUT)){
            return BlockRenderType.MODEL;
        }
        else {
            return BlockRenderType.INVISIBLE;
        }
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntities.FLUORESCENT_LIGHT_BLOCK_ENTITY, (world1, pos, state1, blockEntity) -> blockEntity.tick(world1, pos, state1));
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new FluorescentLightBlockEntity(pos, state);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(BLACKOUT);
    }
}
