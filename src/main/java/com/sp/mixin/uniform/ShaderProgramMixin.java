package com.sp.mixin.uniform;

import com.sp.mixininterfaces.uniformTest;
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
    public GlUniform getWarpAngle() {
        return warpAngle;
    }

    @Override
    public GlUniform getAtlasAspectRatio() {
        return atlasAspectRatio;
    }

    @Unique public GlUniform warpAngle;
    @Unique public GlUniform atlasAspectRatio;

    @Shadow
    abstract GlUniform getUniform(String name);

    //@Inject(method = "<init>", at = @At("TAIL"))
    public void ShaderProgram(ResourceFactory factory, String name, VertexFormat format, CallbackInfo ci){
        this.warpAngle = this.getUniform("warpAngle");
        this.atlasAspectRatio = this.getUniform("atlasAspectRatio");
    }
}
