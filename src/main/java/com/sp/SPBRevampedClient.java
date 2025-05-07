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
import com.sp.render.grass.GrassRenderer;
import com.sp.render.gui.StaminaBar;
import com.sp.render.gui.TitleText;
import com.sp.render.pbr.PbrRegistry;
import com.sp.util.MathStuff;
import com.sp.util.TickTimer;
import com.sp.world.levels.custom.Level2BackroomsLevel;
import com.sp.world.levels.custom.PoolroomsBackroomsLevel;
import de.maxhenkel.voicechat.voice.client.ClientManager;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.VeilRenderer;
import foundry.veil.api.client.render.deferred.VeilDeferredRenderer;
import foundry.veil.api.client.render.deferred.light.renderer.LightRenderer;
import foundry.veil.api.client.render.post.PostPipeline;
import foundry.veil.api.client.render.post.PostProcessingManager;
import foundry.veil.api.client.render.shader.definition.ShaderPreDefinitions;
import foundry.veil.api.client.render.shader.program.MutableUniformAccess;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import foundry.veil.api.event.VeilRenderLevelStageEvent.Stage;
import foundry.veil.platform.VeilEventPlatform;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.joml.Matrix4f;
import org.joml.Vector3f;

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
    private static final Identifier MIXED_SHADER = new Identifier(SPBRevamped.MOD_ID, "vhs/mixed");
    private static final Identifier GLITCH_SHADER = new Identifier(SPBRevamped.MOD_ID, "vhs/glitch");

    static boolean inBackrooms = false;
    public static Camera camera;
    public static Vector3f cameraBobOffset;

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

        com.sp.Keybinds.initializeKeyBinds();

        PbrRegistry.registerPBR(ModBlocks.CarpetBlock,      new PbrRegistry.PbrMaterial(false, 0.0f,1.25f, 1024));
        PbrRegistry.registerPBR(ModBlocks.CeilingTile,      new PbrRegistry.PbrMaterial(false, 0.0f,1.0f,  512));
        PbrRegistry.registerPBR(ModBlocks.GhostCeilingTile, new PbrRegistry.PbrMaterial(false, 0.0f,1.0f,  512));

        PbrRegistry.registerPBR(ModBlocks.ConcreteBlock11,  new PbrRegistry.PbrMaterial(true, 0.7f,16.0f,  2048));
        PbrRegistry.registerPBR(ModBlocks.Bricks,           new PbrRegistry.PbrMaterial(true, 0.7f,5.0f,   2048));
        PbrRegistry.registerPBR(ModBlocks.DIRT,             new PbrRegistry.PbrMaterial(true, 0.5f,3.0f,   1024));
        PbrRegistry.registerPBR(ModBlocks.CHAINFENCE,       new PbrRegistry.PbrMaterial(true, 0.36f,2.8f,   1024));
        PbrRegistry.registerPBR(ModBlocks.WOODEN_CRATE,     new PbrRegistry.PbrMaterial(true, 2.0f,1.0f,   1024));

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

        BlockEntityRendererFactories.register(ModBlockEntities.FLUORESCENT_LIGHT_BLOCK_ENTITY, FluorescentLightBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.THIN_FLUORESCENT_LIGHT_BLOCK_ENTITY, ThinFluorescentLightBlockEntityRenderer::new);

        EntityRendererRegistry.register(ModEntities.SKIN_WALKER_ENTITY, SkinWalkerRenderer::new);
        EntityRendererRegistry.register(ModEntities.SMILER_ENTITY, SmilerRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.SMILER, SmilerModel::getTexturedModelData);



        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return new Identifier(SPBRevamped.MOD_ID, "after_resources");
            }

            @Override
            public void reload(ResourceManager manager) {
                if (System.getProperty("os.name").toLowerCase().contains("mac")) {
                    SPBRevamped.LOGGER.error("This mod is not compatible with MacOS. Please use Windows or Linux (wayland).");
                    MinecraftClient.getInstance().getToastManager().add(new SystemToast(SystemToast.Type.UNSECURE_SERVER_WARNING, Text.of("Potential Incompatibility found"), Text.of("This mod is not compatible with MacOS. Please use Windows or Linux (wayland).")));
                }
            }
        });


        VeilEventPlatform.INSTANCE.onVeilRenderTypeStageRender((stage, levelRenderer, bufferSource, poseStack, projectionMatrix, renderTick, partialTicks, camera, frustum) -> {
            //*Setting for later use
            if(camera != null){
                SPBRevampedClient.camera = camera;
            }

            MinecraftClient client = MinecraftClient.getInstance();
            World clientWorld = client.world;
            if(clientWorld != null) {
                //*Only render the shadow map when in the poolrooms
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



            //*Enable the VHS shader
            PostProcessingManager postProcessingManager = VeilRenderSystem.renderer().getPostProcessingManager();
            if (stage == Stage.AFTER_LEVEL) {
                //*Flashlight
                flashlightRenderer.renderFlashlightForEveryPlayer(partialTicks);


                PostPipeline Pipeline = postProcessingManager.getPipeline(VHS_POST);
                if (Pipeline != null) {
                    if (shouldRenderCameraEffect()) {
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

            if(!shouldRenderCameraEffect()) {
                if(definitions.getDefinition("WARP") != null) {
                    definitions.remove("WARP");
                }
            }

            if (player != null && client.world != null) {
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

                if ((BackroomsLevels.getLevel(client.world)) instanceof Level2BackroomsLevel level) {
                    if (level.isWarping() || !finishedWarp(client.world)) {
                        definitions.define("WARP");
                    } else {
                        definitions.remove("WARP");
                    }
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

        ClientPlayConnectionEvents.JOIN.register(((handler,sender, client) -> {
            VeilDeferredRenderer renderer = VeilRenderSystem.renderer().getDeferredRenderer();
            renderer.reset();

            //*Just in case it become unsynced
            if(client.world != null){
                HelpfulHintManager.sendMessages(client.player);


                if (!((BackroomsLevels.getLevel(client.world)) instanceof PoolroomsBackroomsLevel level)) {
                    return;
                }

                PoolroomsDayCycle.dayTime = level.getTimeOfDay();
            }

        }));

        //*For some reason veil lights aren't removed when you leave the game
        ClientConnectionEvents.DISCONNECT.register(client -> {
            PlayerEntity player = client.player;
            if (player != null) {
                PlayerComponent playerComponent = InitializeComponents.PLAYER.get(player);
                playerComponent.setFlashLightOn(false);
                flashlightRenderer.clearFlashlights();
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

            //Fixes Minecraft spectating not loading chunks bug
            MinecraftClient client1 = MinecraftClient.getInstance();
            PlayerEntity player = client1.player;
            if(player != null) {
                if (player != client1.getCameraEntity()) {
                    Vec3d pos = client1.getCameraEntity().getPos();
                    player.setPosition(pos);
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

                    if (shouldRenderCameraEffect() && isInBackrooms()) {
                        HelpfulHintManager.disableSuffocateHint();
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
        if (!(BackroomsLevels.getLevel(world) instanceof Level2BackroomsLevel level)) {
            return 0;
        }

        if (level.isWarping() || tickTimer.getCurrentTick() != 0) {
            tickTimer.setOnOrOff(true);
            float x = tickTimer.getCurrentTick();
            float w = 0.03141592f;
            float result = MathStuff.mod((x * w) * 0.002f, w);
            if (result == 0 || (!level.isWarping() && result == 0.03141592f/2)) {
                tickTimer.resetToZero();
            }
            return result;

        } else {
            tickTimer.setOnOrOff(false);
            return 0;
        }
    }

    public static boolean finishedWarp(World world) {
        float warp = getWarpTimer(world);
        return warp == 0 || warp == 0.03141592f/2;
    }

    public static void sendComponentSyncPacket(boolean writeBoolean, String component) {
        PacketByteBuf buffer = PacketByteBufs.create();
        buffer.writeBoolean(writeBoolean);
        buffer.writeString(component);
        ClientPlayNetworking.send(InitializePackets.COMPONENT_SYNC, buffer);
    }

    public static boolean isInBackrooms() {
        return inBackrooms;
    }

    public static boolean shouldRenderCameraEffect() {
        return isInBackrooms() || ConfigStuff.enableVhsEffect;
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