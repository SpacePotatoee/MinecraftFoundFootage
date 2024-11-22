package com.sp.entity.ik.parts.ik_chains;

public abstract class AngleConstraintIKChain extends StretchingIKChain {

    public AngleConstraintIKChain(double... lengths) {
        super(lengths);
    }

    public AngleConstraintIKChain(Segment... segments) {
        super(segments);
    }
    
    @Override
    public void reachBackwards(Vec3 base) {
        this.getFirst().move(base);

        Vec3 targetDir = this.get(1).getPosition().subtract(base).normalize();
        Vec3 newPos = base.add(targetDir.scale(this.getFirst().length));

        this.segments.get(1).move(newPos);

        Vec3 referencePoint = this.rotatePointOnLegPlane(base.add(this.getDownNormalOnLegPlane()), base, this.getFirst().angleOffset);

        Vec3 dotBaseDir = referencePoint.subtract(base).normalize();
        Vec3 dotTargetDir = this.get(1).getPosition().subtract(base).normalize();

        double angle = Math.toDegrees(Math.acos(dotBaseDir.dot(dotTargetDir)));

        if (angle > this.getFirst().angleSize) {
            double angleDifference = this.getFirst().angleSize - angle;

            Vec3 rotatedPos = this.rotatePointOnLegPlane(this.get(1).getPosition(), base, angleDifference);

            this.segments.get(1).move(rotatedPos);
        }

        for (int i = 0; i < this.segments.size() - 1; i++) {
            Segment currentSegment = this.segments.get(i);
            Segment nextSegment = this.segments.get(i + 1);

            nextSegment.move(this.moveSegment(nextSegment.getPosition(), currentSegment.getPosition(), currentSegment.length));
        }

        this.endJoint = this.moveSegment(this.endJoint, this.getLast().getPosition(), this.getLast().length);
    }

    public abstract Vec3 getDownNormalOnLegPlane();

    public Vec3 rotatePointOnLegPlane(Vec3 point, Vec3 base, double angle) {
        return MathUtil.rotatePointOnAPlaneAround(point, base, angle, this.getLegPlane());
    }

    /*
    @Override
    public void reachBackwards(Vec3 base) {
        this.getFirst().move(base);
        this.segments.get(1).move(this.getConstrainedPosForRootSegment());

        for (int i = 0; i < this.segments.size() - 1; i++) {
            Segment currentSegment = this.get(i);
            Segment nextSegment = this.get(i + 1);

            nextSegment.move(this.moveSegment(nextSegment.getPosition(), currentSegment.getPosition(), currentSegment.length));

            if (i < this.segments.size() - 2) {
                Segment nextNextSegment = this.get(i + 2);
                nextNextSegment.move(this.getConstrainedPositions(currentSegment.getPosition(), nextSegment, nextNextSegment.getPosition()));
            }
        }

        this.endJoint = this.moveSegment(this.endJoint, this.getLast().getPosition(), this.getLast().length);
        this.endJoint = this.getConstrainedPositions(this.get(this.segments.size() - 2).getPosition(), this.getLast(), this.endJoint);
    }
     */

    /*
    @Override
    public void reachForwards(Vec3 target) {
        this.endJoint = target;

        this.getLast().move(this.moveSegment(this.getLast().getPosition(), this.endJoint, this.getLast().length));
        for (int i = this.segments.size() - 1; i > 0; i--) {
            Segment currentSegment = this.segments.get(i);
            Segment nextSegment = this.segments.get(i - 1);

            nextSegment.move(this.moveSegment(nextSegment.getPosition(), currentSegment.getPosition(), nextSegment.length));
        }
    }
    */

    public Vec3 getLegPlane() {
        return MathUtil.getNormalClosestTo(this.getFirst().getPosition(), this.endJoint, this.getStretchingPos(this.endJoint, this.getFirst().getPosition()), this.getReferencePoint());
    }

    public abstract Vec3 getReferencePoint();

    public Vec3 getConstrainedPosForRootSegment() {
        Vec3 C = new Vec3(0, 1, 0);
        return this.getConstrainedPosForRootSegment(C);
    }

    /**
     * Get the angle at the given index in degrees
     * @param index the index of the segment you want to get the angle of
     * @return the angle at the given index in degrees
     */
    public double getAngleAt(int index) {
        if (index < 1) {
            PrAnCommonClass.throwInDevOnly(new IllegalArgumentException("Called **getAngleAt** with an index of 0. The index always needs to be at least 1" +
                    "Min Example: this.getAngleAt(1)"));
            return 0;
        }
        
        if (index > this.segments.size() - 2) {
            PrAnCommonClass.throwInDevOnly(new IllegalArgumentException("Called **getAngleAt** with an index bigger then the segment about -1. The index always needs to be at least 1 less then the total segments." +
                    "Max Example: this.getAngleAt(this.segments.size() - 2)"));
            return 0;
        }
        
        Segment previousSegment = this.segments.get(index - 1);
        Segment currentSegment = this.segments.get(index);
        Segment nextSegment = this.segments.get(index + 1);

        Vec3 baseDir = previousSegment.getPosition().subtract(currentSegment.getPosition()).normalize();
        Vec3 targetDir = nextSegment.getPosition().subtract(currentSegment.getPosition()).normalize();

        return Math.toDegrees(Math.acos(baseDir.dot(targetDir)));
    }

    public Vec3 getConstrainedPosForRootSegment(Vec3 downVector) {
        double angle = Math.toDegrees(MathUtil.calculateAngle(this.getFirst().getPosition(), this.segments.get(1).getPosition(), this.getFirst().getPosition().add(downVector)));
        double clampedAngle = Math.min(this.getFirst().angleSize, angle);

        if (clampedAngle == angle) return this.segments.get(1).getPosition();

        double angleDelta = clampedAngle - angle;

        //Vec3 normal = MathUtil.getNormalClosestTo(this.segments.get(1).getPosition(), this.getFirst().getPosition(), this.segments.get(2).getPosition(), this.getReferencePoint());
        return MathUtil.rotatePointOnAPlaneAround(this.segments.get(1).getPosition(), this.getFirst().getPosition(), angleDelta, this.getLegPlane());
    }

    public Vec3 getConstrainedPositions(Vec3 reference, Segment middle, Vec3 endpoint) {
        //Vec3 normal = MathUtil.getNormalClosestTo(endpoint, middle.getPosition(), reference, this.getReferencePoint());

        Vec3 referencePoint = MathUtil.rotatePointOnAPlaneAround(reference, middle.getPosition(), middle.angleOffset, this.getLegPlane());

        double angle = Math.toDegrees(MathUtil.calculateAngle(middle.getPosition(), endpoint, referencePoint));
        double clampedAngle = Math.min(middle.angleSize, angle);

        if (clampedAngle == angle) return endpoint;

        double angleDelta = clampedAngle - angle;

        return MathUtil.rotatePointOnAPlaneAround(endpoint, middle.getPosition(), angleDelta, this.getLegPlane());
    }
}
