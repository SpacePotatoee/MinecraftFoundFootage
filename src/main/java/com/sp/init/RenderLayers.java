package com.sp.init;

import com.sp.SPBRevamped;
import com.sp.render.ShadowMapRenderer;
import foundry.veil.api.client.render.VeilRenderBridge;
import foundry.veil.api.client.render.VeilRenderSystem;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.*;
import net.minecraft.util.Identifier;

public class RenderLayers extends RenderLayer {
    private static final RenderPhase.ShaderProgram LIGHT_SHADER = VeilRenderBridge.shaderState(new Identifier(SPBRevamped.MOD_ID, "light/fluorescent_light"));
    private static final RenderPhase.ShaderProgram CEILING_TILE_SHADER = VeilRenderBridge.shaderState(new Identifier(SPBRevamped.MOD_ID, "pbr/ceilingtile/ceilingtile"));
    private static final RenderPhase.ShaderProgram CONCRETE_SHADER = VeilRenderBridge.shaderState(new Identifier(SPBRevamped.MOD_ID, "pbr/concrete/concrete"));
    private static final RenderPhase.ShaderProgram CARPET_SHADER = VeilRenderBridge.shaderState(new Identifier(SPBRevamped.MOD_ID, "pbr/carpet/carpet"));
    private static final RenderPhase.ShaderProgram CHAIN_FENCE_SHADER = VeilRenderBridge.shaderState(new Identifier(SPBRevamped.MOD_ID, "pbr/chainfence/chainfence"));
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

    public static final RenderLayer CONCRETE_LAYER = RenderLayer.of(
            "concrete",
            VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL,
            VertexFormat.DrawMode.QUADS,
            2097152,
            false,
            false,
            RenderLayer.MultiPhaseParameters.builder()
                    .lightmap(ENABLE_LIGHTMAP)
                    .program(CONCRETE_SHADER)
                    .texture(MIPMAP_BLOCK_ATLAS_TEXTURE)
                    .build(true)
    );

    public static final RenderLayer CEILING_TILE = RenderLayer.of(
            "ceiling_tile",
            VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL,
            VertexFormat.DrawMode.QUADS,
            2097152,
            false,
            false,
            RenderLayer.MultiPhaseParameters.builder()
                    .lightmap(ENABLE_LIGHTMAP)
                    .program(CEILING_TILE_SHADER)
                    .texture(MIPMAP_BLOCK_ATLAS_TEXTURE)
                    .build(true)
    );

    public static final RenderLayer CARPET = RenderLayer.of(
            "carpet",
            VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL,
            VertexFormat.DrawMode.QUADS,
            2097152,
            false,
            false,
            RenderLayer.MultiPhaseParameters.builder()
                    .lightmap(ENABLE_LIGHTMAP)
                    .program(CARPET_SHADER)
                    .texture(MIPMAP_BLOCK_ATLAS_TEXTURE)
                    .build(true)
    );

    public static final RenderLayer CHAIN_FENCE = RenderLayer.of(
            "carpet",
            VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL,
            VertexFormat.DrawMode.QUADS,
            2097152,
            false,
            false,
            RenderLayer.MultiPhaseParameters.builder()
                    .lightmap(ENABLE_LIGHTMAP)
                    .program(CHAIN_FENCE_SHADER)
                    .texture(MIPMAP_BLOCK_ATLAS_TEXTURE)
                    .build(true)
    );

    public static RenderLayer getConcreteLayer() {
        return CONCRETE_LAYER;
    }

    public static RenderLayer getCeilingTile() {
        return CEILING_TILE;
    }

    public static RenderLayer getCarpet() {
        return CARPET;
    }

    public static RenderLayer getChainFence() {
        return CHAIN_FENCE;
    }



    public RenderLayers(String name, VertexFormat vertexFormat, VertexFormat.DrawMode drawMode, int expectedBufferSize, boolean hasCrumbling, boolean translucent, Runnable startAction, Runnable endAction) {
        super(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, startAction, endAction);
    }
}
