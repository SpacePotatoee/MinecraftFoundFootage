package com.sp.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.WorldEvents;
import com.sp.init.BackroomsLevels;
import com.sp.world.events.AbstractEvent;
import com.sp.world.events.infinite_grass.InfiniteGrassAmbience;
import com.sp.world.events.level0.Level0Blackout;
import com.sp.world.events.level0.Level0Flicker;
import com.sp.world.events.level0.Level0IntercomBasic;
import com.sp.world.events.level0.Level0Music;
import com.sp.world.events.level1.Level1Ambience;
import com.sp.world.events.level1.Level1Blackout;
import com.sp.world.events.level1.Level1Flicker;
import com.sp.world.events.level2.Level2Warp;
import com.sp.world.events.poolrooms.PoolroomsAmbience;
import com.sp.world.events.poolrooms.PoolroomsSunset;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.world.World;

public class EventCommand {
    private static final SimpleCommandExceptionType FLICKER_BLACKOUT_EXCEPTION = new SimpleCommandExceptionType(new LiteralMessage("Event only occurs in Level 0 and Level 1"));
    private static final SimpleCommandExceptionType ONLY_LEVEL0_EXCEPTION = new SimpleCommandExceptionType(new LiteralMessage("Event only occurs in Level 0"));
    private static final SimpleCommandExceptionType AMBIENCE_EXCEPTION = new SimpleCommandExceptionType(new LiteralMessage("Event only occurs in Level 1, Level 2, The Poolrooms, or the Infinite Field"));
    private static final SimpleCommandExceptionType WARP_EXCEPTION = new SimpleCommandExceptionType(new LiteralMessage("Event only occurs in Level 2"));
    private static final SimpleCommandExceptionType SUNSET_EXCEPTION = new SimpleCommandExceptionType(new LiteralMessage("Event only occurs in The Poolrooms"));

    public static void register(CommandDispatcher<ServerCommandSource> serverCommandSourceCommandDispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        serverCommandSourceCommandDispatcher.register(
                CommandManager.literal("backroomsevent")
                        .requires(source -> source.hasPermissionLevel(2))
                        .then(CommandManager.literal("flicker")
                                .executes(context -> doFlicker(
                                                context.getSource()
                                        )
                                )
                        )
                        .then(CommandManager.literal("blackout")
                                .executes(context -> doBlackout(
                                                context.getSource()
                                        )
                                )
                        )
                        .then(CommandManager.literal("intercom")
                                .executes(context -> doIntercom(
                                                context.getSource()
                                        )
                                )
                        )
                        .then(CommandManager.literal("music")
                                .executes(context -> doMusic(
                                                context.getSource()
                                        )
                                )
                        )
                        .then(CommandManager.literal("ambience")
                                .executes(context -> doAmbience(
                                                context.getSource()
                                        )
                                )
                        )
                        .then(CommandManager.literal("warp")
                                .executes(context -> doWarp(
                                                context.getSource()
                                        )
                                )
                        )
                        .then(CommandManager.literal("sunset")
                                .executes(context -> doSunset(
                                                context.getSource()
                                        )
                                )
                        )
        );
    }

    private static int doFlicker(ServerCommandSource source) throws CommandSyntaxException {
        World world = source.getWorld();
        RegistryKey<World> registryKey = world.getRegistryKey();
        WorldEvents events = InitializeComponents.EVENTS.get(world);

        if (registryKey == BackroomsLevels.LEVEL0_WORLD_KEY) {
            Level0Flicker flicker = new Level0Flicker();
            setEvent(events, world, flicker);
            return 1;
        } else if (registryKey == BackroomsLevels.LEVEL1_WORLD_KEY) {
            Level1Flicker flicker = new Level1Flicker();
            setEvent(events, world, flicker);


            return 1;
        }

        throw FLICKER_BLACKOUT_EXCEPTION.create();
    }

