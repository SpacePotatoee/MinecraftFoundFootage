package com.sp.render.physics;

import java.util.Vector;

public class PhysicsStick {
    private static final Vector<PhysicsStick> allInstances = new Vector<>();
    private PhysicsPoint pointA;
    private PhysicsPoint pointB;
    private final float length;

    public PhysicsStick(PhysicsPoint pointA, PhysicsPoint pointB, float length){
        this.pointA = pointA;
        this.pointB = pointB;
        this.length = length;
//        allInstances.add(this);
    }

    public void set(PhysicsPoint pointA, PhysicsPoint pointB){
        this.pointA = pointA;
        this.pointB = pointB;
    }

    public void updateSticks() {
        double dx = this.pointA.getX() - this.pointB.getX();
        double dy = this.pointA.getY() - this.pointB.getY();
        double dz = this.pointA.getZ() - this.pointB.getZ();

        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

        double difference = this.length - distance;
        double percent = difference / distance / 2;

        if(!this.pointA.isFixed()) {
            this.pointA.x += dx * percent;
            this.pointA.y += dy * percent;
            this.pointA.z += dz * percent;
        }

        if(!this.pointB.isFixed()) {
            this.pointB.x -= dx * percent;
            this.pointB.y -= dy * percent;
            this.pointB.z -= dz * percent;
        }
    }

    public static synchronized Vector<PhysicsStick> getAllInstances(){
        return (Vector<PhysicsStick>) allInstances;
    }



}
