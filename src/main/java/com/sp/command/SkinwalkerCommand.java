package com.sp.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.sp.SPBRevamped;
import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import com.sp.cca_stuff.WorldEvents;
import com.sp.entity.custom.SkinWalkerEntity;
import com.sp.init.ModEntities;
import com.sp.init.ModSounds;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;

import java.util.Collection;

public class SkinwalkerCommand {
    private static final SimpleCommandExceptionType TOO_MANY_TARGETS = new SimpleCommandExceptionType(new LiteralMessage("Can only apply to 1 target"));

    public static void register(CommandDispatcher<ServerCommandSource> serverCommandSourceCommandDispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        serverCommandSourceCommandDispatcher.register(
                CommandManager.literal("skinwalker")
                        .requires(source -> source.hasPermissionLevel(2))
                        .then(CommandManager.argument("targets", EntityArgumentType.players())
                                .executes(context -> execute(
                                                context.getSource(),
                                                EntityArgumentType.getPlayers(context, "targets")
                                        )
                                )
                        )
        );

        serverCommandSourceCommandDispatcher.register(
                CommandManager.literal("release")
                        .requires(source -> source.hasPermissionLevel(2))
                        .then(CommandManager.argument("targets", EntityArgumentType.players())
                                .executes(context -> release(
                                                context.getSource(),
                                                EntityArgumentType.getPlayers(context, "targets")
                                        )
                                )
                        )
        );
    }

    private static int execute(ServerCommandSource source, Collection<ServerPlayerEntity> targets) throws CommandSyntaxException {
        if (targets.size() > 1) {
            throw TOO_MANY_TARGETS.create();
        }

        for (ServerPlayerEntity target : targets) {
            SkinWalkerEntity skinWalkerEntity = ModEntities.SKIN_WALKER_ENTITY.create(source.getWorld());
            if (skinWalkerEntity != null) {
                PlayerComponent targetComponent = InitializeComponents.PLAYER.get(target);
                WorldEvents events = InitializeComponents.EVENTS.get(source.getWorld());

                skinWalkerEntity.refreshPositionAndAngles((double) target.getX(), (double) target.getY(), (double) target.getZ(), target.getYaw(), target.getPitch());
                skinWalkerEntity.setVelocity(target.getVelocity());
                source.getWorld().spawnEntity(skinWalkerEntity);
                events.activeSkinWalkerEntity = skinWalkerEntity;
                events.setActiveSkinwalkerTarget(target.getUuid());

                targetComponent.setPrevGameMode(target.interactionManager.getGameMode());
                targetComponent.setBeingCaptured(true);
                targetComponent.setHasBeenCaptured(true);
                targetComponent.setShouldBeMuted(true);
                targetComponent.sync();

                ((ServerPlayerEntity) target).changeGameMode(GameMode.SPECTATOR);
                ((ServerPlayerEntity) target).setCameraEntity(skinWalkerEntity);
            }
        }
        return 1;
    }

    private static int release(ServerCommandSource source, Collection<ServerPlayerEntity> targets) throws CommandSyntaxException {
        if (targets.size() > 1) {
            throw TOO_MANY_TARGETS.create();
        }

        for (ServerPlayerEntity target : targets) {
            PlayerComponent targetComponent = InitializeComponents.PLAYER.get(target);
            WorldEvents events = InitializeComponents.EVENTS.get(source.getWorld());

            targetComponent.setHasBeenCaptured(false);
            targetComponent.setShouldBeMuted(false);
            targetComponent.sync();

            target.changeGameMode(targetComponent.getPrevGameMode() != null ? targetComponent.getPrevGameMode() : GameMode.SURVIVAL);
            target.setCameraEntity(target);
            events.activeSkinWalkerEntity.discard();
            events.activeSkinWalkerEntity = null;

            SPBRevamped.sendPersonalPlaySoundPacket(target, ModSounds.SKINWALKER_RELEASE, 1.0f, 1.0f);
        }
        return 1;
    }

}
