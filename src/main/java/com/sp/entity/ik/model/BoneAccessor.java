package com.sp.entity.ik.model;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;

public interface BoneAccessor {
    Vec3 getPosition();

    /**
     * @param to     the point to move to
     * @param facing at wha the bone should face, if null, the bone will not rotate
     * @param entity the entity the model of the bone belongs to
     */
    void moveTo(Vec3 to, @Nullable Vec3 facing, Entity entity);

    List<BoneAccessor> getChildren();
}
