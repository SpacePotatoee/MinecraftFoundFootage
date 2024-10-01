package com.sp.cca_stuff;

import com.sp.SPBRevamped;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import net.minecraft.util.Identifier;

public class InitializeComponents implements EntityComponentInitializer {
    public static final ComponentKey<PlayerComponent> PLAYER = ComponentRegistry.getOrCreate(Identifier.of(SPBRevamped.MOD_ID, "player"), PlayerComponent.class);
//    public static final ComponentKey<CameraBlockComponent> CAMERA_BLOCK = ComponentRegistry.getOrCreate(Identifier.of(SPBRevamped.MOD_ID, "camera_block"), CameraBlockComponent.class);
//    public static final ComponentKey<CameraItemComponent> CAMERA_ITEM = ComponentRegistry.getOrCreate(Identifier.of(SPBRevamped.MOD_ID, "camera_item"), CameraItemComponent.class);


    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(PLAYER, PlayerComponent::new, RespawnCopyStrategy.ALWAYS_COPY);
    }

//    @Override
//    public void registerBlockComponentFactories(BlockComponentFactoryRegistry registry) {
//        registry.registerFor(CameraBlockEntity.class, CAMERA_BLOCK, CameraBlockComponent::new);
//    }
//
//    @Override
//    public void registerItemComponentFactories(ItemComponentFactoryRegistry registry) {
//        registry.register(ModBlocks.CAMERA.asItem(), CAMERA_ITEM, CameraItemComponent::new);
//    }
}
