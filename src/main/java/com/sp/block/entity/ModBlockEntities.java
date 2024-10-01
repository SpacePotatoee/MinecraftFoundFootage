package com.sp.block.entity;

import com.sp.SPBRevamped;
import com.sp.block.ModBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {
    public static BlockEntityType<CameraBlockEntity> CAMERA_BLOCK_ENTITY;
    public static BlockEntityType<FluorescentLightBlockEntity> FLUORESCENT_LIGHT_BLOCK_ENTITY;
    public static BlockEntityType<PoolroomsWindowBlockEntity> POOLROOMS_WINDOW_BLOCK_ENTITY;
    public static BlockEntityType<WindowBlockEntity> WINDOW_BLOCK_ENTITY;
    public static BlockEntityType<ThinFluorescentLightBlockEntity> THIN_FLUORESCENT_LIGHT_BLOCK_ENTITY;


    public static void registerAllBlockEntities(){
        CAMERA_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE,
                new Identifier(SPBRevamped.MOD_ID,"camera_block_entity"),
                FabricBlockEntityTypeBuilder.create(CameraBlockEntity::new,
                        ModBlocks.CAMERA).build());

        FLUORESCENT_LIGHT_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE,
                new Identifier(SPBRevamped.MOD_ID,"fluorescent_light_block_entity"),
                FabricBlockEntityTypeBuilder.create(FluorescentLightBlockEntity::new,
                        ModBlocks.FluorescentLight).build());

        POOLROOMS_WINDOW_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE,
                new Identifier(SPBRevamped.MOD_ID,"poolrooms_window_block_entity"),
                FabricBlockEntityTypeBuilder.create(PoolroomsWindowBlockEntity::new,
                        ModBlocks.PoolroomsWindow).build());

        WINDOW_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE,
                new Identifier(SPBRevamped.MOD_ID,"window_block_entity"),
                FabricBlockEntityTypeBuilder.create(WindowBlockEntity::new,
                        ModBlocks.Window).build());

        THIN_FLUORESCENT_LIGHT_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE,
                new Identifier(SPBRevamped.MOD_ID,"thin_fluorescent_light_block_entity"),
                FabricBlockEntityTypeBuilder.create(ThinFluorescentLightBlockEntity::new,
                        ModBlocks.ThinFluorescentLight).build());
    }
}
