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
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.ByteBuffer;

@Mixin(value = AreaLight.class, remap = false)
public abstract class AreaLightMixin extends Light implements InstancedLight, PositionedLight<AreaLight>, EditorAttributeProvider {

    @Shadow @Final private Matrix4d matrix;
    @Shadow @Final protected Vector2f size;
    @Shadow protected float angle;
    @Shadow @Final private static float MAX_ANGLE_SIZE;
    @Shadow protected float distance;

    @Inject(method = "store", at = @At("HEAD"), cancellable = true)
    private void lightFix(ByteBuffer buffer, CallbackInfo ci){
        ci.cancel();
        this.matrix.getFloats(buffer.position(), buffer);
        buffer.position(buffer.position() + Float.BYTES * 16);

        buffer.putFloat(this.color.x() * this.brightness);
        buffer.putFloat(this.color.y() * this.brightness);
        buffer.putFloat(this.color.z() * this.brightness);

        this.size.get(buffer.position(), buffer);
        buffer.position(buffer.position() + Float.BYTES * 2);

        buffer.putFloat((float) MathHelper.clamp((int) (this.angle * MAX_ANGLE_SIZE), 0, 65535));
        buffer.putFloat(this.distance);
    }

}
