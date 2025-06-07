package com.sp.init;

import com.sp.SPBRevamped;
import com.sp.block.entity.*;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {
    public static BlockEntityType<FluorescentLightBlockEntity> FLUORESCENT_LIGHT_BLOCK_ENTITY;
    public static BlockEntityType<DrawingMarkerBlockEntity> DRAWING_MARKER_BLOCK_ENTITY;
    public static BlockEntityType<ThinFluorescentLightBlockEntity> THIN_FLUORESCENT_LIGHT_BLOCK_ENTITY;
    public static BlockEntityType<TinyFluorescentLightBlockEntity> TINY_FLUORESCENT_LIGHT_BLOCK_ENTITY;
    public static BlockEntityType<WoodenCrateBlockEntity> WOODEN_CRATE_BLOCK_ENTITY;

    public static BlockEntityType<CeilingLightBlockEntity> CEILING_LIGHT_BLOCK_ENTITY;
    public static BlockEntityType<EmergencyLightBlockEntity> EMERGENCY_LIGHT_BLOCK_ENTITY;

    public static BlockEntityType<GasPumpBlockEntity> GAS_PUMP_ENTITY;

    public static void registerAllBlockEntities() {

        FLUORESCENT_LIGHT_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE,
                new Identifier(SPBRevamped.MOD_ID,"fluorescent_light_block_entity"),
                FabricBlockEntityTypeBuilder.create(FluorescentLightBlockEntity::new,
                        ModBlocks.FLUORESCENT_LIGHT).build());


        DRAWING_MARKER_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE,
                new Identifier(SPBRevamped.MOD_ID,"poolrooms_window_block_entity"),
                FabricBlockEntityTypeBuilder.create(DrawingMarkerBlockEntity::new,
                        ModBlocks.drawingMarker).build());

        THIN_FLUORESCENT_LIGHT_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE,
                new Identifier(SPBRevamped.MOD_ID,"thin_fluorescent_light_block_entity"),
                FabricBlockEntityTypeBuilder.create(ThinFluorescentLightBlockEntity::new,
                        ModBlocks.THIN_FLUORESCENT_LIGHT).build());

        WOODEN_CRATE_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE,
                new Identifier(SPBRevamped.MOD_ID,"wooden_crate_block_entity"),
                FabricBlockEntityTypeBuilder.create(WoodenCrateBlockEntity::new,
                        ModBlocks.WOODEN_CRATE).build());

        CEILING_LIGHT_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE,
                new Identifier(SPBRevamped.MOD_ID,"ceiling_light_block_entity"),
                FabricBlockEntityTypeBuilder.create(CeilingLightBlockEntity::new,
                        ModBlocks.CEILINGLIGHT).build());

        EMERGENCY_LIGHT_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE,
                new Identifier(SPBRevamped.MOD_ID,"emergency_light_block_entity"),
                FabricBlockEntityTypeBuilder.create(EmergencyLightBlockEntity::new,
                        ModBlocks.EMERGENCY_LIGHT).build());

        GAS_PUMP_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE,
                new Identifier(SPBRevamped.MOD_ID,"gas_pump_block_entity"),
                FabricBlockEntityTypeBuilder.create(GasPumpBlockEntity::new,
                        ModBlocks.GAS_PUMP).build());

        TINY_FLUORESCENT_LIGHT_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE,
                new Identifier(SPBRevamped.MOD_ID,"tiny_fluorescent_light_block_entity"),
                FabricBlockEntityTypeBuilder.create(TinyFluorescentLightBlockEntity::new,
                        ModBlocks.TINY_FLUORESCENT_LIGHT).build());
    }
}
