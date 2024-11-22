package com.sp.entity.ik.components.debug_renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.dumbcode.projectnublar.entity.ik.components.IKAnimatable;
import net.dumbcode.projectnublar.entity.ik.components.IKLegComponent;
import net.dumbcode.projectnublar.entity.ik.parts.Segment;
import net.dumbcode.projectnublar.entity.ik.parts.ik_chains.EntityLeg;
import net.dumbcode.projectnublar.entity.ik.parts.ik_chains.EntityLegWithFoot;
import net.dumbcode.projectnublar.entity.ik.parts.ik_chains.IKChain;
import net.dumbcode.projectnublar.entity.ik.parts.sever_limbs.ServerLimb;
import net.dumbcode.projectnublar.entity.ik.util.MathUtil;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class LegDebugRenderer<E extends IKAnimatable<E>, C extends EntityLeg> extends IKChainDebugRenderer<E, IKLegComponent<C, E>> {
    private static <C extends IKChain> void drawAngleConstraintsForBase(Segment currentSegment, C chain, Entity entity, PoseStack matrices, MultiBufferSource vertexConsumers) {
        Vec3 entityPos = entity.position();

        Vec3 C = chain.getFirst().getPosition().subtract(0, 1, 0);

        double angle = Math.toDegrees(MathUtil.calculateAngle(chain.getFirst().getPosition(), chain.segments.get(1).getPosition(), C));
        double angleDelta = chain.getFirst().angleSize - angle;

        Vec3 normal = MathUtil.getClosestNormalRelativeToEntity(chain.segments.get(1).getPosition(), chain.getFirst().getPosition(), chain.segments.get(2).getPosition(), entity);
        Vec3 newPos = MathUtil.rotatePointOnAPlaneAround(chain.segments.get(1).getPosition(), chain.getFirst().getPosition(), angleDelta, normal);

        /*
        Vec3 otherNewPos = MathUtil.rotatePointOnAPlaneAround(chain.segments.get(1).getPosition(), chain.getFirst().getPosition(), (angleDelta - (chain.getFirst().angleSize * 2)), normal);
        Vec3 middlePos = MathUtil.rotatePointOnAPlaneAround(chain.segments.get(1).getPosition(), chain.getFirst().getPosition(), (angleDelta - chain.getFirst().angleSize), normal);
        */

        /*
        IKDebugRenderer.drawLine(matrices, vertexConsumers, entityPos, currentSegment.getPosition(), newPos, 255, 0, 0, 127);
        IKDebugRenderer.drawLine(matrices, vertexConsumers, entityPos, currentSegment.getPosition(), middlePos, 180, 180, 180, 127);
        IKDebugRenderer.drawLine(matrices, vertexConsumers, entityPos, currentSegment.getPosition(), otherNewPos, 0, 255, 0, 127);

         */
    }

    @Override
    public void renderDebug(IKLegComponent<C, E> component, E animatable, PoseStack poseStack, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        super.renderDebug(component, animatable, poseStack, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);

        for (C limb : component.getLimbs()) {
            if (!(animatable instanceof Entity entity)) {
                return;
            }

            Vec3 entityPos = entity.position();

            renderLeg(poseStack, bufferSource, limb, entity);

            for (ServerLimb endPoint : component.getEndPoints()) {

                Vec3 limbOffset = endPoint.baseOffset.scale(component.getScale());

                if (component.getStillStandCounter() != component.getSettings().standStillCounter()) {
                    limbOffset = limbOffset.add(0, 0, component.getSettings().stepInFront() * component.getScale());
                }

                limbOffset = limbOffset.yRot((float) Math.toRadians(-entity.getYRot()));

                Vec3 rotatedLimbOffset = limbOffset.add(entity.position());

                BlockHitResult rayCastResult = IKLegComponent.rayCastToGround(rotatedLimbOffset, entity, ClipContext.Fluid.NONE);

                Vec3 rayCastHitPos = rayCastResult.getLocation();

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

    private void renderLeg(PoseStack poseStack, MultiBufferSource bufferSource, C chain, Entity entity) {
        Vec3 entityPos = entity.position();

        drawAngleConstraintsForBase(chain.getFirst(), chain, entity, poseStack, bufferSource);
        for (int i = 0; i < chain.getJoints().size() - 1; i++) {
            if (i > 0) {
                this.drawAngleConstraints(i, chain, entity, poseStack, bufferSource);
                continue;
            }
            this.drawAngleConstraintsForBase(chain, entity, poseStack, bufferSource);
        }

        if (chain instanceof EntityLegWithFoot entityLegWithFoot) {
            Vec3 footPos = entityLegWithFoot.foot.getPosition();
            IKDebugRenderer.drawLineToBox(poseStack, bufferSource, entityPos, chain.endJoint, footPos, entity, 255, 165, 0, 127);

            Vec3 angleConstraint = entityLegWithFoot.getFootPosition(entityLegWithFoot.foot.angleSize);

            Vec3 referencePoint = entityLegWithFoot.getFootPosition(0);

            IKDebugRenderer.drawLine(poseStack, bufferSource, entityPos, chain.endJoint, angleConstraint, 255, 0, 0, 127);
            IKDebugRenderer.drawLine(poseStack, bufferSource, entityPos, chain.endJoint, referencePoint, 0, 255, 0, 127);
        }
    }

    private void drawAngleConstraintsForBase(C chain, Entity entity, PoseStack matrices, MultiBufferSource vertexConsumers) {
        Vec3 entityPos = entity.position();

        Vec3 base = chain.getFirst().getPosition();

        Vec3 referencePoint = chain.rotatePointOnLegPlane(base.add(chain.getDownNormalOnLegPlane()), base, chain.getFirst().angleOffset);

        Vec3 dotBaseDir = referencePoint.subtract(base).normalize();
        Vec3 dotTargetDir = chain.get(1).getPosition().subtract(base).normalize();

        double angle = Math.toDegrees(Math.acos(dotBaseDir.dot(dotTargetDir)));

        double angleDifference = chain.getFirst().angleSize - angle;

        Vec3 rotatedPos = MathUtil.rotatePointOnAPlaneAround(chain.getFirst().getPosition().add(chain.getDownNormalOnLegPlane()), chain.getFirst().getPosition(), chain.getFirst().angleSize, chain.getLegPlane());
        Vec3 rotatedPos2 = MathUtil.rotatePointOnAPlaneAround(chain.getFirst().getPosition().add(chain.getDownNormalOnLegPlane()), chain.getFirst().getPosition(), -chain.getFirst().angleSize, chain.getLegPlane());
        Vec3 newPos = MathUtil.rotatePointOnAPlaneAround(chain.get(1).getPosition(), chain.getFirst().getPosition(), angleDifference, chain.getLegPlane());

        IKDebugRenderer.drawLine(matrices, vertexConsumers, entityPos, chain.getFirst().getPosition(), rotatedPos, 255, 0, 0, 127);
        IKDebugRenderer.drawLine(matrices, vertexConsumers, entityPos, chain.getFirst().getPosition(), rotatedPos2, 0, 255, 0, 127);
        IKDebugRenderer.drawLine(matrices, vertexConsumers, entityPos, chain.getFirst().getPosition(), newPos, 0, 0, 255, 127);

        IKDebugRenderer.drawLine(matrices, vertexConsumers, entityPos, chain.getFirst().getPosition(), chain.getFirst().getPosition().add(chain.getDownNormalOnLegPlane()), 180, 180, 180, 127);
        IKDebugRenderer.drawLine(matrices, vertexConsumers, entityPos, chain.getFirst().getPosition(), chain.getFirst().getPosition().add(chain.getLegPlane()), 12, 12, 12, 127);
    }

    private void drawAngleConstraints(int i, C chain, Entity entity, PoseStack matrices, MultiBufferSource vertexConsumers) {
        Vec3 entityPos = entity.position();

        Segment currentSegment = chain.get(i);

        List<Vec3> positions = this.getConstrainedPositions(chain.get(i - 1).getPosition(), currentSegment, chain.getJoints().get(i + 1), chain);

        IKDebugRenderer.drawLine(matrices, vertexConsumers, entityPos, currentSegment.getPosition(), positions.get(0), 255, 0, 0, 127);
        IKDebugRenderer.drawLine(matrices, vertexConsumers, entityPos, currentSegment.getPosition(), positions.get(1), 180, 180, 180, 127);
        IKDebugRenderer.drawLine(matrices, vertexConsumers, entityPos, currentSegment.getPosition(), positions.get(2), 0, 255, 0, 127);
    }

    private List<Vec3> getConstrainedPositions(Vec3 reference, Segment middle, Vec3 endpoint, C chain) {
        //Vec3 normal = MathUtil.getClosestNormalRelativeToEntity(endpoint, middle.getPosition(), reference, entity);

        Vec3 normal = chain.getLegPlane();

        Vec3 referencePoint = MathUtil.rotatePointOnAPlaneAround(reference, middle.getPosition(), middle.angleOffset, normal);

        double angle = Math.toDegrees(MathUtil.calculateAngle(middle.getPosition(), endpoint, referencePoint));
        double angleDelta = middle.angleSize - angle;

        Vec3 newPos = MathUtil.rotatePointOnAPlaneAround(endpoint, middle.getPosition(), angleDelta, normal);
        Vec3 otherNewPos = MathUtil.rotatePointOnAPlaneAround(endpoint, middle.getPosition(), (angleDelta - (middle.angleSize * 2)), normal);
        Vec3 middlePos = MathUtil.rotatePointOnAPlaneAround(endpoint, middle.getPosition(), (angleDelta - middle.angleSize), normal);

        return List.of(newPos, middlePos, otherNewPos);
    }
}
