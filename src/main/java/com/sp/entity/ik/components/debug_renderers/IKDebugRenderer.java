package com.sp.entity.ik.components.debug_renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.dumbcode.projectnublar.entity.ik.components.IKAnimatable;
import net.dumbcode.projectnublar.entity.ik.components.IKModelComponent;
import net.dumbcode.projectnublar.entity.ik.util.MathUtil;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public interface IKDebugRenderer<E extends IKAnimatable<E>, C extends IKModelComponent<E>> {
    static void drawLineToBox(PoseStack matrices, MultiBufferSource vertexConsumers, Vec3 camera, Vec3 startPos, Vec3 targetPos, Entity entity, int red, int green, int blue, int alpha) {
        drawBox(matrices, vertexConsumers, targetPos, entity, red, green, blue, alpha);
        drawLine(matrices, vertexConsumers, camera, startPos, targetPos, red, green, blue, alpha);
    }

    static void drawCone(PoseStack matrices, MultiBufferSource vertexConsumers, Vec3 camera, Vec3 baseCenter, Vec3 tip, int segments, int red, int green, int blue, int alpha) {
        double angle = 360 / (double) segments;
        Vec3 basePoint = MathUtil.rotatePointOnAPlaneAround(tip, baseCenter, angle, baseCenter.subtract(tip).normalize());

        IKDebugRenderer.drawLine(matrices, vertexConsumers, camera, tip, basePoint, red, green, blue, alpha);
        /*
        // Calculate each point on the base
        for (int i = 0; i < segments; i++) {
            // Compute positions on the base
            Vec3 basePoint1 = MathUtil.rotatePointOnAPlaneAround(tip, baseCenter, -angle, baseCenter.subtract(tip).normalize());
            Vec3 basePoint2 = MathUtil.rotatePointOnAPlaneAround(tip, baseCenter, angle, baseCenter.subtract(tip).normalize());

            // Draw line from each base point to the tip
            vertexConsumer.vertex(matrices.last().pose(), (float) (basePoint1.x - camera.x), (float) (basePoint1.y - camera.y), (float) (basePoint1.z - camera.z)).color(getArgb(alpha, red, green, blue)).endVertex();
            vertexConsumer.vertex(matrices.last().pose(), (float) (tip.x - camera.x), (float) (tip.y - camera.y), (float) (tip.z - camera.z)).color(getArgb(alpha, red, green, blue)).endVertex();

            // Optionally, draw base outline by connecting each segment point
            vertexConsumer.vertex(matrices.last().pose(), (float) (basePoint1.x - camera.x), (float) (basePoint1.y - camera.y), (float) (basePoint1.z - camera.z)).color(getArgb(alpha, red, green, blue)).endVertex();
            vertexConsumer.vertex(matrices.last().pose(), (float) (basePoint2.x - camera.x), (float) (basePoint2.y - camera.y), (float) (basePoint2.z - camera.z)).color(getArgb(alpha, red, green, blue)).endVertex();
        }
         */
    }

    static void drawBox(PoseStack matrices, MultiBufferSource vertexConsumers, Vec3 targetPos, Entity entity, int red, int green, int blue, int alpha) {
        Vec3 offsetEntityPos = entity.position().add(0.1, 0.1, 0.1);

        DebugRenderer.renderFilledBox(matrices, vertexConsumers, AABB.unitCubeFromLowerCorner(targetPos).contract(0.8, 0.8, 0.8).move(-offsetEntityPos.x, -offsetEntityPos.y, -offsetEntityPos.z), (float) red / 255, (float) green / 255, (float) blue / 255, (float) alpha / 255);
    }

    static void drawLine(PoseStack matrices, MultiBufferSource vertexConsumers, Vec3 camera, Vec3 startPos, Vec3 targetPos, int red, int green, int blue, int alpha) {
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderType.debugLineStrip(1.0));
        vertexConsumer.vertex(matrices.last().pose(), (float) (startPos.x - camera.x), (float) (startPos.y - camera.y), (float) (startPos.z - camera.z)).color(getArgb(alpha, red, green, blue)).endVertex();
        vertexConsumer.vertex(matrices.last().pose(), (float) (targetPos.x - camera.x), (float) (targetPos.y - camera.y), (float) (targetPos.z - camera.z)).color(getArgb(alpha, red, green, blue)).endVertex();
    }

    static int getArgb(int alpha, int red, int green, int blue) {
        return alpha << 24 | red << 16 | green << 8 | blue;
    }

    void renderDebug(C component, E animatable, PoseStack poseStack, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay);
}
