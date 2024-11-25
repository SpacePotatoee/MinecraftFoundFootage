package com.sp.entity.ik.parts.ik_chains;

import com.sp.entity.ik.parts.Segment;
import com.sp.entity.ik.util.MathUtil;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

import static com.sp.entity.ik.util.MathUtil.getFlatRotationVector;

public class EntityLeg extends AngleConstraintIKChain {
    public Entity entity;

    public EntityLeg(double... lengths) {
        super(lengths);
    }

    public EntityLeg(Segment... segments) {
        super(segments);
    }

    @Override
    public Vec3d getReferencePoint() {
        Vec3d referencePoint = getFlatRotationVector(this.entity.getBodyYaw() + 90);
        return this.getFirst().getPosition().add(referencePoint.multiply(100));
    }

    @Override
    public Vec3d getStretchingPos(Vec3d target, Vec3d base) {
        return base.add(getFlatRotationVector(this.entity).multiply(this.getMaxLength() * 2));
    }

    public Vec3d getDownNormalOnLegPlane() {
        Vec3d baseRotated = this.getFirst().getPosition().rotateY(-this.entity.getBodyYaw());
        Vec3d targetRotated = this.endJoint.rotateY(-this.entity.getBodyYaw());

        Vec3d flatRotatedBase = new Vec3d(baseRotated.x, baseRotated.y, 0);
        Vec3d flatRotatedTarget = new Vec3d(targetRotated.x, targetRotated.y, 0);

        Vec3d flatBase = flatRotatedBase.rotateY(this.entity.getBodyYaw());
        Vec3d flatTarget = flatRotatedTarget.rotateY(this.entity.getBodyYaw());

        return flatTarget.subtract(flatBase).normalize();
    }

    @Override
    public Vec3d getConstrainedPosForRootSegment() {
        return this.getConstrainedPosForRootSegment(this.getDownNormalOnLegPlane());
    }
}
