package com.sp.mixin.uniform;

import com.llamalad7.mixinextras.sugar.Local;
import com.sp.render.ShadowMapRenderer;
import com.sp.util.MatrixMath;
import com.sp.util.uniformTest;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class WorldRendererUniformMixin {

    @Inject(method = "renderLayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gl/GlUniform;set(F)V", ordinal = 3, shift = At.Shift.BY, by = 2))
    public void uniformInject(RenderLayer renderLayer, MatrixStack matrices, double cameraX, double cameraY, double cameraZ, Matrix4f positionMatrix, CallbackInfo ci, @Local ShaderProgram shaderProgram){
        if(shaderProgram instanceof uniformTest) {
            if (((uniformTest) shaderProgram).getOrthoMatrix() != null) {
                Matrix4f matrix4f = createProjMat();
                ((uniformTest) shaderProgram).getOrthoMatrix().set(matrix4f);
            }

            if (((uniformTest) shaderProgram).getViewMatrix() != null) {
                MatrixStack shadowModelView = ShadowMapRenderer.createShadowModelView(cameraX, cameraY, cameraZ, true);

                ((uniformTest) shaderProgram).getViewMatrix().set(shadowModelView.peek().getPositionMatrix());
            }

            if (((uniformTest) shaderProgram).getLightAngle() != null) {
                Matrix4f shadowModelView = new Matrix4f();
                shadowModelView.identity();
                ShadowMapRenderer.rotateShadowModelView(shadowModelView);
                Vector4f lightPosition = new Vector4f(0.0f, 0.0f, 1.0f, 0.0f);
                lightPosition.mul(shadowModelView.invert());

                Vector3f shadowLightDirection = new Vector3f(lightPosition.x(), lightPosition.y(), lightPosition.z());

                ((uniformTest) shaderProgram).getLightAngle().set(shadowLightDirection);
            }
        }
    }

    @Unique
    public Matrix4f createProjMat(){
        return MatrixMath.orthographicMatrix(160, 0.05f, 256.0f);
    }

}
