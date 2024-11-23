package com.sp.entity.ik.parts.ik_chains;


import com.sp.entity.ik.parts.Segment;
import net.minecraft.util.math.Vec3d;

public abstract class StretchingIKChain extends IKChain {


    public StretchingIKChain(double... lengths) {
        super(lengths);
    }

    public StretchingIKChain(Segment... segments) {
        super(segments);
    }

    public static Vec3d stretchToTargetPos(Vec3d target, StretchingIKChain chain) {
        Vec3d direction = target.subtract(chain.getFirst().getPosition());
        return chain.getFirst().getPosition().add(direction.multiply(chain.getMaxLength() * 2));
    }

    @Override
    public void solve(Vec3d target, Vec3d base) {
        this.stretch(this.getStretchingPos(target, base), base);
        super.solve(target, base);
    }

    public abstract Vec3d getStretchingPos(Vec3d target, Vec3d base);

    public void stretch(Vec3d target, Vec3d base) {
        this.extendFully(target, base);
    }
}
