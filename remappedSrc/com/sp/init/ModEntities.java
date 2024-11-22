package com.sp.init;

import com.sp.SPBRevamped;
import com.sp.entity.custom.SkinWalkerEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {

    public static final EntityType<SkinWalkerEntity> SKIN_WALKER_ENTITY = Registry.register(Registries.ENTITY_TYPE,
            new Identifier(SPBRevamped.MOD_ID, "skin_walker"),
            FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, SkinWalkerEntity::new).dimensions(EntityDimensions.fixed(0.6f, 2.0f)).build());

}
