package com.sp.entity.custom;

import com.sp.entity.ik.components.IKAnimatable;
import com.sp.entity.ik.components.IKLegComponent;
import com.sp.entity.ik.components.IKModelComponent;
import com.sp.entity.ik.components.IKWalkerComponent;
import com.sp.entity.ik.parts.Segment;
import com.sp.entity.ik.parts.ik_chains.BendReachingIKChain;
import com.sp.entity.ik.parts.sever_limbs.ServerLimb;
import com.sp.entity.ik.util.MathUtil;
import com.sp.init.ModEntities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaterniond;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.List;

public class WalkerEntity extends Entity implements GeoEntity, GeoAnimatable, IKAnimatable<WalkerEntity> {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    @Nullable
    private Entity target = null;

    private final Quaterniond rotation = new Quaterniond(0,0,0,1); // Default rotation facing down the Z-axis
    private final List<IKModelComponent<WalkerEntity>> components = new ArrayList<>();
    private static final double SPEED_MULTIPLIER = 0.1;
    private static final double SQUARED_MAX_VELOCITY = 16; // 4.0 * 4.0
    public static final double TILTING_TEST_RANGE = 5.0;
    private static final double COLLISION_TEST_RANGE = 3;
    public boolean isWalking = false;
    public Quaterniond upDirection = new Quaterniond(1, 0 ,0 ,1);
    private double roll = 0;

    public WalkerEntity(EntityType<WalkerEntity> entityType, World world) {
        super(ModEntities.WALKER_ENTITY, world);
        this.addComponent(new IKWalkerComponent<>(
                new IKLegComponent.LegSetting.Builder()
                        .maxDistance(2)
                        .stepInFront(1)
                        .movementSpeed(0.2)
                        .maxStandingStillDistance(0.2)
                        .standStillCounter(20).build(),
                List.of(
                        new ServerLimb(2.984375, 0, 1.21875, (limb, legComponent, i, movementSpeed) -> {}),
                        new ServerLimb(-2.984375, 0, 1.21875, (limb, legComponent, i, movementSpeed) -> {}),
                        new ServerLimb(5.1875, 0, -1.25, (limb, legComponent, i, movementSpeed) -> {}),
                        new ServerLimb(-5.1875, 0, -1.25, (limb, legComponent, i, movementSpeed) -> {}),
                        new ServerLimb(3.875, 0, -3.875, (limb, legComponent, i, movementSpeed) -> {}),
                        new ServerLimb(-3.875, 0, -3.875, (limb, legComponent, i, movementSpeed) -> {})
                ),
                new BendReachingIKChain(this, new Segment.Builder().length(0.7).build(), new Segment.Builder().length(0.93).build(), new Segment.Builder().length(1.98).build(), new Segment.Builder().length(1.92).build(), new Segment.Builder().length(2).build()),
                new BendReachingIKChain(this, new Segment.Builder().length(0.7).build(), new Segment.Builder().length(0.93).build(), new Segment.Builder().length(1.98).build(), new Segment.Builder().length(1.92).build(), new Segment.Builder().length(2).build()),
                new BendReachingIKChain(this, new Segment.Builder().length(0.7).build(), new Segment.Builder().length(1.3).build(), new Segment.Builder().length(2.1).build(), new Segment.Builder().length(2.1).build(), new Segment.Builder().length(1.98).build(), new Segment.Builder().length(2.3).build()),
                new BendReachingIKChain(this, new Segment.Builder().length(0.7).build(), new Segment.Builder().length(1.3).build(), new Segment.Builder().length(2.1).build(), new Segment.Builder().length(2.1).build(), new Segment.Builder().length(1.98).build(), new Segment.Builder().length(2.3).build()),
                new BendReachingIKChain(this, new Segment.Builder().length(0.7).build(), new Segment.Builder().length(1.3).build(), new Segment.Builder().length(2.1).build(), new Segment.Builder().length(2.1).build(), new Segment.Builder().length(1.98).build(), new Segment.Builder().length(2.3).build()),
                new BendReachingIKChain(this, new Segment.Builder().length(0.7).build(), new Segment.Builder().length(1.3).build(), new Segment.Builder().length(2.1).build(), new Segment.Builder().length(2.1).build(), new Segment.Builder().length(1.98).build(), new Segment.Builder().length(2.3).build())
        ));
    }

    public double getRoll() {
        return roll;
    }

    public void setRoll(float roll) {
        this.roll = roll;
    }

    public Quaterniond getRotation() {
        return rotation;
    }

    public Quaternionf getRotationF() {
        return new Quaternionf(rotation.x, rotation.y, rotation.z, rotation.w);
    }

    @Override
    protected void initDataTracker() {

    }

    public @Nullable Entity getTarget() {
        return target;
    }

    public void setTarget(@Nullable Entity target) {
        this.target = target;
    }

