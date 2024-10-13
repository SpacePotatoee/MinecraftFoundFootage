package com.sp.block.renderer;

import com.sp.ConfigStuff;
import com.sp.block.custom.ThinFluorescentLightBlock;
import com.sp.block.entity.ThinFluorescentLightBlockEntity;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;

import static net.minecraft.util.math.Direction.*;

public class ThinFluorescentLightBlockEntityRenderer implements BlockEntityRenderer<ThinFluorescentLightBlockEntity> {
    public ThinFluorescentLightBlockEntityRenderer(BlockEntityRendererFactory.Context context){

    }


    @Override
    public void render(ThinFluorescentLightBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {

        Direction facing = entity.getCurrentState().get(ThinFluorescentLightBlock.FACING);
        WallMountLocation wall = entity.getCurrentState().get(ThinFluorescentLightBlock.FACE);

        matrices.translate(0.5, 0.5, 0.5);
        if(wall == WallMountLocation.CEILING) {
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(facing.getOpposite().asRotation()));
        }
        if(wall == WallMountLocation.WALL){
            matrices.multiply(facing.getOpposite().getRotationQuaternion());
        }
        if(wall == WallMountLocation.FLOOR){
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(facing.asRotation()));
        }
        matrices.translate(-0.5, -0.5, -0.5);
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        this.renderCube(entity, matrix4f, vertexConsumers.getBuffer(this.getLayer()));
    }

    private void renderCube(ThinFluorescentLightBlockEntity entity, Matrix4f matrix, VertexConsumer buffer) {
            renderFace(entity, matrix, buffer, 0.375f, 0.625f, 0.875f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, Direction.SOUTH);
            renderFace(entity, matrix, buffer, 0.375f, 0.625f, 1.0f, 0.875f, 0.0f, 0.0f, 0.0f, 0.0f, Direction.NORTH);
            renderFace(entity, matrix, buffer, 0.625f, 0.625f, 1.0f, 0.875f, 0.0f, 1.0f, 1.0f, 0.0f, Direction.EAST);
            renderFace(entity, matrix, buffer, 0.375f, 0.375f, 0.875f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, WEST);
            renderFace(entity, matrix, buffer, 0.375f, 0.625f, 0.875f, 0.875f, 0.0f, 0.0f, 1.0f, 1.0f, Direction.DOWN);
            renderFace(entity, matrix, buffer, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, Direction.UP);

    }

    private void renderFace(ThinFluorescentLightBlockEntity entity, Matrix4f matrix, VertexConsumer buffer, float f, float g, float h, float i, float j, float k, float l, float m, Direction direction) {
            buffer.vertex(matrix, f, h, j).next();
            buffer.vertex(matrix, g, h, k).next();
            buffer.vertex(matrix, g, i, l).next();
            buffer.vertex(matrix, f, i, m).next();
    }

    protected RenderLayer getLayer() {
        return RenderLayers.FLUORESCENT_LIGHT_RL;
    }

    @Override
    public int getRenderDistance() {
        return (int) ConfigStuff.lightRenderDistance + 10;
    }
}
