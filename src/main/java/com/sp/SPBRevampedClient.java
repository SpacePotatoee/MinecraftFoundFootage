package com.sp;

import com.sp.block.renderer.FluorescentLightBlockEntityRenderer;
import com.sp.block.renderer.ThinFluorescentLightBlockEntityRenderer;
import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import com.sp.cca_stuff.WorldEvents;
import com.sp.compat.modmenu.ConfigDefinitions;
import com.sp.compat.modmenu.ConfigStuff;
import com.sp.entity.client.model.SmilerModel;
import com.sp.entity.client.renderer.SkinWalkerRenderer;
import com.sp.entity.client.renderer.SmilerRenderer;
import com.sp.init.*;
import com.sp.networking.InitializePackets;
import com.sp.networking.callbacks.ClientConnectionEvents;
import com.sp.render.*;
import com.sp.render.camera.CameraShake;
import com.sp.render.camera.CutsceneManager;
import com.sp.render.gui.StaminaBar;
import com.sp.render.gui.TitleText;
import com.sp.render.physics.PhysicsPoint;
import com.sp.render.physics.PhysicsStick;
import com.sp.util.MathStuff;
import com.sp.util.TickTimer;
import de.maxhenkel.voicechat.voice.client.ClientManager;
import eu.midnightdust.core.MidnightLibClient;
import eu.midnightdust.fabric.core.MidnightLibClientFabric;
import eu.midnightdust.lib.config.MidnightConfig;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.VeilRenderer;
import foundry.veil.api.client.render.deferred.VeilDeferredRenderer;
import foundry.veil.api.client.render.deferred.light.renderer.LightRenderer;
import foundry.veil.api.client.render.post.PostPipeline;
import foundry.veil.api.client.render.post.PostProcessingManager;
import foundry.veil.api.client.render.shader.definition.ShaderPreDefinitions;
import foundry.veil.api.client.render.shader.program.MutableUniformAccess;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import foundry.veil.api.client.util.Easings;
import foundry.veil.api.event.VeilRenderLevelStageEvent.Stage;
import foundry.veil.platform.VeilEventPlatform;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL43;
import org.lwjgl.opengl.GLDebugMessageCallback;

import java.util.Vector;


public class SPBRevampedClient implements ClientModInitializer {
    private GrassRenderer grassRenderer;
    private static final CutsceneManager cutsceneManager = new CutsceneManager();
    private static final CameraShake cameraShake = new CameraShake();
    private final FlashlightRenderer flashlightRenderer = new FlashlightRenderer();
    private static final Identifier VHS_POST = new Identifier(SPBRevamped.MOD_ID, "vhs");
    private static final Identifier SSAO = new Identifier(SPBRevamped.MOD_ID, "vhs/ssao");
    private static final Identifier EVERYTHING_SHADER = new Identifier(SPBRevamped.MOD_ID, "vhs/everything");
    private static final Identifier POST_VHS = new Identifier(SPBRevamped.MOD_ID, "vhs/vhs_post");
    private static final Identifier WATER_SHADER = new Identifier(SPBRevamped.MOD_ID, "vhs/water");
    private static final Identifier MIXED_SHADER = new Identifier(SPBRevamped.MOD_ID, "vhs/mixed");
    private static final Identifier GLITCH_SHADER = new Identifier(SPBRevamped.MOD_ID, "vhs/glitch");

    public static boolean zoom = false;
    public static double zoomTime = 0;
    public static double zoomed;
    static boolean playedZoomIn = false;
    static boolean playedZoomOut = true;
    static boolean inBackrooms = false;
    public static Camera camera;

    public static TickTimer tickTimer = new TickTimer();
    public static boolean blackScreen;
    public static boolean youCantEscape;

    private static boolean shouldBeUnmuted = false;

    private static final Random random = Random.create();
    private static final Random random2 = Random.create(34563264);

    public static boolean shoudlRenderWarp = false;

