package com.sp.entity.ik.components.debug_renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.dumbcode.projectnublar.entity.ik.components.IKAnimatable;
import net.dumbcode.projectnublar.entity.ik.components.IKTailComponent;
import net.dumbcode.projectnublar.entity.ik.parts.ik_chains.IKChain;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.Entity;

public class IKTailDebugRenderer<E extends IKAnimatable<E>> extends IKChainDebugRenderer<E, IKTailComponent<? extends IKChain, E>> {
    @Override
    public void renderDebug(IKTailComponent<? extends IKChain, E> component, E animatable, PoseStack poseStack, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        super.renderDebug(component, animatable, poseStack, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);

        if (!(animatable instanceof Entity entity)) {
            return;
        }

        IKDebugRenderer.drawBox(poseStack, bufferSource, component.tailTarget, entity, 0, 255, 0, 255);
    }
}
