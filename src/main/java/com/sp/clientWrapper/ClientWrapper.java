package com.sp.clientWrapper;

import com.sp.Keybinds;
import com.sp.SPBRevampedClient;
import com.sp.block.custom.EmergencyLightBlock;
import com.sp.block.custom.FluorescentLightBlock;
import com.sp.block.custom.ThinFluorescentLightBlock;
import com.sp.block.entity.EmergencyLightBlockEntity;
import com.sp.block.entity.FluorescentLightBlockEntity;
import com.sp.block.entity.ThinFluorescentLightBlockEntity;
import com.sp.block.entity.TinyFluorescentLightBlockEntity;
import com.sp.cca_stuff.PlayerComponent;
import com.sp.compat.modmenu.ConfigStuff;
import com.sp.entity.client.SkinWalkerCapturedFlavorText;
import com.sp.entity.custom.SkinWalkerEntity;
import com.sp.entity.custom.SmilerEntity;
import com.sp.entity.ik.parts.sever_limbs.ServerLimb;
import com.sp.init.BackroomsLevels;
import com.sp.init.HelpfulHintManager;
import com.sp.init.ModSounds;
import com.sp.networking.InitializePackets;
import com.sp.sounds.*;
import com.sp.sounds.entity.SkinWalkerChaseSoundInstance;
import com.sp.sounds.entity.SmilerAmbienceSoundInstance;
import com.sp.sounds.entity.SmilerGlitchSoundInstance;
import com.sp.sounds.pipes.GasPipeSoundInstance;
import com.sp.sounds.pipes.WaterPipeSoundInstance;
import com.sp.world.levels.BackroomsLevel;
import com.sp.world.levels.custom.Level0BackroomsLevel;
import com.sp.world.levels.custom.Level1BackroomsLevel;
import com.sp.world.levels.custom.Level2BackroomsLevel;
import com.sp.world.levels.custom.PoolroomsBackroomsLevel;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.deferred.light.AreaLight;
import foundry.veil.api.client.render.deferred.light.PointLight;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.RegistryKey;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3d;

import java.util.List;
import java.util.Optional;

import static com.sp.block.custom.ThinFluorescentLightBlock.FACE;
import static com.sp.block.custom.ThinFluorescentLightBlock.FACING;

/**
 * This class is just here to avoid dedicated server crashes.
 * Minecraft seams to crash even when a client class is present in a method without being called on the client.
 * This mostly happens with Sound Instances. And Veil lights.
 **/
public class ClientWrapper {
    public static void skinWalkerPlayStepSound(ServerLimb limb) {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            MinecraftClient client = MinecraftClient.getInstance();
            client.getSoundManager().play(new PositionedSoundInstance(ModSounds.SKINWALKER_FOOTSTEP, SoundCategory.HOSTILE, 10.0f, 1.0f, limb.random, limb.pos.x, limb.pos.y, limb.pos.z));
        }

