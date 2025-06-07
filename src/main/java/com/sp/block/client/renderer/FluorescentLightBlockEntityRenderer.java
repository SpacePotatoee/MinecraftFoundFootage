package com.sp.block.client.renderer;

import com.sp.block.custom.FluorescentLightBlock;
import com.sp.block.entity.FluorescentLightBlockEntity;
import com.sp.compat.modmenu.ConfigStuff;
import com.sp.render.RenderLayers;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import org.joml.Matrix4f;

public class FluorescentLightBlockEntityRenderer implements BlockEntityRenderer<FluorescentLightBlockEntity> {
    public FluorescentLightBlockEntityRenderer(BlockEntityRendererFactory.Context context){

    }

    @Override
    public void render(FluorescentLightBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        boolean blackout = entity.getCurrentState().get(FluorescentLightBlock.BLACKOUT);
        boolean on = entity.getCurrentState().get(FluorescentLightBlock.ON);

        //don't render if blackout is active
        if(blackout || !on) return;

        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        this.renderCube(entity, matrix4f, vertexConsumers.getBuffer(this.getLayer()));
    }

    private void renderCube(FluorescentLightBlockEntity entity, Matrix4f matrix, VertexConsumer buffer) {
        renderFace(entity, matrix, buffer, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, Direction.SOUTH);
        renderFace(entity, matrix, buffer, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, Direction.NORTH);
        renderFace(entity, matrix, buffer, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, Direction.EAST);
        renderFace(entity, matrix, buffer, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, Direction.WEST);
        renderFace(entity, matrix, buffer, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, Direction.DOWN);
        renderFace(entity, matrix, buffer, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, Direction.UP);
    }

    private void renderFace(FluorescentLightBlockEntity entity, Matrix4f matrix, VertexConsumer buffer, float f, float g, float h, float i, float j, float k, float l, float m, Direction direction) {
            buffer.vertex(matrix, f, h, j).next();
            buffer.vertex(matrix, g, h, k).next();
            buffer.vertex(matrix, g, i, l).next();
            buffer.vertex(matrix, f, i, m).next();
    }

    protected RenderLayer getLayer() {
        return RenderLayers.FLUORESCENT_LIGHT;
    }

    @Override
    public int getRenderDistance() {
        return (int) ConfigStuff.getLightRenderDistance();
    }
}
