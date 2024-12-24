package com.sp;

import com.sp.entity.client.model.SmilerModel;
import com.sp.entity.client.renderer.SkinWalkerRenderer;
import com.sp.entity.client.renderer.SmilerRenderer;
import com.sp.entity.custom.SmilerEntity;
import com.sp.init.*;
import com.sp.block.renderer.*;
import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import com.sp.cca_stuff.WorldEvents;
import com.sp.init.RenderLayers;
import com.sp.networking.InitializePackets;
import com.sp.render.*;
import com.sp.render.camera.CameraShake;
import com.sp.render.camera.CutsceneManager;
import com.sp.render.gui.TitleText;
import com.sp.render.physics.PhysicsPoint;
import com.sp.render.physics.PhysicsStick;
import com.sp.sounds.entity.SmilerGlitchSoundInstance;
import com.sp.util.MathStuff;
import com.sp.util.TickTimer;
import com.sp.init.BackroomsLevels;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.VeilRenderer;
import foundry.veil.api.client.render.deferred.VeilDeferredRenderer;
import foundry.veil.api.client.render.deferred.light.renderer.LightRenderer;
import foundry.veil.api.client.render.post.PostPipeline;
import foundry.veil.api.client.render.post.PostProcessingManager;
import foundry.veil.api.client.render.shader.definition.ShaderPreDefinitions;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import foundry.veil.api.client.util.Easings;
import foundry.veil.api.event.VeilRenderLevelStageEvent.Stage;
import foundry.veil.platform.VeilEventPlatform;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.joml.*;

import java.lang.Math;
import java.util.*;


public class SPBRevampedClient implements ClientModInitializer {
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

    float prevYaw2;
    float prevPitch2;

    public static boolean zoom = false;
    public static double zoomTime = 0;
    public static double zoomed;
    static boolean playedZoomIn = false;
    static boolean playedZoomOut = true;
    static boolean inBackrooms = false;
    Camera camera;

    public static TickTimer tickTimer = new TickTimer();
    public static TickTimer SunsetTimer = new TickTimer();
    public static boolean blackScreen;
    public static boolean youCantEscape;

    Random random = Random.create();

