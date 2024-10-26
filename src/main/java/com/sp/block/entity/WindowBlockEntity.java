package com.sp.block.entity;

import com.sp.init.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class WindowBlockEntity extends BlockEntity {

    public WindowBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.WINDOW_BLOCK_ENTITY, pos, state);
    }

}
