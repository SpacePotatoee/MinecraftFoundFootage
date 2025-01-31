package com.sp.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.sp.SPBRevamped;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.TimeArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Collection;

public class BlackScreenCommand {

    public static void register(CommandDispatcher<ServerCommandSource> serverCommandSourceCommandDispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        serverCommandSourceCommandDispatcher.register(
                CommandManager.literal("blackscreen")
                        .requires(source -> source.hasPermissionLevel(2))
                        .then(CommandManager.argument("targets", EntityArgumentType.players())
                                .then(CommandManager.argument("time", TimeArgumentType.time())
                                        .executes(context -> execute(
                                                EntityArgumentType.getPlayers(context, "targets"),
                                                IntegerArgumentType.getInteger(context, "time")
                                                )
                                        )
                                )
                        )
        );
    }

    private static int execute(Collection<ServerPlayerEntity> targets, int time){
        if(!targets.isEmpty()) {
            for (ServerPlayerEntity serverPlayer : targets) {
                SPBRevamped.sendBlackScreenPacket(serverPlayer, time, false, false);
            }
            return 1;
        } else {
            return -1;
        }

    }
}
