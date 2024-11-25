package com.sp.entity.ik.components.debug_renderers;

import com.sp.entity.ik.components.IKAnimatable;
import com.sp.entity.ik.components.IKLegComponent;
import com.sp.entity.ik.parts.Segment;
import com.sp.entity.ik.parts.ik_chains.EntityLeg;
import com.sp.entity.ik.parts.ik_chains.EntityLegWithFoot;
import com.sp.entity.ik.parts.ik_chains.IKChain;
import com.sp.entity.ik.parts.sever_limbs.ServerLimb;
import com.sp.entity.ik.util.MathUtil;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.util.List;

public class LegDebugRenderer<E extends IKAnimatable<E>, C extends IKChain> extends IKChainDebugRenderer<E, IKLegComponent<C, E>> {
    private static <C extends IKChain> void drawAngleConstraintsForBase(Segment currentSegment, C chain, Entity entity, MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
        Vec3d entityPos = entity.getPos();

        Vec3d C = chain.getFirst().getPosition().subtract(0, 1, 0);

        double angle = Math.toDegrees(MathUtil.calculateAngle(chain.getFirst().getPosition(), chain.segments.get(1).getPosition(), C));
        double angleDelta = chain.getFirst().angleSize - angle;

        Vec3d normal = MathUtil.getClosestNormalRelativeToEntity(chain.segments.get(1).getPosition(), chain.getFirst().getPosition(), chain.segments.get(2).getPosition(), entity);
        Vec3d newPos = MathUtil.rotatePointOnAPlaneAround(chain.segments.get(1).getPosition(), chain.getFirst().getPosition(), angleDelta, normal);

        /*
        Vec3d otherNewPos = MathUtil.rotatePointOnAPlaneAround(chain.segments.get(1).getPosition(), chain.getFirst().getPosition(), (angleDelta - (chain.getFirst().angleSize * 2)), normal);
        Vec3d middlePos = MathUtil.rotatePointOnAPlaneAround(chain.segments.get(1).getPosition(), chain.getFirst().getPosition(), (angleDelta - chain.getFirst().angleSize), normal);
        */

        /*
        IKDebugRenderer.drawLine(matrices, vertexConsumers, entityPos, currentSegment.getPosition(), newPos, 255, 0, 0, 127);
        IKDebugRenderer.drawLine(matrices, vertexConsumers, entityPos, currentSegment.getPosition(), middlePos, 180, 180, 180, 127);
        IKDebugRenderer.drawLine(matrices, vertexConsumers, entityPos, currentSegment.getPosition(), otherNewPos, 0, 255, 0, 127);

         */
    }

    @Override
    public void renderDebug(IKLegComponent<C, E> component, E animatable, MatrixStack poseStack, RenderLayer renderType, VertexConsumerProvider bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        super.renderDebug(component, animatable, poseStack, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);

        for (C limb : component.getLimbs()) {
            if (!(animatable instanceof Entity entity)) {
                return;
            }

            Vec3d entityPos = entity.getPos();

            renderLeg(poseStack, bufferSource, limb, entity);

            for (ServerLimb endPoint : component.getEndPoints()) {

                Vec3d limbOffset = endPoint.baseOffset.multiply(component.getScale());

                if (component.getStillStandCounter() != component.getSettings().standStillCounter()) {
                    limbOffset = limbOffset.add(0, 0, component.getSettings().stepInFront() * component.getScale());
                }

                limbOffset = limbOffset.rotateY((float) Math.toRadians(-entity.getBodyYaw()));

                Vec3d rotatedLimbOffset = limbOffset.add(entity.getPos());

                BlockHitResult rayCastResult = IKLegComponent.rayCastToGround(rotatedLimbOffset, entity, RaycastContext.FluidHandling.NONE);

                Vec3d rayCastHitPos = rayCastResult.getPos();

                double distance = endPoint.target.distanceTo(rayCastHitPos);

                if (distance < 0.1) distance = 0;

                if (distance != 0) {
                    IKDebugRenderer.drawLine(poseStack, bufferSource, entityPos, endPoint.getPos(), endPoint.target, 255, 100, 255, 127);
                }

                IKDebugRenderer.drawBox(poseStack, bufferSource, endPoint.getPos(), entity, endPoint.isGrounded() ? 0 : 255, endPoint.isGrounded() ? 255 : 0, 0, 127);
                IKDebugRenderer.drawBox(poseStack, bufferSource, endPoint.oldTarget, entity, 0, 255, 255, 127);
                IKDebugRenderer.drawBox(poseStack, bufferSource, rayCastHitPos, entity, 0, 0, 255, 127);
            }
        }
    }

    private void renderLeg(MatrixStack poseStack, VertexConsumerProvider bufferSource, C chain, Entity entity) {
        Vec3d entityPos = entity.getPos();

        drawAngleConstraintsForBase(chain.getFirst(), chain, entity, poseStack, bufferSource);
        for (int i = 0; i < chain.getJoints().size() - 1; i++) {
            if (i > 0) {
                this.drawAngleConstraints(i, chain, entity, poseStack, bufferSource);
                continue;
            }
            this.drawAngleConstraintsForBase(chain, entity, poseStack, bufferSource);
        }

        if (chain instanceof EntityLegWithFoot entityLegWithFoot) {
            Vec3d footPos = entityLegWithFoot.foot.getPosition();
            IKDebugRenderer.drawLineToBox(poseStack, bufferSource, entityPos, chain.endJoint, footPos, entity, 255, 165, 0, 127);

            Vec3d angleConstraint = entityLegWithFoot.getFootPosition(entityLegWithFoot.foot.angleSize);

            Vec3d referencePoint = entityLegWithFoot.getFootPosition(0);

            IKDebugRenderer.drawLine(poseStack, bufferSource, entityPos, chain.endJoint, angleConstraint, 255, 0, 0, 127);
            IKDebugRenderer.drawLine(poseStack, bufferSource, entityPos, chain.endJoint, referencePoint, 0, 255, 0, 127);
        }
    }

