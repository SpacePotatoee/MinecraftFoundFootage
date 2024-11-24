package com.sp.entity.ik.parts.ik_chains;

import com.sp.entity.ik.parts.Segment;
import com.sp.entity.ik.util.MathUtil;
import net.minecraft.util.math.Vec3d;

public class TargetReachingIKChain extends StretchingIKChain {
    public TargetReachingIKChain(double... lengths) {
        super(lengths);
    }

    public TargetReachingIKChain(Segment... segments) {
        super(segments);
    }

    @Override
    public Vec3d getStretchingPos(Vec3d target, Vec3d base) {
        Vec3d flatTargetDir = MathUtil.convertToFlatVector(target.subtract(base)).normalize();

        Vec3d newPos = base.add(flatTargetDir.multiply(this.getMaxLength())).add(0, this.getMaxLength(), 0);

        return newPos;
    }
}
