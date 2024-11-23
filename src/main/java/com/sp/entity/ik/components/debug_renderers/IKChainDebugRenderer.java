package com.sp.entity.ik.components.debug_renderers;


import com.sp.entity.ik.components.IKAnimatable;
import com.sp.entity.ik.components.IKChainComponent;
import com.sp.entity.ik.parts.ik_chains.IKChain;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

public class IKChainDebugRenderer<E extends IKAnimatable<E>, C extends IKChainComponent<? extends IKChain, E>> implements IKDebugRenderer<E, C> {
    @Override
    public void renderDebug(C component, E animatable, MatrixStack poseStack, RenderLayer renderType, VertexConsumerProvider bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        if (!(animatable instanceof Entity entity)) {
            return;
        }

        for (IKChain chain : component.getLimbs()) {
            Vec3d entityPos = entity.getPos();

            IKDebugRenderer.drawBox(poseStack, bufferSource, chain.getFirst().getPosition(), entity, 255, 255, 0, 127);

            for (int i = 0; i < chain.getJoints().size() - 1; i++) {
                Vec3d currentJoint = chain.getJoints().get(i);
                Vec3d nextJoint = chain.getJoints().get(i + 1);

                IKDebugRenderer.drawLineToBox(poseStack, bufferSource, entityPos, currentJoint, nextJoint, entity, 255, 165, 0, 127);
            }
        }
    }
}
