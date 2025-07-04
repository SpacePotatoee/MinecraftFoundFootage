package com.sp.entity.ik.parts.ik_chains;

import com.sp.entity.custom.WalkerEntity;
import com.sp.entity.ik.parts.Segment;
import net.minecraft.util.math.Vec3d;

public class BendReachingIKChain extends StretchingIKChain {
    public final WalkerEntity entity;

    public BendReachingIKChain(WalkerEntity entity, double... lengths) {
        super(lengths);
        this.entity = entity;
    }

    public BendReachingIKChain(WalkerEntity entity, Segment... segments) {
        super(segments);
        this.entity = entity;
    }

    @Override
    public Vec3d getStretchingPos(Vec3d target, Vec3d base) {
        return target;
    }

    @Override
    public void stretch(Vec3d target, Vec3d base) {
        Vec3d flatTargetDir = target.subtract(base).add(entity.getUpDirection().multiply(3)).normalize();

        this.getFirst().move(base);

        Vec3d newPos = base.add(flatTargetDir.multiply(2)).add(entity.getUpDirection().multiply(this.getMaxLength()));

        Vec3d directionOfTarget = newPos.subtract(base).normalize();

        for (int i = 1; i < this.segments.size(); i++) {
            Segment prevSegment = this.segments.get(i - 1);
            Segment currentSegment = this.segments.get(i);

            if (i != 1) {
                currentSegment.move(prevSegment.getPosition().add(directionOfTarget.multiply(prevSegment.length * this.getScale())));
                continue;
            }

            currentSegment.move(prevSegment.getPosition().add(flatTargetDir.multiply(prevSegment.length * this.getScale())));
        }

        this.endJoint = this.getLast().getPosition().add(directionOfTarget.multiply(this.getLast().length * this.getScale()));
    }
}
