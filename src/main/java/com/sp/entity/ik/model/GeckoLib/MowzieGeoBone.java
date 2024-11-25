package com.sp.entity.ik.model.GeckoLib;

import com.sp.entity.ik.model.BoneAccessor;
import com.sp.entity.ik.util.MathUtil;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.joml.*;
import software.bernie.geckolib.cache.object.GeoBone;

import java.lang.Math;
import java.util.List;

/**
 * Provided by Bob Mowzie from <a href="https://www.curseforge.com/minecraft/mc-mods/mowzies-mobs">Mowzie's Mobs</a>
 */
public class MowzieGeoBone extends GeoBone implements BoneAccessor /* only the interface is mine but the code in this class is entirely provided by Bob Mowzie */ {

    public Matrix4f rotationOverride;
    public boolean inheritRotation = true;
    public boolean inheritTranslation = true;
    protected boolean forceMatrixTransform = false;

    private boolean isDynamicJoint = false;

    public MowzieGeoBone(@Nullable GeoBone parent, String name, Boolean mirror, @Nullable Double inflate, @Nullable Boolean dontRender, @Nullable Boolean reset) {
        super(parent, name, mirror, inflate, dontRender, reset);
        rotationOverride = null;
    }

    public MowzieGeoBone(MowzieGeoBone geoBone) {
        super(null, geoBone.getName() + "_chain", geoBone.getMirror(), geoBone.getInflate(), geoBone.shouldNeverRender(), geoBone.getReset());
        this.setPos(geoBone.getPos());
        this.setRot(geoBone.getRot());
        this.setPivotX(geoBone.getPivotX());
        this.setPivotY(geoBone.getPivotY());
        this.setPivotZ(geoBone.getPivotZ());
        this.setScale(geoBone.getScale());

        this.getCubes().addAll(geoBone.getCubes());
        this.saveInitialSnapshot();
        this.getChildBones().addAll(geoBone.getChildBones());
    }

    public static void removeMatrixTranslation(Matrix4f matrix) {
        matrix.m30(0);
        matrix.m31(0);
        matrix.m32(0);
    }

    public MowzieGeoBone getParent() {
        return (MowzieGeoBone) super.getParent();
    }

    // Position utils
    public void addPos(Vec3d vec) {
        addPos((float) vec.x, (float) vec.y, (float) vec.z);
    }

    public void addPos(float x, float y, float z) {
        addPosX(x);
        addPosY(y);
        addPosZ(z);
    }

    public void addPosX(float x) {
        setPosX(getPosX() + x);
    }

    public void addPosY(float y) {
        setPosY(getPosY() + y);
    }

    public void addPosZ(float z) {
        setPosZ(getPosZ() + z);
    }

    public void setPos(float x, float y, float z) {
        setPosX(x);
        setPosY(y);
        setPosZ(z);
    }

    public Vec3d getPos() {
        return new Vec3d(getPosX(), getPosY(), getPosZ());
    }

    public void setPos(Vec3d vec) {
        setPos((float) vec.x, (float) vec.y, (float) vec.z);
    }

    // Rotation utils
    public void addRot(Vec3d vec) {
        addRot((float) vec.x, (float) vec.y, (float) vec.z);
    }

    public void addRot(float x, float y, float z) {
        addRotX(x);
        addRotY(y);
        addRotZ(z);
    }

    public void addRotX(float x) {
        setRotX(getRotX() + x);
    }

    public void addRotY(float y) {
        setRotY(getRotY() + y);
    }

    public void addRotZ(float z) {
        setRotZ(getRotZ() + z);
    }

    public void setRot(float x, float y, float z) {
        setRotX(x);
        setRotY(y);
        setRotZ(z);
    }

    public Vector3d getRot() {
        return new Vector3d(getRotX(), getRotY(), getRotZ());
    }

    public void setRot(Vector3d vec) {
        setRot((float) vec.x(), (float) vec.y(), (float) vec.z());
    }

    public void setRot(Vec3d vec) {
        setRot((float) vec.x, (float) vec.y, (float) vec.z);
    }

