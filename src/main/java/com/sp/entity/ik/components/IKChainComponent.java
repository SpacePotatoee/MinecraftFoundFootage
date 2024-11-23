package com.sp.entity.ik.components;

import com.sp.entity.ik.parts.ik_chains.IKChain;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public abstract class IKChainComponent<C extends IKChain, E extends IKAnimatable<E>> implements IKModelComponent<E> {

    protected List<C> limbs = new ArrayList<>();

    public List<C> getLimbs() {
        return this.limbs;
    }

    abstract C setLimb(int index, Vec3d base, Entity entity);
}
