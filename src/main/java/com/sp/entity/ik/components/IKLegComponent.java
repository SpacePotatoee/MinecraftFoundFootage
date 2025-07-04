package com.sp.entity.ik.components;

import com.sp.entity.ik.components.debug_renderers.LegDebugRenderer;
import com.sp.entity.ik.model.BoneAccessor;
import com.sp.entity.ik.model.ModelAccessor;
import com.sp.entity.ik.parts.ik_chains.EntityLeg;
import com.sp.entity.ik.parts.ik_chains.EntityLegWithFoot;
import com.sp.entity.ik.parts.ik_chains.IKChain;
import com.sp.entity.ik.parts.sever_limbs.ServerLimb;
import com.sp.entity.ik.util.PrAnCommonClass;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IKLegComponent<C extends IKChain, E extends IKAnimatable<E>> extends IKChainComponent<C, E> {
    /// summon projectnublar:tyrannosaurus_rex ~ ~ ~ {NoAI:1b}
    protected List<ServerLimb> endPoints;
    protected List<Vec3d> bases;
    private List<LegSetting> settings;
    public double scale = 1;
    protected int stillStandCounter = 0;

    @SafeVarargs
    public IKLegComponent(List<LegSetting> settings, List<ServerLimb> endpoints, C... limbs) {
        this.init(settings, endpoints, limbs);
    }

    public void init(List<LegSetting> settings, List<ServerLimb> endpoints, C... limbs) {
        this.limbs.addAll(List.of(limbs));
        this.settings = settings;
        this.endPoints = endpoints;
        this.bases = new ArrayList<>();
        Arrays.stream(limbs).forEach(
                limb -> this.bases.add(new Vec3d(0,0,0))
        );
    }

    @SafeVarargs
    public IKLegComponent(LegSetting settings, List<ServerLimb> endpoints, C... limbs) {
        List<LegSetting> setting = new ArrayList<>();
        endpoints.forEach(e -> setting.add(settings));

        this.init(setting, endpoints, limbs);
    }

    public boolean hasMovedOverLastTick(Entity entity) {
        Vec3d oldPos = new Vec3d(entity.prevX, entity.prevY, entity.prevZ);
        return !oldPos.equals(entity.getPos());
    }

    public static BlockHitResult rayCastToGround(Vec3d rotatedLimbOffset, Entity entity, RaycastContext.FluidHandling fluid) {
        World world = entity.getWorld();
        return world.raycast(new RaycastContext(rotatedLimbOffset.offset(Direction.UP, 3), rotatedLimbOffset.offset(Direction.DOWN, 10), RaycastContext.ShapeType.COLLIDER, fluid, entity));
    }

    @Override
    public void tickClient(E animatable, ModelAccessor model) {
        if (!(animatable instanceof Entity entity)) {
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

            Vec3d basePosWorldSpace = this.bases.get(i);

            C limb = this.setLimb(i, basePosWorldSpace, entity);

            for (int k = 0; k < limb.getJoints().size() - 1; k++) {
                Vec3d modelPosWorldSpace = limb.getJoints().get(k);
                Vec3d targetVecWorldSpace = limb.getJoints().get(k + 1);

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

                    Vec3d shortenedEndPoint = limb.getLast().getPosition().add(limb.endJoint.subtract(limb.getLast().getPosition()).normalize().multiply(limb.getLast().length * 0.8));

                    double yOffset = shortenedEndPoint.subtract(limb.endJoint).y;

                    footSegmentAccessor.moveTo(PrAnCommonClass.shouldRenderDebugLegs ? shortenedEndPoint.subtract(0, 200, 0) : shortenedEndPoint, entityLegWithFoot.getFootPosition().add(0, yOffset, 0), entity);
                }
            }
        }
    }

    @Override
    public void getModelPositions(E animatable, ModelAccessor model) {
        for (int i = 0; i < this.limbs.size(); i++) {
            if (model.getBone("base_" + "leg" + (i + 1)).isEmpty()) {
                return;
            }
            BoneAccessor baseAccessor = model.getBone("base_" + "leg" + (i + 1)).get();

            Vec3d basePosWorldSpace = baseAccessor.getPosition();

            this.bases.set(i, basePosWorldSpace);
        }
    }

    @Override
    public void tickServer(E animatable) {
        this.setScale(animatable.getSize());

        if (!(animatable instanceof Entity entity)) {
            return;
        }

        for (int i = 0; i < this.endPoints.size(); i++) {
            ServerLimb limb = this.endPoints.get(i);

            limb.tick(this, i);

            Vec3d limbOffset = limb.baseOffset.multiply(this.getScale());

            if (hasMovedOverLastTick(entity)) {
                limbOffset = limbOffset.add(0, 0, this.getSettings().get(0).stepInFront() * this.getScale());
            }

            limbOffset = limbOffset.rotateY((float) Math.toRadians(-entity.getBodyYaw()));

            Vec3d rotatedLimbOffset = limbOffset.add(entity.getPos());

            BlockHitResult rayCastResult = IKLegComponent.rayCastToGround(rotatedLimbOffset, entity, RaycastContext.FluidHandling.NONE);

            Vec3d rayCastHitPos = rayCastResult.getPos();

            if (limb.hasToBeSet) {
                limb.set(rayCastHitPos);
                limb.hasToBeSet = false;
            }

            if (!rayCastHitPos.isInRange(limb.target, this.getMaxLegFormTargetDistance(entity))) {
                limb.setTarget(rayCastHitPos);
            }
        }
    }

    @Override
    public void renderDebug(MatrixStack poseStack, E animatable, RenderLayer renderType, VertexConsumerProvider bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        new LegDebugRenderer<E, C>().renderDebug(this, animatable, poseStack, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
    }

    double getMaxLegFormTargetDistance(Entity entity) {
        if (this.stillStandCounter >= this.settings.get(0).standStillCounter() && hasMovedOverLastTick(entity)) {
            this.stillStandCounter = 0;
        } else if (this.stillStandCounter < this.settings.get(0).standStillCounter()) {
            this.stillStandCounter += 1;
        }

        if (this.stillStandCounter == this.settings.get(0).standStillCounter()) {
            return this.settings.get(0).maxStandingStillDistance() * this.getScale();
        } else {
            return this.settings.get(0).maxDistance() * this.getScale();
        }
    }

    public List<ServerLimb> getEndPoints() {
        return this.endPoints;
    }

    public List<LegSetting> getSettings() {
        return this.settings;
    }

    public int getStillStandCounter() {
        return this.stillStandCounter;
    }

    @Override
    public C setLimb(int index, Vec3d base, Entity entity) {
        C limb = this.limbs.get(index);

        if (limb instanceof EntityLeg entityLeg) {
            entityLeg.entity = entity;
        }

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
        private RaycastContext.FluidHandling fluid;
        private double maxStandingStillDistance;
        private double maxDistance;
        private double stepInFront;
        private double movementSpeed;
        private int standStillCounter;
        private double steppingParabolaStrength = 2;

        private LegSetting(RaycastContext.FluidHandling fluid, double maxStandingStillDistance, double maxDistance, double stepInFront, double movementSpeed, int standStillCounter, double steppingParabolaStrength) {
            this.fluid = fluid;
            if (fluid == null) {
                this.fluid = RaycastContext.FluidHandling.NONE;
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
            this.steppingParabolaStrength = steppingParabolaStrength;
        }

        public RaycastContext.FluidHandling fluid() {
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

        public double steppingParabolaStrength() {
            return this.steppingParabolaStrength;
        }

        public static class Builder {
            private RaycastContext.FluidHandling fluid;
            private double maxStandingStillDistance;
            private double maxDistance;
            private double stepInFront;
            private double movementSpeed;
            private int standStillCounter;
            private double steppingParabolaStrength = 2;

            public Builder() {
            }

            public LegSetting.Builder fluid(RaycastContext.FluidHandling fluid) {
                this.fluid = fluid;
                return this;
            }

            public LegSetting.Builder steppingParabolaStrength(double steppingParabolaStrength) {
                this.steppingParabolaStrength = steppingParabolaStrength;
                return this;
            }

            public LegSetting.Builder maxStandingStillDistance(double maxStandingStillDistance) {
                this.maxStandingStillDistance = maxStandingStillDistance;
                return this;
            }

            public LegSetting.Builder maxDistance(double maxDistance) {
                this.maxDistance = maxDistance;
                return this;
            }

            public LegSetting.Builder standStillCounter(int standStillCounter) {
                this.standStillCounter = standStillCounter;
                return this;
            }

            public LegSetting.Builder stepInFront(double stepInFront) {
                this.stepInFront = stepInFront;
                return this;
            }

            public LegSetting.Builder movementSpeed(double movementSpeed) {
                this.movementSpeed = movementSpeed;
                return this;
            }

            public LegSetting build() {
                return new LegSetting(this.fluid, this.maxStandingStillDistance, this.maxDistance, this.stepInFront, this.movementSpeed, this.standStillCounter, this.steppingParabolaStrength);
            }
        }
    }
}