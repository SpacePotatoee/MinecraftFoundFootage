package com.sp.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.SmilerComponent;
import com.sp.entity.custom.SmilerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> {
    private LivingEntity entity;

    @Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("HEAD"))
    private void getEntity(T livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci){
        this.entity = livingEntity;
    }

    @ModifyExpressionValue(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = {
                @At(value = "CONSTANT", args = "floatValue=1.0", ordinal = 3),
                @At(value = "CONSTANT", args = "floatValue=1.0", ordinal = 4),
                @At(value = "CONSTANT", args = "floatValue=1.0", ordinal = 5),
                @At(value = "CONSTANT", args = "floatValue=1.0", ordinal = 6)
            })
    private float setOpacity(float constant){
        if(entity instanceof SmilerEntity) {
            SmilerComponent component = InitializeComponents.SMILER.get(entity);
            return component.getOpacity();
        }

        return constant;
    }

}
