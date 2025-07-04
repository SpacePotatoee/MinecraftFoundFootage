package com.sp.entity.client.renderer;

import com.sp.SPBRevamped;
import com.sp.entity.client.debug.IKDebugRenderLayer;
import com.sp.entity.client.model.WalkerModel;
import com.sp.entity.custom.WalkerEntity;
import com.sp.entity.ik.model.GeckoLib.GeoModelAccessor;
import com.sp.entity.ik.model.GeckoLib.MowzieGeoBone;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.cache.object.GeoCube;
import software.bernie.geckolib.renderer.DynamicGeoEntityRenderer;
import software.bernie.geckolib.util.RenderUtils;

public class WalkerRenderer extends DynamicGeoEntityRenderer<WalkerEntity> {
    private final Identifier EYES_TEXTURE = new Identifier(SPBRevamped.MOD_ID, "textures/entity/skinwalker/skinwalker_eyes.png");

    public WalkerRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new WalkerModel());
        this.addRenderLayer(new IKDebugRenderLayer<>(this));
    }

    @Override
    public void render(WalkerEntity entity, float entityYaw, float partialTick, MatrixStack poseStack, VertexConsumerProvider bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);

        entity.getModelPositions(entity, new GeoModelAccessor(this.model));
    }

    @Override
    public void renderRecursively(MatrixStack poseStack, WalkerEntity animatable, GeoBone bone, RenderLayer renderType, VertexConsumerProvider bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        if (bone == null) return;
        poseStack.push();
        /*
        if (Objects.equals(bone.getName(), "root") && bone instanceof MowzieGeoBone mowzieGeoBone) {
            mowzieGeoBone.setForceMatrixTransform(true);
            bone.updateRotation((float) -Math.toRadians(animatable.getPitch() + 90), (float) -Math.toRadians(animatable.getYaw()), 0);
        }

         */

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



            super.renderCubesOfBone(poseStack, bone, buffer, packedLight, packedOverlay, red, green, blue, alpha);

        if (!isReRender)
            applyRenderLayersForBone(poseStack, animatable, bone, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);

        renderChildBones(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);

        poseStack.pop();
    }

    @Override
    public void renderChildBones(MatrixStack poseStack, WalkerEntity animatable, GeoBone bone, RenderLayer renderType, VertexConsumerProvider bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        for (GeoBone childBone : bone.getChildBones()) {
            if (!bone.isHidingChildren() || (childBone instanceof MowzieGeoBone mowzieGeoBone && mowzieGeoBone.isDynamicJoint())) {
                renderRecursively(poseStack, animatable, childBone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
            }
        }
    }

    protected boolean boneRenderOverride(WalkerEntity animatable, MatrixStack poseStack, GeoBone bone, VertexConsumerProvider bufferSource) {
        if (bone.getName().equals("eyes")) {
            VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderLayer.getEyes(EYES_TEXTURE));

            if (bone.isHidden())
                return false;


            for (GeoCube cube : bone.getCubes()) {
                poseStack.push();
                renderCube(poseStack, cube, vertexConsumer, 15728640, 655360, 1.0F, 1.0F, 1.0F, 1.0F);
                poseStack.pop();
            }
            /*
            for (GeoCube cube : bone.getCubes()) {
                poseStack.push();
                renderCube(poseStack, cube, vertexConsumer, 15728640, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
                poseStack.pop();
            }

             */

            return true;
        }

        return false;
    }
}
