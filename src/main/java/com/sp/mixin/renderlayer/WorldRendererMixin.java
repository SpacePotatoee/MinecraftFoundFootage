package com.sp.mixin.renderlayer;

import com.sp.init.RenderLayers;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {

    @Shadow protected abstract void renderLayer(RenderLayer renderLayer, MatrixStack matrices, double cameraX, double cameraY, double cameraZ, Matrix4f positionMatrix);

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;renderLayer(Lnet/minecraft/client/render/RenderLayer;Lnet/minecraft/client/util/math/MatrixStack;DDDLorg/joml/Matrix4f;)V", ordinal = 0))
    private void renderRenderLayer(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f projectionMatrix, CallbackInfo ci){
        Vec3d cameraPos = camera.getPos();
        this.renderLayer(RenderLayers.getCeilingTile(), matrices, cameraPos.getX(), cameraPos.getY(), cameraPos.getZ(), projectionMatrix);
        this.renderLayer(RenderLayers.getCarpet(), matrices, cameraPos.getX(), cameraPos.getY(), cameraPos.getZ(), projectionMatrix);
        this.renderLayer(RenderLayers.getConcreteLayer(), matrices, cameraPos.getX(), cameraPos.getY(), cameraPos.getZ(), projectionMatrix);
        this.renderLayer(RenderLayers.getBricksLayer(), matrices, cameraPos.getX(), cameraPos.getY(), cameraPos.getZ(), projectionMatrix);
        this.renderLayer(RenderLayers.getChainFence(), matrices, cameraPos.getX(), cameraPos.getY(), cameraPos.getZ(), projectionMatrix);
        this.renderLayer(RenderLayers.getWoodenCrateLayer(), matrices, cameraPos.getX(), cameraPos.getY(), cameraPos.getZ(), projectionMatrix);
        this.renderLayer(RenderLayers.getPoolroomsSky(), matrices, cameraPos.getX(), cameraPos.getY(), cameraPos.getZ(), projectionMatrix);
    }

}
