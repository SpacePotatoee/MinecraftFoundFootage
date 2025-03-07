package com.sp.block.custom;

import com.sp.block.entity.WindowBlockEntity;
import com.sp.init.ModBlockEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecation")
public class WindowBlock extends BlockWithEntity {
    public static final IntProperty color = IntProperty.of("color", 1, 5);
    public static final IntProperty point = IntProperty.of("point", 0, 3);
    public static final BooleanProperty pointActive = BooleanProperty.of("pointactive");

    public WindowBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(color, 1).with(point, 0).with(pointActive, false));
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState blockState = ctx.getWorld().getBlockState(ctx.getBlockPos());
        if(ctx.getPlayer() != null){
            if(ctx.getPlayer().isSneaking()){
                if (blockState.isOf(this)) {
                    return blockState.with(point, Math.min(3, blockState.get(point) + 1)).with(pointActive, false);
                } else {
                    return this.getDefaultState().with(point, 1).with(pointActive, false);
                }
            }
        }

        if (blockState.isOf(this)) {
            return blockState.with(color, Math.min(5, blockState.get(color) + 1)).with(point, 0).with(pointActive, false);
        } else {
            return this.getDefaultState().with(color, 1).with(point, 0).with(pointActive, false);
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if(player.isSneaking()) {
            world.setBlockState(pos, state.with(pointActive, !state.get(pointActive)));
            return ActionResult.SUCCESS;
        }

        return super.onUse(state, world, pos, player, hand, hit);
    }

    @Override
    public boolean canReplace(BlockState state, ItemPlacementContext context) {
        return !context.shouldCancelInteraction() && context.getStack().isOf(this.asItem()) && state.get(color) < 5 || super.canReplace(state, context);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new WindowBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModBlockEntities.WINDOW_BLOCK_ENTITY, (world1, pos, state1, blockEntity) -> blockEntity.tick(world1, pos, state1));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(color, point, pointActive);
    }
}
