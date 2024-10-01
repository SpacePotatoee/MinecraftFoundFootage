package com.sp;

import com.mojang.blaze3d.systems.RenderSystem;
import com.sp.block.ModBlocks;
import com.sp.block.entity.ModBlockEntities;
import com.sp.block.renderer.*;
import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import com.sp.mixin.WorldRendererAccessor;
import com.sp.networking.InitializePackets;
import com.sp.player.CameraRoll;
import com.sp.player.Keybinds;
import com.sp.sounds.ModSounds;
import com.sp.util.MatrixMath;
import com.sp.util.uniformTest;
import com.sp.world.biome.ModBiomes;
import com.sp.world.levels.BackroomsLevels;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.VeilRenderer;
import foundry.veil.api.client.render.deferred.VeilDeferredRenderer;
import foundry.veil.api.client.render.deferred.light.AreaLight;
import foundry.veil.api.client.render.deferred.light.renderer.LightRenderer;
import foundry.veil.api.client.render.framebuffer.AdvancedFbo;
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
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.*;

import static net.minecraft.util.math.MathHelper.lerp;
import static net.minecraft.util.math.MathHelper.sin;


public class SPBRevampedClient implements ClientModInitializer {
    private static final Identifier VHS_SHADER = new Identifier(SPBRevamped.MOD_ID, "vhs");
    private static final Identifier MOTION_BLUR = new Identifier(SPBRevamped.MOD_ID, "vhs/motion_blur");
    private static final Identifier WATER_SHADER = new Identifier(SPBRevamped.MOD_ID, "vhs/water");

    public static HashMap<AbstractClientPlayerEntity, AreaLight> flashLightList = new HashMap<>();
    AreaLight flashLight;
    int ticks = 0;

    float yaw;
    float pitch;
    float yawLerp = 0;
    float pitchLerp = 0;
    float prevYaw;
    float prevPitch;
    float yawRotAmount;
    float pitchRotAmount;

    float prevYaw2;
    float prevPitch2;

    boolean sent = false;
    boolean on = false;
    public static boolean zoom = false;
    public static double zoomTime = 0;
    public static double zoomed;
    static boolean playedZoomIn = false;
    static boolean playedZoomOut = true;
    static boolean inBackrooms = false;

    boolean changed = false;
    static boolean renderingShadowMap;

    Camera camera;







