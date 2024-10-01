package com.sp.mixin;

import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(WorldRenderer.class)
public interface WorldRendererAccessor {

    @Invoker("renderLayer")
    void invokeRenderLayer(RenderLayer renderLayer, MatrixStack matrices, double cameraX, double cameraY, double cameraZ, Matrix4f positionMatrix);

    @Invoker("setupTerrain")
    void invokeSetupTerrain(Camera camera, Frustum frustum, boolean hasForcedFrustum, boolean spectator);

    @Invoker("setupFrustum")
    void invokeSetupFrustum(MatrixStack matrices, Vec3d pos, Matrix4f projectionMatrix);

    @Accessor("frustum")
    void setFrustum(Frustum frustum);

    @Accessor("frustum")
    Frustum getFrustum();
}
