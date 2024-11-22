package com.sp.entity.ik.components;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.dumbcode.projectnublar.entity.ik.model.EntityAccessor;
import net.dumbcode.projectnublar.entity.ik.model.ModelAccessor;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;

import java.util.List;

public interface IKAnimatable<E extends IKAnimatable<E>> {
    List<IKModelComponent<E>> getComponents();

    double getSize();

    default boolean containsComponent(Class type) {
        return !this.getComponents().stream().filter(eikModelComponent -> eikModelComponent.getClass() == type).toList().isEmpty();
    }

    default List<? extends IKModelComponent<E>> getComponentOfType(Class type) {
        return this.getComponents().stream().filter(eikModelComponent -> eikModelComponent.getClass() == type).toList();
    }

    default void addComponent(IKModelComponent<E> component) {
        this.getComponents().add(component);
    }

    default void tickComponentsClient(E animatable, ModelAccessor model) {
        this.getComponents().forEach(ikModelComponent -> ikModelComponent.tickClient(animatable, model));
    }

    default void tickComponentsServer(E animatable) {
        this.getComponents().forEach(ikModelComponent -> ikModelComponent.tickServer(animatable));
    }

    default void renderDebug(PoseStack poseStack, E animatable, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        this.getComponents().forEach(eikModelComponent -> eikModelComponent.renderDebug(poseStack, animatable, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay));
    }
}