    @Override
    public void onInitializeClient() {
        HudRenderCallback.EVENT.register(new TitleText());

        InitializePackets.registerS2CPackets();

        Keybinds.inizializeKeyBinds();

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.ConcreteBlock11, RenderLayers.getConcreteLayer());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.Bricks, RenderLayers.getBricksLayer());

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
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.PoolTileSlope, RenderLayer.getCutout());

        BlockEntityRendererFactories.register(ModBlockEntities.FLUORESCENT_LIGHT_BLOCK_ENTITY, FluorescentLightBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.THIN_FLUORESCENT_LIGHT_BLOCK_ENTITY, ThinFluorescentLightBlockEntityRenderer::new);

        EntityRendererRegistry.register(ModEntities.SKIN_WALKER_ENTITY, SkinWalkerRenderer::new);
        EntityRendererRegistry.register(ModEntities.SMILER_ENTITY, SmilerRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.SMILER, SmilerModel::getTexturedModelData);


        VeilEventPlatform.INSTANCE.onVeilRenderTypeStageRender((stage, levelRenderer, bufferSource, poseStack, projectionMatrix, renderTick, partialTicks, camera, frustum) -> {
            //Setting for later use
            if(camera != null){
                this.camera = camera;
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
            }

            ResourcePackManager resourcePackManager = client.getResourcePackManager();
            resourcePackManager.enable("veil:deferred");


            //Flashlight
            flashlightRenderer.renderFlashlightForEveryPlayer(partialTicks);


            //Enable the VHS shader
            PostProcessingManager postProcessingManager = VeilRenderSystem.renderer().getPostProcessingManager();
            if (stage == Stage.AFTER_LEVEL) {
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
                                    buffer.writeBoolean(playerComponent.canSeeActiveSkinWalkerTarget());
                                    ClientPlayNetworking.send(InitializePackets.SEE_SKINWALKER_SYNC, buffer);
                                }
                            } else {
                                if (playerComponent.canSeeActiveSkinWalkerTarget()) {
                                    playerComponent.setCanSeeActiveSkinWalkerTarget(false);

                                    PacketByteBuf buffer = PacketByteBufs.create();
                                    buffer.writeBoolean(playerComponent.canSeeActiveSkinWalkerTarget());
                                    ClientPlayNetworking.send(InitializePackets.SEE_SKINWALKER_SYNC, buffer);
                                }
                            }
                        }
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
                float yaw = player.getYaw();
                float pitch = player.getPitch();
                float yawRotAmount = yaw - prevYaw2;
                float pitchRotAmount = pitch - prevPitch2;

                if (VHS_POST.equals(name)) {
                    ShaderProgram shaderProgram = context.getShader(POST_VHS);
                    if (shaderProgram != null) {
                        if(client.getCameraEntity() == player) {
                            if (!getCutsceneManager().isPlaying) {
                                shaderProgram.setVector("Velocity", MathHelper.cos((float) Math.toRadians(yawRotAmount + 90.0f)), MathHelper.sin((float) Math.toRadians(pitchRotAmount)));
                            } else {
                                shaderProgram.setVector("Velocity", 0, 0);
                            }
                        } else {
                            shaderProgram.setVector("Velocity", 0, 0);
                        }

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

                        shaderProgram.setFloat("sunsetTimer", getSunsetTimer(client.world));
                        shaderProgram.setVectorI("resolution", client.getFramebuffer().viewportWidth, client.getFramebuffer().viewportHeight);
                    }

                    shaderProgram = context.getShader(MIXED_SHADER);
                    if (shaderProgram != null) {
                        if(this.camera != null) {
                            setShadowUniforms(shaderProgram, client.world);
                        }

                        if (client.world.getRegistryKey() == BackroomsLevels.POOLROOMS_WORLD_KEY) {
                            shaderProgram.setInt("ShadowToggle", 1);
                        } else {
                            shaderProgram.setInt("ShadowToggle", 0);
                        }

                        shaderProgram.setFloat("sunsetTimer", getSunsetTimer(client.world));

                    }

                    shaderProgram = context.getShader(WATER_SHADER);
                    if (shaderProgram != null) {
                        if (this.camera != null) {
                            setShadowUniforms(shaderProgram, client.world);
                        }
                        if (inBackrooms) {
                            shaderProgram.setInt("OverWorld", 0);
                        } else {
                            shaderProgram.setInt("OverWorld", 1);
                        }
                    }

                    shaderProgram = context.getShader(GLITCH_SHADER);
                    if (shaderProgram != null) {
                        shaderProgram.setFloat("glitchTime", playerComponent.getGlitchTimer());
                    }

                }

                if (events.isLevel2Warp()) {
                    if(definitions.getDefinition("WARP") == null) {
                        definitions.define("WARP");
                    }
                } else {
                    if(definitions.getDefinition("WARP") != null) {
                        definitions.remove("WARP");
                    }
                }
                prevYaw2 = yaw;
                prevPitch2 = pitch;
            }
        }));

        //For some reason veil lights aren't removed when you leave the game
        ClientPlayConnectionEvents.JOIN.register(((handler,sender, client) -> {
            VeilDeferredRenderer renderer = VeilRenderSystem.renderer().getDeferredRenderer();
            renderer.reset();
        }));

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> client.execute(() -> {
            PlayerEntity player = MinecraftClient.getInstance().player;
            if (player != null) {
                PlayerComponent playerComponent = InitializeComponents.PLAYER.get(player);
                playerComponent.setFlashLightOn(false);
                flashlightRenderer.flashLightList.clear();
            }
        }));


        ClientTickEvents.END_WORLD_TICK.register((client) ->{
            Vector<TickTimer> tickTimers = TickTimer.getAllInstances();
            if(!tickTimers.isEmpty()){
                for(TickTimer timer : tickTimers){
                    timer.addCurrentTick();
                }
            }
        });

        ClientTickEvents.END_CLIENT_TICK.register((client) ->{

            Vector<PhysicsPoint> physicsPoints = PhysicsPoint.getAllInstances();
            if(!physicsPoints.isEmpty()){
                for(PhysicsPoint point : physicsPoints){
                    if(!point.isFixed()) {
                        point.updatePoint();
                    }
                }
            }

            Vector<PhysicsStick> physicsSticks = PhysicsStick.getAllInstances();
            if(!physicsSticks.isEmpty()){
                for(PhysicsStick sticks : physicsSticks){
                    sticks.updateSticks();
                }
            }

            PlayerEntity playerClient = client.player;
            if(playerClient != null){
                SimpleOption<Integer> fps =  MinecraftClient.getInstance().options.getMaxFps();
                PlayerComponent playerComponent = InitializeComponents.PLAYER.get(playerClient);
                if (BackroomsLevels.isInBackrooms(playerClient.getWorld().getRegistryKey())){
                    setInBackrooms(true);
                    //fps.setValue(30);
                }else {
                    setInBackrooms(ConfigStuff.forceBackrooms);
                }

                if(client.world != null) {
                    VeilRenderer renderer = VeilRenderSystem.renderer();
                    VeilDeferredRenderer deferredRenderer = renderer.getDeferredRenderer();
                    LightRenderer lightRenderer = deferredRenderer.getLightRenderer();

                    if (inBackrooms) {
                        if (!ConfigStuff.enableVanillaLighting && client.world.getRegistryKey() != BackroomsLevels.POOLROOMS_WORLD_KEY) {
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

    //TODO: USE SYSTEM TIME INSTEAD OF RENDER TIME TO MAKE FRAME INDEPENDENT
    public static double doCameraZoom (double fov, MinecraftClient client, Entity player){
        SimpleOption<Double> currentSens = client.options.getMouseSensitivity();
        double originalSensitivity = currentSens.getValue();

        if (Keybinds.Zoom.isPressed() && !player.isSprinting() && zoomTime == 0) {
            zoom = true;
        } else if (zoomTime == 1 && !Keybinds.Zoom.isPressed()) {
            zoom = false;
        }


        if (client.world != null && player != null) {
            if (zoom) {
                zoomTime = MathHelper.clamp(zoomTime + 0.007, 0, 1);
                double easedZoom = Easings.Easing.linear.ease((float) zoomTime);

                zoomed = fov / (1 + easedZoom * 4);
                currentSens.setValue(currentSens.getValue() / (1 + easedZoom));

                if (!playedZoomIn) {
                    client.getSoundManager().play(PositionedSoundInstance.master(ModSounds.ZOOM_IN_SOUND, 1.0F));
                    playedZoomIn = true;
                    playedZoomOut = false;
                }

            } else if (zoomTime != 0) {
                zoomTime = MathHelper.clamp(zoomTime - 0.007, 0, 1);
                double easedZoom = Easings.Easing.linear.ease((float) zoomTime);

                zoomed = fov / (1 + easedZoom * 4);
                currentSens.setValue(currentSens.getValue() / (easedZoom));

                if (!playedZoomOut) {
                    client.getSoundManager().play(PositionedSoundInstance.master(ModSounds.ZOOM_OUT_SOUND, 1.0F));
                    playedZoomIn = false;
                    playedZoomOut = true;
                }

            } else {
                currentSens.setValue(originalSensitivity);
                zoomTime = 0;
                zoomed = fov;
            }
        }
        return zoomed;
    }

    public void setShadowUniforms(ShaderProgram shaderProgram, World world) {
        Matrix4f shadowModelView = new Matrix4f();
        shadowModelView.identity();
        ShadowMapRenderer.rotateShadowModelView(shadowModelView, world);
        Vector4f lightPosition = new Vector4f(0.0f, 0.0f, 1.0f, 0.0f);
        lightPosition.mul(shadowModelView.invert());

        Vector3f shadowLightDirection = new Vector3f(lightPosition.x(), lightPosition.y(), lightPosition.z());
        shaderProgram.setMatrix("viewMatrix", ShadowMapRenderer.createShadowModelView(camera.getPos().x, camera.getPos().y, camera.getPos().z, world, true).peek().getPositionMatrix());
        shaderProgram.setMatrix("orthographMatrix", ShadowMapRenderer.createProjMat());
        shaderProgram.setVector("lightAngled", shadowLightDirection);
    }

    public static float getWarpTimer(World world){
        WorldEvents events = InitializeComponents.EVENTS.get(world);

        if (events.isLevel2Warp()) {
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

    public static float getSunsetTimer(World world) {
        WorldEvents events = InitializeComponents.EVENTS.get(world);
        double seconds = 8.0;
        int maxTicks = (int) (seconds * 20);

        if(events.isSunsetTransition() && events.isNoon()){
            SunsetTimer.setOnOrOff(true);

            if(SunsetTimer.getCurrentTick() >= maxTicks){
                SunsetTimer.setOnOrOff(false);
                return 1.0f;
            }
            else{
                return Math.min((float) SunsetTimer.getCurrentTick() / maxTicks, 1.0f);
            }
        }
        else if(events.isSunsetTransition() && !events.isNoon()){
            SunsetTimer.setOnOrOff(true);

            if(SunsetTimer.getCurrentTick() >= maxTicks){
                SunsetTimer.setOnOrOff(false);
                return 0.0f;
            }
            else{
                return Math.max(1.0f - ((float) SunsetTimer.getCurrentTick() / maxTicks), 0.0f);
            }
        }
        else{
            SunsetTimer.setOnOrOff(false);
            SunsetTimer.resetToZero();
            if(events.isNoon()){
                return 0;
            }
            else {
                return 1;
            }
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
