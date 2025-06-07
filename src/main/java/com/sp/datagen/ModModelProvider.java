package com.sp.datagen;

import com.sp.init.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;

public class ModModelProvider extends FabricModelProvider {
    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.CARPET_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.CONCRETE_BLOCK_1);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.CONCRETE_BLOCK_2);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.CONCRETE_BLOCK_5);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.CONCRETE_BLOCK_6);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.CONCRETE_BLOCK_7);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.CONCRETE_BLOCK_10);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.CONCRETE_BLOCK_11);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.ROAD);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.CONCRETE_BLOCK_12);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.POOL_TILES);

        BlockStateModelGenerator.BlockTexturePool concretePool = blockStateModelGenerator.registerCubeAllModelTexturePool(ModBlocks.CONCRETE_BLOCK_9);
        concretePool.slab(ModBlocks.CONCRETE_BLOCK_9_SLAB);

    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {

    }
}
