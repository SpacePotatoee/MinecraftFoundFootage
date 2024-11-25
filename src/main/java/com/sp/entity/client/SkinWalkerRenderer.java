package com.sp.entity.client;

import com.sp.SPBRevamped;
import com.sp.entity.client.debug.IKDebugRenderLayer;
import com.sp.entity.custom.SkinWalkerEntity;
import com.sp.entity.ik.model.GeckoLib.MowzieGeoBone;
import com.sp.render.physics.PhysicsPoint;
import com.sp.render.physics.PhysicsStick;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.joml.*;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.cache.object.GeoCube;
import software.bernie.geckolib.cache.object.GeoQuad;
import software.bernie.geckolib.cache.object.GeoVertex;
import software.bernie.geckolib.renderer.DynamicGeoEntityRenderer;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.util.RenderUtils;

import java.lang.Math;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SkinWalkerRenderer extends DynamicGeoEntityRenderer<SkinWalkerEntity> {
    private final Identifier SPIDER_LEGS_TEXTURE = new Identifier(SPBRevamped.MOD_ID, "textures/entity/skinwalker_legs_texture.png");
    private Double prevWorldX;
    private Double prevWorldY;
    private Double prevWorldZ;
    private PhysicsPoint pointA;
    private PhysicsPoint pointB;
    private PhysicsStick stick;
    private final List<String> spiderLegBones = new ArrayList<>();


    public SkinWalkerRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new SkinWalkerModel());
        this.addRenderLayer(new IKDebugRenderLayer(this));
        spiderLegBones.add("top_left_spider_leg");
        spiderLegBones.add("bone2");
        spiderLegBones.add("bone3");
        spiderLegBones.add("bone4");

        spiderLegBones.add("top_right_spider_leg");
        spiderLegBones.add("bone5");
        spiderLegBones.add("bone6");
        spiderLegBones.add("bone7");

        spiderLegBones.add("bottom_left_spider_leg2");
        spiderLegBones.add("bone11");
        spiderLegBones.add("bone12");
        spiderLegBones.add("bone13");

        spiderLegBones.add("bottom_right_spider_leg2");
        spiderLegBones.add("bone8");
        spiderLegBones.add("bone9");
        spiderLegBones.add("bone10");
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

//        Vec3d vec3d = this.getPositionOffset(entity, partialTick);
//        double e = entity.getY() + vec3d.getY();
//        poseStack.push();
//        poseStack.translate(0, e, 0);
//        poseStack.pop();

        float animationProgress = this.getAnimationProgress(entity, partialTick);