    @Override
    public void onInitializeClient() {
        InitializePackets.registerS2CPackets();

        Keybinds.inizializeKeyBinds();

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.ConcreteBlock11, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.BottomTrim, RenderLayer.getCutout());
        //BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.PoolTileCurve, RenderLayer.getCutout());

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
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.PoolTileSlope, RenderLayer.getCutout());

        BlockEntityRendererFactories.register(ModBlockEntities.FLUORESCENT_LIGHT_BLOCK_ENTITY, FluorescentLightBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.WINDOW_BLOCK_ENTITY, PoolroomsWindowBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.POOLROOMS_WINDOW_BLOCK_ENTITY, PoolroomsWindowBlockEntityRenderer::new);







        VeilEventPlatform.INSTANCE.onVeilRenderTypeStageRender(((stage, levelRenderer, bufferSource, poseStack, projectionMatrix, renderTick, partialTicks, camera, frustum) -> {
            if(camera != null){
                this.camera = camera;
            }
            MinecraftClient client = MinecraftClient.getInstance();
            PlayerEntity playerClient = client.player;
            if(playerClient != null){
                SimpleOption<Integer> fps =  MinecraftClient.getInstance().options.getMaxFps();
                PlayerComponent playerComponent = InitializeComponents.PLAYER.get(playerClient);
                if (client.world.getBiome(playerClient.getBlockPos()).getKey().get() == ModBiomes.BASE_BACKROOMS_BIOME || client.world.getRegistryKey() == BackroomsLevels.LEVEL2_WORLD_KEY){
                    setInBackrooms(true);
                    //fps.setValue(30);
                }else {
                    setInBackrooms(ConfigStuff.forceBackrooms);
                }

                if(client.world != null) {
                    if (client.world.getRegistryKey() == BackroomsLevels.LEVEL2_WORLD_KEY) {
                        if (!changed) {
                            playerComponent.setLightRenderDistance(ConfigStuff.lightRenderDistance);
                            changed = true;
                        }

                        if (ConfigStuff.lightRenderDistance != 32) {
                            playerComponent.setLightRenderDistance(ConfigStuff.lightRenderDistance);
                        }

                        if (ConfigStuff.lightRenderDistance > 32) {
                            ConfigStuff.lightRenderDistance = 32;
                        }
                    } else {
                        if (changed) {
                            ConfigStuff.lightRenderDistance = playerComponent.getLightRenderDistance();
                            changed = false;
                        }
                    }
                }

                VeilRenderer renderer = VeilRenderSystem.renderer();
                VeilDeferredRenderer deferredRenderer = renderer.getDeferredRenderer();
                LightRenderer lightRenderer = deferredRenderer.getLightRenderer();
                if (!ConfigStuff.enableVanillaLighting && inBackrooms) {
                    lightRenderer.disableVanillaLight();
                }else{
                    lightRenderer.enableVanillaLight();
                }

//                if(playerClient.getWorld().getRegistryKey() == BackroomsLevels.POOLROOMS_WORLD_KEY){
//                    if(lightRenderer.isAmbientOcclusionEnabled()) {
//                        lightRenderer.disableAmbientOcclusion();
//                    }
//                }
//                else{
//                    if(ConfigStuff.enableAmbientOcclusion) lightRenderer.enableAmbientOcclusion();
//                    else lightRenderer.disableAmbientOcclusion();
//                }

            }






//            Iterator<AreaLight> areaLights = deferredRenderer.getLightRenderer().getLights(LightTypeRegistry.AREA.get()).iterator();
//            while (areaLights.hasNext()) {
//                AreaLight light = areaLights.next();
//                light.markDirty();
//                System.out.println(light);
//            }


            if(stage == Stage.AFTER_SKY) {
                if (camera != null) {
                    renderShadowMap(camera, partialTicks);
                }
            }



            ticks++;
            assert MinecraftClient.getInstance().world != null;
            Iterator<AbstractClientPlayerEntity> playerList = MinecraftClient.getInstance().world.getPlayers().iterator();

            PlayerEntity playerd = MinecraftClient.getInstance().player;

            if(playerd != null){
                PlayerComponent playerComponent = InitializeComponents.PLAYER.get(playerd);
                if (playerComponent.isFlashLightOn()) {
                    if (flashLight == null) {
                        flashLight = new AreaLight();
                        VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().addLight(this.flashLight
                                .setBrightness(1f)
                                .setDistance(25f)
                                .setSize(0, 0)
                        );
                    }
                    else{
                        yaw = playerd.getYaw(partialTicks);
                            pitch = playerd.getPitch(partialTicks);

                            if (prevYaw != -10000 && prevPitch != -10000) {
                                yawRotAmount = yaw - prevYaw;
                                yawLerp += lerp(0.000001f, yawRotAmount, 0);
                                yawLerp = yawLerp * 0.985f;

                                pitchRotAmount = pitch - prevPitch;
                                pitchLerp += lerp(0.000001f, pitchRotAmount, 0);
                                pitchLerp = pitchLerp * 0.985f;

                                flashLight.setOrientation(new Quaternionf().rotateXYZ((float) -(Math.toRadians(pitch - pitchLerp)), (float) Math.toRadians(yaw - yawLerp), 0.0f));

                                Vec3d cameraPos = playerd.getCameraPosVec(partialTicks);

                                flashLight.setPosition(cameraPos.getX(), cameraPos.getY(), cameraPos.getZ());
                            }

                            prevYaw = yaw;
                            prevPitch = pitch;
                    }
                }
                else {
                    pitchLerp = 0;
                    yawLerp = 0;
                    prevYaw = playerd.getYaw();
                    prevPitch = playerd.getPitch();
                    if (flashLight != null) {
                        VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().removeLight(flashLight);
                        flashLight = null;
                        //flashLightList.remove(playerd);
                    }
                }
            }

            //Flashlight
//            while (playerList.hasNext()) {
//                AbstractClientPlayerEntity player = playerList.next();
//                if (player != null) {
//                    PlayerComponent playerComponent = InitializeComponents.PLAYER.get(player);
//                    if (playerComponent.isFlashLightOn()) {
//                        if (!flashLightList.containsKey(player)) {
//                            flashLight = new AreaLight();
//                            VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().addLight(flashLight
//                                    .setBrightness(1f)
//                                    .setDistance(25f)
//                                    .setSize(0, 0)
//                            );
//                            flashLightList.put(player, flashLight);
//                        } else {
//                            yaw = player.getYaw(partialTicks);  //act as the flashlight to get the player from the iterator
//                            pitch = player.getPitch(partialTicks);
//
//                            if (prevYaw != -10000 && prevPitch != -10000) {
//                                yawRotAmount = yaw - prevYaw;
//                                yawLerp += lerp(0.000001f, yawRotAmount, 0);
//                                yawLerp = yawLerp * 0.985f;
//
//                                pitchRotAmount = pitch - prevPitch;
//                                pitchLerp += lerp(0.000001f, pitchRotAmount, 0);
//                                pitchLerp = pitchLerp * 0.985f;
//
//                                flashLight.setOrientation(new Quaternionf().rotateXYZ((float) -(Math.toRadians(pitch - pitchLerp)), (float) Math.toRadians(yaw - yawLerp), 0.0f));
//
//                                Vec3d cameraPos = player.getCameraPosVec(partialTicks);
//
//                                flashLight.setPosition(cameraPos.getX(), cameraPos.getY(), cameraPos.getZ());
//                            }
//
//                            prevYaw = yaw;
//                            prevPitch = pitch;
//                        }
//                    } else {
//                        pitchLerp = 0;
//                        yawLerp = 0;
//                        prevYaw = player.getYaw();
//                        prevPitch = player.getPitch();
//                        if (flashLight != null) {
//                            if (flashLightList.containsKey(player)) {
//                                VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().removeLight(flashLightList.get(player));
//                                flashLightList.remove(player);
//                            }
//                        }
//                    }
//                }
//            }





            PostProcessingManager postProcessingManager = VeilRenderSystem.renderer().getPostProcessingManager();
                if (stage == Stage.AFTER_LEVEL) {
                    PostPipeline Pipeline = postProcessingManager.getPipeline(VHS_SHADER);
                    if (Pipeline != null) {
                        if (ConfigStuff.enableVhsEffect && inBackrooms) {
                            postProcessingManager.add(VHS_SHADER);
                        } else if (postProcessingManager.isActive(VHS_SHADER)) {
                            postProcessingManager.remove(VHS_SHADER);

                        }
                    }
//
//
////                if (Pipeline3 != null) {
////                    if (ConfigStuff.renderDepth) {
////                        postProcessingManager.add(DEPTH);
////                        postProcessingManager.add(SSAO);
////                    }
////                }
                }


                //Camera Rotation

                if (ticks % 15 == 0) {
                    PlayerEntity player = MinecraftClient.getInstance().player;
                    if (player != null) {
                        PlayerComponent playerComponent = InitializeComponents.PLAYER.get(player);
                        if (inBackrooms) {
                            CameraRoll.doCameraRoll(player);
                        }
                    }
                }



            //Camera on ground Check

//            ClientWorld world = MinecraftClient.getInstance().world;
//            PlayerEntity player = MinecraftClient.getInstance().player;
//            Iterator<Entity> EntityList = world.getEntities().iterator();
//
//            while (EntityList.hasNext()){
//                Entity entity = EntityList.next();
//
//                if(entity != null ){
//
//                    if(entity instanceof ItemEntity){
//                        ItemStack itemStack = ((ItemEntity) entity).getStack();
//
//                        if (itemStack.getSubNbt("attachedPlayer") != null) {
//                            UUID AttachedPlayerUUID = itemStack.getSubNbt("attachedPlayer").getUuid("attachedPlayer");
//                            PlayerEntity AttachedPlayer = world.getPlayerByUuid(AttachedPlayerUUID);
//
//                            if(AttachedPlayer != null && AttachedPlayer == player){
//                                PlayerComponent playerComponent = InitializeComponents.PLAYER.get(AttachedPlayer);
//                                Vec3d pos = entity.getPos();
//                                float yaw = entity.getYaw();
//
//                                playerComponent.setCameraItemYaw(yaw);
//                                playerComponent.setCameraItemPos(pos);
//                                playerComponent.setCameraItem(true);
//                                playerComponent.SetCameraDown(false);
//                                playerComponent.setCameraInOtherInventory(false);
//                                InitializeComponents.PLAYER.sync(AttachedPlayer);
//                            }
//                        }
//
//                    }
//                }
//            }




        }));

        VeilEventPlatform.INSTANCE.onVeilRenderTypeStageRender((stage, levelRenderer, bufferSource, poseStack, projectionMatrix, renderTick, partialTicks, camera, frustum) -> {
//            PostProcessingManager postProcessingManager = VeilRenderSystem.renderer().getPostProcessingManager();
//            if(stage == Stage.AFTER_LEVEL) {
//                PostPipeline pipeline = postProcessingManager.getPipeline(FOG);
//                if (pipeline != null) {
//                    if(ConfigStuff.enableFogTest) {
//                        postProcessingManager.runPipeline(pipeline, false);
//                    }else if(postProcessingManager.isActive(FOG)){
//                        postProcessingManager.remove(FOG);
//                    }
//                }
//            }

        });

        VeilEventPlatform.INSTANCE.preVeilPostProcessing(((name, pipeline, context) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            PlayerEntity player = MinecraftClient.getInstance().player;
            VeilRenderer renderer = VeilRenderSystem.renderer();
            ShaderPreDefinitions definitions = renderer.getShaderDefinitions();

            if(inBackrooms) {
                if (player != null) {
                    float yaw = player.getYaw();
                    float pitch = player.getPitch();
                    float yawRotAmount = yaw - prevYaw2;
                    float pitchRotAmount = pitch - prevPitch2;

                    if (VHS_SHADER.equals(name)) {
                        ShaderProgram shaderProgram = context.getShader(MOTION_BLUR);
                        if (shaderProgram != null) {
                            shaderProgram.setVector("Velocity", MathHelper.cos((float) Math.toRadians(yawRotAmount + 90.0f)), MathHelper.sin((float) Math.toRadians(pitchRotAmount)));
                        }

                        shaderProgram = context.getShader(VHS_SHADER);
                        if (shaderProgram != null && client.world != null) {
                            if (client.world.getRegistryKey() == BackroomsLevels.LEVEL2_WORLD_KEY) {
                                shaderProgram.setInt("FogToggle", 1);
                            }
                            else
                            {
                                shaderProgram.setInt("FogToggle", 0);
                            }
                        }

                        shaderProgram = context.getShader(WATER_SHADER);
                        if (shaderProgram != null && client.world != null) {
                            if (client.world.getRegistryKey() == BackroomsLevels.POOLROOMS_WORLD_KEY) {
                                if(this.camera != null) {
                                    Matrix4f shadowModelView = new Matrix4f();
                                    shadowModelView.identity();
                                    shadowModelView.rotate(RotationAxis.POSITIVE_X.rotationDegrees(25.0f * sin(RenderSystem.getShaderGameTime() * 200) + 90.0f));
                                    Vector4f lightPosition = new Vector4f(0.0f, 0.0f, 1.0f, 0.0f);
                                    lightPosition.mul(shadowModelView.invert());

                                    Vector3f shadowLightDirection = new Vector3f(lightPosition.x(), lightPosition.y(), lightPosition.z());

                                    shaderProgram.setMatrix("viewMatrix", createShadowModelView(camera.getPos().x, camera.getPos().y, camera.getPos().z, true).peek().getPositionMatrix());
                                    shaderProgram.setMatrix("orthographMatrix", createProjMat());
                                    shaderProgram.setVector("lightAngled", shadowLightDirection);
                                }
                            }
                        }


                    }

                    if (ConfigStuff.enableWarp) {
                        if(definitions.getDefinition("WARP") == null) {
                            definitions.define("WARP");
                        }
                    }
                    else {
                        if(definitions.getDefinition("WARP") != null) {
                            definitions.remove("WARP");
                        }
                    }
                    prevYaw2 = yaw;
                    prevPitch2 = pitch;
                }
            }
            else {
                if(definitions.getDefinition("WARP") != null) {
                    definitions.remove("WARP");
                }
            }



        }));



        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> client.execute(() -> {
            PlayerEntity player = MinecraftClient.getInstance().player;

            if (player != null) {
                PlayerComponent playerComponent = InitializeComponents.PLAYER.get(player);

                playerComponent.setFlashLightOn(false);
                flashLightList.clear();


            }
        }));


        ClientTickEvents.END_CLIENT_TICK.register((client -> {
            PlayerEntity player = MinecraftClient.getInstance().player;

            if(player != null) {
                PlayerComponent playerComponent = InitializeComponents.PLAYER.get(player);
                if(Keybinds.toggleFlashlight.wasPressed()){
                    //client.getSoundManager().play(PositionedSoundInstance.master(ModSounds.FLASHLIGHT_CLICK, 1.0F));
                    player.playSound(ModSounds.FLASHLIGHT_CLICK, 1, 1);
                    if (player.getWorld().getRegistryKey() != BackroomsLevels.POOLROOMS_WORLD_KEY) {
                        if (on) {
                            playerComponent.setFlashLightOn(false);
                            on = false;
                        } else {
                            playerComponent.setFlashLightOn(true);
                            on = true;
                        }
                    }
                    else{
                        playerComponent.setFlashLightOn(false);
                        player.sendMessage(Text.literal("Your flashlight got wet. ").append(Text.literal("It no longer works").formatted(Formatting.RED)), true);
                    }
                }

            }
        }));


        ClientTickEvents.END_WORLD_TICK.register((client) ->{
            PlayerEntity player = MinecraftClient.getInstance().player;

            if(player != null){
                PlayerComponent playerComponent = InitializeComponents.PLAYER.get(player);
                PacketByteBuf buffer = PacketByteBufs.create();

                if (!sent) {
                    buffer.writeBoolean(playerComponent.isFlashLightOn());
                    ClientPlayNetworking.send(InitializePackets.FL_SYNC, buffer);
                    sent = true;
                }
            }

        });

    }

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

    public static boolean isInBackrooms() {
        return inBackrooms;
    }

    public static void setInBackrooms(boolean inBackrooms) {
        SPBRevampedClient.inBackrooms = inBackrooms;
    }

    public static boolean isRenderingShadowMap() {
        return renderingShadowMap;
    }

    public static void setRenderingShadowMap(boolean l) {
        renderingShadowMap = l;
    }

    private void renderShadowMap(Camera camera, float tickDelta){
        VeilRenderer renderer = VeilRenderSystem.renderer();
        MinecraftClient client = MinecraftClient.getInstance();
        WorldRendererAccessor accessor = (WorldRendererAccessor) client.worldRenderer;
        Vec3d cameraPos = camera.getPos();
        MatrixStack shadowModelView = createShadowModelView(cameraPos.x, cameraPos.y, cameraPos.z, true);
        Matrix4f shadowProjMat = createProjMat();
        int width = client.getFramebuffer().viewportWidth;
        int height = client.getFramebuffer().viewportHeight;
        Frustum frustum;

        AdvancedFbo shadowMap = VeilRenderSystem.renderer().getFramebufferManager().getFramebuffer(new Identifier(SPBRevamped.MOD_ID, "shadowmap"));
        AdvancedFbo waterShadows = VeilRenderSystem.renderer().getFramebufferManager().getFramebuffer(new Identifier(SPBRevamped.MOD_ID, "water_shadows"));
        if(shadowMap != null) {
            shadowMap.bind(true);
            setRenderingShadowMap(true);

            frustum = new Frustum(shadowModelView.peek().getPositionMatrix(), shadowProjMat);
            frustum.setPosition(cameraPos.x, cameraPos.y, cameraPos.z);
            accessor.setFrustum(frustum);
            accessor.invokeSetupTerrain(camera, frustum, false, false);
            accessor.invokeRenderLayer(RenderLayer.getCutout(), shadowModelView, cameraPos.x, cameraPos.y, cameraPos.z, shadowProjMat);
            accessor.invokeRenderLayer(RenderLayer.getCutoutMipped(), shadowModelView, cameraPos.x, cameraPos.y, cameraPos.z, shadowProjMat);
            accessor.invokeRenderLayer(RenderLayer.getSolid(), shadowModelView, cameraPos.x, cameraPos.y, cameraPos.z, shadowProjMat);

            setRenderingShadowMap(false);
            AdvancedFbo.unbind();
            RenderSystem.viewport(0, 0, width, height);

        }
    }

    public static MatrixStack createShadowModelView(double CameraX, double CameraY, double CameraZ, boolean doInterval){
        MatrixStack shadowModelView = new MatrixStack();

        shadowModelView.peek().getNormalMatrix().identity();
        shadowModelView.peek().getPositionMatrix().identity();

        shadowModelView.peek().getPositionMatrix().translate(0.0f, 0.0f, -100.0f);
        //shadowModelView.multiply(RotationAxis.POSITIVE_X.rotationDegrees(45f));
        //shadowModelView.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(45f));
//        shadowModelView.multiply(RotationAxis.POSITIVE_X.rotationDegrees(75f));
//        shadowModelView.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(25f));
        shadowModelView.multiply(RotationAxis.POSITIVE_X.rotationDegrees(15.0f * sin(RenderSystem.getShaderGameTime() * 200) + 90.0f));


        /**
            This bit was taken from the Iris Shadow Matrices class in order to keep the Shadows from flashing
            https://github.com/IrisShaders/Iris/blob/3fc94e8f41535feebce0bcb4235eff4a809f5eea/common/src/main/java/net/irisshaders/iris/shadows/ShadowMatrices.java
        */
        if(doInterval) {
            float offsetX = (float) CameraX % 2.0f;
            float offsetY = (float) CameraY % 2.0f;
            float offsetZ = (float) CameraZ % 2.0f;

            float halfIntervalSize = 1.0f;

            offsetX -= halfIntervalSize;
            offsetY -= halfIntervalSize;
            offsetZ -= halfIntervalSize;
            shadowModelView.peek().getPositionMatrix().translate(offsetX, offsetY, offsetZ);
        }
        return shadowModelView;
    }

    public Matrix4f createProjMat(){
        return MatrixMath.orthographicMatrix(160, 0.05f, 256.0f);
    }


}
