package com.sp.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.sp.SPBRevamped;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Collection;

public class SetDoingTestCommand {

    public static void register(CommandDispatcher<ServerCommandSource> serverCommandSourceCommandDispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        serverCommandSourceCommandDispatcher.register(
                CommandManager.literal("dotest")
                        .requires(source -> source.hasPermissionLevel(2))
                        .then(CommandManager.argument("targets", EntityArgumentType.players())
                                .then(CommandManager.argument("value", BoolArgumentType.bool())
                                        .executes(context -> execute(
                                                        EntityArgumentType.getPlayers(context, "targets"),
                                                        BoolArgumentType.getBool(context, "value")
                                                )
                                        )
                                )
                        )
        );
    }

    private static int execute(Collection<ServerPlayerEntity> targets, boolean value){
        if(!targets.isEmpty()) {
            for (ServerPlayerEntity serverPlayer : targets) {
                SPBRevamped.sendDoTestPacket(serverPlayer, value);
            }
            return 1;
        } else {
            return -1;
        }

    }

}
