package com.sp.entity.ik.components;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.dumbcode.projectnublar.entity.ik.components.debug_renderers.LegDebugRenderer;
import net.dumbcode.projectnublar.entity.ik.model.BoneAccessor;
import net.dumbcode.projectnublar.entity.ik.model.ModelAccessor;
import net.dumbcode.projectnublar.entity.ik.parts.ik_chains.EntityLeg;
import net.dumbcode.projectnublar.entity.ik.parts.ik_chains.EntityLegWithFoot;
import net.dumbcode.projectnublar.entity.ik.parts.sever_limbs.ServerLimb;
import net.dumbcode.projectnublar.entity.ik.util.PrAnCommonClass;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class IKLegComponent<C extends EntityLeg, E extends IKAnimatable<E>> extends IKChainComponent<C, E> {
    /// summon projectnublar:tyrannosaurus_rex ~ ~ ~ {NoAI:1b}
    private final List<ServerLimb> endPoints;
    private final LegSetting settings;
    public double scale = 1;
    private int stillStandCounter = 0;

    @SafeVarargs
    public IKLegComponent(LegSetting settings, List<ServerLimb> endpoints, C... limbs) {
        this.limbs.addAll(List.of(limbs));
        this.settings = settings;
        this.endPoints = endpoints;
    }

    private static boolean hasMovedOverLastTick(PathfinderMob entity) {
        Vec3 oldPos = new Vec3(entity.xo, entity.yo, entity.zo);
        return !entity.position().equals(oldPos);
    }

    public static BlockHitResult rayCastToGround(Vec3 rotatedLimbOffset, Entity entity, ClipContext.Fluid fluid) {
        Level world = entity.level();
        return world.clip(new ClipContext(rotatedLimbOffset.relative(Direction.UP, 3), rotatedLimbOffset.relative(Direction.DOWN, 10), ClipContext.Block.COLLIDER, fluid, entity));
    }

    @Override
    public void tickClient(E animatable, ModelAccessor model) {
        if (!(animatable instanceof Entity entity)) {
            return;
        }

        double sum = 0;

        for (Vec3 point : this.endPoints.stream().map((serverLimb -> serverLimb.target)).toList()) {
            sum += point.y;
        }

        double average = sum / this.endPoints.size();

        if (model.getBone("entity_base").isEmpty()) {
            return;
        }
        BoneAccessor entityBase = model.getBone("entity_base").get();

        double newY = average; //Mth.lerp(2, entityBase.getPosition(entity).y(), average);

        //entityBase.moveTo(new Vec3(entity.position().x(), newY, entity.position().z()), null, entity);

        for (int i = 0; i < this.limbs.size(); i++) {
            if (model.getBone("base_" + "leg" + (i + 1)).isEmpty()) {
                return;
            }
            BoneAccessor baseAccessor = model.getBone("base_" + "leg" + (i + 1)).get();

            Vec3 basePosWorldSpace = baseAccessor.getPosition();

            C limb = this.setLimb(i, basePosWorldSpace, entity);

            for (int k = 0; k < limb.getJoints().size() - 1; k++) {
                Vec3 modelPosWorldSpace = limb.getJoints().get(k);
                Vec3 targetVecWorldSpace = limb.getJoints().get(k + 1);

                if (model.getBone("seg" + (k + 1) + "_leg" + (i + 1)).isEmpty()) {
                    return;
                }
                BoneAccessor legSegmentAccessor = model.getBone("seg" + (k + 1) + "_leg" + (i + 1)).get();

                if (PrAnCommonClass.shouldRenderDebugLegs) {
                    modelPosWorldSpace = modelPosWorldSpace.subtract(0, 200, 0);
                    targetVecWorldSpace = targetVecWorldSpace.subtract(0, 200, 0);
                }

                legSegmentAccessor.moveTo(modelPosWorldSpace, targetVecWorldSpace, entity);

                if (limb instanceof EntityLegWithFoot entityLegWithFoot) {
                    if (model.getBone("foot_leg" + (i + 1)).isEmpty()) {
                        return;
                    }
                    BoneAccessor footSegmentAccessor = model.getBone("foot_leg" + (i + 1)).get();

                    Vec3 shortenedEndPoint = limb.getLast().getPosition().add(limb.endJoint.subtract(limb.getLast().getPosition()).normalize().scale(limb.getLast().length * 0.8));

                    double yOffset = shortenedEndPoint.subtract(limb.endJoint).y;

                    footSegmentAccessor.moveTo(PrAnCommonClass.shouldRenderDebugLegs ? shortenedEndPoint.subtract(0, 200, 0) : shortenedEndPoint, entityLegWithFoot.getFootPosition().add(0, yOffset, 0), entity);
                }
            }
        }
    }

    @Override
    public void tickServer(E animatable) {
        this.setScale(animatable.getSize());

        if (!(animatable instanceof PathfinderMob entity)) {
            return;
        }

        Vec3 pos = entity.position();

        for (int i = 0; i < this.endPoints.size(); i++) {
            ServerLimb limb = this.endPoints.get(i);

            limb.tick(this, i, this.settings.movementSpeed);

            Vec3 limbOffset = limb.baseOffset.scale(this.getScale());

            if (hasMovedOverLastTick(entity)) {
                limbOffset = limbOffset.add(0, 0, this.settings.stepInFront() * this.getScale());
            }

            limbOffset = limbOffset.yRot((float) Math.toRadians(-entity.getYRot()));

            Vec3 rotatedLimbOffset = limbOffset.add(pos);

            BlockHitResult rayCastResult = rayCastToGround(rotatedLimbOffset, entity, this.settings.fluid());

            Vec3 rayCastHitPos = rayCastResult.getLocation();

            if (limb.hasToBeSet) {
                limb.set(rayCastHitPos);
                limb.hasToBeSet = false;
            }

            if (!rayCastHitPos.closerThan(limb.target, this.getMaxLegFormTargetDistance(entity))) {
                limb.setTarget(rayCastHitPos);
            }
        }
    }

    @Override
    public void renderDebug(PoseStack poseStack, E animatable, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        new LegDebugRenderer<E, C>().renderDebug(this, animatable, poseStack, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
    }

    private double getMaxLegFormTargetDistance(PathfinderMob entity) {
        if (this.stillStandCounter >= this.settings.standStillCounter() && hasMovedOverLastTick(entity)) {
            this.stillStandCounter = 0;
        } else if (this.stillStandCounter < this.settings.standStillCounter()) {
            this.stillStandCounter += 1;
        }

        if (this.stillStandCounter == this.settings.standStillCounter()) {
            return this.settings.maxStandingStillDistance() * this.getScale();
        } else {
            return this.settings.maxDistance() * this.getScale();
        }
    }

    public List<C> getLimbs() {
        return this.limbs;
    }

    public List<ServerLimb> getEndPoints() {
        return this.endPoints;
    }

    public LegSetting getSettings() {
        return this.settings;
    }

    public int getStillStandCounter() {
        return this.stillStandCounter;
    }

    @Override
    public C setLimb(int index, Vec3 base, Entity entity) {
        C limb = this.limbs.get(index);

        limb.entity = entity;


        limb.setScale(this.getScale());

        limb.solve(this.endPoints.get(index).getPos(), base);

        return limb;
    }

    public double getScale() {
        return this.scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    public static class LegSetting {
        private ClipContext.Fluid fluid;
        private double maxStandingStillDistance;
        private double maxDistance;
        private double stepInFront;
        private double movementSpeed;
        private int standStillCounter;

        private LegSetting(ClipContext.Fluid fluid, double maxStandingStillDistance, double maxDistance, double stepInFront, double movementSpeed, int standStillCounter) {
            this.fluid = fluid;
            if (fluid == null) {
                this.fluid = ClipContext.Fluid.NONE;
            }
            this.maxStandingStillDistance = maxStandingStillDistance;
            if (maxStandingStillDistance == 0) {
                this.maxStandingStillDistance = 0.1;
            }
            this.maxDistance = maxDistance;
            if (maxDistance == 0) {
                this.maxDistance = 1;
            }
            this.stepInFront = stepInFront;
            if (stepInFront == 0) {
                this.stepInFront = 1;
            }
            this.movementSpeed = movementSpeed;
            if (movementSpeed == 0) {
                this.movementSpeed = 0.2;
            }
            this.standStillCounter = standStillCounter;
            if (standStillCounter == 0) {
                this.standStillCounter = 20;
            }
        }

        public ClipContext.Fluid fluid() {
            return this.fluid;
        }

        public double maxStandingStillDistance() {
            return this.maxStandingStillDistance;
        }

        public double maxDistance() {
            return this.maxDistance;
        }

        public double stepInFront() {
            return this.stepInFront;
        }

        public double movementSpeed() {
            return this.movementSpeed;
        }

        public int standStillCounter() {
            return this.standStillCounter;
        }

        public static class Builder {
            private ClipContext.Fluid fluid;
            private double maxStandingStillDistance;
            private double maxDistance;
            private double stepInFront;
            private double movementSpeed;
            private int standStillCounter;

            public Builder() {
            }

            public Builder fluid(ClipContext.Fluid fluid) {
                this.fluid = fluid;
                return this;
            }

            public Builder maxStandingStillDistance(double maxStandingStillDistance) {
                this.maxStandingStillDistance = maxStandingStillDistance;
                return this;
            }

            public Builder maxDistance(double maxDistance) {
                this.maxDistance = maxDistance;
                return this;
            }

            public Builder standStillCounter(int standStillCounter) {
                this.standStillCounter = standStillCounter;
                return this;
            }

            public Builder stepInFront(double stepInFront) {
                this.stepInFront = stepInFront;
                return this;
            }

            public Builder movementSpeed(double movementSpeed) {
                this.movementSpeed = movementSpeed;
                return this;
            }

            public LegSetting build() {
                return new LegSetting(this.fluid, this.maxStandingStillDistance, this.maxDistance, this.stepInFront, this.movementSpeed, this.standStillCounter);
            }
        }
    }
}