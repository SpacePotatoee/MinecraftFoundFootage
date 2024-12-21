package com.sp.entity.client.renderer;

import com.sp.SPBRevamped;
import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.SmilerComponent;
import com.sp.entity.client.model.SmilerModel;
import com.sp.entity.custom.SmilerEntity;
import com.sp.init.ModModelLayers;
import com.sp.util.MathStuff;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec2f;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class SmilerRenderer extends MobEntityRenderer<SmilerEntity, SmilerModel<SmilerEntity>> {
    private static final Identifier defaultTexture = new Identifier(SPBRevamped.MOD_ID, "textures/entity/smiler/smiler.png");
    private static final Identifier texture1 = new Identifier(SPBRevamped.MOD_ID, "textures/entity/smiler/smiler1.png");
    private static final Identifier texture2 = new Identifier(SPBRevamped.MOD_ID, "textures/entity/smiler/smiler2.png");

    public SmilerRenderer(EntityRendererFactory.Context context) {
        super(context, new SmilerModel<>(context.getPart(ModModelLayers.SMILER)), 0);
    }

    @Override
    public void render(SmilerEntity mobEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
        matrixStack.translate(0,1,0);
        float angle = MathStuff.lookAtEntityAroundYAxis(mobEntity.getEyePos(), camera.getPos());
        matrixStack.multiply(new Quaternionf().rotateXYZ(0, (float) Math.toRadians(angle), 0));
        matrixStack.translate(0,-1,0);


        super.render(mobEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }

    @Override
    public Identifier getTexture(SmilerEntity entity) {
        SmilerComponent component = InitializeComponents.SMILER.get(entity);
        return switch (component.getRandomTexture()) {
            case 1 -> texture1;
            case 2 -> texture2;
            default -> defaultTexture;
        };
    }

    @Override
    protected @Nullable RenderLayer getRenderLayer(SmilerEntity entity, boolean showBody, boolean translucent, boolean showOutline) {
        return super.getRenderLayer(entity, showBody, translucent, showOutline);
    }
}
