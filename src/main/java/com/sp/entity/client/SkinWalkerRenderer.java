package com.sp.entity.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.sp.entity.custom.SkinWalkerEntity;
import com.sp.render.physics.PhysicsPoint;
import com.sp.render.physics.PhysicsStick;
import com.sp.util.MathStuff;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.joml.*;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.cache.object.GeoCube;
import software.bernie.geckolib.cache.object.GeoQuad;
import software.bernie.geckolib.cache.object.GeoVertex;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import java.lang.Math;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SkinWalkerRenderer extends GeoEntityRenderer<SkinWalkerEntity> {
    private Double prevWorldX;
    private Double prevWorldY;
    private Double prevWorldZ;
//    private PhysicsPoint pointA;
    private PhysicsPoint pointB;
//    private PhysicsStick stick;
    private ArrayList<PhysicsPoint> points;
    private ArrayList<PhysicsStick> sticks;


    public SkinWalkerRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new SkinWalkerModel());
    }

    @Override
    public void render(SkinWalkerEntity entity, float entityYaw, float partialTick, MatrixStack poseStack, VertexConsumerProvider bufferSource, int packedLight) {
        float speed = entity.limbAnimator.getSpeed(partialTick);
        float pos = entity.limbAnimator.getPos(partialTick);

        if (speed > 1.0F) {
            speed = 1.0F;
        }



        float h = MathHelper.lerpAngleDegrees(partialTick, entity.prevBodyYaw, entity.bodyYaw);
        float j = MathHelper.lerpAngleDegrees(partialTick, entity.prevHeadYaw, entity.headYaw);
        float yaw = h;

        float pitch = MathHelper.lerp(partialTick, entity.prevPitch, entity.getPitch());

        Vec3d vec3d = this.getPositionOffset(entity, partialTick);
        double e = entity.getY() + vec3d.getY();
        poseStack.push();
        poseStack.translate(0, e, 0);
        poseStack.pop();

        float animationProgress = this.getAnimationProgress(entity, partialTick);
        this.animateModel(entity, bufferSource, poseStack, pos, speed, animationProgress, -yaw, -pitch, partialTick);
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    protected void animateModel(SkinWalkerEntity entity, VertexConsumerProvider vertexConsumers, MatrixStack matrices, float limbPos, float limbSpeed, float animationProgress, float yaw, float pitch, float partialTicks) {
//        Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();

//        if(camera != null) {
//            Vec3d cameraPos = camera.getPos();
//            PhysicsPoint pointA = new PhysicsPoint(new Vec3d(48 + MathHelper.sin(RenderSystem.getShaderGameTime() * 2000f) * 5, 78, 494), new Vec3d(48 + MathHelper.sin(RenderSystem.getShaderGameTime() * 2000f) * 5, 78, 494), true);
//            if(this.pointB == null){
//                this.pointB = new PhysicsPoint(Vec3d.ZERO, Vec3d.ZERO, false);
//            }
//            PhysicsStick stick = new PhysicsStick(pointA, this.pointB, 3);
//
//            this.pointB.updatePoint();
//            stick.updateSticks();
//            Matrix4f matrix4f = matrices.peek().getPositionMatrix();
//            VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getDebugLineStrip(20.0));
//
//            vertexConsumer.vertex(matrix4f, (float) (pointA.x - cameraPos.x), (float) (pointA.y - cameraPos.y), (float) (pointA.z - cameraPos.z)).color(1, 0, 0, 0.0f).next();
//            vertexConsumer.vertex(matrix4f, (float) (pointA.x - cameraPos.x), (float) (pointA.y - cameraPos.y), (float) (pointA.z - cameraPos.z)).color(1, 0, 0, 1.0f).next();
//            vertexConsumer.vertex(matrix4f, (float) (this.pointB.getX() - cameraPos.x), (float) (this.pointB.getY() - cameraPos.y), (float) (this.pointB.getZ() - cameraPos.z)).color(1, 0, 0, 1.0f).next();
//            vertexConsumer.vertex(matrix4f, (float) (this.pointB.getX() - cameraPos.x), (float) (this.pointB.getY() - cameraPos.y), (float) (this.pointB.getZ() - cameraPos.z)).color(1, 0, 0, 0.0f).next();
//        }


        Optional<GeoBone> headBone = this.model.getBone("head");
        Optional<GeoBone> bodyBone = this.model.getBone("body");
        Optional<GeoBone> rightArm = this.model.getBone("right_arm");
        Optional<GeoBone> leftArm = this.model.getBone("left_arm");
        Optional<GeoBone> rightLeg = this.model.getBone("right_leg");
        Optional<GeoBone> leftLeg = this.model.getBone("left_leg");

        if(headBone.isPresent() && bodyBone.isPresent() && rightArm.isPresent() && leftArm.isPresent() && rightLeg.isPresent() && leftLeg.isPresent()) {
            Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
            GeoBone head = headBone.get();
            GeoBone body = bodyBone.get();
            GeoBone right_arm = rightArm.get();
            GeoBone left_arm = leftArm.get();
            GeoBone right_leg = rightLeg.get();
            GeoBone left_leg = leftLeg.get();

//            List<GeoBone> boneList = new ArrayList<>();
//
//            boneList.add(right_arm);
//            boneList.add(left_arm);
//            boneList.add(right_leg);
//            boneList.add(left_leg);
//
            if(camera != null) {
//                for (GeoBone bone : boneList) {
                    Vec3d cameraPos = camera.getPos();
//                    int index = boneList.indexOf(bone);

                if(this.prevWorldX == null || this.prevWorldY == null || this.prevWorldZ == null){
                    this.prevWorldX = right_arm.getModelPosition().add(entity.getLerpedPos(partialTicks).toVector3f()).x;
                    this.prevWorldY = right_arm.getModelPosition().add(entity.getLerpedPos(partialTicks).toVector3f()).y;
                    this.prevWorldZ = right_arm.getModelPosition().add(entity.getLerpedPos(partialTicks).toVector3f()).z;
                }
                    Vector3d vector3d = right_arm.getModelPosition().add(entity.getLerpedPos(partialTicks).toVector3f());

//                    Vector3d vector3d = right_arm.getWorldPosition();

//                    double x = vector3d.x;
//                    double y = vector3d.y;
//                    double z = vector3d.z;

                    if (right_arm.getWorldPosition().x != 0 && right_arm.getWorldPosition().y != 0 && right_arm.getWorldPosition().z != 0) {
                        PhysicsPoint pointA = new PhysicsPoint(new Vec3d(vector3d.x, vector3d.y, vector3d.z), true);
//                        if(this.pointA == null){
//                            this.pointA = new PhysicsPoint(new Vec3d(x, y, z), true);
//                        }
//                        pointA.set(vector3d);

                        if(this.pointB == null){
                            this.pointB = new PhysicsPoint(Vec3d.ZERO, Vec3d.ZERO, false);
                        }
                        PhysicsStick stick = new PhysicsStick(pointA, this.pointB, 1);

                        this.pointB.updatePoint();
                        stick.updateSticks();

//                        if (this.points == null) {
//                            this.initialize(boneList);
//                        }
//                        PhysicsPoint currentPoint = this.points.get(index);
//                        this.points.get(index).updatePoint();

//                        Vec3d currentPointPosition = this.pointB.getPosition().subtract(cameraPos).rotateX((float) Math.toRadians(90));

//                        this.sticks.get(index).set(pointA);
//                        this.sticks.get(index).updateSticks();


                        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
                        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getDebugLineStrip(20.0));

                        Vec2f angles = this.calculateAngles(vector3d.x, vector3d.y, vector3d.z, this.pointB.getX(), this.pointB.getY(), this.pointB.getZ(), yaw);

                        float dirX = (float) (this.pointB.getX() - vector3d.x);
                        float dirY = (float) (this.pointB.getY() - vector3d.y);
                        float dirZ = (float) (this.pointB.getZ() - vector3d.z);

//                        System.out.println(entity.getYaw());
//                        System.out.println(yaw);
//                        System.out.println("=====================");
                        Quaternionf quaternionf = new Quaternionf(new AxisAngle4f((float) Math.toRadians(90), new Vector3f(0,0,1).rotateY((float) Math.toRadians(yaw + 90))));
                        Vector3f normalizedDir = new Vector3f(dirX, dirY, dirZ).rotate(quaternionf).normalize();

//                        right_arm.setRotX(-angles.x);
                        right_arm.setRotZ(-angles.y);

                        vertexConsumer.vertex(matrix4f, (float) (0 - cameraPos.x), (float) (0 - cameraPos.y), (float) (0 - cameraPos.z)).color(0, 0, 0, 0.0f).next();
                        vertexConsumer.vertex(matrix4f, (float) (0 - cameraPos.x), (float) (0 - cameraPos.y), (float) (0 - cameraPos.z)).color(0, 0, 0, 1.0f).next();
                        vertexConsumer.vertex(matrix4f, (float) (normalizedDir.x - cameraPos.x), (float) (normalizedDir.y - cameraPos.y), (float) (normalizedDir.z - cameraPos.z)).color(1, 0, 0, 1.0f).next();
                        vertexConsumer.vertex(matrix4f, (float) (normalizedDir.x - cameraPos.x), (float) (normalizedDir.y - cameraPos.y), (float) (normalizedDir.z - cameraPos.z)).color(1, 0, 0, 0.0f).next();

                    }
//                }

//                if(this.prevWorldX != right_arm.getWorldPosition().x) {
                    this.prevWorldX = vector3d.x;
//                }
//                if(this.prevWorldY != right_arm.getWorldPosition().y) {
                    this.prevWorldY = vector3d.y;
//                }
//                if(this.prevWorldZ != right_arm.getWorldPosition().z) {
                    this.prevWorldZ = vector3d.z;
//                }
            }

//            head.setRotY(yaw * (float) (Math.PI / 180.0));
//            head.setRotX(pitch * (float) (Math.PI / 180.0));
//
//            right_arm.setRotX(MathHelper.cos(limbPos * 0.6662F + (float) Math.PI) * 2.0F * limbSpeed * 0.5F);
//            left_arm.setRotX(MathHelper.cos(limbPos * 0.6662F) * 2.0F * limbSpeed * 0.5F);
//            right_arm.setRotZ(0.0F);
//            left_arm.setRotZ(0.0F);
//            right_leg.setRotX(MathHelper.cos(limbPos * 0.6662F) * 1.4F * limbSpeed);
//            left_leg.setRotX(MathHelper.cos(limbPos * 0.6662F + (float) Math.PI) * 1.4F * limbSpeed);
//            right_leg.setRotY(0.005F);
//            left_leg.setRotY(-0.005F);
//            right_leg.setRotZ(0.005F);
//            left_leg.setRotZ(-0.005F);
//
//            right_arm.setRotY(0.0F);
//            left_arm.setRotY(0.0F);
//
//            this.punch(entity, partialTicks, body, head, right_arm, left_arm);
//
//            if (entity.component.isSneaking()) {
//                body.setRotX(-0.5F);
//                right_arm.setRotX(right_arm.getRotX() - 0.4F);
//                left_arm.setRotX(left_arm.getRotX() - 0.4F);
//
//                head.setPosY(-3.2f);
//                body.setPosY(-3.2f);
//
//                right_arm.setPosY(-3.2f);
//                left_arm.setPosY(-3.2f);
//
//                right_leg.setPosZ(4f);
//                left_leg.setPosZ(4f);
//            } else {
//                right_leg.setPosZ(0);
//                left_leg.setPosZ(0);
//            }
//
//            this.swingArm(right_arm, animationProgress, 1.0f);
//            this.swingArm(left_arm, animationProgress, -1.0f);
        }
    }




    private Vec2f calculateAngles(double x, double y, double z, double x2, double y2, double z2, float bodyYaw) {
        float dirX = (float) (x2 - x);
        float dirY = (float) (y2 - y);
        float dirZ = (float) (z2 - z);

        Quaternionf quaternionf = new Quaternionf(new AxisAngle4f((float) Math.toRadians(90), new Vector3f(0,0,1).rotateY((float) Math.toRadians(bodyYaw + 90))));
        Vector3f normalizedDir = new Vector3f(dirX, dirY, dirZ).normalize();

        float pitch = (float) Math.asin(normalizedDir.y);
        float yaw = (float) Math.atan2(normalizedDir.x, normalizedDir.z);
        System.out.println(yaw);
        return new Vec2f((pitch) * 10, yaw * 10);
    }

    private void initialize(List<GeoBone> boneList) {
        this.points = new ArrayList<>();
        this.sticks = new ArrayList<>();
        for(GeoBone bone: boneList){
            float x = (float) (bone.getWorldPosition().x);
            float y = (float) (bone.getWorldPosition().y);
            float z = (float) (bone.getWorldPosition().z);

            PhysicsPoint pointA = new PhysicsPoint(new Vec3d(x, y, z), new Vec3d(x, y, z), true );
            PhysicsPoint pointB = new PhysicsPoint(new Vec3d(0, -1, 0).rotateX(bone.getRotX()).rotateY(bone.getRotY()).add(x, y, z), new Vec3d(0, -1, 0).rotateX(bone.getRotX()).rotateY(bone.getRotY()).add(x, y, z), false);
            this.sticks.add(new PhysicsStick(pointA, pointB, 1));
            this.points.add(pointB);
        }
    }

    private void punch(SkinWalkerEntity entity, float tickDelta, GeoBone body, GeoBone head, GeoBone rightArm, GeoBone leftArm){
        if (!(entity.getHandSwingProgress(tickDelta) <= 0.0F)) {
            float f = entity.getHandSwingProgress(tickDelta);
            body.setRotY(MathHelper.sin(MathHelper.sqrt(f) * (float) (Math.PI * 2)) * 0.2F);

            rightArm.setPivotZ(-MathHelper.sin(body.getRotY()) * 5.0F);
            rightArm.setPivotX(MathHelper.cos(body.getRotY()) * 5.0F);
            leftArm.setPivotZ(MathHelper.sin(body.getRotY()) * 5.0F);
            leftArm.setPivotX(-MathHelper.cos(body.getRotY()) * 5.0F);
            rightArm.setRotY((rightArm.getRotY() + body.getRotY()));
            leftArm.setRotY(-(leftArm.getRotY() + body.getRotY()));
            leftArm.setRotX(-(leftArm.getRotX() + body.getRotY()));
            f = 1.0F - entity.getHandSwingProgress(tickDelta);
            f *= f;
            f *= f;
            f = 1.0F - f;
            float g = MathHelper.sin(f * (float) Math.PI);
            float h = MathHelper.sin(entity.getHandSwingProgress(tickDelta) * (float) Math.PI) * -(-head.getRotX() - 0.7F) * 0.75F;
            rightArm.setRotX(-(rightArm.getRotX() - g * 1.2F - h));
            rightArm.setRotY(-(rightArm.getRotY() + body.getRotY() * 2.0F));
            rightArm.setRotZ((rightArm.getRotZ() + MathHelper.sin(entity.getHandSwingProgress(tickDelta) * (float) Math.PI) * -0.4F));
        }
    }

    private void swingArm(GeoBone arm, float animationProgress, float sigma){
        arm.setRotZ(arm.getRotZ() + sigma * (MathHelper.cos(animationProgress * 0.09F) * 0.05F + 0.05F));
        arm.setRotX(arm.getRotX() + sigma * MathHelper.sin(animationProgress * 0.067F) * 0.05F);
    }

    protected float getAnimationProgress(Entity entity, float tickDelta) {
        return (float)entity.age + tickDelta;
    }

    @Override
    public Vec3d getPositionOffset(SkinWalkerEntity entity, float tickDelta) {
        if (entity.component.isSneaking()) {
            return new Vec3d(0, -0.125, 0);
        }
        return super.getPositionOffset(entity, tickDelta);
    }
}
