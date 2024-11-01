package com.sp;

import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import com.sp.init.ModBlockEntities;
import com.sp.item.ModItemGroups;
import com.sp.init.ModItems;
import com.sp.networking.InitializePackets;
import com.sp.init.ModSounds;
import com.sp.world.generation.Level0ChunkGenerator;
import com.sp.world.generation.Level1ChunkGenerator;
import com.sp.world.generation.Level2ChunkGenerator;
import com.sp.world.generation.PoolroomsChunkGenerator;
import eu.midnightdust.lib.config.MidnightConfig;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SPBRevamped implements ModInitializer {
	public static final String MOD_ID = "spb-revamped";
    public static final Logger LOGGER = LoggerFactory.getLogger("spb-revamped");
	public static final int finalMazeSize = 5;

	@Override
	public void onInitialize() {
		MidnightConfig.init(MOD_ID, ConfigStuff.class);

		Registry.register(Registries.CHUNK_GENERATOR, new Identifier(MOD_ID, "level0_chunk_generator"), Level0ChunkGenerator.CODEC);
		Registry.register(Registries.CHUNK_GENERATOR, new Identifier(MOD_ID, "level1_chunk_generator"), Level1ChunkGenerator.CODEC);
		Registry.register(Registries.CHUNK_GENERATOR, new Identifier(MOD_ID, "level2_chunk_generator"), Level2ChunkGenerator.CODEC);
		Registry.register(Registries.CHUNK_GENERATOR, new Identifier(MOD_ID, "poolrooms_chunk_generator"), PoolroomsChunkGenerator.CODEC);

		ModItems.registerModItems();
		ModSounds.registerSounds();
		InitializePackets.registerC2SPackets();
		ModItemGroups.registerItemGroups();
		ModBlockEntities.registerAllBlockEntities();

		System.out.println("\"WOOOOOOOOOOOOOOOOOOOOOOOooooooooooooooooooooooooo..........\" -He said as he fell into the backrooms, never to be seen again.");

		ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register(((player, origin, destination) -> {
			PlayerComponent playerComponent = InitializeComponents.PLAYER.get(player);

			playerComponent.setReloadLights(true);
			playerComponent.sync();

		}));
	}
}