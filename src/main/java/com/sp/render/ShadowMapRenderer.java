package com.sp.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.VertexSorter;
import com.sp.SPBRevamped;
import com.sp.init.RenderLayers;
import com.sp.mixin.WorldRendererAccessor;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.framebuffer.AdvancedFbo;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

public class ShadowMapRenderer {
    private static boolean renderingShadowMap;


    public static void renderShadowMap(Camera camera, float tickDelta, World world){
        MinecraftClient client = MinecraftClient.getInstance();
        WorldRendererAccessor accessor = (WorldRendererAccessor) client.worldRenderer;
        Vec3d cameraPos = camera.getPos();
        MatrixStack shadowModelView = createShadowModelView(cameraPos.x, cameraPos.y, cameraPos.z, world, true);
        Matrix4f shadowProjMat = createProjMat();
        Matrix4f backupProjMat = RenderSystem.getProjectionMatrix();

        int width = client.getFramebuffer().viewportWidth;
        int height = client.getFramebuffer().viewportHeight;
        Frustum frustum;

        AdvancedFbo shadowMap = VeilRenderSystem.renderer().getFramebufferManager().getFramebuffer(new Identifier(SPBRevamped.MOD_ID, "shadowmap"));
        if(shadowMap != null) {
            RenderSystem.setProjectionMatrix(shadowProjMat, VertexSorter.BY_Z);

            shadowMap.bind(true);
            setRenderingShadowMap(true);



            frustum = new Frustum(shadowModelView.peek().getPositionMatrix(), shadowProjMat);
            frustum.setPosition(cameraPos.x, cameraPos.y, cameraPos.z);
            accessor.setFrustum(frustum);
            accessor.invokeSetupTerrain(camera, frustum, false, false);
            accessor.invokeRenderLayer(RenderLayer.getCutout(), shadowModelView, cameraPos.x, cameraPos.y, cameraPos.z, shadowProjMat);
            accessor.invokeRenderLayer(RenderLayer.getCutoutMipped(), shadowModelView, cameraPos.x, cameraPos.y, cameraPos.z, shadowProjMat);
            accessor.invokeRenderLayer(RenderLayer.getSolid(), shadowModelView, cameraPos.x, cameraPos.y, cameraPos.z, shadowProjMat);
            accessor.invokeRenderLayer(RenderLayers.getPoolTileLayer(), shadowModelView, cameraPos.x, cameraPos.y, cameraPos.z, shadowProjMat);

            if(client.world != null) {
                VertexConsumerProvider.Immediate immediate = accessor.getBufferBuilders().getEntityVertexConsumers();

                for(Entity entity : client.world.getEntities()){
                    if(accessor.getEntityRenderDispatcher().shouldRender(entity, accessor.getFrustum(), cameraPos.x, cameraPos.y, cameraPos.z) || entity.isSpectator()){
                        accessor.invokeRenderEntity(entity, cameraPos.x, cameraPos.y, cameraPos.z, tickDelta, shadowModelView, immediate);
                    }
                }

                immediate.draw();

            }
            
            setRenderingShadowMap(false);
            AdvancedFbo.unbind();
            RenderSystem.viewport(0, 0, width, height);

            RenderSystem.setProjectionMatrix(backupProjMat, VertexSorter.BY_DISTANCE);



        }
    }

