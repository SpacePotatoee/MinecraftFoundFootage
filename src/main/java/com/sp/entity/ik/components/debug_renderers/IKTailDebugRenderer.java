package com.sp.entity.ik.components.debug_renderers;

import com.sp.entity.ik.components.IKAnimatable;
import com.sp.entity.ik.components.IKTailComponent;
import com.sp.entity.ik.parts.ik_chains.IKChain;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

public class IKTailDebugRenderer<E extends IKAnimatable<E>> extends IKChainDebugRenderer<E, IKTailComponent<? extends IKChain, E>> {
    @Override
    public void renderDebug(IKTailComponent<? extends IKChain, E> component, E animatable, MatrixStack poseStack, RenderLayer renderType, VertexConsumerProvider bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        super.renderDebug(component, animatable, poseStack, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);

        if (!(animatable instanceof Entity entity)) {
            return;
        }

        IKDebugRenderer.drawBox(poseStack, bufferSource, component.tailTarget, entity, 0, 255, 0, 255);
    }
}
