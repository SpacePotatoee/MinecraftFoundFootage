package com.sp.block.custom;

import com.sp.block.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecation")
public class WallBlock extends Block {
    public static final BooleanProperty BOTTOM = BooleanProperty.of("bottom_wall");;

    public WallBlock(Settings settings) {
        super(settings);
    }


    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if(world.getBlockState(pos.down()).isOf(this.asBlock())){
            world.setBlockState(pos, state.with(BOTTOM, false));
        }else{
            world.setBlockState(pos, state.with(BOTTOM, true));
        }
        super.scheduledTick(state, world, pos, random);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        if(ctx.getWorld().getBlockState(ctx.getBlockPos().down()).isOf(this.asBlock())){
            return this.getDefaultState().with(BOTTOM, false);
        }else{
            return this.getDefaultState().with(BOTTOM, true);
        }
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        world.scheduleBlockTick(pos, this, 0);
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(BOTTOM);
    }
}
