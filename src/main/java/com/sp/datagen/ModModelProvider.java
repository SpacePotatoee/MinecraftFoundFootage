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
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.CarpetBlock);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.ConcreteBlock1);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.ConcreteBlock2);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.ConcreteBlock5);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.ConcreteBlock6);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.ConcreteBlock7);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.ConcreteBlock10);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.ConcreteBlock11);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.ConcreteBlock12);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.PoolTiles);

        BlockStateModelGenerator.BlockTexturePool concretePool = blockStateModelGenerator.registerCubeAllModelTexturePool(ModBlocks.ConcreteBlock9);
        concretePool.slab(ModBlocks.ConcreteBlock9Slab);

    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {

    }
}
