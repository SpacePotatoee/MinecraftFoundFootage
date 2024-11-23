package com.sp.entity.ik.model;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface BoneAccessor {
    Vec3d getPosition();

    /**
     * @param to     the point to move to
     * @param facing at wha the bone should face, if null, the bone will not rotate
     * @param entity the entity the model of the bone belongs to
     */
    void moveTo(Vec3d to, @Nullable Vec3d facing, Entity entity);

    List<BoneAccessor> getChildren();
}
