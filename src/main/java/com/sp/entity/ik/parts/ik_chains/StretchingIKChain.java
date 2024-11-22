package com.sp.entity.ik.parts.ik_chains;

import net.dumbcode.projectnublar.entity.ik.parts.Segment;
import net.minecraft.world.phys.Vec3;

public abstract class StretchingIKChain extends IKChain {


    public StretchingIKChain(double... lengths) {
        super(lengths);
    }

    public StretchingIKChain(Segment... segments) {
        super(segments);
    }

    public static Vec3 stretchToTargetPos(Vec3 target, StretchingIKChain chain) {
        Vec3 direction = target.subtract(chain.getFirst().getPosition());
        return chain.getFirst().getPosition().add(direction.scale(chain.getMaxLength() * 2));
    }

    @Override
    public void solve(Vec3 target, Vec3 base) {
        this.stretch(this.getStretchingPos(target, base), base);
        super.solve(target, base);
    }

    public abstract Vec3 getStretchingPos(Vec3 target, Vec3 base);

    public void stretch(Vec3 target, Vec3 base) {
        this.extendFully(target, base);
    }
}
