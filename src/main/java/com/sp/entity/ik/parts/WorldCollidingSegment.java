package com.sp.entity.ik.parts;

public class WorldCollidingSegment extends Segment {
    private Level level;

    public WorldCollidingSegment(Builder builder) {
        super(builder);
    }

    public Level getLevel() {
        return this.level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    /**
     * You need to call {@link #setLevel(Level)} before calling this method!!
     */
    @Override
    public void move(Vec3 position) {
        this.move(position, true);
    }

    public void move(Vec3 position, boolean checkCollision) {
        this.move(position, checkCollision, 0);
    }

    public void move(Vec3 position, boolean checkCollision, double risingAmount) {
        if (this.level == null) {
            throw new IllegalStateException("WorldCollidingSegment has not been setup with a level");
        }

        Vec3 oldPosition = this.getPosition();

        super.move(position);

        if (checkCollision) {
            Vec3 collisionPoint = this.level.clip(new ClipContext(
                    oldPosition.add(0, risingAmount, 0),
                    this.getPosition(),
                    ClipContext.Block.COLLIDER,
                    ClipContext.Fluid.NONE,
                    new Arrow(this.level, this.getPosition().x(), this.getPosition().y(), this.getPosition().z())
            )).getLocation();

            super.move(collisionPoint);
        }
    }
}
