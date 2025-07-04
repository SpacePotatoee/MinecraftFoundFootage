package com.sp.entity.ik.parts.ik_chains;

import com.sp.entity.ik.parts.Segment;
import com.sp.entity.ik.parts.WorldCollidingSegment;
import com.sp.entity.ik.util.MathUtil;
import com.sp.entity.ik.util.PrAnCommonClass;
import net.minecraft.util.math.Vec3d;

public class EntityLegWithFoot extends EntityLeg {
    public final WorldCollidingSegment foot;
    private double footAngel = 90;

    public EntityLegWithFoot(WorldCollidingSegment foot, double... lengths) {
        super(lengths);
        this.foot = foot;
    }

    public EntityLegWithFoot(WorldCollidingSegment foot, Segment... segments) {
        super(segments);
        this.foot = foot;
    }

    public double getFootAngel() {
        return this.footAngel;
    }

    @Override
    public void solve(Vec3d target, Vec3d base) {
        super.solve(target, base);
        if (this.foot.getLevel() == null) {
            this.foot.setLevel(this.entity.getWorld());
        }

        Vec3d referencePoint = MathUtil.rotatePointOnAPlaneAround(this.endJoint.add(this.getDownNormalOnLegPlane()), this.endJoint, this.foot.angleOffset, this.getLegPlane());
        this.footAngel = Math.toDegrees(MathUtil.calculateAngle(this.endJoint, this.foot.getPosition(), referencePoint));

        if (this.footAngel > 2) {
            this.foot.move(this.getFootPosition().subtract(0, 0.05, 0), true, 0.05);
        }

        this.footAngel = Math.toDegrees(MathUtil.calculateAngle(this.endJoint, this.foot.getPosition(), referencePoint));

        double clampedAngle = Math.max(Math.min(this.foot.angleSize, this.footAngel), 0);

        this.footAngel = clampedAngle;

        Vec3d newFootPosition = this.getFootPosition(clampedAngle);

        this.foot.move(newFootPosition, false);

        if (Double.isNaN(this.footAngel)) {
            this.footAngel = 0;
            PrAnCommonClass.LOGGER.warning("Foot has dropped to NaN, resetting to 0");
        }
    }

    public Vec3d getFootPosition() {
        return this.getFootPosition(this.footAngel);
    }

    public Vec3d getFootPosition(double angle) {
        //Vec3d normal = MathUtil.getNormalClosestTo(this.endJoint, this.getLast().getPosition(), this.get(this.segments.size() - 2).getPosition(), this.getReferencePoint());

        return MathUtil.rotatePointOnAPlaneAround(this.endJoint.add(this.getDownNormalOnLegPlane().multiply(this.foot.length * this.getScale())), this.endJoint, angle + this.foot.angleOffset, this.getLegPlane());
    }
}
