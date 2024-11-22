package com.sp.entity.ik.parts.ik_chains;

import net.dumbcode.projectnublar.entity.ik.parts.Segment;
import net.dumbcode.projectnublar.entity.ik.util.MathUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import static net.dumbcode.projectnublar.entity.ik.util.MathUtil.getFlatRotationVector;

public class EntityLeg extends AngleConstraintIKChain {
    public Entity entity;

    public EntityLeg(double... lengths) {
        super(lengths);
    }

    public EntityLeg(Segment... segments) {
        super(segments);
    }

    @Override
    public Vec3 getReferencePoint() {
        Vec3 referencePoint = getFlatRotationVector(this.entity.getYRot() + 90);
        return this.getFirst().getPosition().add(referencePoint.scale(100));
    }

    @Override
    public Vec3 getStretchingPos(Vec3 target, Vec3 base) {
        return base.add(MathUtil.getFlatRotationVector(this.entity).scale(this.getMaxLength() * 2));
    }

    public Vec3 getDownNormalOnLegPlane() {
        Vec3 baseRotated = this.getFirst().getPosition().yRot(-this.entity.getYRot());
        Vec3 targetRotated = this.endJoint.yRot(-this.entity.getYRot());

        Vec3 flatRotatedBase = new Vec3(baseRotated.x(), baseRotated.y(), 0);
        Vec3 flatRotatedTarget = new Vec3(targetRotated.x(), targetRotated.y(), 0);

        Vec3 flatBase = flatRotatedBase.yRot(this.entity.getYRot());
        Vec3 flatTarget = flatRotatedTarget.yRot(this.entity.getYRot());

        return flatTarget.subtract(flatBase).normalize();
    }

    @Override
    public Vec3 getConstrainedPosForRootSegment() {
        return this.getConstrainedPosForRootSegment(this.getDownNormalOnLegPlane());
    }
}
