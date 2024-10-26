package com.sp.init;

import com.sp.SPBRevamped;
import foundry.veil.api.client.render.VeilRenderBridge;
import net.minecraft.client.render.*;
import net.minecraft.util.Identifier;

public class RenderLayers extends RenderLayer {
    private static final RenderPhase.ShaderProgram LIGHT_SHADER = VeilRenderBridge.shaderState(new Identifier(SPBRevamped.MOD_ID, "light/fluorescent_light"));
    private static final RenderPhase.ShaderProgram CONCRETE_SHADER = VeilRenderBridge.shaderState(new Identifier(SPBRevamped.MOD_ID, "concrete_shader"));
    private static final RenderPhase.ShaderProgram WINDOW = VeilRenderBridge.shaderState(new Identifier(SPBRevamped.MOD_ID, "light/window"));


    public static final RenderLayer FLUORESCENT_LIGHT = RenderLayer.of(
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

    public static final RenderLayer POOLROOMS_WINDOW = RenderLayer.of(
            "window",
            VertexFormats.POSITION,
            VertexFormat.DrawMode.QUADS,
            256,
            false,
            false,
            RenderLayer.MultiPhaseParameters.builder()
                    .program(WINDOW)
                    .build(false)
    );

    public static final RenderLayer CONCRETE_NOISE = RenderLayer.of(
            "concrete_shader",
            VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL,
            VertexFormat.DrawMode.QUADS,
            256,
            false,
            false,
            RenderLayer.MultiPhaseParameters.builder().lightmap(ENABLE_LIGHTMAP).program(CONCRETE_SHADER).texture(BLOCK_ATLAS_TEXTURE).build(true)
    );

    public static RenderLayer getConcreteNoise() {
        return CONCRETE_NOISE;
    }



    public RenderLayers(String name, VertexFormat vertexFormat, VertexFormat.DrawMode drawMode, int expectedBufferSize, boolean hasCrumbling, boolean translucent, Runnable startAction, Runnable endAction) {
        super(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, startAction, endAction);
    }
}
