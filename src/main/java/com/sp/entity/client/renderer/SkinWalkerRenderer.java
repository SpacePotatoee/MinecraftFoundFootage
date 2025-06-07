package com.sp.entity.client.renderer;

import com.sp.SPBRevamped;
import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.SkinWalkerComponent;
import com.sp.entity.client.debug.IKDebugRenderLayer;
import com.sp.entity.client.model.SkinWalkerModel;
import com.sp.entity.custom.SkinWalkerEntity;
import com.sp.entity.ik.model.GeckoLib.GeoModelAccessor;
import com.sp.entity.ik.model.GeckoLib.MowzieGeoBone;
import com.sp.entity.ik.parts.sever_limbs.ServerLimb;
import com.sp.render.RenderLayers;
import it.unimi.dsi.fastutil.ints.IntIntImmutablePair;
import it.unimi.dsi.fastutil.ints.IntIntPair;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import software.bernie.geckolib.cache.object.*;
import software.bernie.geckolib.renderer.DynamicGeoEntityRenderer;
import software.bernie.geckolib.util.RenderUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class SkinWalkerRenderer extends DynamicGeoEntityRenderer<SkinWalkerEntity> {
    private final Identifier SPIDER_LEGS_TEXTURE = new Identifier(SPBRevamped.MOD_ID, "textures/entity/skinwalker/skinwalker_legs_texture.png");
    private final Identifier HEAD_TEXTURE = new Identifier(SPBRevamped.MOD_ID, "textures/entity/skinwalker/final_form_head_texture.png");
    private final Identifier EYES_TEXTURE = new Identifier(SPBRevamped.MOD_ID, "textures/entity/skinwalker/skinwalker_eyes.png");
    private final List<String> spiderLegBones = new ArrayList<>();


    public SkinWalkerRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new SkinWalkerModel());
        this.addRenderLayer(new IKDebugRenderLayer(this));

        spiderLegBones.add("seg1_leg1");
        spiderLegBones.add("seg2_leg1");
        spiderLegBones.add("seg3_leg1");
        spiderLegBones.add("seg4_leg1");

        spiderLegBones.add("seg1_leg2");
        spiderLegBones.add("seg2_leg2");
        spiderLegBones.add("seg3_leg2");
        spiderLegBones.add("seg4_leg2");

        spiderLegBones.add("seg1_leg3");
        spiderLegBones.add("seg2_leg3");
        spiderLegBones.add("seg3_leg3");
        spiderLegBones.add("seg4_leg3");

        spiderLegBones.add("seg1_leg4");
        spiderLegBones.add("seg2_leg4");
        spiderLegBones.add("seg3_leg4");
        spiderLegBones.add("seg4_leg4");
    }


    @Override
    public void preRender(MatrixStack poseStack, SkinWalkerEntity animatable, BakedGeoModel model, VertexConsumerProvider bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);

        float speed = animatable.limbAnimator.getSpeed(partialTick);
        float pos = animatable.limbAnimator.getPos(partialTick);

        if (speed > 1.0F) {
            speed = 1.0F;
        }

        float h = MathHelper.lerpAngleDegrees(partialTick, animatable.prevBodyYaw, animatable.bodyYaw);
        float j = MathHelper.lerpAngleDegrees(partialTick, animatable.prevHeadYaw, animatable.headYaw);
        float yaw = j- h;

        float pitch = MathHelper.lerp(partialTick, animatable.prevPitch, animatable.getPitch());

        float animationProgress = this.getAnimationProgress(animatable, partialTick);
        this.animateModel(animatable, pos, speed, animationProgress, -yaw, -pitch, partialTick);
    }

    @Override
    public void render(SkinWalkerEntity entity, float entityYaw, float partialTick, MatrixStack poseStack, VertexConsumerProvider bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);

        if(InitializeComponents.SKIN_WALKER.get(entity).isInTrueForm()) {
            entity.getModelPositions(entity, new GeoModelAccessor(this.model));
        }
    }

    protected void animateModel(SkinWalkerEntity entity, float limbPos, float limbSpeed, float animationProgress, float yaw, float pitch, float partialTicks) {
        Optional<GeoBone> headBone = this.getGeoModel().getBone("head");
        Optional<GeoBone> bodyBone = this.getGeoModel().getBone("body");
        Optional<GeoBone> rightArm = this.getGeoModel().getBone("right_arm");
        Optional<GeoBone> leftArm = this.getGeoModel().getBone("left_arm");
        Optional<GeoBone> rightLeg = this.getGeoModel().getBone("right_leg");
        Optional<GeoBone> leftLeg = this.getGeoModel().getBone("left_leg");

        if(headBone.isPresent() && bodyBone.isPresent() && rightArm.isPresent() && leftArm.isPresent() && rightLeg.isPresent() && leftLeg.isPresent()) {
            GeoBone head = headBone.get();
            GeoBone body = bodyBone.get();
            GeoBone right_arm = rightArm.get();
            GeoBone left_arm = leftArm.get();
            GeoBone right_leg = rightLeg.get();
            GeoBone left_leg = leftLeg.get();


            SkinWalkerComponent component = InitializeComponents.SKIN_WALKER.get(entity);

            float pitchOffset = 0;
            if(component.isInTrueForm()) {
                Vector3f offset = new Vector3f();
                for (Object limbs : component.getIKComponent().getEndPoints()) {
                    ServerLimb limb = (ServerLimb) limbs;
                    offset.add(limb.pos.toVector3f());
                }
                offset.mul(0.25f);

                body.setWorldSpaceMatrix(new Matrix4f().identity().translate(offset.sub(entity.getLerpedPos(partialTicks).toVector3f()).mul(0.2f)));

                pitchOffset = 35;
            }

            if(!component.shouldBeginReveal()){
                head.setRotZ(0.0f);
                head.setRotY(yaw * (float) (Math.PI / 180.0));
                head.setRotX((pitch + pitchOffset) * (float) (Math.PI / 180.0));
            }


            if(!component.isInTrueForm()) {
                right_arm.setRotX(MathHelper.cos(limbPos * 0.6662F + (float) Math.PI) * 2.0F * limbSpeed * 0.5F);
                left_arm.setRotX(MathHelper.cos(limbPos * 0.6662F) * 2.0F * limbSpeed * 0.5F);

                right_arm.setRotZ(0.0F);
                left_arm.setRotZ(0.0F);
                right_leg.setRotX(MathHelper.cos(limbPos * 0.6662F) * 1.4F * limbSpeed);
                left_leg.setRotX(MathHelper.cos(limbPos * 0.6662F + (float) Math.PI) * 1.4F * limbSpeed);
                right_leg.setRotY(0.005F);
                left_leg.setRotY(-0.005F);
                right_leg.setRotZ(0.005F);
                left_leg.setRotZ(-0.005F);

                right_arm.setRotY(0.0F);
                left_arm.setRotY(0.0F);

                this.punch(entity, partialTicks, body, head, right_arm, left_arm);

                if (component.isSneaking()) {
                    body.setRotX(-0.5F);
                    right_arm.setRotX(right_arm.getRotX() - 0.4F);
                    left_arm.setRotX(left_arm.getRotX() - 0.4F);

                    head.setPosY(-3.2f);
                    body.setPosY(-3.2f);

                    right_arm.setPosY(-3.2f);
                    left_arm.setPosY(-3.2f);

                    right_leg.setPosZ(4f);
                    left_leg.setPosZ(4f);
                } else {
                    right_leg.setPosZ(0);
                    left_leg.setPosZ(0);
                }

                this.swingArm(right_arm, animationProgress, 1.0f);
                this.swingArm(left_arm, animationProgress, -1.0f);
            }
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
    protected Identifier getTextureOverrideForBone(GeoBone bone, SkinWalkerEntity animatable, float partialTick) {
        SkinWalkerComponent component = InitializeComponents.SKIN_WALKER.get(animatable);
        String name = bone.getName();
        if(component.isInTrueForm()) {
            if (spiderLegBones.contains(name)) {
                return SPIDER_LEGS_TEXTURE;
            }
            else if (name.equals("head") || name.equals("headwear")) {
                return HEAD_TEXTURE;
            }
        }

        return null;
    }

    protected boolean boneRenderOverride(SkinWalkerEntity animatable, MatrixStack poseStack, GeoBone bone, VertexConsumerProvider bufferSource) {
        SkinWalkerComponent component = InitializeComponents.SKIN_WALKER.get(animatable);

        if(component.isInTrueForm()) {
            if (bone.getName().equals("headwear")) {
                VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderLayer.getEyes(EYES_TEXTURE));

                if (bone.isHidden())
                    return false;

                for (GeoCube cube : bone.getCubes()) {
                    poseStack.push();
                    renderCube(poseStack, cube, vertexConsumer, 15728640, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
                    poseStack.pop();
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public Vec3d getPositionOffset(SkinWalkerEntity entity, float tickDelta) {
        if (entity.component.isSneaking()) {
            return new Vec3d(0, -0.125, 0);
        }
        return super.getPositionOffset(entity, tickDelta);
    }

    @Override
    public void createVerticesOfQuad(GeoQuad quad, Matrix4f poseState, Vector3f normal, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        if (this.textureOverride == null) {
            super.createVerticesOfQuad(quad, poseState, normal, buffer, packedLight, packedOverlay, red, green,
                    blue, alpha);

            return;
        }

        IntIntPair boneTextureSize = computeTextureSize(this.textureOverride);
        IntIntPair entityTextureSize = IntIntImmutablePair.of(64, 64);

        if (boneTextureSize == null) {
            super.createVerticesOfQuad(quad, poseState, normal, buffer, packedLight, packedOverlay, red, green,
                    blue, alpha);

            return;
        }

        for (GeoVertex vertex : quad.vertices()) {
            Vector4f vector4f = poseState.transform(new Vector4f(vertex.position().x(), vertex.position().y(), vertex.position().z(), 1.0f));
            float texU = (vertex.texU() * entityTextureSize.firstInt()) / boneTextureSize.firstInt();
            float texV = (vertex.texV() * entityTextureSize.secondInt()) / boneTextureSize.secondInt();

            buffer.vertex(vector4f.x(), vector4f.y(), vector4f.z(), red, green, blue, alpha, texU, texV,
                    packedOverlay, packedLight, normal.x(), normal.y(), normal.z());
        }
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
            if(animatable != null) {
                SkinWalkerComponent component = InitializeComponents.SKIN_WALKER.get(animatable);

                if(component.isInTrueForm()) {
                    if (bone.getName().equals("body")) {
                        Matrix4f matrix4f = new Matrix4f();
                        matrix4f = matrix4f.mul(bone.getWorldSpaceMatrix());
                        poseStack.peek().getPositionMatrix().mul(matrix4f);
                    }
                }
            }

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

        this.textureOverride = getTextureOverrideForBone(bone, this.animatable, partialTick);
        Identifier texture = this.textureOverride == null ? getTexture(this.animatable) : this.textureOverride;
        RenderLayer renderTypeOverride = getRenderTypeOverrideForBone(bone, this.animatable, texture, bufferSource, partialTick);

        if (texture != null && renderTypeOverride == null)
            renderTypeOverride = getRenderType(this.animatable, texture, bufferSource, partialTick);

        if (renderTypeOverride != null)
            buffer = bufferSource.getBuffer(renderTypeOverride);

        if (!this.boneRenderOverride(animatable, poseStack, bone, bufferSource))
            super.renderCubesOfBone(poseStack, bone, buffer, packedLight, packedOverlay, red, green, blue, alpha);

        if (renderTypeOverride != null)
            buffer = bufferSource.getBuffer(getRenderType(this.animatable, getTexture(this.animatable), bufferSource, partialTick));

        if (!isReRender)
            applyRenderLayersForBone(poseStack, animatable, bone, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);

        renderChildBones(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);

        poseStack.pop();
    }

    @Override
    protected @Nullable RenderLayer getRenderTypeOverrideForBone(GeoBone bone, SkinWalkerEntity animatable, Identifier texturePath, VertexConsumerProvider bufferSource, float partialTick) {
        if (Objects.equals(bone.getName(), "head") && InitializeComponents.SKIN_WALKER.get(animatable).isInTrueForm()) {
            return RenderLayers.getDistortedEntity(texturePath);
        }


        return super.getRenderTypeOverrideForBone(bone, animatable, texturePath, bufferSource, partialTick);
    }
}
