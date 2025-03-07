package com.sp.render.physics;

import net.minecraft.util.math.Vec3d;
import org.joml.Vector3d;

import java.util.Vector;

public class PhysicsPoint {
    private static final Vector<PhysicsPoint> allInstances = new Vector<>();
    public double x;
    public double y;
    public double z;
    private double oldX;
    private double oldY;
    private double oldZ;

    private final boolean fixed;

    public PhysicsPoint(Vec3d position, boolean fixed){
        this(position, position, fixed);
    }

    public PhysicsPoint(Vec3d position, Vec3d oldPosition, boolean fixed){
        this.x = position.getX();
        this.y = position.getY();
        this.z = position.getZ();
        this.oldX = oldPosition.getX();
        this.oldY = oldPosition.getY();
        this.oldZ = oldPosition.getZ();

        this.fixed = fixed;

//        allInstances.add(this);
    }

    public void set(Vector3d position){
        this.set(new Vec3d(position.x, position.y, position.z));
    }

    public void set(Vec3d position){
        this.set(position, position);
    }

    public void set(Vec3d position, Vec3d oldPosition){
        this.x = position.getX();
        this.y = position.getY();
        this.z = position.getZ();
        this.oldX = oldPosition.getX();
        this.oldY = oldPosition.getY();
        this.oldZ = oldPosition.getZ();
    }

    public void updatePoint(){

        if(!this.isFixed()) {
            double velocityX = (this.x - this.oldX);
            double velocityY = (this.y - this.oldY);
            double velocityZ = (this.z - this.oldZ);

            this.oldX = this.x;
            this.oldY = this.y;
            this.oldZ = this.z;

            this.x += velocityX;
            this.y += velocityY;
            this.z += velocityZ;

            this.y -= 0.5;


//            if (this.x > this.width) {
//                this.x = this.width;
//                this.oldX = this.x + velocityX;
//            } else if (this.x < 0) {
//                this.x = 0;
//                this.oldX = this.x + velocityX;
//            }
//
//            if (this.y > this.height) {
//                this.y = this.height;
//                this.oldY = this.y + velocityY;
//            } else if (this.y < 0) {
//                this.y = 0;
//                this.oldY = this.y + velocityY;
//            }
        }
    }

    public double getX(){
        return this.x;
    }

    public double getY(){
        return this.y;
    }

    public double getZ(){
        return this.z;
    }

    public boolean isFixed(){
        return this.fixed;
    }


    public Vec3d getPosition() {
        return new Vec3d(this.x, this.y, this.z);
    }

    public static synchronized Vector<PhysicsPoint> getAllInstances(){
        return (Vector<PhysicsPoint>) allInstances;
    }
}
