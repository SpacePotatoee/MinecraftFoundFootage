package com.sp.entity.ik.components;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.dumbcode.projectnublar.Constants;
import net.dumbcode.projectnublar.entity.ik.components.debug_renderers.IKTailDebugRenderer;
import net.dumbcode.projectnublar.entity.ik.model.BoneAccessor;
import net.dumbcode.projectnublar.entity.ik.model.ModelAccessor;
import net.dumbcode.projectnublar.entity.ik.parts.Segment;
import net.dumbcode.projectnublar.entity.ik.parts.WorldCollidingSegment;
import net.dumbcode.projectnublar.entity.ik.parts.ik_chains.IKChain;
import net.dumbcode.projectnublar.entity.ik.util.PrAnCommonClass;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.Vec3;

import java.util.Objects;

public class IKTailComponent<C extends IKChain, E extends IKAnimatable<E>> extends IKChainComponent<C, E> {
    public Vec3 tailTarget = Vec3.ZERO;

    public IKTailComponent(C limb) {
        this.limbs.add(limb);
    }

    @Override
    C setLimb(int index, Vec3 base, Entity entity) {
        for (Segment segment : this.limbs.get(index).segments) {
            if (segment instanceof WorldCollidingSegment worldCollidingSegment && worldCollidingSegment.getLevel() == null) {
                worldCollidingSegment.setLevel(entity.level());
                worldCollidingSegment.move(entity.position(), false);
            }
        }

        this.limbs.get(index).solve(this.tailTarget, base);

        return this.limbs.get(index);
    }

    @Override
    public void tickServer(E animatable) {

    }

    @Override
    public void tickClient(E animatable, ModelAccessor model) {
        if (!(animatable instanceof Entity entity)) {
            return;
        }

        if (Objects.equals(this.tailTarget, new Vec3(0, 0, 0))) {
            if (model.getBone("tail1_base").isEmpty()) {
                return;
            }
            this.tailTarget = model.getBone("tail1_base").get().getPosition();
        }

        if (model.getBone("head").isEmpty()) {
            return;
        }
        Vec3 headPos = model.getBone("head").get().getPosition();

        if (model.getBone("center_of_mass").isEmpty()) {
            return;
        }
        Vec3 centerDirection = model.getBone("center_of_mass").get().getPosition().subtract(headPos).normalize();

        if (model.getBone("tail1_base").isEmpty()) {
            return;
        }
        Vec3 tailStart = model.getBone("tail1_base").get().getPosition();

        this.tailTarget = this.getMovedTailPos(tailStart.add(centerDirection.scale(this.getLimb().getMaxLength())), entity);

        this.setLimb(0, tailStart, entity);

        for (int i = 0; i < this.limbs.get(0).getJoints().size() - 1; i++) {
            Segment currentSegment = this.getLimb().segments.get(i);

            if (model.getBone("tail" + 1 + "_seg" + (i + 1)).isEmpty()) {
                return;
            }
            BoneAccessor bone = model.getBone("tail" + 1 + "_seg" + (i + 1)).get();

            Vec3 endPos = this.getLimb().getJoints().get(i + 1);

            if (PrAnCommonClass.shouldRenderDebugLegs) {
                bone.moveTo(currentSegment.getPosition().subtract(0, 200, 0), endPos.subtract(0, 200, 0), entity);
                continue;
            }

            bone.moveTo(currentSegment.getPosition(), endPos, entity);
        }
    }

    private Vec3 getMovedTailPos(Vec3 newPos, Entity entity) {
        Vec3 collisionPoint = entity.level().clip(new ClipContext(
                this.tailTarget,
                newPos,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                new Arrow(entity.level(), newPos.x(), newPos.y(), newPos.z())
        )).getLocation();

        if (collisionPoint != newPos) {
            collisionPoint = this.tailTarget;
        }
        return collisionPoint;
    }

    private C getLimb() {
        return this.limbs.get(0);
    }

    @Override
    public void renderDebug(PoseStack poseStack, E animatable, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        new IKTailDebugRenderer<E>().renderDebug(this, animatable, poseStack, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
    }
}