    private static int doBlackout(ServerCommandSource source) throws CommandSyntaxException {
        World world = source.getWorld();
        RegistryKey<World> registryKey = world.getRegistryKey();
        WorldEvents events = InitializeComponents.EVENTS.get(world);

        if (registryKey == BackroomsLevels.LEVEL0_WORLD_KEY) {
            Level0Blackout blackout = new Level0Blackout();
            setEvent(events, world, blackout);

            return 1;
        } else if (registryKey == BackroomsLevels.LEVEL1_WORLD_KEY) {
            Level1Blackout blackout = new Level1Blackout();
            setEvent(events, world, blackout);

            return 1;
        }

        throw FLICKER_BLACKOUT_EXCEPTION.create();
    }

    private static int doIntercom(ServerCommandSource source) throws CommandSyntaxException {
        World world = source.getWorld();
        RegistryKey<World> registryKey = world.getRegistryKey();
        WorldEvents events = InitializeComponents.EVENTS.get(world);

        if (registryKey == BackroomsLevels.LEVEL0_WORLD_KEY) {
            Level0IntercomBasic intercom = new Level0IntercomBasic();
            setEvent(events, world, intercom);
            return 1;
        }

        throw ONLY_LEVEL0_EXCEPTION.create();
    }

    private static int doMusic(ServerCommandSource source) throws CommandSyntaxException {
        World world = source.getWorld();
        RegistryKey<World> registryKey = world.getRegistryKey();
        WorldEvents events = InitializeComponents.EVENTS.get(world);

        if (registryKey == BackroomsLevels.LEVEL0_WORLD_KEY) {
            Level0Music music = new Level0Music();
            setEvent(events, world, music);
            return 1;
        }

        throw ONLY_LEVEL0_EXCEPTION.create();
    }

    private static int doAmbience(ServerCommandSource source) throws CommandSyntaxException {
        World world = source.getWorld();
        RegistryKey<World> registryKey = world.getRegistryKey();
        WorldEvents events = InitializeComponents.EVENTS.get(world);

        if (registryKey == BackroomsLevels.POOLROOMS_WORLD_KEY) {
            PoolroomsAmbience ambience = new PoolroomsAmbience();
            setEvent(events, world, ambience);
            return 1;
        } else if (registryKey == BackroomsLevels.LEVEL1_WORLD_KEY) {
            Level1Ambience ambience = new Level1Ambience();
            setEvent(events, world, ambience);

            return 1;
        } else if (registryKey == BackroomsLevels.LEVEL2_WORLD_KEY) {
            Level1Ambience ambience = new Level1Ambience();
            setEvent(events, world, ambience);

            return 1;
        } else if (registryKey == BackroomsLevels.INFINITE_FIELD_WORLD_KEY) {
            InfiniteGrassAmbience ambience = new InfiniteGrassAmbience();
            setEvent(events, world, ambience);

            return 1;
        }

        throw AMBIENCE_EXCEPTION.create();
    }

    private static int doWarp(ServerCommandSource source) throws CommandSyntaxException {
        World world = source.getWorld();
        RegistryKey<World> registryKey = world.getRegistryKey();
        WorldEvents events = InitializeComponents.EVENTS.get(world);

        if (registryKey == BackroomsLevels.LEVEL2_WORLD_KEY) {
            Level2Warp warp = new Level2Warp();
            setEvent(events, world, warp);
            return 1;
        }

        throw WARP_EXCEPTION.create();
    }

    private static int doSunset(ServerCommandSource source) throws CommandSyntaxException {
        World world = source.getWorld();
        RegistryKey<World> registryKey = world.getRegistryKey();
        WorldEvents events = InitializeComponents.EVENTS.get(world);

        if (registryKey == BackroomsLevels.POOLROOMS_WORLD_KEY) {
            PoolroomsSunset sunset = new PoolroomsSunset();
            setEvent(events, world, sunset);
            return 1;
        }

        throw SUNSET_EXCEPTION.create();
    }

    private static void setEvent(WorldEvents events, World world, AbstractEvent activeEvent) {
        if (events.getActiveEvent() != null) {
            events.getActiveEvent().end(world);
        }
        events.setActiveEvent(activeEvent);
        activeEvent.init(world);
        events.ticks = 0;
    }

}
