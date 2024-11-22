package com.sp.entity.ik.components;

import net.dumbcode.projectnublar.entity.ik.parts.ik_chains.IKChain;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public abstract class IKChainComponent<C extends IKChain, E extends IKAnimatable<E>> implements IKModelComponent<E> {

    protected List<C> limbs = new ArrayList<>();

    public List<C> getLimbs() {
        return this.limbs;
    }

    abstract C setLimb(int index, Vec3 base, Entity entity);
}
