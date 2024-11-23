package com.sp.mixin.respawnsystem;

import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import com.sp.util.Timer;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import foundry.veil.api.client.util.Easings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin <T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements FeatureRendererContext<T, M> {

    @Unique Timer staticTimer;
    @Shadow public abstract M getModel();

    @Shadow protected M model;

    @Shadow protected abstract float getHandSwingProgress(T entity, float tickDelta);

    @Shadow
    public static boolean shouldFlipUpsideDown(LivingEntity entity) {
        return false;
    }

    @Shadow protected abstract float getAnimationProgress(T entity, float tickDelta);

    @Shadow protected abstract void setupTransforms(T entity, MatrixStack matrices, float animationProgress, float bodyYaw, float tickDelta);

    @Shadow protected abstract void scale(T entity, MatrixStack matrices, float amount);

    @Shadow protected abstract boolean isVisible(T entity);

    @Shadow protected abstract @Nullable RenderLayer getRenderLayer(T entity, boolean showBody, boolean translucent, boolean showOutline);

    @Shadow
    public static int getOverlay(LivingEntity entity, float whiteOverlayProgress) {
        return 0;
    }

    @Shadow protected abstract float getAnimationCounter(T entity, float tickDelta);

    @Shadow @Final protected List<FeatureRenderer<T, M>> features;
    @Unique ShaderProgram shader;
    @Unique Identifier SHADER_LOCATION = new Identifier("spbrevamped", "warp_player");

    protected LivingEntityRendererMixin(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/VertexConsumerProvider;getBuffer(Lnet/minecraft/client/render/RenderLayer;)Lnet/minecraft/client/render/VertexConsumer;", shift = At.Shift.BEFORE))
    private void setShaderAndUniforms(T livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci){
        if(livingEntity instanceof AbstractClientPlayerEntity){
            PlayerComponent playerComponent = InitializeComponents.PLAYER.get(livingEntity);

            shader = VeilRenderSystem.setShader(SHADER_LOCATION);
            if(shader == null){
                return;
            }

//            System.out.println(livingEntity.getName().getString() + ": " + playerComponent.isShouldDoStatic());

            if(playerComponent.isShouldDoStatic()) {
                if (this.staticTimer == null) {
                    this.staticTimer = new Timer(2000, Easings.Easing.linear);
                    this.staticTimer.startTimer();
                }

                if(this.staticTimer.isDone()){
                    this.staticTimer = null;
                    shader.setFloat("StaticTimer", 2.0f);
                } else {
                    shader.setFloat("StaticTimer", this.staticTimer.getCurrentTime());
                }

            } else {
                shader.setFloat("StaticTimer", 2.0f);
            }

        }
    }

    @Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V", shift = At.Shift.BEFORE))
    private void bind(T livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci){
        if(livingEntity instanceof AbstractClientPlayerEntity){
            shader.bind();
        }
    }

    @Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V", shift = At.Shift.AFTER))
    private void unbind(T livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci){
        if(livingEntity instanceof AbstractClientPlayerEntity){
            ShaderProgram.unbind();
        }
    }

    //@Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("HEAD"), cancellable = true)
    private void test(T livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci){
        ci.cancel();
        matrixStack.push();
        this.model.handSwingProgress = this.getHandSwingProgress(livingEntity, g);
        this.model.riding = livingEntity.hasVehicle();
        this.model.child = livingEntity.isBaby();
        float h = MathHelper.lerpAngleDegrees(g, livingEntity.prevBodyYaw, livingEntity.bodyYaw);
        float j = MathHelper.lerpAngleDegrees(g, livingEntity.prevHeadYaw, livingEntity.headYaw);
        float k = j-h;
//        if (livingEntity.hasVehicle() && livingEntity.getVehicle() instanceof LivingEntity) {
//            LivingEntity livingEntity2 = (LivingEntity)livingEntity.getVehicle();
//            h = MathHelper.lerpAngleDegrees(g, livingEntity2.prevBodyYaw, livingEntity2.bodyYaw);
//            k = j - h;
//            float l = MathHelper.wrapDegrees(k);
//            if (l < -85.0F) {
//                l = -85.0F;
//            }
//
//            if (l >= 85.0F) {
//                l = 85.0F;
//            }
//
//            h = j - l;
//            if (l * l > 2500.0F) {
//                h += l * 0.2F;
//            }
//
//            k = j - h;
//        }
//
        float m = MathHelper.lerp(g, livingEntity.prevPitch, livingEntity.getPitch());
//        if (shouldFlipUpsideDown(livingEntity)) {
//            m *= -1.0F;
//            k *= -1.0F;
//        }
//
//        if (livingEntity.isInPose(EntityPose.SLEEPING)) {
//            Direction direction = livingEntity.getSleepingDirection();
//            if (direction != null) {
//                float n = livingEntity.getEyeHeight(EntityPose.STANDING) - 0.1F;
//                matrixStack.translate((float)(-direction.getOffsetX()) * n, 0.0F, (float)(-direction.getOffsetZ()) * n);
//            }
//        }
//
        float lx = this.getAnimationProgress(livingEntity, g);
        this.setupTransforms(livingEntity, matrixStack, lx, h, g);
        matrixStack.scale(-1.0F, -1.0F, 1.0F);
        this.scale(livingEntity, matrixStack, g);
        matrixStack.translate(0.0F, -1.501F, 0.0F);
        float n = 0.0F;
        float o = 0.0F;
        if (!livingEntity.hasVehicle() && livingEntity.isAlive()) {
            n = livingEntity.limbAnimator.getSpeed(g);
            o = livingEntity.limbAnimator.getPos(g);
            if (livingEntity.isBaby()) {
                o *= 3.0F;
            }

            if (n > 1.0F) {
                n = 1.0F;
            }
        }
//
        this.model.animateModel(livingEntity, o, n, g);
        this.model.setAngles(livingEntity, o, n, lx, 0, m);
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        boolean bl = this.isVisible(livingEntity);
        boolean bl2 = !bl && !livingEntity.isInvisibleTo(minecraftClient.player);
        boolean bl3 = minecraftClient.hasOutline(livingEntity);
        RenderLayer renderLayer = this.getRenderLayer(livingEntity, bl, bl2, bl3);
        if (renderLayer != null) {
            VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(renderLayer);
            int p = getOverlay(livingEntity, this.getAnimationCounter(livingEntity, g));
            this.model.render(matrixStack, vertexConsumer, i, p, 1.0F, 1.0F, 1.0F, bl2 ? 0.15F : 1.0F);
        }

        if (!livingEntity.isSpectator()) {
            for (FeatureRenderer<T, M> featureRenderer : this.features) {
                featureRenderer.render(matrixStack, vertexConsumerProvider, i, livingEntity, o, n, g, lx, 0, m);
            }
        }
//
        matrixStack.pop();
        super.render(livingEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }

    //@Inject(method = "setupTransforms", at = @At("HEAD"), cancellable = true)
    private void test2(T entity, MatrixStack matrices, float animationProgress, float bodyYaw, float tickDelta, CallbackInfo ci){
        ci.cancel();
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F - bodyYaw));
    }

}
