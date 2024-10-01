package com.sp.util;

import com.mojang.blaze3d.systems.RenderSystem;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.VeilRenderer;
import foundry.veil.api.client.render.deferred.VeilDeferredRenderer;
import foundry.veil.api.client.render.framebuffer.AdvancedFbo;
import foundry.veil.ext.GameRendererExtension;
import foundry.veil.ext.RenderTargetExtension;
import foundry.veil.impl.client.render.LevelPerspectiveCamera;
import foundry.veil.mixin.accessor.GameRendererAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.joml.*;

import java.lang.Math;

import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;

/**
    Copied from VeilLevelPerspectiveRenderer so that I could have more control over what it does
 */

public class VeilPerspectiveRendererCopy {
    private static final LevelPerspectiveCamera CAMERA = new LevelPerspectiveCamera();
    private static final Matrix4f TRANSFORM = new Matrix4f();
    private static final Matrix3f NORMAL = new Matrix3f();

    private static final Matrix4f BACKUP_PROJECTION = new Matrix4f();
    private static final Matrix3f BACKUP_INVERSE_VIEW_ROTATION = new Matrix3f();
    private static final Vector3f BACKUP_LIGHT0_POSITION = new Vector3f();
    private static final Vector3f BACKUP_LIGHT1_POSITION = new Vector3f();

    private static boolean renderingPerspective = false;

    private VeilPerspectiveRendererCopy() {
    }

    public static void render(AdvancedFbo framebuffer, Matrix4fc modelView, Matrix4fc projection, Vector3dc cameraPosition, Quaternionfc cameraOrientation, float renderDistance, float partialTicks) {
        render(framebuffer, MinecraftClient.getInstance().cameraEntity, modelView, projection, cameraPosition, cameraOrientation, renderDistance, partialTicks);
    }

    public static void render(AdvancedFbo framebuffer, @Nullable Entity cameraEntity, Matrix4fc modelView, Matrix4fc projection, Vector3dc cameraPosition, Quaternionfc cameraOrientation, float renderDistance, float partialTicks) {
        if (renderingPerspective) {
            return;
        }
        VeilRenderer renderer = VeilRenderSystem.renderer();
        float farPlane = renderer.getCameraMatrices().getFarPlane();
        float nearPlane = renderer.getCameraMatrices().getNearPlane();
        Matrix4f ProjMat = RenderSystem.getProjectionMatrix();
        Matrix4f MViewMat = RenderSystem.getModelViewMatrix();

        MinecraftClient minecraft = MinecraftClient.getInstance();
        GameRenderer gameRenderer = minecraft.gameRenderer;
        WorldRenderer levelRenderer = minecraft.worldRenderer;
        Window window = minecraft.getWindow();
        GameRendererAccessor accessor = (GameRendererAccessor) gameRenderer;
        RenderTargetExtension renderTargetExtension = (RenderTargetExtension) minecraft.getFramebuffer();
        long time = ((GameRendererExtension) gameRenderer).veil$getFrameStartNanos();

        CAMERA.setup(cameraPosition, cameraEntity, minecraft.world, cameraOrientation);

        MatrixStack poseStack = new MatrixStack();
        MatrixStack.Entry pose = poseStack.peek();

        poseStack.multiplyPositionMatrix(TRANSFORM.set(modelView));
        pose.getNormalMatrix().mul(TRANSFORM.normal(NORMAL));
        //poseStack.multiply(CAMERA.getRotation());

        BACKUP_INVERSE_VIEW_ROTATION.set(RenderSystem.getInverseViewRotationMatrix());
        RenderSystem.setInverseViewRotationMatrix(NORMAL.rotate(CAMERA.getRotation()).invert());
        float backupRenderDistance = gameRenderer.getViewDistance();
        accessor.setRenderDistance(renderDistance);

        int backupWidth = window.getFramebufferWidth();
        int backupHeight = window.getFramebufferHeight();
        window.setFramebufferWidth(framebuffer.getWidth());
        window.setFramebufferHeight(framebuffer.getHeight());

        VeilDeferredRenderer deferredRenderer = VeilRenderSystem.renderer().getDeferredRenderer();
        boolean backupEnabled = deferredRenderer.isEnabled();
        if (backupEnabled) {
            deferredRenderer.disable();
        }

        BACKUP_PROJECTION.set(RenderSystem.getProjectionMatrix());
        gameRenderer.loadProjectionMatrix(TRANSFORM.set(projection));
        BACKUP_LIGHT0_POSITION.set(VeilRenderSystem.getLight0Position());
        BACKUP_LIGHT1_POSITION.set(VeilRenderSystem.getLight1Position());

        HitResult backupHitResult = minecraft.crosshairTarget;
        Entity backupCrosshairPickEntity = minecraft.targetedEntity;

        renderingPerspective = true;
        framebuffer.bindDraw(true);
        renderTargetExtension.veil$setWrapper(framebuffer);
        levelRenderer.setupFrustum(poseStack, new Vec3d(cameraPosition.x(), cameraPosition.y(), cameraPosition.z()), TRANSFORM);
        levelRenderer.render(poseStack, partialTicks, time, false, CAMERA, gameRenderer, gameRenderer.getLightmapTextureManager(), TRANSFORM);
        //levelRenderer.drawEntityOutlinesFramebuffer();
        renderTargetExtension.veil$setWrapper(null);
        AdvancedFbo.unbind();
        renderingPerspective = false;

        minecraft.targetedEntity = backupCrosshairPickEntity;
        minecraft.crosshairTarget = backupHitResult;

        RenderSystem.setShaderLights(BACKUP_LIGHT0_POSITION, BACKUP_LIGHT1_POSITION);
        gameRenderer.loadProjectionMatrix(BACKUP_PROJECTION);

        window.setFramebufferWidth(backupWidth);
        window.setFramebufferHeight(backupHeight);

        if (backupEnabled) {
            deferredRenderer.enable();
        }

        accessor.setRenderDistance(backupRenderDistance);
        RenderSystem.setInverseViewRotationMatrix(BACKUP_INVERSE_VIEW_ROTATION);


        renderer.getCameraMatrices().update(ProjMat, MViewMat, new Vector3f((float) cameraPosition.x(),(float) cameraPosition.y(),(float) cameraPosition.z()), nearPlane, farPlane);
        RenderSystem.projectionMatrix = ProjMat;
        RenderSystem.modelViewMatrix = MViewMat;
    }

    /**
     * @return Whether a perspective is being rendered
     */
    public static boolean isRenderingPerspective() {
        return renderingPerspective;
    }
}
