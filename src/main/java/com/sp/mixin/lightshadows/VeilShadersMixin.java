package com.sp.mixin.lightshadows;

import foundry.veil.api.client.render.shader.VeilShaders;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(VeilShaders.class)
public class VeilShadersMixin {

    @Shadow
    public static final Identifier LIGHT_POINT = new Identifier("spbrevamped", "point");

    @Shadow
    public static final Identifier LIGHT_AREA = new Identifier("spbrevamped", "area");
}
