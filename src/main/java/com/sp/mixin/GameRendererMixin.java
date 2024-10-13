package com.sp.mixin;

import com.sp.ConfigStuff;
import com.sp.SPBRevamped;
import com.sp.SPBRevampedClient;
import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import com.sp.render.ShadowMapRenderer;
import foundry.veil.api.client.render.VeilRenderSystem;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = GameRenderer.class)
public abstract class GameRendererMixin {
    @Unique
    private static final Identifier shadowSolid = new Identifier(SPBRevamped.MOD_ID, "shadowmap/rendertype_solid");

    @Unique
    private static final Identifier shadowEntity = new Identifier(SPBRevamped.MOD_ID, "shadowmap/rendertype_entity");

    @Final
    @Shadow
    MinecraftClient client;

    @Shadow
    public abstract void setBlockOutlineEnabled(boolean blockOutlineEnabled);

    @Shadow
    private static ShaderProgram renderTypeTranslucentProgram;

    @Shadow public abstract MinecraftClient getClient();

    @Inject(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;bobView(Lnet/minecraft/client/util/math/MatrixStack;F)V", shift = At.Shift.BY, by = 2))
    public void renderWorld(float tickDelta, long limitTime, MatrixStack matrices, CallbackInfo ci){
        PlayerEntity player = client.player;
        this.setBlockOutlineEnabled(true);

        if (player != null) {
            PlayerComponent playerComponent = InitializeComponents.PLAYER.get(player);

            if(ConfigStuff.enableCameraRoll) {
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(playerComponent.getCameraRoll()));
            }
        }
    }


    @Inject(method = "getFov", at = @At(value = "RETURN", ordinal = 1), cancellable = true)
    private void getFov(Camera camera, float tickDelta, boolean changingFov, CallbackInfoReturnable<Double> cir) {
        cir.setReturnValue(SPBRevampedClient.doCameraZoom(cir.getReturnValue(), this.client, camera.getFocusedEntity()));
    }

//    @Inject(method = {"getRenderTypeTranslucentProgram"}, at = @At("HEAD"), cancellable = true)
//    private static void setWaterShader(CallbackInfoReturnable<ShaderProgram> cir) {
//        if(SPBRevampedClient.isInBackrooms()) {
//            net.minecraft.client.gl.ShaderProgram shader = VeilRenderSystem.renderer().getShaderManager().getShader(shaderid).toShaderInstance();
//            if (shader != null) cir.setReturnValue(shader);
//        }
//        else{
//            cir.setReturnValue(renderTypeTranslucentProgram);
//        }
//
//    }

    @Inject(method = {"getRenderTypeSolidProgram", "getRenderTypeCutoutProgram", "getRenderTypeCutoutMippedProgram"}, at = @At("HEAD"), cancellable = true)
    private static void setSolidShader(CallbackInfoReturnable<ShaderProgram> cir) {
        if(ShadowMapRenderer.isRenderingShadowMap()) {
            foundry.veil.api.client.render.shader.program.ShaderProgram shader = VeilRenderSystem.setShader(shadowSolid);
            if (shader == null) {
                return;
            }
            cir.setReturnValue(shader.toShaderInstance());
            return;
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
            foundry.veil.api.client.render.shader.program.ShaderProgram shader = VeilRenderSystem.setShader(shadowEntity);
            if (shader == null) {
                return;
            }
            cir.setReturnValue(shader.toShaderInstance());
            return;
        }
    }


}