    public static void renderLevel0ShadowMap(Camera camera, World world){
        MinecraftClient client = MinecraftClient.getInstance();
        WorldRendererAccessor accessor = (WorldRendererAccessor) client.worldRenderer;
        Vec3d cameraPos = camera.getPos();
        MatrixStack shadowModelView = createShadowModelView(cameraPos.x, cameraPos.y, cameraPos.z, true);
        Matrix4f shadowProjMat = createProjMat();
        Matrix4f backupProjMat = RenderSystem.getProjectionMatrix();

        int width = client.getFramebuffer().viewportWidth;
        int height = client.getFramebuffer().viewportHeight;
        Frustum frustum;

        AdvancedFbo shadowMap = VeilRenderSystem.renderer().getFramebufferManager().getFramebuffer(new Identifier(SPBRevamped.MOD_ID, "shadowmap"));
        if(shadowMap != null) {
            RenderSystem.setProjectionMatrix(shadowProjMat, VertexSorter.BY_Z);

            shadowMap.bind(true);
            setRenderingShadowMap(true);


            GL11.glEnable(GL30.GL_CLIP_DISTANCE0);

            frustum = new Frustum(shadowModelView.peek().getPositionMatrix(), shadowProjMat);
            frustum.setPosition(cameraPos.x, cameraPos.y, cameraPos.z);
            accessor.setFrustum(frustum);
            accessor.invokeSetupTerrain(camera, frustum, false, false);
            accessor.invokeRenderLayer(RenderLayer.getCutout(), shadowModelView, cameraPos.x, cameraPos.y, cameraPos.z, shadowProjMat);
            accessor.invokeRenderLayer(RenderLayer.getCutoutMipped(), shadowModelView, cameraPos.x, cameraPos.y, cameraPos.z, shadowProjMat);
            accessor.invokeRenderLayer(RenderLayer.getSolid(), shadowModelView, cameraPos.x, cameraPos.y, cameraPos.z, shadowProjMat);
            accessor.invokeRenderLayer(RenderLayers.getCarpet(), shadowModelView, cameraPos.x, cameraPos.y, cameraPos.z, shadowProjMat);


            GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
            setRenderingShadowMap(false);
            AdvancedFbo.unbind();
            RenderSystem.viewport(0, 0, width, height);

            RenderSystem.setProjectionMatrix(backupProjMat, VertexSorter.BY_DISTANCE);



        }
    }

    /**
     The "do interval" bit was taken from the Iris Shadow Matrices class in order to keep the Shadows from flashing
     <a href="https://github.com/IrisShaders/Iris/blob/3fc94e8f41535feebce0bcb4235eff4a809f5eea/common/src/main/java/net/irisshaders/iris/shadows/ShadowMatrices.java">HERE</a>
     */
    public static MatrixStack createShadowModelView(double CameraX, double CameraY, double CameraZ, World world, boolean doInterval){
        MatrixStack shadowModelView = new MatrixStack();

        shadowModelView.peek().getNormalMatrix().identity();
        shadowModelView.peek().getPositionMatrix().identity();

        shadowModelView.peek().getPositionMatrix().translate(0.0f, 0.0f, -100.0f);
        rotateShadowModelView(shadowModelView.peek().getPositionMatrix(), world);

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

    public static MatrixStack createShadowModelView(double CameraX, double CameraY, double CameraZ, boolean doInterval){
        MatrixStack shadowModelView = new MatrixStack();

        shadowModelView.peek().getNormalMatrix().identity();
        shadowModelView.peek().getPositionMatrix().identity();

        shadowModelView.peek().getPositionMatrix().translate(0.0f, 0.0f, -100.0f);
        rotateShadowModelView(shadowModelView.peek().getPositionMatrix());

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

    public static void rotateShadowModelView(Matrix4f shadowModelView){
        shadowModelView.rotate(RotationAxis.POSITIVE_X.rotationDegrees(90f));
    }

    //Global Light Rotation
    public static void rotateShadowModelView(Matrix4f shadowModelView, World world){
        shadowModelView.rotate(RotationAxis.POSITIVE_X.rotationDegrees(PoolroomsDayCycle.getSunAngle()));
    }

    public static Matrix4f createProjMat(){
        return orthographicMatrix(160, 0.05f, 256.0f);
    }


    /**
     Also taken from Iris lol. Turns out their orthographic Matrix is better
     <a href="https://github.com/IrisShaders/Iris/blob/3fc94e8f41535feebce0bcb4235eff4a809f5eea/common/src/main/java/net/irisshaders/iris/shadows/ShadowMatrices.java">HERE</a>
     */
    public static Matrix4f orthographicMatrix(float halfPlaneLength, float nearPlane, float farPlane) {
        return new Matrix4f(
                1.0f / halfPlaneLength, 0f, 0f, 0f,
                0f, 1.0f / halfPlaneLength, 0f, 0f,
                0f, 0f, 2.0f / (nearPlane - farPlane), 0f,
                0f, 0f, -(farPlane + nearPlane) / (farPlane - nearPlane), 1f
        );
    }

    public static boolean isRenderingShadowMap() {
        return renderingShadowMap;
    }

    public static void setRenderingShadowMap(boolean l) {
        renderingShadowMap = l;
    }

}
