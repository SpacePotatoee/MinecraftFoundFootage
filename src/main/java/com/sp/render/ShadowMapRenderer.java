package com.sp.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.VertexSorter;
import com.sp.SPBRevamped;
import com.sp.mixin.WorldRendererAccessor;
import com.sp.util.MatrixMath;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.VeilRenderer;
import foundry.veil.api.client.render.framebuffer.AdvancedFbo;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

import static net.minecraft.util.math.MathHelper.sin;

public class ShadowMapRenderer {
    private static boolean renderingShadowMap;


    public static void renderShadowMap(Camera camera, float tickDelta){
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


            frustum = new Frustum(shadowModelView.peek().getPositionMatrix(), shadowProjMat);
            frustum.setPosition(cameraPos.x, cameraPos.y, cameraPos.z);
            accessor.setFrustum(frustum);
            accessor.invokeSetupTerrain(camera, frustum, false, false);
            accessor.invokeRenderLayer(RenderLayer.getCutout(), shadowModelView, cameraPos.x, cameraPos.y, cameraPos.z, shadowProjMat);
            accessor.invokeRenderLayer(RenderLayer.getCutoutMipped(), shadowModelView, cameraPos.x, cameraPos.y, cameraPos.z, shadowProjMat);
            accessor.invokeRenderLayer(RenderLayer.getSolid(), shadowModelView, cameraPos.x, cameraPos.y, cameraPos.z, shadowProjMat);

            //shadowMap.bindDraw(true);
            if(client.world != null) {
                VertexConsumerProvider.Immediate immediate = accessor.getBufferBuilders().getEntityVertexConsumers();

                //accessor.getEntityRenderDispatcher().;
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

    public static MatrixStack createShadowModelView(double CameraX, double CameraY, double CameraZ, boolean doInterval){
        MatrixStack shadowModelView = new MatrixStack();

        shadowModelView.peek().getNormalMatrix().identity();
        shadowModelView.peek().getPositionMatrix().identity();

        shadowModelView.peek().getPositionMatrix().translate(0.0f, 0.0f, -100.0f);
        rotateShadowModelView(shadowModelView.peek().getPositionMatrix());


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

    public static void rotateShadowModelView(Matrix4f shadowModelView){
        shadowModelView.rotate(RotationAxis.POSITIVE_X.rotationDegrees(45f));
        shadowModelView.rotate(RotationAxis.POSITIVE_Y.rotationDegrees(45f));
//        shadowModelView.rotate(RotationAxis.POSITIVE_X.rotationDegrees(75f));
//        shadowModelView.rotate(RotationAxis.POSITIVE_Y.rotationDegrees(25f));
        //shadowModelView.rotate(RotationAxis.POSITIVE_X.rotationDegrees(15.0f * sin(RenderSystem.getShaderGameTime() * 200) + 90.0f));
    }

    public static Matrix4f createProjMat(){
        return MatrixMath.orthographicMatrix(160, 0.05f, 256.0f);
    }

    public static boolean isRenderingShadowMap() {
        return renderingShadowMap;
    }

    public static void setRenderingShadowMap(boolean l) {
        renderingShadowMap = l;
    }

}
