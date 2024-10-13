package com.sp.cca_stuff;

import com.sp.SPBRevamped;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import dev.onyxstudios.cca.api.v3.world.WorldComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.world.WorldComponentInitializer;
import net.minecraft.util.Identifier;

public class InitializeComponents implements EntityComponentInitializer, WorldComponentInitializer {
    public static final ComponentKey<PlayerComponent> PLAYER = ComponentRegistry.getOrCreate(Identifier.of(SPBRevamped.MOD_ID, "player"), PlayerComponent.class);
    public static final ComponentKey<WorldEvents> EVENTS = ComponentRegistry.getOrCreate(Identifier.of(SPBRevamped.MOD_ID, "events"), WorldEvents.class);


    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(PLAYER, PlayerComponent::new, RespawnCopyStrategy.ALWAYS_COPY);
    }

    @Override
    public void registerWorldComponentFactories(WorldComponentFactoryRegistry registry) {
        registry.register(EVENTS, WorldEvents::new);
    }
}
