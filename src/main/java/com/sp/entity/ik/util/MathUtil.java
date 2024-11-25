package com.sp.entity.ik.util;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaterniond;
import org.joml.Quaternionf;
import org.joml.Vector3d;

import java.util.Arrays;
import java.util.List;

public class MathUtil {
    public static Vec3d toVec3(Vector3d vector3d) {
        return new Vec3d(vector3d.x(), vector3d.y(), vector3d.z());
    }

    public static Vector3d toVector3d(Vec3d vector3d) {
        return new Vector3d(vector3d.x, vector3d.y, vector3d.z);
    }

    public static Vec3d getFlatRotationVector(Entity entity) {
        return getFlatRotationVector(entity.getBodyYaw());
    }

    public static Vec3d getFlatRotationVector(double yRot) {
        float f = 0;
        float g = (float) (-yRot * 0.017453292F);
        float h = MathHelper.cos(g);
        float i = MathHelper.sin(g);
        float j = MathHelper.cos(f);
        float k = MathHelper.sin(f);
        return new Vec3d(i * j, -k, h * j).normalize();
    }

    public static Vec3d getOpposingFlatRotationVector(Entity entity) {
        return getFlatRotationVector(entity.getBodyYaw() - 180);
    }

    public static Vec3d getRotation(Vec3d base, Vec3d target) {
        float dz = (float) (target.z - base.z);
        float dx = (float) (target.x - base.x);
        float dy = (float) (target.y - base.y);

        float yaw = (float) MathHelper.atan2(dz, dx);
        float pitch = (float) MathHelper.atan2(Math.sqrt(dz * dz + dx * dx), dy);
        return wrapAngles(new Vec3d(yaw, pitch, 0));
    }

    private static Vec3d wrapAngles(Vec3d vec3d) {
        double x = vec3d.x;
        double y = vec3d.y;
        double z = vec3d.z;

        while (x > Math.PI) x -= 2 * Math.PI;
        while (x < -Math.PI) x += 2 * Math.PI;

        while (y > Math.PI) y -= 2 * Math.PI;
        while (y < -Math.PI) y += 2 * Math.PI;

        while (z > Math.PI) z -= 2 * Math.PI;
        while (z < -Math.PI) z += 2 * Math.PI;

        return new Vec3d(x, y, z);
    }

    public static Vec3d dividePos(Vec3d v1, double divide) {
        return new Vec3d(v1.x / divide, v1.y / divide, v1.z / divide);
    }

    public static Quaternionf quatFromRotationXYZ(float x, float y, float z, boolean degrees) {
        if (degrees) {
            x *= ((float) Math.PI / 180F);
            y *= ((float) Math.PI / 180F);
            z *= ((float) Math.PI / 180F);
        }
        return (new Quaternionf()).rotationXYZ(x, y, z);
    }

    public static Vec3d getAverage(List<Vec3d> points) {
        Vec3d sum = Vec3d.ZERO;

        for (Vec3d point : points) {
            sum = sum.add(point);
        }

        return dividePos(sum, points.size());
    }

    public static Vec3d getAverage(Vec3d... points) {
        return getAverage(Arrays.stream(points).toList());
    }

    /**
     * @param A Point you want the angle of
     * @param B the other point
     * @param C the 0 Point
     * @return the angle of A in radiant [0 - 180]
     **/
    public static double calculateAngle(Vec3d A, Vec3d B, Vec3d C) {
        double a = C.distanceTo(B);
        double b = A.distanceTo(C);
        double c = A.distanceTo(B);

        double cosA = (b * b + c * c - a * a) / (2 * b * c);

        return Math.acos(cosA);
    }

    public static Vec3d rotatePointOnAPlaneAround(Vec3d RotatedPoint, Vec3d stationaryPoint, double angle, Vec3d rotationAxis) {
        Vector3d A = new Vector3d(stationaryPoint.x, stationaryPoint.y, stationaryPoint.z); // Point A
        //rotated vector
        Vector3d C = new Vector3d(RotatedPoint.x, RotatedPoint.y, RotatedPoint.z); // Point C

        // Create the rotation quaternion
        Quaterniond rotation = new Quaterniond().rotateAxis(Math.toRadians(angle), rotationAxis.x, rotationAxis.y, rotationAxis.z);

        // Rotate the vector
        Vector3d rotatedV1 = new Vector3d();
        rotation.transform(new Vector3d(C).sub(A), rotatedV1);

        return new Vec3d(new Vector3d(rotatedV1).add(A).x(), new Vector3d(rotatedV1).add(A).y(), new Vector3d(rotatedV1).add(A).z());
    }

    public static Vec3d getUpDirection(Vec3d v1, Vec3d v2, Vec3d v3) {
        // Calculate AB and AC
        Vec3d AB = v2.subtract(v1);
        Vec3d AC = v3.subtract(v1);

        Vec3d axis = AB.crossProduct(AC).normalize();

        return axis;
    }

    public static Vec3d getClosestNormalRelativeToEntity(Vec3d basePoint, Vec3d v2, Vec3d v3, Entity entity) {
        Vec3d referencePoint = getFlatRotationVector(entity.getBodyYaw() + 90);
        Vec3d normal = getNormalClosestTo(basePoint, v2, v3, basePoint.add(referencePoint.multiply(100)));
        return normal;
    }

    public static Vec3d getNormalClosestTo(Vec3d basePoint, Vec3d v2, Vec3d v3, Vec3d orientationPoint) {
        Vec3d normal = getUpDirection(basePoint, v2, v3);
        Vec3d oppositeNormal = normal.negate();

        return basePoint.add(normal).squaredDistanceTo(orientationPoint) < basePoint.add(oppositeNormal).squaredDistanceTo(orientationPoint) ? normal : oppositeNormal;
    }

    public static Vec3d lerpVec3(int step, Vec3d OldPos, Vec3d newPos) {
        double d = 1.0 / (double) step;
        double newX = MathHelper.lerp(d, OldPos.x, newPos.x);
        double newY = MathHelper.lerp(d, OldPos.y, newPos.y);
        double newZ = MathHelper.lerp(d, OldPos.z, newPos.z);
        return new Vec3d(newX, newY, newZ);
    }

    public static Vec3d lerpVector3d(int step, Vector3d OldPos, Vector3d newPos) {
        return lerpVec3(step, toVec3(OldPos), toVec3(newPos));
    }

    public static Vec3d convertToFlatVector(Vec3d v1) {
        return new Vec3d((float) v1.x, 0, (float) v1.z);
    }
}
