package com.sp.mixin.uniform;

import com.sp.util.uniformTest;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.resource.ResourceFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ShaderProgram.class)
public abstract class ShaderProgramMixin implements uniformTest {
    @Override
    public GlUniform getOrthoMatrix() {
        return orthoMatrix;
    }

    @Override
    public GlUniform getViewMatrix() {
        return viewMatrix;
    }

    @Override
    public GlUniform getLightAngle() {
        return lightAngle;
    }

    @Override
    public GlUniform getWarpAngle() {
        return warpAngle;
    }

    @Unique
    public GlUniform orthoMatrix;

    @Unique
    public GlUniform viewMatrix;

    @Unique
    public GlUniform lightAngle;

    @Unique
    public GlUniform warpAngle;

    @Shadow
    abstract GlUniform getUniform(String name);

    @Inject(method = "<init>", at = @At("TAIL"))
    public void ShaderProgram(ResourceFactory factory, String name, VertexFormat format, CallbackInfo ci){
        this.orthoMatrix = this.getUniform("orthoMatrix");
        this.viewMatrix = this.getUniform("viewRix");
        this.lightAngle = this.getUniform("lightAngle");
        this.warpAngle = this.getUniform("warpAngle");
    }
}
