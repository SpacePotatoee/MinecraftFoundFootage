package com.sp.command;

import com.mojang.brigadier.CommandDispatcher;
import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Collection;

public class GimmeMyInventoryBack {

    public static void register(CommandDispatcher<ServerCommandSource> serverCommandSourceCommandDispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        serverCommandSourceCommandDispatcher.register(
                CommandManager.literal("gimmmiemyinventoryback")
                        .requires(source -> source.hasPermissionLevel(2))
                        .then(CommandManager.argument("targets", EntityArgumentType.players())
                                .executes(context -> execute(
                                                EntityArgumentType.getPlayers(context, "targets")
                                        )
                                )
                        )
        );
    }

    private static int execute(Collection<ServerPlayerEntity> targets) {
        for(ServerPlayerEntity player : targets) {
            PlayerComponent component = InitializeComponents.PLAYER.get(player);
            component.loadPlayerSavedInventory();
            return 1;
        }
        return 0;
    }

}