    @Override
    public void onInitializeClient() {



        HudRenderCallback.EVENT.register(new TitleText());
        HudRenderCallback.EVENT.register(new StaminaBar());

        InitializePackets.registerS2CPackets();

        Keybinds.initializeKeyBinds();

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.ConcreteBlock11, RenderLayers.getConcreteLayer());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.Bricks, RenderLayers.getBricksLayer());

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.DIRT, RenderLayers.getDirtLayer());
//        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.POWER_POLE, RenderLayers.getUtilityPoleLayer());
//        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.POWER_POLE_TOP, RenderLayers.getUtilityPoleLayer());

//        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.PoolTiles, RenderLayers.getPoolTileLayer());

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.CHAINFENCE, RenderLayers.getChainFence());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.CeilingTile, RenderLayers.getCeilingTile());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.GhostCeilingTile, RenderLayers.getCeilingTile());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.CarpetBlock, RenderLayers.getCarpet());

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.WOODEN_CRATE, RenderLayers.getWoodenCrateLayer());

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.PoolroomsSkyBlock, RenderLayers.getPoolroomsSky());

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.BottomTrim, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.WallText1, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.WallText2, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.WallText3, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.WallText4, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.WallText5, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.WallText6, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.WallText7, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.WallText8, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.WallText99, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.WallArrow1, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.WallArrow2, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.WallArrow3, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.WallArrow4, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.WallSmall1, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.WallSmall2, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.WallDrawingDoor, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.WallDrawingWindow, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.Rug1, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.Rug2, RenderLayer.getCutout());
//        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.POWER_POLE, RenderLayer.getTranslucent());
//        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.PoolTileSlope, RenderLayer.getCutout());

        BlockEntityRendererFactories.register(ModBlockEntities.FLUORESCENT_LIGHT_BLOCK_ENTITY, FluorescentLightBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.THIN_FLUORESCENT_LIGHT_BLOCK_ENTITY, ThinFluorescentLightBlockEntityRenderer::new);

        EntityRendererRegistry.register(ModEntities.SKIN_WALKER_ENTITY, SkinWalkerRenderer::new);
        EntityRendererRegistry.register(ModEntities.SMILER_ENTITY, SmilerRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.SMILER, SmilerModel::getTexturedModelData);


        VeilEventPlatform.INSTANCE.onVeilRenderTypeStageRender((stage, levelRenderer, bufferSource, poseStack, projectionMatrix, renderTick, partialTicks, camera, frustum) -> {
            //Setting for later use
            if(camera != null){
                SPBRevampedClient.camera = camera;
            }

            MinecraftClient client = MinecraftClient.getInstance();
            World clientWorld = client.world;
            if(clientWorld != null) {
                //Only render the shadow map when in the poolrooms
                if (clientWorld.getRegistryKey() == BackroomsLevels.POOLROOMS_WORLD_KEY) {
                    if (stage == Stage.AFTER_SKY) {
                        if (camera != null) {
                            ShadowMapRenderer.renderShadowMap(camera, partialTicks, clientWorld);
                        }
                    }
                }

                if(!cutsceneManager.fall) {
                    if (clientWorld.getRegistryKey() == BackroomsLevels.LEVEL0_WORLD_KEY) {
                        if (stage == Stage.AFTER_SKY) {
                            if (camera != null) {
                                ShadowMapRenderer.renderLevel0ShadowMap(camera, clientWorld);
                            }
                        }
                    }
                }

                if (clientWorld.getRegistryKey() == BackroomsLevels.INFINITE_FIELD_WORLD_KEY) {
                    if (stage == Stage.AFTER_SOLID_BLOCKS) {
                        if (this.grassRenderer == null) {
                            this.grassRenderer = new GrassRenderer();
                        }

                        this.grassRenderer.render();
                    }
                } else if(this.grassRenderer != null){
                    this.grassRenderer.close();
                    this.grassRenderer = null;
                }

            }



            //Enable the VHS shader
            PostProcessingManager postProcessingManager = VeilRenderSystem.renderer().getPostProcessingManager();
            if (stage == Stage.AFTER_LEVEL) {
                //Flashlight
                flashlightRenderer.renderFlashlightForEveryPlayer(partialTicks);


                PostPipeline Pipeline = postProcessingManager.getPipeline(VHS_POST);
                if (Pipeline != null) {
                    if (ConfigStuff.enableVhsEffect || isInBackrooms()) {
                        if(!postProcessingManager.isActive(VHS_POST)) {
                            postProcessingManager.add(VHS_POST);
                        }
                    } else if (postProcessingManager.isActive(VHS_POST)) {
                        postProcessingManager.remove(VHS_POST);
                    }
                }


                if (client.player != null) {
                    if (clientWorld != null) {
                        PlayerComponent playerComponent = InitializeComponents.PLAYER.get(client.player);
                        WorldEvents events = InitializeComponents.EVENTS.get(clientWorld);
                        Entity activeSkinwalker = events.getActiveSkinwalkerTarget();


                        if (activeSkinwalker != null) {
                            Box box = activeSkinwalker.getVisibilityBoundingBox().expand(0.1);
                            boolean inFrustum = frustum.isVisible(box);

                            if (inFrustum && client.player.canSee(activeSkinwalker)) {
                                if (!playerComponent.canSeeActiveSkinWalkerTarget()) {
                                    playerComponent.setCanSeeActiveSkinWalkerTarget(true);

                                    PacketByteBuf buffer = PacketByteBufs.create();
                                    buffer.writeBoolean(true);
                                    ClientPlayNetworking.send(InitializePackets.SEE_SKINWALKER_SYNC, buffer);
                                }
                            } else {
                                if (playerComponent.canSeeActiveSkinWalkerTarget()) {
                                    playerComponent.setCanSeeActiveSkinWalkerTarget(false);

                                    PacketByteBuf buffer = PacketByteBufs.create();
                                    buffer.writeBoolean(false);
                                    ClientPlayNetworking.send(InitializePackets.SEE_SKINWALKER_SYNC, buffer);
                                }
                            }
                        }
                    }

                    if(!client.player.isSpectator() && !client.player.isCreative()){
                        client.options.debugEnabled = false;
                    }

                }
            }

        });


        VeilEventPlatform.INSTANCE.preVeilPostProcessing(((name, pipeline, context) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            PlayerEntity player = MinecraftClient.getInstance().player;
            VeilRenderer renderer = VeilRenderSystem.renderer();
            ShaderPreDefinitions definitions = renderer.getShaderDefinitions();

            if(!inBackrooms) {
                if(definitions.getDefinition("WARP") != null) {
                    definitions.remove("WARP");
                }
            }

            if (player != null && client.world != null) {
                WorldEvents events = InitializeComponents.EVENTS.get(client.world);
                PlayerComponent playerComponent = InitializeComponents.PLAYER.get(player);

                if (VHS_POST.equals(name)) {
                    ShaderProgram shaderProgram = context.getShader(POST_VHS);
                    if (shaderProgram != null) {
                        if(youCantEscape) {
                            shaderProgram.setInt("youCantEscape", 1);
                        } else {
                            shaderProgram.setInt("youCantEscape", 0);
                        }

                        if(playerComponent.isBeingCaptured()){
                            SkinwalkerJumpscare.doJumpscare(shaderProgram, client, playerComponent);
                        } else {
                            shaderProgram.setInt("Jumpscare", 0);
                            shaderProgram.setInt("CreepyFace1", 0);
                            shaderProgram.setInt("CreepyFace2", 0);
                            shaderProgram.setVector("Rand", 0, 0);
                        }

                        if(PreviousUniforms.prevModelViewMat != null && PreviousUniforms.prevProjMat != null){
                            shaderProgram.setMatrix("prevViewMat", PreviousUniforms.prevModelViewMat);
                            shaderProgram.setMatrix("prevProjMat", PreviousUniforms.prevProjMat);
                            shaderProgram.setVector("prevCameraPos", PreviousUniforms.prevCameraPos);
                        }

                        shaderProgram.setFloat("MotionBlurStrength", ConfigStuff.motionBlurStrength);
                        shaderProgram.setFloat("DistortionStrength", ConfigStuff.VHSDistortionMultiplier);
                    }

                    shaderProgram = context.getShader(SSAO);
                    if(shaderProgram != null){
                        shaderProgram.setVectors("samples", SSAOSamples.getSSAOSamples());
                    }

                    shaderProgram = context.getShader(EVERYTHING_SHADER);
                    if (shaderProgram != null) {
                        if (client.world.getRegistryKey() == BackroomsLevels.LEVEL1_WORLD_KEY) {
                            shaderProgram.setInt("FogToggle", 1);
                        } else {
                            shaderProgram.setInt("FogToggle", 0);
                        }


                        if(blackScreen || (player.isInsideWall() && !getCutsceneManager().isPlaying) || playerComponent.isBeingReleased()){
                            shaderProgram.setInt("blackScreen", 1);
                        } else {
                            shaderProgram.setInt("blackScreen", 0);
                        }

                        if (client.world.getRegistryKey() == BackroomsLevels.LEVEL1_WORLD_KEY) {
                            shaderProgram.setInt("TogglePuddles", 1);
                        } else {
                            shaderProgram.setInt("TogglePuddles", 0);
                        }

                        shaderProgram.setVector("shadowColor", PoolroomsDayCycle.getLightColor());
                    }

                    shaderProgram = context.getShader(MIXED_SHADER);
                    if (shaderProgram != null) {
                        shaderProgram.setVector("Rand", random.nextFloat() * 2.0f - 1.0f, random2.nextFloat() * 2.0f - 1.0f);

                        shaderProgram.setVector("shadowColor", PoolroomsDayCycle.getLightColor());

                    }

                    shaderProgram = context.getShader(GLITCH_SHADER);
                    if (shaderProgram != null) {
                        shaderProgram.setFloat("glitchTime", playerComponent.getGlitchTimer());
                    }

                }

                if (events.isLevel2Warp()) {
                    definitions.define("WARP");
                } else {
                    definitions.remove("WARP");
                }


                ConfigDefinitions.definitions.forEach((s, aBoolean) -> {
                    if(aBoolean.get()){
                        definitions.define(s);
                    } else {
                        definitions.remove(s);
                    }
                });

                BackroomsLevels.definitions.forEach((s, registryKey) -> {
                    if(client.world.getRegistryKey() == registryKey){
                        definitions.define(s);
                    } else {
                        definitions.remove(s);
                    }
                });

                PreviousUniforms.update();
            }
        }));

        //For some reason veil lights aren't removed when you leave the game
        ClientPlayConnectionEvents.JOIN.register(((handler,sender, client) -> {
            VeilDeferredRenderer renderer = VeilRenderSystem.renderer().getDeferredRenderer();
            renderer.reset();

            client.player.sendMessage(Text.translatable("flashlight.hint", Keybinds.toggleFlashlight.getBoundKeyLocalizedText().copyContentOnly().formatted(Formatting.BOLD, Formatting.UNDERLINE)));

            //Just in case it become  unsynced
            if(client.world != null){
                WorldEvents events = InitializeComponents.EVENTS.get(client.world);
                PoolroomsDayCycle.dayTime = events.getCurrentPoolroomsTime();
            }

        }));

        ClientConnectionEvents.DISCONNECT.register(client -> {
            PlayerEntity player = client.player;
            if (player != null) {
                PlayerComponent playerComponent = InitializeComponents.PLAYER.get(player);
                playerComponent.setFlashLightOn(false);
                flashlightRenderer.flashLightList2.clear();
                playerComponent.setDoingCutscene(false);
            }

            cutsceneManager.reset();

            if(this.grassRenderer != null) {
                this.grassRenderer.close();
                this.grassRenderer = null;
            }
        });

        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
            cutsceneManager.reset();
            if(this.grassRenderer != null) {
                this.grassRenderer.close();
                this.grassRenderer = null;
            }
        });


        ClientTickEvents.END_WORLD_TICK.register((client) ->{
            Vector<TickTimer> tickTimers = TickTimer.getAllInstances();
            if(!tickTimers.isEmpty()){
                for(TickTimer timer : tickTimers){
                    timer.addCurrentTick();
                }
            }
        });

        ClientTickEvents.END_CLIENT_TICK.register((client) ->{
            if(cutsceneManager.isPlaying) {
                if(!ClientManager.getPlayerStateManager().isMuted()) {
                    shouldBeUnmuted = true;
                    ClientManager.getPlayerStateManager().setMuted(true);
                }
            } else if(shouldBeUnmuted) {
                ClientManager.getPlayerStateManager().setMuted(false);
                shouldBeUnmuted = false;
            }

            PlayerEntity playerClient = client.player;
            if(playerClient != null){
                //*Main Set in Backrooms
                setInBackrooms(BackroomsLevels.isInBackrooms(playerClient.getWorld().getRegistryKey()));

                if(client.world != null) {
                    VeilRenderer renderer = VeilRenderSystem.renderer();
                    VeilDeferredRenderer deferredRenderer = renderer.getDeferredRenderer();
                    LightRenderer lightRenderer = deferredRenderer.getLightRenderer();

                    if (inBackrooms) {
                        if (client.world.getRegistryKey() != BackroomsLevels.POOLROOMS_WORLD_KEY && client.world.getRegistryKey() != BackroomsLevels.INFINITE_FIELD_WORLD_KEY) {
                            lightRenderer.disableVanillaLight();
                        } else {
                            lightRenderer.enableVanillaLight();
                        }

                        lightRenderer.disableAmbientOcclusion();
                    } else {
                        lightRenderer.enableVanillaLight();
                        lightRenderer.enableAmbientOcclusion();
                    }
                }
            }
        });

    }

    public static void setShadowUniforms(MutableUniformAccess access, World world) {
        Matrix4f level0ViewMat = ShadowMapRenderer.createShadowModelView(camera.getPos().x, camera.getPos().y, camera.getPos().z, true).peek().getPositionMatrix();
        Matrix4f viewMat = ShadowMapRenderer.createShadowModelView(camera.getPos().x, camera.getPos().y, camera.getPos().z, world, true).peek().getPositionMatrix();

        access.setMatrix("level0ViewMatrix", level0ViewMat);
        access.setMatrix("viewMatrix", viewMat);
        access.setMatrix("IShadowViewMatrix", viewMat.invert());

        access.setMatrix("orthographMatrix", ShadowMapRenderer.createProjMat());
    }

    public static float getWarpTimer(World world) {
        WorldEvents events = InitializeComponents.EVENTS.get(world);

        if (events.isLevel2Warp() || tickTimer.getCurrentTick() != 0) {
            tickTimer.setOnOrOff(true);
            float x = tickTimer.getCurrentTick();
            float w = 0.03141592f;
            float result = MathStuff.mod((x * w) * 0.002f, w);
            if (result == 0) {
                tickTimer.resetToZero();
            }
            return result;

        } else {
            tickTimer.setOnOrOff(false);
            return 0;
        }
    }

    public static void sendGlitchDamagePacket(boolean shouldDamage) {
        PacketByteBuf buffer = PacketByteBufs.create();
        buffer.writeBoolean(shouldDamage);
        ClientPlayNetworking.send(InitializePackets.GLITCH_DAMAGE_SYNC, buffer);
    }

    public static boolean isInBackrooms() {
        return inBackrooms;
    }

    public static void setInBackrooms(boolean inBackrooms) {
        SPBRevampedClient.inBackrooms = inBackrooms;
    }

    public static CutsceneManager getCutsceneManager() {
        return cutsceneManager;
    }

    public static CameraShake getCameraShake() {
        return cameraShake;
    }



}
