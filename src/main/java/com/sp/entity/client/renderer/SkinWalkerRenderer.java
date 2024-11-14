package com.sp.entity.client.renderer;

import com.sp.entity.client.model.SkinWalkerModel;
import com.sp.entity.custom.SkinWalkerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import java.util.Optional;

public class SkinWalkerRenderer extends GeoEntityRenderer<SkinWalkerEntity> {


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
        float yaw = j - h;

        float pitch = MathHelper.lerp(partialTick, entity.prevPitch, entity.getPitch());

        float animationProgress = this.getAnimationProgress(entity, partialTick);
        this.animateModel(pos, speed, animationProgress, -yaw, -pitch);
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    protected void animateModel(float limbPos, float limbSpeed, float animationProgress, float yaw, float pitch) {

        Optional<GeoBone> headBone = this.model.getBone("head");
        Optional<GeoBone> rightArm = this.model.getBone("right_arm");
        Optional<GeoBone> leftArm = this.model.getBone("left_arm");
        Optional<GeoBone> rightLeg = this.model.getBone("right_leg");
        Optional<GeoBone> leftLeg = this.model.getBone("left_leg");

        if(headBone.isPresent() && rightArm.isPresent() && leftArm.isPresent() && rightLeg.isPresent() && leftLeg.isPresent()) {
            GeoBone head = headBone.get();
            GeoBone right_arm = rightArm.get();
            GeoBone left_arm = leftArm.get();
            GeoBone right_leg = rightLeg.get();
            GeoBone left_leg = leftLeg.get();

            head.setRotY(yaw * (float) (Math.PI / 180.0));
            head.setRotX(pitch * (float) (Math.PI / 180.0));

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

            this.swingArm(right_arm, animationProgress, 1.0f);
            this.swingArm(left_arm, animationProgress, -1.0f);
        }
    }

    private void swingArm(GeoBone arm, float animationProgress, float sigma){
        arm.setRotZ(arm.getRotZ() + sigma * (MathHelper.cos(animationProgress * 0.09F) * 0.05F + 0.05F));
        arm.setRotX(arm.getRotX() + sigma * MathHelper.sin(animationProgress * 0.067F) * 0.05F);
    }

    protected float getAnimationProgress(Entity entity, float tickDelta) {
        return (float)entity.age + tickDelta;
    }
}
