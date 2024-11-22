package com.sp.entity.ik.parts;

import net.minecraft.world.phys.Vec3;

public class Segment {
    public double length;
    public StretchDirection stretchDirection;
    public boolean hasAngleConstraints;
    public double angleOffset;
    public double angleSize;
    private Vec3 position = Vec3.ZERO;

    protected Segment(Builder builder) {
        this.length = builder.length;
        this.stretchDirection = builder.stretchDirection;
        this.angleSize = builder.angleSize;
        this.angleOffset = builder.angleOffset;
        this.hasAngleConstraints = builder.hasAngleConstraints;
    }

    public Vec3 getPosition() {
        return this.position;
    }

    public void move(Vec3 position) {
        this.position = position;
    }

    public enum StretchDirection {
        FORWARDS,
        BACKWARDS,
        DOWN,
        TARGET,
        NONE
    }

    public static class Builder {
        private double length;
        private StretchDirection stretchDirection = StretchDirection.TARGET;
        private boolean hasAngleConstraints = true;
        private double angleOffset = 90; // plus is forwards and minus is backwards
        private double angleSize = 30;

        public Builder length(double length) {
            this.length = length;
            return this;
        }

        public Builder stretchDirection(StretchDirection stretchDirection) {
            this.stretchDirection = stretchDirection;
            return this;
        }

        public Builder angleConstraints(boolean hasAngleConstraints) {
            this.hasAngleConstraints = hasAngleConstraints;
            return this;
        }

        public Builder angleOffset(double angleOffset) {
            this.angleOffset = angleOffset;
            return this;
        }

        public Builder angleSize(double angleSize) {
            this.angleSize = angleSize;
            return this;
        }

        public Segment build() {
            return new Segment(this);
        }
    }
}
