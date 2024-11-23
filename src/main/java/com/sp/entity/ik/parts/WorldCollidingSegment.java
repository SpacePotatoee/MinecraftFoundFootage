package com.sp.entity.ik.parts;

import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class WorldCollidingSegment extends Segment {
    private World level;

    public WorldCollidingSegment(Builder builder) {
        super(builder);
    }

    public World getLevel() {
        return this.level;
    }

    public void setLevel(World level) {
        this.level = level;
    }

    /**
     * You need to call {@link #setLevel(World)} before calling this method!!
     */
    @Override
    public void move(Vec3d position) {
        this.move(position, true);
    }

    public void move(Vec3d position, boolean checkCollision) {
        this.move(position, checkCollision, 0);
    }

    public void move(Vec3d position, boolean checkCollision, double risingAmount) {
        if (this.level == null) {
            throw new IllegalStateException("WorldCollidingSegment has not been setup with a level");
        }

        Vec3d oldPosition = this.getPosition();

        super.move(position);

        if (checkCollision) {
            Vec3d collisionPoint = this.level.raycast(new RaycastContext(
                    oldPosition.add(0, risingAmount, 0),
                    this.getPosition(),
                    RaycastContext.ShapeType.COLLIDER,
                    RaycastContext.FluidHandling.NONE,
                    new ArrowEntity(this.level, this.getPosition().x, this.getPosition().y, this.getPosition().z)
            )).getPos();

            super.move(collisionPoint);
        }
    }
}