        limb.playedStepSound = true;
    }

    public static void tickClientPlayerComponent(PlayerComponent playerComponent) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.player != null && playerComponent.player == client.player) {
            SoundManager soundManager = client.getSoundManager();

            ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

            //Get a list of all the smilers in the area and see if any of them can see you
            List<SmilerEntity> smilerEntityList = playerComponent.player.getWorld().getEntitiesByClass(SmilerEntity.class, playerComponent.player.getBoundingBox().expand(15, 1, 15), livingEntity -> true);
            boolean isSeen = false;
            if (!smilerEntityList.isEmpty()) {
                for (SmilerEntity smiler : smilerEntityList) {
                    if (smiler.canSee(playerComponent.player)) {
                        playerComponent.setShouldGlitch(true);
                        isSeen = true;
                        break;
                    }
                }
            }

            if (!isSeen) {
                playerComponent.setShouldGlitch(false);
            }
            //Update smiler glitch effect
            if (playerComponent.shouldGlitch()) {
                playerComponent.glitchTick = Math.min(playerComponent.glitchTick + 1, 80);
                playerComponent.glitchTimer = Math.min((float) playerComponent.glitchTick / 80, 1.0f);

                if (!soundManager.isPlaying(playerComponent.GlitchAmbience)) {
                    playerComponent.GlitchAmbience = new SmilerGlitchSoundInstance(playerComponent.player);
                    soundManager.play(playerComponent.GlitchAmbience);
                }

                if (playerComponent.glitchTimer >= 0.25f) {
                    if (!playerComponent.shouldInflictGlitchDamage) {
                        playerComponent.shouldInflictGlitchDamage = true;
//                                System.out.println("SENT TRUE TO: " + playerComponent.player.getName().toString());
                        SPBRevampedClient.sendComponentSyncPacket(true, "glitch");
                    }
                }

            } else if (!playerComponent.isTeleportingToPoolrooms() && !(SPBRevampedClient.isInLevel(BackroomsLevels.LEVEL324_BACKROOMS_LEVEL) && playerComponent.player.getWorld().getBlockState(playerComponent.player.getBlockPos().offset(Direction.DOWN, 2)).isOf(Blocks.GREEN_WOOL))) {
                playerComponent.glitchTick = Math.max(playerComponent.glitchTick - 1, 0);
                playerComponent.glitchTimer = Math.max((float) playerComponent.glitchTick / 80, 0.0f);

                if (playerComponent.glitchTimer <= 0) {
                    if (soundManager.isPlaying(playerComponent.GlitchAmbience)) {
                        soundManager.stop(playerComponent.GlitchAmbience);
                    }
                }

                if (playerComponent.glitchTimer <= 0.75f) {
                    if (playerComponent.shouldInflictGlitchDamage) {
                        playerComponent.shouldInflictGlitchDamage = false;
//                                System.out.println("SENT FALSE TO: " + playerComponent.player.getName().toString());
                        SPBRevampedClient.sendComponentSyncPacket(false, "glitch");
                    }
                }
            }

            if (SPBRevampedClient.isInLevel(BackroomsLevels.LEVEL324_BACKROOMS_LEVEL) && playerComponent.player.getWorld().getBlockState(playerComponent.player.getBlockPos().offset(Direction.DOWN, 2)).isOf(Blocks.GREEN_WOOL)) {
                playerComponent.glitchTick = Math.min(playerComponent.glitchTick + 4, 120);
                playerComponent.glitchTimer = (float) playerComponent.glitchTick / 30;

                if (!soundManager.isPlaying(playerComponent.GlitchAmbience)) {
                    playerComponent.GlitchAmbience = new SmilerGlitchSoundInstance(playerComponent.player);
                    soundManager.play(playerComponent.GlitchAmbience);
                }


                if (playerComponent.glitchTimer >= 3) {
                    if (SPBRevampedClient.isInLevel(BackroomsLevels.LEVEL324_BACKROOMS_LEVEL) && playerComponent.player.getWorld().getBlockState(playerComponent.player.getBlockPos().offset(Direction.DOWN, 3)).isOf(Blocks.RED_WOOL)) {
                        playerComponent.player.teleport(playerComponent.player.getX(), playerComponent.player.getY() - 65, playerComponent.player.getZ());
                    }
                }
            }


            //Teleporting to poolrooms Glitch
            if (playerComponent.isTeleportingToPoolrooms()) {
                playerComponent.glitchTick = Math.min(playerComponent.glitchTick + 1, 120);
                playerComponent.glitchTimer = Math.min((float) playerComponent.glitchTick / 120, 1.0f);

                if (!soundManager.isPlaying(playerComponent.GlitchAmbience)) {
                    playerComponent.GlitchAmbience = new SmilerGlitchSoundInstance(playerComponent.player);
                    soundManager.play(playerComponent.GlitchAmbience);
                }
            }

            ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

            //Sync Target Entity for updating SkinWalker suspicion
            if (playerComponent.getTargetEntity() != client.targetedEntity) {
                playerComponent.setTargetEntity(client.targetedEntity);

                PacketByteBuf buffer = PacketByteBufs.create();
                if (playerComponent.getTargetEntity() != null) {
                    buffer.writeInt(playerComponent.getTargetEntity().getId());
                } else {
                    buffer.writeInt(-1);
                }
                ClientPlayNetworking.send(InitializePackets.TARGET_ENTITY_SYNC, buffer);
            }

            ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

            //Flavor text while being controlled by the SkinWalker
            if (playerComponent.hasBeenCaptured() && !playerComponent.isBeingCaptured()) {
                SkinWalkerCapturedFlavorText.tickFlavorText(playerComponent.player);
            }

            ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

            //Client side stuff for level 0 -> 1 and 1 -> 2 and so on.

            Optional<BackroomsLevel> backroomsLevel = BackroomsLevels.getLevel(playerComponent.player.getWorld());

            if (backroomsLevel.isPresent()) {
                BackroomsLevel level = backroomsLevel.get();

                List<BackroomsLevel.LevelTransition> teleports = level.checkForTransition(playerComponent, playerComponent.player.getWorld());

                if (!teleports.isEmpty() && playerComponent.currentTransition == null) {
                    playerComponent.currentTransition = teleports.get(0);
                }

                ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

                //Flashlight

                if (Keybinds.toggleFlashlight.wasPressed() && !SPBRevampedClient.getCutsceneManager().isPlaying && !SPBRevampedClient.getCutsceneManager().blackScreen.isBlackScreen && !playerComponent.hasBeenCaptured && !playerComponent.isBeingCaptured()) {
                    playerComponent.player.playSound(ModSounds.FLASHLIGHT_CLICK, 0.5f, 1);
                    if (level.allowsTorch().value()) {
                        playerComponent.setFlashLightOn(!playerComponent.isFlashLightOn());
                        HelpfulHintManager.disableFlashlightHint();

                        if (!playerComponent.player.isSpectator()) {
                            SPBRevampedClient.sendComponentSyncPacket(playerComponent.isFlashLightOn(), "flashlight");
                        }
                    } else {
                        playerComponent.setFlashLightOn(false);
                        playerComponent.player.sendMessage(level.allowsTorch().string(), true);
                    }
                } else if (playerComponent.hasBeenCaptured && playerComponent.isBeingCaptured()) {
                    if (playerComponent.isFlashLightOn()) {
                        playerComponent.setFlashLightOn(false);

                        SPBRevampedClient.sendComponentSyncPacket(playerComponent.isFlashLightOn(), "flashlight");
                    }
                }

                if (!level.allowsTorch().value()) {
                    if (playerComponent.isFlashLightOn()) {
                        SPBRevampedClient.sendComponentSyncPacket(false, "flashlight");
                    }
                    playerComponent.setFlashLightOn(false);
                }

                ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            }


            if (playerComponent.currentTransition != null) {
                if (playerComponent.getTeleportingTimer() == -1) {
                    playerComponent.setTeleportingTimer(playerComponent.currentTransition.duration());
                }

                if (playerComponent.getTeleportingTimer() == 0) {
                    playerComponent.currentTransition.teleport().to().transitionOut(playerComponent.currentTransition.teleport());
                    playerComponent.currentTransition.teleport().to().transitionIn(playerComponent.currentTransition.teleport());
                    playerComponent.currentTransition = null;
                }
            }

            if (playerComponent.getTeleportingTimer() >= 1) {
                if (playerComponent.currentTransition != null) {
                    playerComponent.currentTransition.callback().tick(playerComponent.currentTransition.teleport(), playerComponent.getTeleportingTimer());
                }
                playerComponent.setTeleportingTimer(playerComponent.getTeleportingTimer() - 1);
            }

            ////AMBIENCE////
            RegistryKey<World> levelKey = playerComponent.player.getWorld().getRegistryKey();

            if ((levelKey == BackroomsLevels.LEVEL1_WORLD_KEY || levelKey == BackroomsLevels.LEVEL2_WORLD_KEY) && !soundManager.isPlaying(playerComponent.DeepAmbience)) {
                playerComponent.DeepAmbience = new AmbientSoundInstance(playerComponent.player);
                soundManager.play(playerComponent.DeepAmbience);
            }

            if (levelKey == BackroomsLevels.LEVEL2_WORLD_KEY && !soundManager.isPlaying(playerComponent.WaterPipeAmbience) && !soundManager.isPlaying(playerComponent.GasPipeAmbience)) {
                playerComponent.WaterPipeAmbience = new WaterPipeSoundInstance(playerComponent.player);
                playerComponent.GasPipeAmbience = new GasPipeSoundInstance(playerComponent.player);

                soundManager.play(playerComponent.WaterPipeAmbience);
                soundManager.play(playerComponent.GasPipeAmbience);
            }

            if ((BackroomsLevels.getLevel(playerComponent.player.getWorld()).orElse(BackroomsLevels.OVERWORLD_REPRESENTING_BACKROOMS_LEVEL))
                    instanceof Level2BackroomsLevel level) {
                if (levelKey == BackroomsLevels.LEVEL2_WORLD_KEY && !soundManager.isPlaying(playerComponent.WarpAmbience) && level.isWarping()) {
                    playerComponent.WarpAmbience = new CreakingSoundInstance(playerComponent.player);
                    soundManager.play(playerComponent.WarpAmbience);
                }
            }

            if ((BackroomsLevels.getLevel(playerComponent.player.getWorld()).orElse(BackroomsLevels.OVERWORLD_REPRESENTING_BACKROOMS_LEVEL))
                    instanceof PoolroomsBackroomsLevel level) {
                if (level.isNoon() && !soundManager.isPlaying(playerComponent.PoolroomsNoonAmbience)) {
                    playerComponent.PoolroomsNoonAmbience = new PoolroomsNoonAmbienceSoundInstance(playerComponent.player);
                    soundManager.play(playerComponent.PoolroomsNoonAmbience);
                }
            }

            if ((BackroomsLevels.getLevel(playerComponent.player.getWorld()).orElse(BackroomsLevels.OVERWORLD_REPRESENTING_BACKROOMS_LEVEL))
                    instanceof PoolroomsBackroomsLevel level) {
                if (!level.isNoon() && !soundManager.isPlaying(playerComponent.PoolroomsSunsetAmbience)) {
                    playerComponent.PoolroomsSunsetAmbience = new PoolroomsSunsetAmbienceSoundInstance(playerComponent.player);
                    soundManager.play(playerComponent.PoolroomsSunsetAmbience);
                }
            }

            if ((BackroomsLevels.getLevel(playerComponent.player.getWorld()).orElse(BackroomsLevels.OVERWORLD_REPRESENTING_BACKROOMS_LEVEL))
                    instanceof Level1BackroomsLevel level) {
                if (level.getLightState() == Level0BackroomsLevel.LightState.BLACKOUT && !soundManager.isPlaying(playerComponent.SmilerAmbience)) {
                    playerComponent.SmilerAmbience = new SmilerAmbienceSoundInstance(playerComponent.player);
                    soundManager.play(playerComponent.SmilerAmbience);
                }
            }

            if ((levelKey == BackroomsLevels.INFINITE_FIELD_WORLD_KEY) && !soundManager.isPlaying(playerComponent.WindAmbience)) {
                playerComponent.WindAmbience = new InfiniteGrassAmbienceSoundInstance(playerComponent.player);
                soundManager.play(playerComponent.WindAmbience);
            }

            if ((levelKey == BackroomsLevels.LEVEL324_WORLD_KEY) && !soundManager.isPlaying(playerComponent.WindAmbience) && playerComponent.player.getY() > 20) {
                playerComponent.WindAmbience = new InfiniteGrassAmbienceSoundInstance(playerComponent.player);
                soundManager.play(playerComponent.WindAmbience);
            }

            if ((levelKey == BackroomsLevels.LEVEL324_WORLD_KEY) && !soundManager.isPlaying(playerComponent.WindTunnelAmbience) && playerComponent.player.getY() < 20) {
                playerComponent.WindTunnelAmbience = new WindTunnelAmbienceSoundInstance(playerComponent.player);
                soundManager.play(playerComponent.WindTunnelAmbience);
                if (soundManager.isPlaying(playerComponent.WindAmbience)) {
                    soundManager.stop(playerComponent.WindAmbience);
                }
            }

            ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

            //Level0 Cutscene
            if (playerComponent.player.isInsideWall() && playerComponent.player.getWorld().getRegistryKey() == World.OVERWORLD && !playerComponent.isDoingCutscene()) {
                playerComponent.suffocationTimer++;
                if (playerComponent.suffocationTimer >= 40) {
                    playerComponent.setDoingCutscene(true);
                    playerComponent.suffocationTimer = 0;
                }
            }

        }
    }

    public static void onRemoveSkinWalkerClientSide(SkinWalkerEntity entity) {
        if (entity.chaseSoundInstance != null && entity.getWorld().isClient) {
            MinecraftClient.getInstance().getSoundManager().stop(entity.chaseSoundInstance);
        }
    }

    public static void handleSkinWalkerEntityClientSide(SkinWalkerEntity entity) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (!client.getSoundManager().isPlaying(entity.chaseSoundInstance)) {
            entity.chaseSoundInstance = new SkinWalkerChaseSoundInstance(entity);
            client.getSoundManager().play(entity.chaseSoundInstance);
        }
    }

    public static void tickEmergencyLight(World world, BlockPos pos, BlockState state, EmergencyLightBlockEntity block) {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;

        if (player == null) {
            return;
        }

        if (state.get(EmergencyLightBlock.RED_LIGHT)) {
            if (!block.playingEmergencyAlarm) {
                block.emergencyAlarmSoundInstance = new EmergencyAlarmSoundInstance(block, player);
                client.getSoundManager().play(block.emergencyAlarmSoundInstance);
                block.setEmergencyAlarm(true);
            }

            AxisAngle4f axisAngle4d = new AxisAngle4f();
            Quaternionf quaternionf = new Quaternionf();
            Vec3d centerPos = pos.toCenterPos();
            switch (state.get(EmergencyLightBlock.FACE)) {
                case WALL -> {
                    switch (state.get(EmergencyLightBlock.FACING)) {
                        case EAST -> {
                            axisAngle4d.set(0.0f, 1, 0, 0);
                            quaternionf.rotateXYZ(0.0f, 0.0f, (float) Math.toRadians(90.0f));
                            centerPos = centerPos.add(-0.28125, 0.0f, 0.0f);
                        }
                        case WEST -> {
                            axisAngle4d.set(0.0f, -1, 0, 0);
                            quaternionf.rotateXYZ(0.0f, 0.0f, (float) Math.toRadians(90.0f));
                            centerPos = centerPos.add(0.28125, 0.0f, 0.0f);
                        }
                        case NORTH -> {
                            axisAngle4d.set(0.0f, 0, 0, -1);
                            quaternionf.rotateXYZ((float) Math.toRadians(90.0f), 0.0f, 0.0f);
                            centerPos = centerPos.add(0.0f, 0.0f, 0.28125);
                        }
                        case SOUTH -> {
                            axisAngle4d.set(0.0f, 0, 0, 1);
                            quaternionf.rotateXYZ((float) Math.toRadians(90.0f), 0.0f, 0.0f);
                            centerPos = centerPos.add(0.0f, 0.0f, -0.28125);
                        }
                    }

                }
                case FLOOR -> {
                    axisAngle4d.set(0.0f, 0, 1, 0);
                    centerPos = centerPos.add(0.0f, -0.28125, 0.0f);
                }
                default -> {
                    axisAngle4d.set(0.0f, 0, -1, 0);
                    centerPos = centerPos.add(0.0f, 0.28125, 0.0f);
                }
            }

            block.removeNormalLights();

            if (!block.initEmergencyLights) {
                block.areaLight1 = new AreaLight();
                block.areaLight2 = new AreaLight();
                block.pointLight = new PointLight();


                VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().addLight(block.areaLight1
                        .setBrightness(1.0f)
                        .setColor(1.0f, 0.0f, 0.0f)
                        .setSize(0.0, 0.0)
                        .setAngle((float) Math.toRadians(50.0f))
                        .setOrientation(new Quaternionf().rotateXYZ(0, 0, 0))
                        .setPosition(new Vector3d(centerPos.x, centerPos.y, centerPos.z))
                        .setDistance(15)
                );
                VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().addLight(block.areaLight2
                        .setBrightness(1.0f)
                        .setColor(1.0f, 0.0f, 0.0f)
                        .setSize(0.0, 0.0)
                        .setAngle((float) Math.toRadians(50.0f))
                        .setOrientation(new Quaternionf().rotateXYZ(0, 0, 0))
                        .setPosition(new Vector3d(centerPos.x, centerPos.y, centerPos.z))
                        .setDistance(15)
                );
                VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().addLight(block.pointLight
                        .setBrightness(0.5f)
                        .setColor(1.0f, 0.0f, 0.0f)
                        .setPosition(new Vector3d(centerPos.x, centerPos.y, centerPos.z))
                        .setRadius(15.0f)
                );
                block.initEmergencyLights = true;
            }

            Quaternionf quaternionf1 = new Quaternionf(quaternionf);
            block.areaLight1.setOrientation(quaternionf1.rotateLocalY((float) Math.toRadians(block.randomOffset + world.getTime() * 20)));

            Quaternionf quaternionf2 = new Quaternionf(quaternionf);
            block.areaLight2.setOrientation(quaternionf2.rotateLocalY((float) Math.toRadians(block.randomOffset + 180.0f + world.getTime() * 20)));

            return;
        }

        if (block.playingEmergencyAlarm) {
            client.getSoundManager().stop(block.emergencyAlarmSoundInstance);
            block.emergencyAlarmSoundInstance = null;
            block.setEmergencyAlarm(false);
        }

        block.removeEmergencyLights();

        if (!block.initNormalLights) {
            block.pointLight = new PointLight();
            Vec3d centerPos = pos.toCenterPos().add(0.0f, -0.15625f, 0.0f);
            VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().addLight(block.pointLight
                    .setBrightness(1.0f)
                    .setPosition(new Vector3d(centerPos.x, centerPos.y, centerPos.z))
                    .setRadius(15.0f)
            );
            block.initNormalLights = true;
        }
    }

    public static void doClientSideThinFluorescentsTick(World world, BlockPos pos, BlockState state, java.util.Random random1, Vec3d position, ThinFluorescentLightBlockEntity block) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;

        if (player != null) {
            Vec3d playerPos = player.getPos();
            double distance;

            if (world.getRegistryKey() == BackroomsLevels.LEVEL2_WORLD_KEY) {
                distance = Math.min(ConfigStuff.getLightRenderDistance(), 32);
            } else {
                distance = ConfigStuff.getLightRenderDistance();
            }

            boolean withinDistance = pos.isWithinDistance(playerPos, distance);

            if (withinDistance) {
                if (!state.get(ThinFluorescentLightBlock.COPY) && pos.isWithinDistance(playerPos, 15.0f)) {
                    if (block.prevOn != world.getBlockState(pos).get(ThinFluorescentLightBlock.ON)) {
                        MinecraftClient.getInstance().getSoundManager().play(new PositionedSoundInstance(ModSounds.LIGHT_BLINK, SoundCategory.AMBIENT, 0.2F, random1.nextFloat(0.9f, 1.1f), block.random, pos));
                    }
                }

                if (!state.get(ThinFluorescentLightBlock.COPY) && state.get(ThinFluorescentLightBlock.ON) && !state.get(ThinFluorescentLightBlock.BLACKOUT)) {

                    if (!block.isPlayingSound() && pos.isWithinDistance(playerPos, 15.0f) && !SPBRevampedClient.blackScreen) {
                        MinecraftClient.getInstance().getSoundManager().play(new ThinFluorescentLightSoundInstance(block, player));
                        block.setPlayingSound(true);
                    }

                    if (block.pointLight == null) {
                        block.pointLight = new PointLight();
                        VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().addLight(block.pointLight
                                .setRadius(18f)
                                .setBrightness(0.0024f)
                        );
                        switch (state.get(FACE)) {
                            case FLOOR:
                                block.pointLight.setPosition(position.x, position.y, position.z);
                            case WALL:
                                switch (state.get(FACING)) {
                                    case EAST:
                                        block.pointLight.setPosition(position.x, position.y, position.z + 0.5);
                                    case WEST:
                                        block.pointLight.setPosition(position.x, position.y, position.z - 0.5);
                                    case SOUTH:
                                        block.pointLight.setPosition(position.x + 0.5, position.y, position.z);
                                    case NORTH:
                                    default:
                                        block.pointLight.setPosition(position.x - 0.5, position.y, position.z);
                                }
                            case CEILING:
                            default:
                                block.pointLight.setPosition(position.x, position.y, position.z);

                        }

                        switch (world.getRegistryKey().getValue().toString()) {
                            case "spb-revamped:poolrooms": {
                                block.pointLight
                                        .setColor(175, 175, 255)
                                        .setBrightness(0.0035f);
                            }
                            break;
                            case "spb-revamped:level0": {
                                block.pointLight
                                        .setColor(200, 200, 255)
                                        .setBrightness(0.005f);
                            }
                            break;
                            default: {
                                block.pointLight.setColor(255, 255, 255);
                            }
                        }

                        if (world.getRegistryKey() == BackroomsLevels.LEVEL2_WORLD_KEY) {
                            block.pointLight
                                    .setColor(200, 200, 255)
                                    .setBrightness(0.005f);
                        }
                    }
                } else {
                    if (block.pointLight != null) {
                        VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().removeLight(block.pointLight);
                        block.pointLight = null;
                    }
                }
            } else {
                if (block.pointLight != null) {
                    VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().removeLight(block.pointLight);
                    block.pointLight = null;
                }
            }
        }
    }


    public static void doClientSideTinyFluorescentsTick(World world, BlockPos pos, BlockState state, java.util.Random random1, Vec3d position, TinyFluorescentLightBlockEntity block) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;

        if (player != null) {
            Vec3d playerPos = player.getPos();
            double distance;

            if (world.getRegistryKey() == BackroomsLevels.LEVEL2_WORLD_KEY) {
                distance = Math.min(ConfigStuff.getLightRenderDistance(), 32);
            } else {
                distance = ConfigStuff.getLightRenderDistance();
            }

            boolean withinDistance = pos.isWithinDistance(playerPos, distance);

            if (withinDistance) {
                if (!state.get(ThinFluorescentLightBlock.COPY) && pos.isWithinDistance(playerPos, 15.0f)) {
                    if (block.prevOn != world.getBlockState(pos).get(ThinFluorescentLightBlock.ON)) {
                        MinecraftClient.getInstance().getSoundManager().play(new PositionedSoundInstance(ModSounds.LIGHT_BLINK, SoundCategory.AMBIENT, 0.2F, random1.nextFloat(0.9f, 1.1f), block.random, pos));
                    }
                }

                if (!state.get(ThinFluorescentLightBlock.COPY) && state.get(ThinFluorescentLightBlock.ON) && !state.get(ThinFluorescentLightBlock.BLACKOUT)) {

                    if (!block.isPlayingSound() && pos.isWithinDistance(playerPos, 15.0f) && !SPBRevampedClient.blackScreen) {
                        MinecraftClient.getInstance().getSoundManager().play(new TinyFluorescentLightSoundInstance(block, player));
                        block.setPlayingSound(true);
                    }

                    if (block.pointLight == null) {
                        block.pointLight = new PointLight();
                        VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().addLight(block.pointLight
                                .setRadius(18f)
                                .setBrightness(0.0024f)
                        );

                        block.pointLight.setPosition(position.x, position.y, position.z);

                        block.pointLight.setColor(255, 255, 255);

                        if (world.getRegistryKey().equals(BackroomsLevels.POOLROOMS_WORLD_KEY)) {
                            block.pointLight
                                    .setColor(175, 175, 255)
                                    .setBrightness(0.0035f);
                        }

                        if (world.getRegistryKey() == BackroomsLevels.LEVEL2_WORLD_KEY) {
                            block.pointLight
                                    .setColor(200, 200, 255)
                                    .setBrightness(0.005f);
                        }

                        if (world.getRegistryKey().equals(BackroomsLevels.LEVEL0_WORLD_KEY)) {
                            block.pointLight
                                    .setColor(200, 200, 255)
                                    .setBrightness(0.005f);
                        }
                    }
                } else {
                    if (block.pointLight != null) {
                        VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().removeLight(block.pointLight);
                        block.pointLight = null;
                    }
                }
            } else {
                if (block.pointLight != null) {
                    VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().removeLight(block.pointLight);
                    block.pointLight = null;
                }
            }
        }
    }

    public static void doClientSideTick(World world, BlockPos pos, BlockState state, FluorescentLightBlockEntity block) {
        if (!world.isClient) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;

        Vec3d position = pos.toCenterPos();

        if (player != null) {

            if (!state.get(FluorescentLightBlock.COPY)) {
                if (pos.isWithinDistance(player.getPos(), 20)) {
                    if (block.prevOn != world.getBlockState(pos).get(FluorescentLightBlock.ON)) {
                        client.getSoundManager().play(new PositionedSoundInstance(ModSounds.LIGHT_BLINK, SoundCategory.AMBIENT, 0.1F, block.random1.nextFloat(0.9f, 1.1f), block.random, pos));
                    }
                }
            }

            Vec3d playerPos = player.getPos();
            boolean withinDistance = pos.isWithinDistance(playerPos, ConfigStuff.getLightRenderDistance());
            if (withinDistance) {
                if (!state.get(FluorescentLightBlock.COPY) &&
                        state.get(FluorescentLightBlock.ON) &&
                        !state.get(FluorescentLightBlock.BLACKOUT)) {
                    if (!block.isPlayingSound() && pos.isWithinDistance(playerPos, 16.0f) && !state.get(FluorescentLightBlock.BLACKOUT) && !SPBRevampedClient.blackScreen) {
                        MinecraftClient.getInstance().getSoundManager().play(new FluorescentLightSoundInstance(block, player));
                        block.setPlayingSound(true);
                    }

                    if (block.pointLight == null) {
                        block.pointLight = new PointLight();
                        VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().addLight(block.pointLight
                                .setRadius(13f)
                                .setColor((float) 255 / 255, (float) 240 / 255, (float) 100 / 255)
                                .setPosition(position.x, position.y - 1, position.z)
                                .setBrightness(1.0f)
                        );
                    }
                } else {
                    if (block.pointLight != null) {
                        VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().removeLight(block.pointLight);
                        block.pointLight = null;
                    }
                    block.setPlayingSound(false);
                }

            } else {
                if (block.pointLight != null) {
                    VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().removeLight(block.pointLight);
                    block.pointLight = null;
                }
            }
        }
    }

}
