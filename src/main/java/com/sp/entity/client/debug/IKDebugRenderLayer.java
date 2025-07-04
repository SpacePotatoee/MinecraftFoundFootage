package com.sp.entity.client.debug;

import com.sp.entity.ik.components.IKAnimatable;
import com.sp.entity.ik.util.PrAnCommonClass;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class IKDebugRenderLayer<T extends GeoAnimatable> extends GeoRenderLayer<T> {
    ///summon projectnublar:tyrannosaurus_rex ~ ~ ~ {NoAI:1b}

    public static int getArgb(int alpha, int red, int green, int blue) {
        return alpha << 24 | red << 16 | green << 8 | blue;
    }

    public IKDebugRenderLayer(GeoRenderer<T> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(MatrixStack poseStack, T animatable, BakedGeoModel bakedModel, RenderLayer renderType, VertexConsumerProvider bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        if (!PrAnCommonClass.shouldRenderDebugLegs || !(animatable instanceof IKAnimatable ikAnimatable)) {
            return;
        }

        ikAnimatable.renderDebug(poseStack, ikAnimatable, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
    }

}
