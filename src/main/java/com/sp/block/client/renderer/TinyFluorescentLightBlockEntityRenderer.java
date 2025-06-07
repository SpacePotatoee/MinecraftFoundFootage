package com.sp.block.client.renderer;

import com.sp.SPBRevamped;
import com.sp.SPBRevampedClient;
import com.sp.block.custom.TinyFluorescentLightBlock;
import com.sp.block.entity.TinyFluorescentLightBlockEntity;
import com.sp.compat.modmenu.ConfigStuff;
import com.sp.render.RenderLayers;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;

import static net.minecraft.util.math.Direction.WEST;

public class TinyFluorescentLightBlockEntityRenderer implements BlockEntityRenderer<TinyFluorescentLightBlockEntity> {
    private static final Identifier SHADER = new Identifier(SPBRevamped.MOD_ID, "light/fluorescent_light");


    public TinyFluorescentLightBlockEntityRenderer(BlockEntityRendererFactory.Context context){

    }


    @Override
    public void render(TinyFluorescentLightBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        MinecraftClient client = MinecraftClient.getInstance();
        boolean blackout = entity.getCurrentState().get(TinyFluorescentLightBlock.BLACKOUT);
        boolean on = entity.getCurrentState().get(TinyFluorescentLightBlock.ON);

        ShaderProgram shader = VeilRenderSystem.setShader(SHADER);
        if(shader == null){
            return;
        }

        if(client.world != null) {
            shader.setFloat("warAngle", SPBRevampedClient.getWarpTimer(client.world));
        }

        //don't render if blackout is active
        if(blackout || !on) return;



        Matrix4f matrix4f = matrices.peek().getPositionMatrix();

        shader.bind();
        this.renderCube(entity, matrix4f, vertexConsumers.getBuffer(this.getLayer()));
        ShaderProgram.unbind();
    }

    private void renderCube(TinyFluorescentLightBlockEntity entity, Matrix4f matrix, VertexConsumer buffer) {
            renderFace(entity, matrix, buffer, 0.375f, 0.625f, 0.875f, 1, 0.625f, 0.625f, 0.625f, 0.625f, Direction.SOUTH);
            renderFace(entity, matrix, buffer, 0.375f, 0.625f, 1.0f, 0.875f, 0.375f, 0.375f, 0.375f, 0.375f, Direction.NORTH);
            renderFace(entity, matrix, buffer, 0.625f, 0.625f, 1.0f, 0.875f, 0.375f, 0.625f, 0.625f, 0.375f, Direction.EAST);
            renderFace(entity, matrix, buffer, 0.375f, 0.375f, 0.875f, 1.0f, 0.375f, 0.625f, 0.625f, 0.375f, WEST);
            renderFace(entity, matrix, buffer, 0.375f, 0.625f, 0.875f, 0.875f, 0.375f, 0.375f, 0.625f, 0.625f, Direction.DOWN);
            renderFace(entity, matrix, buffer, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.375f, 0.375f, 0.375f, Direction.UP);
    }

    private void renderFace(TinyFluorescentLightBlockEntity entity, Matrix4f matrix, VertexConsumer buffer, float x, float x2, float y, float y2, float z, float z2, float z3, float z4, Direction direction) {
            buffer.vertex(matrix, x, y, z).next();
            buffer.vertex(matrix, x2, y, z2).next();
            buffer.vertex(matrix, x2, y2, z3).next();
            buffer.vertex(matrix, x, y2, z4).next();
    }

    protected RenderLayer getLayer() {
        return RenderLayers.FLUORESCENT_LIGHT;
    }

    @Override
    public int getRenderDistance() {
        return (int) ConfigStuff.getLightRenderDistance();
    }
}
