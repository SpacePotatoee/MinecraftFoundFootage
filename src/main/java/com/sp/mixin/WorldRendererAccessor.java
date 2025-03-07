package com.sp.mixin;

import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
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

    @Invoker("renderEntity")
    void invokeRenderEntity(Entity entity, double cameraX, double cameraY, double cameraZ, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers);

    @Accessor("frustum")
    Frustum getFrustum();

    @Accessor("entityRenderDispatcher")
    EntityRenderDispatcher getEntityRenderDispatcher();

    @Accessor("frustum")
    void setFrustum(Frustum frustum);

    @Accessor("bufferBuilders")
    BufferBuilderStorage getBufferBuilders();
}
