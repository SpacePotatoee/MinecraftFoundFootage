package com.sp.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.sp.init.BackroomsLevels;
import com.sp.world.levels.BackroomsLevel;
import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;

import java.util.Optional;

public class LevelCommand {
    public static void register(CommandDispatcher<ServerCommandSource> serverCommandSourceCommandDispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        serverCommandSourceCommandDispatcher.register(
                CommandManager.literal("level")
                        .requires(source -> source.hasPermissionLevel(2))
                        .then(CommandManager.argument("level", StringArgumentType.word()).suggests(
                                (context, builder) -> {
                                    for (BackroomsLevel backroomsLevel : BackroomsLevels.BACKROOMS_LEVELS) {
                                        builder.suggest(backroomsLevel.getLevelId());
                                    }
                                    return builder.buildFuture();
                                }
                        ).executes((context -> {
                            String levelId = StringArgumentType.getString(context, "level");
                            Optional<BackroomsLevel> optionalBackroomsLevel = BackroomsLevels.getById(levelId);

                            if (optionalBackroomsLevel.isPresent()) {
                                BackroomsLevel backroomsLevel = optionalBackroomsLevel.get();

                                Entity entity = context.getSource().getEntityOrThrow();

                                if (entity instanceof PlayerEntity player) {

                                    TeleportTarget target = new TeleportTarget(backroomsLevel.getSpawnPos(), Vec3d.ZERO, 0, 90);
                                    FabricDimensions.teleport(player, context.getSource().getWorld().getServer().getWorld(backroomsLevel.getWorldKey()), target);
                                }


                            }

                            return 1;
                        })))
        );
    }
}
