package com.sp.entity.ik.components.debug_renderers;

import com.sp.entity.custom.WalkerEntity;
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
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.List;

public class WalkerLegDebugRenderer<E extends IKAnimatable<E>, C extends IKChain> extends IKChainDebugRenderer<E, IKLegComponent<C, E>> {
    @Override
    public void renderDebug(IKLegComponent<C, E> component, E animatable, MatrixStack poseStack, RenderLayer renderType, VertexConsumerProvider bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        super.renderDebug(component, animatable, poseStack, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
        if (!(animatable instanceof WalkerEntity entity)) {
            return;
        }


        double hitAmount = 0;

        Vec3d hitAverage = new Vec3d(0.0, 0.0, 0.0);

        List<Vec3d> testPositions = new ArrayList<>();



        testPositions.add(new Vec3d(1, 0, 0).normalize());
        testPositions.add(new Vec3d(-1, 0, 0).normalize());

        testPositions.add(new Vec3d(1, 1, 0).normalize());
        testPositions.add(new Vec3d(-1, -1, 0).normalize());
        testPositions.add(new Vec3d(1, -1, 0).normalize());
        testPositions.add(new Vec3d(-1, 1, 0).normalize());

        testPositions.add(new Vec3d(1, 1, 1).normalize());
        testPositions.add(new Vec3d(-1, -1, -1).normalize());
        testPositions.add(new Vec3d(1, 1, -1).normalize());
        testPositions.add(new Vec3d(-1, -1, 1).normalize());

        testPositions.add(new Vec3d(1, -1, -1).normalize());
        testPositions.add(new Vec3d(-1, 1, 1).normalize());
        testPositions.add(new Vec3d(1, -1, 1).normalize());
        testPositions.add(new Vec3d(-1, 1, -1).normalize());

        testPositions.add(new Vec3d(0, 1, 0).normalize());
        testPositions.add(new Vec3d(0, -1, 0).normalize());

        testPositions.add(new Vec3d(0, 1, 1).normalize());
        testPositions.add(new Vec3d(0, -1, -1).normalize());
        testPositions.add(new Vec3d(0, 1, -1).normalize());
        testPositions.add(new Vec3d(0, -1, 1).normalize());


        testPositions.add(new Vec3d(0, 0, 1).normalize());
        testPositions.add(new Vec3d(0, 0, -1).normalize());

        testPositions.add(new Vec3d(1, 0, 1).normalize());
        testPositions.add(new Vec3d(-1, 0, -1).normalize());
        testPositions.add(new Vec3d(-1, 0, 1).normalize());
        testPositions.add(new Vec3d(1, 0, -1).normalize());

        for (Vec3d testPosition : testPositions) {
            Vec3d targetPos = entity.getPos().add(testPosition.multiply(WalkerEntity.TILTING_TEST_RANGE));

            boolean hit = entity.getWorld().raycast(new RaycastContext(entity.getPos(), targetPos, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, entity)).getType() == HitResult.Type.BLOCK;

            if (hit) {
                hitAverage = hitAverage.add(testPosition);
                hitAmount++;
            }

            IKDebugRenderer.drawLine(poseStack, bufferSource, entity.getPos(), entity.getPos(), targetPos, hit ? 0 : 255, hit ? 255 : 0, 0, 127);
        }

        hitAverage = new Vec3d(hitAverage.x / hitAmount, hitAverage.y / hitAmount, hitAverage.z / hitAmount);

        Vec3d entityPos = entity.getPos();

        IKDebugRenderer.drawLine(poseStack, bufferSource, entityPos, entityPos, entity.getPos().add(entity.getRightDirection()), 255, 0, 0, 127);
        IKDebugRenderer.drawLine(poseStack, bufferSource, entityPos, entityPos, entity.getPos().add(entity.getRightDirection().negate()), 255, 0, 0, 127);

        IKDebugRenderer.drawLine(poseStack, bufferSource, entityPos, entityPos, entity.getPos().add(entity.getUpStationaryDirection()), 255, 0, 0, 127);
        IKDebugRenderer.drawLine(poseStack, bufferSource, entityPos, entityPos, entity.getPos().add(entity.getUpStationaryDirection().negate()), 255, 0, 0, 127);

        IKDebugRenderer.drawLine(poseStack, bufferSource, entityPos, entityPos, entity.getPos().add(entity.getRotationVector()), 255, 0, 0, 127);

        IKDebugRenderer.drawLine(poseStack, bufferSource, entityPos, entityPos, entity.getPos().add(hitAverage.normalize()), 0, 0, 255, 127);
        IKDebugRenderer.drawLine(poseStack, bufferSource, entityPos, entityPos, entity.getPos().add(entity.getUpDirection().normalize()), 0, 125, 125, 127);


        for (C limb : component.getLimbs()) {
            //renderLeg(poseStack, bufferSource, limb, entity);

            for (ServerLimb endPoint : component.getEndPoints()) {

                Vec3d limbOffset = Vec3d.ZERO;
                Vec3d limbOffsetMultiplier = endPoint.baseOffset.multiply(component.getScale());

                limbOffset = limbOffset.add(entity.getUpDirection().crossProduct(entity.getRotationVector()).multiply(limbOffsetMultiplier.x));

                limbOffset = limbOffset.add(entity.getUpDirection().multiply(limbOffsetMultiplier.y));

                limbOffset = limbOffset.add(entity.getRotationVector().multiply(limbOffsetMultiplier.z));

                if (component.hasMovedOverLastTick(entity)) {
                    limbOffset = limbOffset.add(0, 0, component.getSettings().get(0).stepInFront() * component.getScale());
                }

                Vec3d rotatedLimbOffset = limbOffset.add(entity.getPos());
                Vec3d upPoint = rotatedLimbOffset.add(entity.getUpDirection().multiply(1));
                HitResult baseRayCastResult = entity.getWorld().raycast(new RaycastContext(upPoint, rotatedLimbOffset.add(entity.getUpDirection().multiply(-10)), RaycastContext.ShapeType.COLLIDER, component.getSettings().get(0).fluid(), entity));

                Vec3d bestHit = baseRayCastResult.getPos();
                double bestDistance = baseRayCastResult.getType() == HitResult.Type.MISS ? Double.MAX_VALUE : 0.5;

                List<Vec3d> upDirs = new ArrayList<>();
                upDirs.add(MathUtil.toVec3(entity.upDirection.transform(new Vector3d(0, 1, -2).normalize())));
                upDirs.add(MathUtil.toVec3(entity.upDirection.transform(new Vector3d(0, -1, -2).normalize())));
                upDirs.add(MathUtil.toVec3(entity.upDirection.transform(new Vector3d(1, 0, -2).normalize())));
                upDirs.add(MathUtil.toVec3(entity.upDirection.transform(new Vector3d(-1, 0, -2).normalize())));
                upDirs.add(MathUtil.toVec3(entity.upDirection.transform(new Vector3d(-0.5, 0.5, -2).normalize())));
                upDirs.add(MathUtil.toVec3(entity.upDirection.transform(new Vector3d(0.5, 0.5, -2).normalize())));
                upDirs.add(MathUtil.toVec3(entity.upDirection.transform(new Vector3d(-0.5, -0.5, -2).normalize())));
                upDirs.add(MathUtil.toVec3(entity.upDirection.transform(new Vector3d(0.5, -0.5, -2).normalize())));

                for (Vec3d upDir : upDirs) {
                    BlockHitResult rayCastResult = entity.getWorld().raycast(new RaycastContext(upPoint, upPoint.add(upDir.multiply(-10)), RaycastContext.ShapeType.COLLIDER, component.getSettings().get(0).fluid(), entity));
                    IKDebugRenderer.drawLine(poseStack, bufferSource, entityPos, upPoint, rayCastResult.getPos(), 0, 0, 255, 127);

                    if (rayCastResult.getType() == BlockHitResult.Type.MISS) {
                        continue;
                    }

                    if (rayCastResult.getPos().squaredDistanceTo(baseRayCastResult.getPos()) < bestDistance) {
                        bestDistance = rayCastResult.getPos().squaredDistanceTo(baseRayCastResult.getPos());
                        bestHit = rayCastResult.getPos();
                    }
                }

                IKDebugRenderer.drawLine(poseStack, bufferSource, entityPos, upPoint, bestHit, 255, 0, 0, 127);

                double distance = endPoint.target.distanceTo(bestHit);

                if (distance < 0.1) distance = 0;

                if (distance != 0) {
                    IKDebugRenderer.drawLine(poseStack, bufferSource, entityPos, endPoint.getPos(), endPoint.target, 255, 100, 255, 127);
                }

                IKDebugRenderer.drawBox(poseStack, bufferSource, endPoint.getPos(), entity, endPoint.isGrounded() ? 0 : 255, endPoint.isGrounded() ? 255 : 0, 0, 127);
                IKDebugRenderer.drawBox(poseStack, bufferSource, endPoint.oldTarget, entity, 0, 255, 255, 127);
                IKDebugRenderer.drawBox(poseStack, bufferSource, bestHit, entity, 0, 0, 255, 127);
            }
        }
    }

    private void renderLeg(MatrixStack poseStack, VertexConsumerProvider bufferSource, C chain, Entity entity) {
        Vec3d entityPos = entity.getPos();

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