    // Scale utils
    public void multiplyScale(Vec3d vec) {
        multiplyScale((float) vec.x, (float) vec.y, (float) vec.z);
    }

    public void multiplyScale(float x, float y, float z) {
        setScaleX(getScaleX() * x);
        setScaleY(getScaleY() * y);
        setScaleZ(getScaleZ() * z);
    }

    public void setScale(float x, float y, float z) {
        setScaleX(x);
        setScaleY(y);
        setScaleZ(z);
    }

    public Vector3d getScale() {
        return new Vector3d(getScaleX(), getScaleY(), getScaleZ());
    }

    public void setScale(Vec3d vec) {
        setScale((float) vec.x, (float) vec.y, (float) vec.z);
    }

    public void setScale(Vector3d vec) {
        setScale((float) vec.x(), (float) vec.y(), (float) vec.z());
    }

    public void setScale(float scale) {
        setScale(scale, scale, scale);
    }

    public void addRotationOffsetFromBone(MowzieGeoBone source) {
        setRotX(getRotX() + source.getRotX() - source.getInitialSnapshot().getRotX());
        setRotY(getRotY() + source.getRotY() - source.getInitialSnapshot().getRotY());
        setRotZ(getRotZ() + source.getRotZ() - source.getInitialSnapshot().getRotZ());
    }

    public boolean isForceMatrixTransform() {
        return forceMatrixTransform;
    }

    public void setForceMatrixTransform(boolean forceMatrixTransform) {
        this.forceMatrixTransform = forceMatrixTransform;
    }

    public Matrix4f getModelRotationMat() {
        Matrix4f matrix = new Matrix4f(getModelSpaceMatrix());
        removeMatrixTranslation(matrix);
        return matrix;
    }

    public void setModelXformOverride(Matrix4f mat) {
        rotationOverride = mat;
    }

    public boolean isDynamicJoint() {
        return isDynamicJoint;
    }

    public void setDynamicJoint(boolean dynamicJoint) {
        isDynamicJoint = dynamicJoint;
    }

    @Override
    public Vec3d getPosition() {
        return MathUtil.toVec3(this.getWorldPosition());
    }

    @Override
    public void moveTo(Vec3d to, @Nullable Vec3d facing, Entity entity) {
        this.setForceMatrixTransform(true);

        Matrix4f xformOverride = new Matrix4f();

        Vec3d newModelPosWorldSpace = MathUtil.rotatePointOnAPlaneAround(to, entity.getPos(), -180 + entity.getBodyYaw(), new Vec3d(0, 1, 0));
        // Translation
        xformOverride = xformOverride.translate((float) newModelPosWorldSpace.x, (float) newModelPosWorldSpace.y, (float) newModelPosWorldSpace.z);

        if (facing != null) {
            Vec3d newTargetVecWorldSpace = MathUtil.rotatePointOnAPlaneAround(facing, entity.getPos(), -180 + entity.getBodyYaw(), new Vec3d(0, 1, 0));

            Vector3f forward = new Vector3f(facing.toVector3f());
            forward.sub(to.toVector3f()).normalize();

            Quaternionf q;
            Vector3d p1 = MathUtil.toVector3d(newModelPosWorldSpace);
            Vector3d p2 = MathUtil.toVector3d(newTargetVecWorldSpace);

            Vector3f desiredDir = newTargetVecWorldSpace.toVector3f().sub(newModelPosWorldSpace.toVector3f(),  new Vector3f()).normalize();

            Vector3f startingDir = new Vector3f(0, 0, 1);
            double dot = desiredDir.dot(startingDir);
            if (dot > 0.9999999) {
                q = new Quaternionf();
            } else {
                q = new Quaternionf().rotateTo(new Vector3f(0,0,1), desiredDir).normalize();
            }
            xformOverride.rotate(q);
        }

        this.setWorldSpaceMatrix(xformOverride);
    }

    @Override
    public List<BoneAccessor> getChildren() {
        return this.getChildBones().stream().map(bone -> (BoneAccessor) bone).toList();
    }
}