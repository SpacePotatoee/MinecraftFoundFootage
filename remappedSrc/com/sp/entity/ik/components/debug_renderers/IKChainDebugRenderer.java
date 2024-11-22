package com.sp.entity.ik.components.debug_renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.dumbcode.projectnublar.entity.ik.components.IKAnimatable;
import net.dumbcode.projectnublar.entity.ik.components.IKChainComponent;
import net.dumbcode.projectnublar.entity.ik.parts.ik_chains.IKChain;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class IKChainDebugRenderer<E extends IKAnimatable<E>, C extends IKChainComponent<? extends IKChain, E>> implements IKDebugRenderer<E, C> {
    @Override
    public void renderDebug(C component, E animatable, PoseStack poseStack, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        if (!(animatable instanceof Entity entity)) {
            return;
        }

        for (IKChain chain : component.getLimbs()) {
            Vec3 entityPos = entity.position();

            IKDebugRenderer.drawBox(poseStack, bufferSource, chain.getFirst().getPosition(), entity, 255, 255, 0, 127);

            for (int i = 0; i < chain.getJoints().size() - 1; i++) {
                Vec3 currentJoint = chain.getJoints().get(i);
                Vec3 nextJoint = chain.getJoints().get(i + 1);

                IKDebugRenderer.drawLineToBox(poseStack, bufferSource, entityPos, currentJoint, nextJoint, entity, 255, 165, 0, 127);
            }
        }
    }
}