    @Override
    public void tick() {
        ((Entity) this).baseTick(); // Leave this as is. Trust me.
        this.tickComponentsServer(this);

        PlayerEntity nearestPlayer = this.getWorld().getClosestPlayer(this, 100);
        if (nearestPlayer != null && nearestPlayer.getMainHandStack().isOf(Items.BONE)) {
            this.setTarget(nearestPlayer);
        } else {
            this.setTarget(null);
        }

        this.isWalking = false;

        if (this.getTarget() != null) {
            this.isWalking = true;

            Vec3d direction = this.getFacingTarget();
            double horizontalDistance = Math.sqrt(direction.x * direction.x + direction.z * direction.z);

            double yaw = Math.atan2(direction.x, direction.z);
            double pitch = -Math.atan2(direction.y, horizontalDistance);
            //double roll = Math.atan2(direction.y, direction.x);

            boolean shouldBeInverted = this.getUpDirection().negate().squaredDistanceTo(this.getRightDirection()) > this.getUpStationaryDirection().squaredDistanceTo(this.getRightDirection());
            double roll = (shouldBeInverted ? (-1) : (1)) * Math.toDegrees(Math.acos(this.getUpDirection().negate().dotProduct(this.getUpStationaryDirection().negate())));

            Quaterniond newRotation = new Quaterniond()
                    .identity()
                    .rotateY(yaw)
                    .rotateX(pitch);

            rotation.nlerp(newRotation, 0.6);

            Vec3d newVelocity = this.getRotationVector().multiply(this.getTarget().distanceTo(this) * SPEED_MULTIPLIER);

            if (Math.min(SQUARED_MAX_VELOCITY, newVelocity.lengthSquared()) != SQUARED_MAX_VELOCITY) {
                this.setVelocity(newVelocity);
            }

            this.setPosition(
                    this.getPos().add(this.getVelocity()).x,
                    this.getPos().add(this.getVelocity()).y,
                    this.getPos().add(this.getVelocity()).z);

            this.setYaw((float) MathHelper.wrapDegrees(-(Math.toDegrees(yaw))));
            this.setPitch((float) MathHelper.wrapDegrees(Math.toDegrees(pitch) + 270));
            this.setRoll((float) MathHelper.wrapDegrees(roll));

            updateUpDirection();
        }
    }

    private Vec3d getFacingTarget() {
        Vec3d averageDirection = this.getTarget().getPos().subtract(this.getPos()).normalize();
        int directionsApplied = 1;

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
            Vec3d targetPos = this.getPos().add(testPosition.multiply(COLLISION_TEST_RANGE));

            HitResult hitResult = this.getWorld().raycast(new RaycastContext(this.getPos(), targetPos, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, this));
            boolean hit = hitResult.getType() != HitResult.Type.MISS;

            if (hit) {
                averageDirection = averageDirection.add(testPosition.negate().multiply(hitResult.getPos().squaredDistanceTo(targetPos) / COLLISION_TEST_RANGE * COLLISION_TEST_RANGE));
                directionsApplied++;
            }
        }

        return new Vec3d(averageDirection.x / directionsApplied,
                         averageDirection.y / directionsApplied,
                         averageDirection.z / directionsApplied).normalize();
    }

    public void updateUpDirection() {
        double hitAmount = 0;

        Vec3d hitAverage = Vec3d.ZERO;

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
            Vec3d targetPos = this.getPos().add(testPosition.multiply(TILTING_TEST_RANGE));

            boolean hit = this.getWorld().raycast(new RaycastContext(this.getPos(), targetPos, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, this)).getType() == HitResult.Type.BLOCK;

            if (hit) {
                hitAverage = hitAverage.add(testPosition);
                hitAmount++;
            }
        }

        if (hitAverage.lengthSquared() < 0.01) {
            return;
        } else {
            hitAverage = new Vec3d(hitAverage.x / hitAmount, hitAverage.y / hitAmount, hitAverage.z / hitAmount);
        }

        double yaw = Math.atan2(hitAverage.x, hitAverage.z);

        double horizontalDistance = Math.sqrt(hitAverage.x * hitAverage.x + hitAverage.z * hitAverage.z);
        double pitch = -Math.atan2(hitAverage.y, horizontalDistance);

        Quaterniond newRotation = new Quaterniond()
                .identity()
                .rotateY(yaw)
                .rotateX(pitch);

        upDirection.nlerp(newRotation, 0.6);
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {

    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {

    }

    @Override
    public Vec3d getRotationVector() {
        return MathUtil.toVec3(rotation.transform(new Vector3d(0, 0, 1))).normalize();
    }

    public Vec3d getUpDirection() {
        return MathUtil.toVec3(upDirection.transform(new Vector3d(0, 0, 1))).negate().normalize();
    }

    public Vec3d getUpStationaryDirection() {
        return MathUtil.toVec3(rotation.transform(new Vector3d(0, 1, 0))).normalize();
    }

    public Vec3d getRightDirection() {
        return MathUtil.toVec3(rotation.transform(new Vector3d(1, 0, 0))).normalize();
    }

    @Override
    public List<IKModelComponent<WalkerEntity>> getComponents() {
        return components;
    }

    @Override
    public double getSize() {
        return 1;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
