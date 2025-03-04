package com.sp.command;

import com.mojang.brigadier.CommandDispatcher;
import com.sp.SPBRevamped;
import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import com.sp.init.ModSounds;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Collection;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CastToTheBackroomsCommand {

    public static void register(CommandDispatcher<ServerCommandSource> serverCommandSourceCommandDispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        serverCommandSourceCommandDispatcher.register(
                CommandManager.literal("casttothebackrooms")
                        .requires(source -> source.hasPermissionLevel(2))
                        .then(CommandManager.argument("targets", EntityArgumentType.players())
                                    .executes(context -> execute(
                                                    EntityArgumentType.getPlayers(context, "targets")
                                            )
                                    )
                        )
        );
    }

    private static int execute(Collection<ServerPlayerEntity> targets){
        if(!targets.isEmpty()) {
            for (ServerPlayerEntity serverPlayer : targets) {
                PlayerComponent component = InitializeComponents.PLAYER.get(serverPlayer);
                ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
                ScheduledExecutorService executorService2 = Executors.newSingleThreadScheduledExecutor();

                //First set them to noclip and play the sound
                executorService.schedule(() -> {
                    SPBRevamped.sendCameraShakePacket(serverPlayer, 1.5, 2.5);
                    component.setShouldNoClip(true);
                    component.sync();
                    SPBRevamped.sendPersonalPlaySoundPacket(serverPlayer, ModSounds.NO_ESCAPE, 1.0f, 1.0f);
                    executorService.shutdown();
                }, 10000, TimeUnit.MILLISECONDS);

                //Half a second later tp to backrooms and stop the sound
                executorService2.schedule(() -> {
                    component.suffocationTimer = 40;
                    executorService2.shutdown();
                }, 10300, TimeUnit.MILLISECONDS);
            }
            return 1;
        } else {
            return -1;
        }

    }

}
