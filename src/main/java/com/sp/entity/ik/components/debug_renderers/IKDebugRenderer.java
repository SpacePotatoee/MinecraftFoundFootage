package com.sp.entity.ik.components.debug_renderers;

import com.sp.entity.ik.components.IKAnimatable;
import com.sp.entity.ik.components.IKModelComponent;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public interface IKDebugRenderer<E extends IKAnimatable<E>, C extends IKModelComponent<E>> {
    static void drawLineToBox(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Vec3d camera, Vec3d startPos, Vec3d targetPos, Entity entity, int red, int green, int blue, int alpha) {
        drawBox(matrices, vertexConsumers, targetPos, entity, red, green, blue, alpha);
        drawLine(matrices, vertexConsumers, camera, startPos, targetPos, red, green, blue, alpha);
    }

    static void drawBox(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Vec3d targetPos, Entity entity, int red, int green, int blue, int alpha) {
        Vec3d offsetEntityPos = entity.getPos().add(0.5, 0.5, 0.5);
        DebugRenderer.drawBox(matrices, vertexConsumers, Box.from(targetPos).contract(0.65, 0.65, 0.65).offset(-offsetEntityPos.x, -offsetEntityPos.y, -offsetEntityPos.z), (float) red / 255, (float) green / 255, (float) blue / 255, (float) alpha / 255);
    }

    static void drawLine(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Vec3d camera, Vec3d startPos, Vec3d targetPos, int red, int green, int blue, int alpha) {
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getDebugLineStrip(1.0));
        vertexConsumer.vertex(matrices.peek().getPositionMatrix(), (float) (startPos.x - camera.x), (float) (startPos.y - camera.y), (float) (startPos.z - camera.z)).color(getArgb(alpha, red, green, blue)).next();
        vertexConsumer.vertex(matrices.peek().getPositionMatrix(), (float) (targetPos.x - camera.x), (float) (targetPos.y - camera.y), (float) (targetPos.z - camera.z)).color(getArgb(alpha, red, green, blue)).next();
    }

    static int getArgb(int alpha, int red, int green, int blue) {
        return alpha << 24 | red << 16 | green << 8 | blue;
    }

    void renderDebug(C component, E animatable, MatrixStack poseStack, RenderLayer renderType, VertexConsumerProvider bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay);
}
