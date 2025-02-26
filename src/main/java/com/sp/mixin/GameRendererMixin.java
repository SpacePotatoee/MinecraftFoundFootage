package com.sp.mixin;

import com.sp.compat.modmenu.ConfigStuff;
import com.sp.SPBRevamped;
import com.sp.SPBRevampedClient;
import com.sp.render.camera.CameraRoll;
import com.sp.render.camera.CutsceneManager;
import com.sp.render.ShadowMapRenderer;
import com.sp.util.MathStuff;
import foundry.veil.api.client.render.VeilRenderSystem;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = GameRenderer.class)
public abstract class GameRendererMixin {
    @Unique
    Entity newCamera;
    @Unique
    private static final Identifier shadowSolid = new Identifier(SPBRevamped.MOD_ID, "shadowmap/rendertype_solid");

    @Unique
    private static final Identifier shadowEntity = new Identifier(SPBRevamped.MOD_ID, "shadowmap/rendertype_entity");

    @Unique
    private static final Identifier warpEntity = new Identifier("spbrevamped", "warp_player");

    @Unique
    private float smoothPitch = 0.0f;

    @Unique
    private float smoothYaw = 0.0f;


    @Shadow @Final private Camera camera;
    @Shadow @Final MinecraftClient client;


    @Shadow public abstract void setBlockOutlineEnabled(boolean blockOutlineEnabled);
    @Shadow public abstract void tick();
    @Shadow protected abstract void renderHand(MatrixStack matrices, Camera camera, float tickDelta);

    @Shadow private static @Nullable ShaderProgram renderTypeEntityTranslucentProgram;

    @Shadow public abstract void render(float tickDelta, long startTime, boolean tick);

    @Inject(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;tiltViewWhenHurt(Lnet/minecraft/client/util/math/MatrixStack;F)V"))
    public void renderWorld(float tickDelta, long limitTime, MatrixStack matrices, CallbackInfo ci){
        PlayerEntity player = this.client.player;
        this.setBlockOutlineEnabled(true);

        if (player != null) {
            CutsceneManager cutsceneManager = SPBRevampedClient.getCutsceneManager();

            if(ConfigStuff.enableRealCamera && !cutsceneManager.isPlaying && client.getCameraEntity() == player) {
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(CameraRoll.doCameraRoll(player, tickDelta)));
            }
            else if(cutsceneManager.isPlaying){
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(cutsceneManager.cameraRotZ));
            }
        }
    }

    @ModifyArg(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/RotationAxis;rotationDegrees(F)Lorg/joml/Quaternionf;", ordinal = 2))
    private float smoothPitch(float deg) {
        PlayerEntity player = this.client.player;

        if(player != null && ConfigStuff.enableSmoothCamera){
            this.smoothYaw = MathStuff.Lerp(this.smoothYaw, deg, ConfigStuff.cameraSmoothing, client.getLastFrameDuration());
            return this.smoothYaw;
        }

        return deg;
    }

    @ModifyArg(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/RotationAxis;rotationDegrees(F)Lorg/joml/Quaternionf;", ordinal = 3))
    private float smoothYaw(float deg){
        PlayerEntity player = this.client.player;

        if(player != null && ConfigStuff.enableSmoothCamera){
            this.smoothPitch = MathStuff.Lerp(this.smoothPitch, deg, ConfigStuff.cameraSmoothing, client.getLastFrameDuration());
            return this.smoothPitch;
        }

        return deg;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    private void bobView(MatrixStack matrices, float tickDelta){
        if (this.client.getCameraEntity() instanceof PlayerEntity) {
            PlayerEntity playerEntity = (PlayerEntity)this.client.getCameraEntity();
            float f = playerEntity.horizontalSpeed - playerEntity.prevHorizontalSpeed;
            float g = -(playerEntity.horizontalSpeed + f * tickDelta);
            float h = MathHelper.lerp(tickDelta, playerEntity.prevStrideDistance, playerEntity.strideDistance);
            matrices.translate(MathHelper.sin(g * (float) Math.PI) * h * 0.5F, -Math.abs(MathHelper.cos(g * (float) Math.PI) * h), 0.0F);
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(MathHelper.sin(g * (float) Math.PI) * h * 3.0F));
            float multiplier = 5.0f;
            if (ConfigStuff.enableRealCamera) {
                multiplier = 10.0f;
            }
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(Math.abs(MathHelper.cos(g * (float) Math.PI - 0.2F) * h) * multiplier));
        }
    }


    @Inject(method = "getFov", at = @At(value = "RETURN", ordinal = 1), cancellable = true)
    private void getFov(Camera camera, float tickDelta, boolean changingFov, CallbackInfoReturnable<Double> cir) {
        if(SPBRevampedClient.isInBackrooms()){
            cir.setReturnValue(85.0);
        }
//        cir.setReturnValue(SPBRevampedClient.doCameraZoom(cir.getReturnValue(), this.client, camera.getFocusedEntity()));
    }


    @Inject(method = {
            "getRenderTypeSolidProgram",
            "getRenderTypeCutoutProgram",
            "getRenderTypeCutoutMippedProgram"
    }, at = @At("HEAD"), cancellable = true)
    private static void setSolidShader(CallbackInfoReturnable<ShaderProgram> cir) {
        if(ShadowMapRenderer.isRenderingShadowMap()) {
            foundry.veil.api.client.render.shader.program.ShaderProgram shader = VeilRenderSystem.renderer().getShaderManager().getShader(shadowSolid);
            if (shader == null) {
                return;
            }
            cir.setReturnValue(shader.toShaderInstance());
        }
    }

    @Inject(method = {
            "getRenderTypeEntityTranslucentProgram",
            "getRenderTypeEntitySolidProgram",
            "getRenderTypeEntityCutoutProgram",
            "getRenderTypeEntityCutoutNoNullProgram",
            "getRenderTypeEntityTranslucentCullProgram"
    }, at = @At("TAIL"), cancellable = true)
    private static void setPlayerShader(CallbackInfoReturnable<ShaderProgram> cir) {
        if(ShadowMapRenderer.isRenderingShadowMap()) {
            foundry.veil.api.client.render.shader.program.ShaderProgram shader = VeilRenderSystem.renderer().getShaderManager().getShader(shadowEntity);
            if (shader == null) {
                return;
            }
            cir.setReturnValue(shader.toShaderInstance());
        }
    }

    @Inject(method = {
            "getRenderTypeEntityTranslucentProgram"
    }, at = @At("TAIL"), cancellable = true)
    private static void setPlayerWarpShader(CallbackInfoReturnable<ShaderProgram> cir) {
        foundry.veil.api.client.render.shader.program.ShaderProgram shader = VeilRenderSystem.renderer().getShaderManager().getShader(warpEntity);
        if (shader == null || !SPBRevampedClient.shoudlRenderWarp) {
            cir.setReturnValue(renderTypeEntityTranslucentProgram);
            return;
        }
        cir.setReturnValue(shader.toShaderInstance());
    }

    @Redirect(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;renderHand(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/Camera;F)V"))
    private void redirect(GameRenderer instance, MatrixStack matrices, Camera camera, float tickDelta) {
        if(!SPBRevampedClient.getCutsceneManager().isPlaying){
            this.renderHand(matrices, camera, tickDelta);
        }
    }

    @Redirect(method = "updateTargetedEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;getReachDistance()F"))
    private float increaseReach(ClientPlayerInteractionManager instance){
        return 6;
    }

    @ModifyConstant(method = "updateTargetedEntity", constant = @Constant(doubleValue = 9.0))
    private double increaseReach(double constant){
        return 36;
    }


}
