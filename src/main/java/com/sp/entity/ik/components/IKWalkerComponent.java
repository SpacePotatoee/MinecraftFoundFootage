package com.sp.entity.ik.components;

import com.sp.entity.custom.WalkerEntity;
import com.sp.entity.ik.components.debug_renderers.WalkerLegDebugRenderer;
import com.sp.entity.ik.model.BoneAccessor;
import com.sp.entity.ik.model.GeckoLib.MowzieGeoBone;
import com.sp.entity.ik.model.ModelAccessor;
import com.sp.entity.ik.parts.ik_chains.IKChain;
import com.sp.entity.ik.parts.sever_limbs.ServerLimb;
import com.sp.entity.ik.util.MathUtil;
import com.sp.entity.ik.util.PrAnCommonClass;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.joml.Matrix4f;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class IKWalkerComponent<C extends IKChain, E extends IKAnimatable<E>> extends IKLegComponent<C, E> {
    /// summon projectnublar:tyrannosaurus_rex ~ ~ ~ {NoAI:1b}
    public IKWalkerComponent(List<IKLegComponent.LegSetting> settings, List<ServerLimb> endpoints, C... limbs) {
        super(settings, endpoints, limbs);
    }

    public IKWalkerComponent(IKLegComponent.LegSetting settings, List<ServerLimb> endpoints, C... limbs) {
        super(settings, endpoints, limbs);
    }

    @Override
    public void tickClient(E animatable, ModelAccessor model) {
        if (!(animatable instanceof WalkerEntity entity)) {
            return;
        }

        for (int i = 0; i < this.limbs.size(); i++) {
            if (model.getBone("base_" + "leg" + (i + 1)).isEmpty()) {
                return;
            }
            //BoneAccessor baseAccessor = model.getBone("base_" + "leg" + (i + 1)).get();//

            //Vec3d basePosWorldSpace = baseAccessor.getgetPos()();
            if (this.bases.isEmpty()) {
                return;
            }

            Optional<BoneAccessor> root = model.getBone("root");

            if (root.isPresent()) {
                BoneAccessor bone = root.get();

                if (bone instanceof MowzieGeoBone mowzieGeoBone) {
                    mowzieGeoBone.setForceMatrixTransform(true);

                    Matrix4f xformOverride = new Matrix4f();

                    Vec3d newModelPosWorldSpace = MathUtil.rotatePointOnAPlaneAround(entity.getPos(), entity.getPos(), -180, new Vec3d(0, 1, 0));

                    xformOverride = xformOverride.translate(newModelPosWorldSpace.toVector3f());

                    xformOverride.rotateYXZ((float) -Math.toRadians(entity.getYaw()), (float) -Math.toRadians(entity.getPitch() + 90), 0);


                    xformOverride.rotateZ((float) -Math.toRadians(entity.getRoll()));

                    mowzieGeoBone.setWorldSpaceMatrix(xformOverride);
                }
            }

            Vec3d basePosWorldSpace = this.bases.get(i);

            C limb = this.setLimb(i, basePosWorldSpace, entity);

            for (int k = 0; k < limb.getJoints().size() - 1; k++) {
                Vec3d modelPosWorldSpace = limb.getJoints().get(k);
                Vec3d targetVecWorldSpace = limb.getJoints().get(k + 1);

                if (model.getBone("segment" + (k + 1) + "_leg" + (i + 1)).isEmpty()) {
                    return;
                }

                BoneAccessor legSegmentAccessor = model.getBone("segment" + (k + 1) + "_leg" + (i + 1)).get();

                if (PrAnCommonClass.shouldRenderDebugLegs) {
                    modelPosWorldSpace = modelPosWorldSpace.subtract(0, 200, 0);
                    targetVecWorldSpace = targetVecWorldSpace.subtract(0, 200, 0);
                }

                legSegmentAccessor.moveTo(modelPosWorldSpace, targetVecWorldSpace, entity);
            }
        }
    }

    @Override
    public void tickServer(E animatable) {
        this.setScale(animatable.getSize());

        if (!(animatable instanceof WalkerEntity entity)) {
            return;
        }
        World world = entity.getWorld();

        for (int i = 0; i < this.endPoints.size(); i++) {
            ServerLimb limb = this.endPoints.get(i);

            limb.tick(this, i);

            Vec3d limbOffsetMultiplier = limb.baseOffset.multiply(this.getScale());

            Vec3d limbOffset = Vec3d.ZERO;

            limbOffset = limbOffset.add(entity.getUpDirection().crossProduct(entity.getRotationVector()).multiply(limbOffsetMultiplier.x));

            limbOffset = limbOffset.add(entity.getUpDirection().multiply(limbOffsetMultiplier.y));

            limbOffset = limbOffset.add(entity.getRotationVector().multiply(limbOffsetMultiplier.z));

            if (hasMovedOverLastTick(entity)) {
                limbOffset = limbOffset.add(0, 0, this.getSettings().get(0).stepInFront() * this.getScale());
            }

            Vec3d rotatedLimbOffset = limbOffset.add(entity.getPos());
            Vec3d upPoint = rotatedLimbOffset.add(entity.getUpDirection().multiply(1));
            HitResult baseRayCastResult = world.raycast(new RaycastContext(upPoint, rotatedLimbOffset.add(entity.getUpDirection().multiply(-10)), RaycastContext.ShapeType.COLLIDER, this.getSettings().get(0).fluid(), entity));

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
                BlockHitResult rayCastResult = world.raycast(new RaycastContext(upPoint, upPoint.add(upDir.multiply(-10)), RaycastContext.ShapeType.COLLIDER, this.getSettings().get(0).fluid(), entity));

                if (rayCastResult.getType() == BlockHitResult.Type.MISS) {
                    continue;
                }

                if (rayCastResult.getPos().squaredDistanceTo(baseRayCastResult.getPos()) < bestDistance) {
                    bestDistance = rayCastResult.getPos().squaredDistanceTo(baseRayCastResult.getPos());
                    bestHit = rayCastResult.getPos();
                }
            }


            if (limb.hasToBeSet) {
                limb.set(bestHit);
                limb.hasToBeSet = false;
            }

            if (!bestHit.isInRange(limb.target, this.getMaxLegFormTargetDistance(entity))) {
                limb.setTarget(bestHit);
            }
        }
    }

    @Override
    public boolean hasMovedOverLastTick(Entity entity) {
        if (entity instanceof WalkerEntity walker) {
            return walker.isWalking;
        }

        return super.hasMovedOverLastTick(entity);
    }

    @Override
    public void renderDebug(MatrixStack poseStack, E animatable, RenderLayer renderType, VertexConsumerProvider bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        new WalkerLegDebugRenderer<E, C>().renderDebug(this, animatable, poseStack, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
    }
}