package com.sp.mixin.arealightfix;

import foundry.veil.api.client.editor.EditorAttributeProvider;
import foundry.veil.api.client.render.deferred.light.AreaLight;
import foundry.veil.api.client.render.deferred.light.InstancedLight;
import foundry.veil.api.client.render.deferred.light.Light;
import foundry.veil.api.client.render.deferred.light.PositionedLight;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4d;
import org.joml.Vector2f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.nio.ByteBuffer;

@Mixin(value = AreaLight.class, remap = false)
public abstract class AreaLightMixin extends Light implements InstancedLight, PositionedLight<AreaLight>, EditorAttributeProvider {

    @Shadow @Final private Matrix4d matrix;
    @Shadow @Final protected Vector2f size;
    @Shadow protected float angle;
    @Shadow @Final private static float MAX_ANGLE_SIZE;
    @Shadow protected float distance;

    @Redirect(method = "store", at = @At(value = "INVOKE", target = "Ljava/nio/ByteBuffer;putShort(S)Ljava/nio/ByteBuffer;"))
    private ByteBuffer lightFix(ByteBuffer instance, short i){
        return instance.putFloat((float) MathHelper.clamp((int) (this.angle * MAX_ANGLE_SIZE), 0, 65535));
    }

}
