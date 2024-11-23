package com.sp.block.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.sp.ConfigStuff;
import com.sp.init.RenderLayers;
import com.sp.render.physics.PhysicsPoint;
import com.sp.render.physics.PhysicsStick;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

public class PoolroomsWindowBlockEntityRenderer implements BlockEntityRenderer<BlockEntity> {
    private PhysicsPoint pointB;

    public PoolroomsWindowBlockEntityRenderer(BlockEntityRendererFactory.Context context){

    }


    @Override
    public void render(BlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        this.renderCube(entity, matrix4f, vertexConsumers.getBuffer(this.getLayer()));
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getDebugLineStrip(20.0));
        Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();

        Vec3d pointA = new Vec3d(0, 0, 0);
        Vec3d pointB = new Vec3d(0, -1, 0);

        if(camera != null) {
            Vec3d cameraPos = camera.getPos();
            vertexConsumer.vertex(matrix4f, (float) (pointA.x - cameraPos.x), (float) (pointA.y - cameraPos.y), (float) (pointA.z - cameraPos.z)).color(0, 0, 0, 0.0f).next();
            vertexConsumer.vertex(matrix4f, (float) (pointA.x - cameraPos.x), (float) (pointA.y - cameraPos.y), (float) (pointA.z - cameraPos.z)).color(0, 0, 0, 1.0f).next();
            vertexConsumer.vertex(matrix4f, (float) (pointB.x - cameraPos.x), (float) (pointB.y - cameraPos.y), (float) (pointB.z - cameraPos.z)).color(1, 0, 0, 1.0f).next();
            vertexConsumer.vertex(matrix4f, (float) (pointB.x - cameraPos.x), (float) (pointB.y - cameraPos.y), (float) (pointB.z - cameraPos.z)).color(1, 0, 0, 0.0f).next();
        }

    }

    private void renderCube(BlockEntity entity, Matrix4f matrix, VertexConsumer buffer) {
        renderFace(entity, matrix, buffer, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, Direction.SOUTH);
        renderFace(entity, matrix, buffer, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, Direction.NORTH);
        renderFace(entity, matrix, buffer, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, Direction.EAST);
        renderFace(entity, matrix, buffer, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, Direction.WEST);
        renderFace(entity, matrix, buffer, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, Direction.DOWN);
        renderFace(entity, matrix, buffer, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, Direction.UP);
    }

    private void renderFace(BlockEntity entity, Matrix4f matrix, VertexConsumer buffer, float f, float g, float h, float i, float j, float k, float l, float m, Direction direction) {
            buffer.vertex(matrix, f, h, j).next();
            buffer.vertex(matrix, g, h, k).next();
            buffer.vertex(matrix, g, i, l).next();
            buffer.vertex(matrix, f, i, m).next();
    }

    protected RenderLayer getLayer() {
        return RenderLayers.POOLROOMS_WINDOW;
    }

    @Override
    public int getRenderDistance() {
        return (int) ConfigStuff.lightRenderDistance;
    }
}
