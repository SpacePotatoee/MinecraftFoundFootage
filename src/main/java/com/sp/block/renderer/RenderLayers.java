package com.sp.block.renderer;

import com.sp.SPBRevamped;
import foundry.veil.api.client.render.VeilRenderBridge;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;

public class RenderLayers extends RenderLayer {
    public static final RenderPhase.ShaderProgram LIGHT_SHADER = VeilRenderBridge.shaderState(new Identifier(SPBRevamped.MOD_ID, "fluorescent_light"));
    public static final RenderPhase.ShaderProgram CONCRETE_SHADER = VeilRenderBridge.shaderState(new Identifier(SPBRevamped.MOD_ID, "concrete_shader"));
    public static final RenderPhase.ShaderProgram CURVE = VeilRenderBridge.shaderState(new Identifier(SPBRevamped.MOD_ID, "red_tint"));


    public static final RenderLayer FLUORESCENT_LIGHT_RL = RenderLayer.of(
            "fluorescent_light",
            VertexFormats.POSITION,
            VertexFormat.DrawMode.QUADS,
            256,
            false,
            false,
            RenderLayer.MultiPhaseParameters.builder()
                    .program(LIGHT_SHADER)
                    .build(false)

    );

    public static final RenderLayer RED_TINT = RenderLayer.of(
            "red_tint",
            VertexFormats.POSITION,
            VertexFormat.DrawMode.QUADS,
            256,
            false,
            false,
            RenderLayer.MultiPhaseParameters.builder()
                    .program(CURVE)
                    .build(false)

    );

    public static final RenderLayer CONCRETE_NOISE = RenderLayer.of(
            "concrete_shader",
            VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL,
            VertexFormat.DrawMode.QUADS,
            131072,
            false,
            false,
            RenderLayer.MultiPhaseParameters.builder()
                    .program(CONCRETE_SHADER)
                    .build(false)

    );

    public static RenderLayer getConcreteNoise() {
        return CONCRETE_NOISE;
    }



    public RenderLayers(String name, VertexFormat vertexFormat, VertexFormat.DrawMode drawMode, int expectedBufferSize, boolean hasCrumbling, boolean translucent, Runnable startAction, Runnable endAction) {
        super(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, startAction, endAction);
    }
}
