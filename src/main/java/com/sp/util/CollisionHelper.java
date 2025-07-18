package com.sp.util;


import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector2d;
import org.joml.Vector4d;

public class CollisionHelper {
    public static double getCollisionOffset(Entity player, Vec3d adjustedMovement) {
        return Math.max(0, (getY(player.getPos()) - player.getY() + adjustedMovement.getY()));
    }

    public static boolean doesCollide(Entity player, Vec3d adjustedMovement) {
        return getCollisionOffset(player, adjustedMovement) > 0;
    }

    public static double getY(Vec3d pos) {
        return ((perlinNoise(new Vec3d(pos.getX(), 0.0, pos.getZ()).multiply(0.05))) * 10) + 31;
    }

    private static Vector4d mod289(Vector4d x) {
        Vector4d result = new Vector4d();
        result.x = x.x - Math.floor(x.x * (1.0 / 289.0)) * 289.0;
        result.y = x.y - Math.floor(x.y * (1.0 / 289.0)) * 289.0;
        result.z = x.z - Math.floor(x.z * (1.0 / 289.0)) * 289.0;
        result.w = x.w - Math.floor(x.w * (1.0 / 289.0)) * 289.0;
        return result;
    }

    private static Vector4d perm(Vector4d x) {
        Vector4d temp = new Vector4d(x);
        temp.mul(34.0);
        temp.add(1.0, 1.0, 1.0, 1.0);
        temp.mul(x);
        return mod289(temp);
    }

    public static float perlinNoise(Vec3d p) {
        org.joml.Vector3d a = new org.joml.Vector3d(
            Math.floor(p.x),
            Math.floor(p.y),
            Math.floor(p.z)
        );

        org.joml.Vector3d d = new org.joml.Vector3d(
            p.x - a.x,
            p.y - a.y,
            p.z - a.z
        );

        org.joml.Vector3d d2 = new org.joml.Vector3d(d).mul(d);
        org.joml.Vector3d temp = new org.joml.Vector3d(
            3.0 - 2.0 * d.x,
            3.0 - 2.0 * d.y,
            3.0 - 2.0 * d.z
        );
        d = new org.joml.Vector3d(d2).mul(temp);

        Vector4d b = new Vector4d(a.x, a.x, a.y, a.y);
        b.add(new Vector4d(0.0, 1.0, 0.0, 1.0));

        Vector4d k1 = perm(new Vector4d(b.x, b.y, b.x, b.y));

        Vector4d k1xy = new Vector4d(k1.x, k1.y, k1.x, k1.y);
        Vector4d bzzww = new Vector4d(b.z, b.z, b.w, b.w);
        Vector4d k2 = perm(new Vector4d(k1xy).add(bzzww));

        Vector4d c = new Vector4d(k2);
        c.add(new Vector4d(a.z, a.z, a.z, a.z));

        Vector4d k3 = perm(c);
        Vector4d k4 = perm(new Vector4d(c).add(1.0, 1.0, 1.0, 1.0));

        Vector4d o1 = fract(new Vector4d(k3).mul(1.0 / 41.0));
        Vector4d o2 = fract(new Vector4d(k4).mul(1.0 / 41.0));

        Vector4d o3 = new Vector4d();
        o3.add(new Vector4d(o2).mul(d.z));
        o3.add(new Vector4d(o1).mul(1.0 - d.z));

        Vector2d o4 = new Vector2d();
        o4.x = o3.y * d.x + o3.x * (1.0 - d.x);
        o4.y = o3.w * d.x + o3.z * (1.0 - d.x);

        return (float)(o4.y * d.y + o4.x * (1.0 - d.y));
    }

    private static Vector4d fract(Vector4d v) {
        Vector4d result = new Vector4d();
        result.x = v.x - Math.floor(v.x);
        result.y = v.y - Math.floor(v.y);
        result.z = v.z - Math.floor(v.z);
        result.w = v.w - Math.floor(v.w);
        return result;
    }
}
