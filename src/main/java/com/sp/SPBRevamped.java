package com.sp;

import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import com.sp.command.EventCommand;
import com.sp.command.GimmeMyInventoryBack;
import com.sp.command.SkinwalkerCommand;
import com.sp.compat.modmenu.ConfigStuff;
import com.sp.entity.custom.SkinWalkerEntity;
import com.sp.entity.custom.SmilerEntity;
import com.sp.entity.ik.model.GeckoLib.MowzieModelFactory;
import com.sp.entity.ik.util.PrAnCommonClass;
import com.sp.init.*;
import com.sp.item.ModItemGroups;
import com.sp.networking.InitializePackets;
import eu.midnightdust.lib.config.MidnightConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.bernie.geckolib.GeckoLib;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SPBRevamped implements ModInitializer {
	public static final String MOD_ID = "spb-revamped";
    public static final Logger LOGGER = LoggerFactory.getLogger("spb-revamped");
	public static final int finalMazeSize = 5;

	private static final UUID SLOW_SPEED_MODIFIER_ID = UUID.fromString("6a11099c-c3b8-4eba-9dad-f0c0bb997d35");
	public static final EntityAttributeModifier SLOW_SPEED_MODIFIER = new EntityAttributeModifier(SLOW_SPEED_MODIFIER_ID, "SPBRevamped slow walk speed", -0.2f, EntityAttributeModifier.Operation.MULTIPLY_TOTAL);

	@Override
	public void onInitialize() {
		if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            PrAnCommonClass.isDev = true;
            PrAnCommonClass.shouldRenderDebugLegs = true;
            PrAnCommonClass.LOGGER.info("Started in a development environment. Debug renderers will be activated by default.");
        }

		BackroomsLevels.init();

		ModItems.registerModItems();
		ModSounds.registerSounds();
		InitializePackets.registerC2SPackets();
		ModItemGroups.registerItemGroups();
		ModBlocks.init();
		ModBlockEntities.registerAllBlockEntities();
		MidnightConfig.init(MOD_ID, ConfigStuff.class);

		CommandRegistrationCallback.EVENT.register(EventCommand::register);
		CommandRegistrationCallback.EVENT.register(GimmeMyInventoryBack::register);
		CommandRegistrationCallback.EVENT.register(SkinwalkerCommand::register);

		// Thanks Bob Mowzie
		GeckoLibUtil.addCustomBakedModelFactory(MOD_ID, new MowzieModelFactory());
		GeckoLib.initialize();

		PrAnCommonClass.init();

		FabricDefaultAttributeRegistry.register(ModEntities.SKIN_WALKER_ENTITY, SkinWalkerEntity.createSkinWalkerAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.SMILER_ENTITY, SmilerEntity.createSmilerAttributes());

		LOGGER.info("\"WOOOOOOOOOOOOOOOOOOOOOOOooooooooooooooooooooooooo..........\" -He said as he fell into the backrooms, never to be seen again.");

		ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register(((player, origin, destination) -> {
			PacketByteBuf buffer = PacketByteBufs.create();
			ServerPlayNetworking.send(player, InitializePackets.RELOAD_LIGHTS, buffer);
		}));

		ServerPlayerEvents.AFTER_RESPAWN.register(((oldPlayer, newPlayer, alive) -> {
			if(!BackroomsLevels.isInBackrooms(oldPlayer.getWorld().getRegistryKey())) {
				return;
			}

			boolean backupInvulnerable;
			try {
				ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
				PlayerComponent playerComponent = InitializeComponents.PLAYER.get(newPlayer);

				sendBlackScreenPacket(newPlayer, 120, false, true);
				backupInvulnerable = newPlayer.getAbilities().invulnerable;
				newPlayer.getAbilities().invulnerable = true;
				playerComponent.setShouldRender(false);
				playerComponent.sync();
				newPlayer.networkHandler.sendPacket(new PlaySoundS2CPacket(RegistryEntry.of(ModSounds.NO_ESCAPE), SoundCategory.AMBIENT, newPlayer.getPos().getX(), newPlayer.getPos().getY(), newPlayer.getPos().getZ(), 100.0f, 1.0f, newPlayer.getRandom().nextLong()));

				//After YOU CAN'T ESCAPE is over
				executorService.schedule(() -> {
					playerComponent.setShouldRender(true);
					playerComponent.setShouldDoStatic(true);
					playerComponent.sync();
					newPlayer.getAbilities().invulnerable = backupInvulnerable;
					executorService.shutdown();
				}, 6000, TimeUnit.MILLISECONDS);

				executorService.schedule(() -> {
					playerComponent.setShouldDoStatic(false);
					playerComponent.sync();
					executorService.shutdown();
				}, 8000, TimeUnit.MILLISECONDS);
			} catch (Exception e) {
				LOGGER.error("Error in AFTER_RESPAWN event: {}", String.valueOf(e));
			}
		}));
	}

	public static void sendCameraShakePacket(ServerPlayerEntity player, double speed, double trauma){
		PacketByteBuf buffer = PacketByteBufs.create();
		buffer.writeDouble(speed);
		buffer.writeDouble(trauma);
		ServerPlayNetworking.send(player, InitializePackets.SCREEN_SHAKE, buffer);
	}

	public static void sendBlackScreenPacket(ServerPlayerEntity player, int duration, boolean shouldPauseSounds, boolean noEscape){
		PacketByteBuf buffer = PacketByteBufs.create();
		buffer.writeInt(duration);
		buffer.writeBoolean(shouldPauseSounds);
		buffer.writeBoolean(noEscape);
		ServerPlayNetworking.send(player, InitializePackets.BLACK_SCREEN, buffer);
	}

	public static void sendPersonalPlaySoundPacket(ServerPlayerEntity player, SoundEvent sound, float volume, float pitch){
		PacketByteBuf buffer = PacketByteBufs.create();
		buffer.writeRegistryEntry(Registries.SOUND_EVENT.getIndexedEntries(), RegistryEntry.of(sound), (packetByteBuf, soundEvent) -> soundEvent.writeBuf(packetByteBuf));
		buffer.writeFloat(volume);
		buffer.writeFloat(pitch);
		ServerPlayNetworking.send(player, InitializePackets.SOUND, buffer);
	}

	public static void sendLevelTransitionLightsOutPacket(ServerPlayerEntity player, int time) {
		PlayerComponent component = InitializeComponents.PLAYER.get(player);
		component.setTeleporting(true);
		PacketByteBuf buffer = PacketByteBufs.create();
		buffer.writeInt(time);
		ServerPlayNetworking.send(player, InitializePackets.LEVEL_TRANSITION_LIGHTSOUT, buffer);
	}
}