//        this.animateModel(entity, bufferSource, poseStack, pos, speed, animationProgress, -yaw, -pitch, partialTick);
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    protected @Nullable Identifier getTextureOverrideForBone(GeoBone bone, SkinWalkerEntity animatable, float partialTick) {
        if(spiderLegBones.contains(bone.getName())){
            return SPIDER_LEGS_TEXTURE;
        } else {
            return null;
        }
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

            if(camera != null) {
                Vec3d cameraPos = camera.getPos();


                if(this.prevWorldX == null || this.prevWorldY == null || this.prevWorldZ == null) {
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
                    if(this.pointA == null) {
                        this.pointA = new PhysicsPoint(new Vec3d(vector3d.x, vector3d.y, vector3d.z), true);
                    }

                    PhysicsPoint pointA = new PhysicsPoint(new Vec3d(vector3d.x, vector3d.y, vector3d.z), true);

//                    this.pointA.set(vector3d);

                    if(this.pointB == null) {
                        this.pointB = new PhysicsPoint(Vec3d.ZERO, Vec3d.ZERO, false);
                    }

                    PhysicsStick stick = new PhysicsStick(pointA, this.pointB, 1);

//                    if(this.stick == null) {
//                        this.stick = new PhysicsStick(pointA, this.pointB, 1);
//                    }

                    this.pointB.updatePoint();
                    stick.updateSticks();

                    Vec2f angles = this.calculateAngles(vector3d.x, vector3d.y, vector3d.z, this.pointB.getX(), this.pointB.getY(), this.pointB.getZ(), yaw);



                    right_arm.setRotX((float) (angles.x + Math.toRadians(50)));
                    right_arm.setRotZ((float) (angles.y - Math.toRadians(90)));

                }
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
        Vector3f normalizedDir = new Vector3f(dirX, dirY, dirZ).rotate(quaternionf).normalize();
        Vec3d normalizedDir2 = new Vec3d(dirX, dirY, dirZ).rotateX((float) Math.toRadians(90)).normalize();

        float pitch = (float) Math.asin(normalizedDir.y);
        float yaw = (float) Math.atan2(normalizedDir2.z, normalizedDir2.x);
        return new Vec2f(pitch * 5, yaw * 5);
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

    @Override
    public void renderRecursively(MatrixStack poseStack, SkinWalkerEntity animatable, GeoBone bone, RenderLayer renderType, VertexConsumerProvider bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        if (bone == null) return;
        poseStack.push();
        if (bone instanceof MowzieGeoBone mowzieGeoBone && mowzieGeoBone.isForceMatrixTransform() && animatable != null) {
            MatrixStack.Entry last = poseStack.peek();
            double d0 = animatable.getX();
            double d1 = animatable.getY();
            double d2 = animatable.getZ();
            Matrix4f matrix4f = new Matrix4f();
            matrix4f = matrix4f.translate(0, -0.01f, 0);
            matrix4f = matrix4f.translate((float) -d0, (float) -d1, (float) -d2);
            matrix4f = matrix4f.mul(bone.getWorldSpaceMatrix());
            last.getPositionMatrix().mul(matrix4f);
            last.getNormalMatrix().mul(bone.getWorldSpaceNormal());

            RenderUtils.translateAwayFromPivotPoint(poseStack, bone);
        } else {
            boolean rotOverride = false;
            if (bone instanceof MowzieGeoBone mowzieGeoBone) {
                rotOverride = mowzieGeoBone.rotationOverride != null;
            }

            RenderUtils.translateMatrixToBone(poseStack, bone);
            RenderUtils.translateToPivotPoint(poseStack, bone);

            if (bone instanceof MowzieGeoBone mowzieGeoBone) {
                if (!mowzieGeoBone.inheritRotation && !mowzieGeoBone.inheritTranslation) {
                    poseStack.peek().getPositionMatrix().identity();
                    poseStack.peek().getPositionMatrix().mul(this.entityRenderTranslations);
                } else if (!mowzieGeoBone.inheritRotation) {
                    Vector4f t = new Vector4f().mul(poseStack.peek().getPositionMatrix());
                    poseStack.peek().getPositionMatrix().identity();
                    poseStack.translate(t.x, t.y, t.z);
                } else if (!mowzieGeoBone.inheritTranslation) {
                    MowzieGeoBone.removeMatrixTranslation(poseStack.peek().getPositionMatrix());
                    poseStack.peek().getPositionMatrix().mul(this.entityRenderTranslations);
                }
            }

            if (rotOverride) {
                MowzieGeoBone mowzieGeoBone = (MowzieGeoBone) bone;
                poseStack.peek().getPositionMatrix().mul(mowzieGeoBone.rotationOverride);
                poseStack.peek().getNormalMatrix().mul(new Matrix3f(mowzieGeoBone.rotationOverride));
            } else {
                RenderUtils.rotateMatrixAroundBone(poseStack, bone);
            }

            RenderUtils.scaleMatrixForBone(poseStack, bone);

            if (bone.isTrackingMatrices()) {
                Matrix4f poseState = new Matrix4f(poseStack.peek().getPositionMatrix());
                Matrix4f localMatrix = RenderUtils.invertAndMultiplyMatrices(poseState, this.entityRenderTranslations);

                bone.setModelSpaceMatrix(RenderUtils.invertAndMultiplyMatrices(poseState, this.modelRenderTranslations));
                bone.setLocalSpaceMatrix(RenderUtils.translateMatrix(localMatrix, getPositionOffset(this.animatable, 1).toVector3f()));
                bone.setWorldSpaceMatrix(RenderUtils.translateMatrix(new Matrix4f(localMatrix), this.animatable.getPos().toVector3f()));
            }

            RenderUtils.translateAwayFromPivotPoint(poseStack, bone);
        }

        renderCubesOfBone(poseStack, bone, buffer, packedLight, packedOverlay, red, green, blue, alpha);

        if (!isReRender)
            applyRenderLayersForBone(poseStack, animatable, bone, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);

        renderChildBones(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);

        poseStack.pop();
    }
}
