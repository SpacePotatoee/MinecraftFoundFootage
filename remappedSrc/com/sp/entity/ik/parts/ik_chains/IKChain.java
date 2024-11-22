package com.sp.entity.ik.parts.ik_chains;

import net.dumbcode.projectnublar.entity.ik.parts.Segment;
import net.dumbcode.projectnublar.entity.ik.util.ArrayUtil;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IKChain {
    public static final int MAX_ITERATIONS = 10;
    public static final double TOLERANCE = 0.01;
    public List<Segment> segments = new ArrayList<>();
    public Vec3 endJoint = Vec3.ZERO;
    public double scale = 1;
    private double maxLength = 0;

    public IKChain(double... lengths) {
        for (double length : lengths) {
            this.segments.add(new Segment.Builder().length(length).build());
        }
        this.maxLength = Arrays.stream(lengths).sum();
    }

    public IKChain(Segment... segments) {
        Arrays.stream(segments).forEach(segment -> this.maxLength += segment.length);
        this.segments.addAll(List.of(segments));
    }

    public void solve(Vec3 target, Vec3 base) {
        for (int iteration = 0; iteration < MAX_ITERATIONS; iteration++) {
            if (this.isTargetToFar(target)) {
                this.iterate(target, base);
                break;
            }

            this.iterate(target, base);

            if (this.areStoppingConditionsMeet(target)) {
                break;
            }
        }
    }

    public void iterate(Vec3 target, Vec3 base) {
        this.getFirst().move(base);

        this.reachForwards(target);
        this.reachBackwards(base);
    }

    public void extendFully(Vec3 target, Vec3 base) {
        this.getFirst().move(base);

        Vec3 directionOfTarget = target.subtract(base).normalize();
        for (int i = 1; i < this.segments.size(); i++) {
            Segment prevSegment = this.segments.get(i - 1);
            Segment currentSegment = this.segments.get(i);

            currentSegment.move(prevSegment.getPosition().add(directionOfTarget.scale(prevSegment.length * this.getScale())));
        }
        this.endJoint = this.getLast().getPosition().add(directionOfTarget.scale(this.getLast().length * this.getScale()));
    }

    protected boolean isTargetToFar(Vec3 target) {
        return !target.closerThan(this.getFirst().getPosition(), this.getMaxLength());
    }

    protected boolean areStoppingConditionsMeet(Vec3 target) {
        return this.endJoint.closerThan(target, TOLERANCE);
    }

    public void reachForwards(Vec3 target) {
        this.endJoint = target;

        this.getLast().move(this.moveSegment(this.getLast().getPosition(), this.endJoint, this.getLast().length));
        for (int i = this.segments.size() - 1; i > 0; i--) {
            Segment currentSegment = this.segments.get(i);
            Segment nextSegment = this.segments.get(i - 1);

            nextSegment.move(this.moveSegment(nextSegment.getPosition(), currentSegment.getPosition(), nextSegment.length));
        }
    }

    public void reachBackwards(Vec3 base) {
        this.getFirst().move(base);

        for (int i = 0; i < this.segments.size() - 1; i++) {
            Segment currentSegment = this.segments.get(i);
            Segment nextSegment = this.segments.get(i + 1);

            nextSegment.move(this.moveSegment(nextSegment.getPosition(), currentSegment.getPosition(), currentSegment.length));
        }
        this.endJoint = this.moveSegment(this.endJoint, this.getLast().getPosition(), this.getLast().length);
    }

    public double getScale() {
        return this.scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    public void setSegmentsTo(List<Vec3> joints) {
        for (int i = 0; i < joints.size() - 2; i++) {
            this.segments.get(i).move(joints.get(i));
        }
        this.endJoint = ArrayUtil.getLast(joints);
    }

    public Vec3 moveSegment(Vec3 point, Vec3 pullTowards, double length) {
        Vec3 direction = pullTowards.subtract(point).normalize();
        return pullTowards.subtract(direction.scale(length * this.getScale()));
    }

    public double getMaxLength() {
        return this.maxLength;
    }

    public List<Vec3> getJoints() {
        List<Vec3> joints = new ArrayList<>();
        for (Segment segment : this.segments) {
            joints.add(segment.getPosition());
        }
        joints.add(this.endJoint);
        return joints;
    }

    public Segment getFirst() {
        return ArrayUtil.getFirst(this.segments);
    }

    public Segment getLast() {
        return ArrayUtil.getLast(this.segments);
    }

    public Segment get(int i) {
        return this.segments.get(i);
    }
}