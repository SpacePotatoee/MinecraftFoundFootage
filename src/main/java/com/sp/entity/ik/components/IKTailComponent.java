package com.sp.entity.ik.components;

import com.sp.entity.ik.components.debug_renderers.IKTailDebugRenderer;
import com.sp.entity.ik.model.BoneAccessor;
import com.sp.entity.ik.model.ModelAccessor;
import com.sp.entity.ik.parts.Segment;
import com.sp.entity.ik.parts.WorldCollidingSegment;
import com.sp.entity.ik.parts.ik_chains.IKChain;
import com.sp.entity.ik.util.PrAnCommonClass;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.util.Objects;

public class IKTailComponent<C extends IKChain, E extends IKAnimatable<E>> extends IKChainComponent<C, E> {
    public Vec3d tailTarget = Vec3d.ZERO;

    public IKTailComponent(C limb) {
        this.limbs.add(limb);
    }

    @Override
    C setLimb(int index, Vec3d base, Entity entity) {
        for (Segment segment : this.limbs.get(index).segments) {
            if (segment instanceof WorldCollidingSegment worldCollidingSegment && worldCollidingSegment.getLevel() == null) {
                worldCollidingSegment.setLevel(entity.getWorld());
                worldCollidingSegment.move(entity.getPos(), false);
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

        if (Objects.equals(this.tailTarget, new Vec3d(0, 0, 0))) {
            if (model.getBone("tail1_base").isEmpty()) {
                return;
            }
            this.tailTarget = model.getBone("tail1_base").get().getPosition();
        }

        if (model.getBone("head").isEmpty()) {
            return;
        }
        Vec3d headPos = model.getBone("head").get().getPosition();

        if (model.getBone("center_of_mass").isEmpty()) {
            return;
        }
        Vec3d centerDirection = model.getBone("center_of_mass").get().getPosition().subtract(headPos).normalize();

        if (model.getBone("tail1_base").isEmpty()) {
            return;
        }
        Vec3d tailStart = model.getBone("tail1_base").get().getPosition();

        this.tailTarget = this.getMovedTailPos(tailStart.add(centerDirection.multiply(this.getLimb().getMaxLength())), entity);

        this.setLimb(0, tailStart, entity);

        for (int i = 0; i < this.limbs.get(0).getJoints().size() - 1; i++) {
            Segment currentSegment = this.getLimb().segments.get(i);

            if (model.getBone("tail" + 1 + "_seg" + (i + 1)).isEmpty()) {
                return;
            }
            BoneAccessor bone = model.getBone("tail" + 1 + "_seg" + (i + 1)).get();

            Vec3d endPos = this.getLimb().getJoints().get(i + 1);

            if (PrAnCommonClass.shouldRenderDebugLegs) {
                bone.moveTo(currentSegment.getPosition().subtract(0, 200, 0), endPos.subtract(0, 200, 0), entity);
                continue;
            }

            bone.moveTo(currentSegment.getPosition(), endPos, entity);
        }
    }

    private Vec3d getMovedTailPos(Vec3d newPos, Entity entity) {
        Vec3d collisionPoint = entity.getWorld().raycast(new RaycastContext(
                this.tailTarget,
                newPos,
                RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE,
                new ArrowEntity(entity.getWorld(), newPos.x, newPos.y, newPos.z)
        )).getPos();

        if (collisionPoint != newPos) {
            collisionPoint = this.tailTarget;
        }
        return collisionPoint;
    }

    private C getLimb() {
        return this.limbs.get(0);
    }

    @Override
    public void renderDebug(MatrixStack poseStack, E animatable, RenderLayer renderType, VertexConsumerProvider bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        new IKTailDebugRenderer<E>().renderDebug(this, animatable, poseStack, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
    }
}