    private void drawAngleConstraintsForBase(C chain, Entity entity, MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
        if (!(chain instanceof EntityLeg entityLeg)) {
            return;
        }
        Vec3d entityPos = entity.getPos();

        Vec3d base = entityLeg.getFirst().getPosition();

        Vec3d referencePoint = entityLeg.rotatePointOnLegPlane(base.add(entityLeg.getDownNormalOnLegPlane()), base, chain.getFirst().angleOffset);

        Vec3d dotBaseDir = referencePoint.subtract(base).normalize();
        Vec3d dotTargetDir = chain.get(1).getPosition().subtract(base).normalize();

        double angle = Math.toDegrees(Math.acos(dotBaseDir.dotProduct(dotTargetDir)));

        double angleDifference = chain.getFirst().angleSize - angle;

        Vec3d rotatedPos = MathUtil.rotatePointOnAPlaneAround(chain.getFirst().getPosition().add(entityLeg.getDownNormalOnLegPlane()), chain.getFirst().getPosition(), chain.getFirst().angleSize, entityLeg.getLegPlane());
        Vec3d rotatedPos2 = MathUtil.rotatePointOnAPlaneAround(chain.getFirst().getPosition().add(entityLeg.getDownNormalOnLegPlane()), chain.getFirst().getPosition(), -chain.getFirst().angleSize, entityLeg.getLegPlane());
        Vec3d newPos = MathUtil.rotatePointOnAPlaneAround(chain.get(1).getPosition(), chain.getFirst().getPosition(), angleDifference, entityLeg.getLegPlane());

        IKDebugRenderer.drawLine(matrices, vertexConsumers, entityPos, chain.getFirst().getPosition(), rotatedPos, 255, 0, 0, 127);
        IKDebugRenderer.drawLine(matrices, vertexConsumers, entityPos, chain.getFirst().getPosition(), rotatedPos2, 0, 255, 0, 127);
        IKDebugRenderer.drawLine(matrices, vertexConsumers, entityPos, chain.getFirst().getPosition(), newPos, 0, 0, 255, 127);

        IKDebugRenderer.drawLine(matrices, vertexConsumers, entityPos, chain.getFirst().getPosition(), chain.getFirst().getPosition().add(entityLeg.getDownNormalOnLegPlane()), 180, 180, 180, 127);
        IKDebugRenderer.drawLine(matrices, vertexConsumers, entityPos, chain.getFirst().getPosition(), chain.getFirst().getPosition().add(entityLeg.getLegPlane()), 12, 12, 12, 127);
    }

    private void drawAngleConstraints(int i, C chain, Entity entity, MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
        if (!(chain instanceof EntityLeg entityLeg)) {
            return;
        }

        Vec3d entityPos = entity.getPos();

        Segment currentSegment = chain.get(i);

        List<Vec3d> positions = this.getConstrainedPositions(chain.get(i - 1).getPosition(), currentSegment, chain.getJoints().get(i + 1), entityLeg);

        IKDebugRenderer.drawLine(matrices, vertexConsumers, entityPos, currentSegment.getPosition(), positions.get(0), 255, 0, 0, 127);
        IKDebugRenderer.drawLine(matrices, vertexConsumers, entityPos, currentSegment.getPosition(), positions.get(1), 180, 180, 180, 127);
        IKDebugRenderer.drawLine(matrices, vertexConsumers, entityPos, currentSegment.getPosition(), positions.get(2), 0, 255, 0, 127);
    }

    private List<Vec3d> getConstrainedPositions(Vec3d reference, Segment middle, Vec3d endpoint, EntityLeg chain) {
        //Vec3d normal = MathUtil.getClosestNormalRelativeToEntity(endpoint, middle.getPosition(), reference, entity);

        Vec3d normal = chain.getLegPlane();

        Vec3d referencePoint = MathUtil.rotatePointOnAPlaneAround(reference, middle.getPosition(), middle.angleOffset, normal);

        double angle = Math.toDegrees(MathUtil.calculateAngle(middle.getPosition(), endpoint, referencePoint));
        double angleDelta = middle.angleSize - angle;

        Vec3d newPos = MathUtil.rotatePointOnAPlaneAround(endpoint, middle.getPosition(), angleDelta, normal);
        Vec3d otherNewPos = MathUtil.rotatePointOnAPlaneAround(endpoint, middle.getPosition(), (angleDelta - (middle.angleSize * 2)), normal);
        Vec3d middlePos = MathUtil.rotatePointOnAPlaneAround(endpoint, middle.getPosition(), (angleDelta - middle.angleSize), normal);

        return List.of(newPos, middlePos, otherNewPos);
    }
}
