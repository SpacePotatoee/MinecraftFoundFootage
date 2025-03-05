package com.sp.render;

import com.sp.SPBRevamped;
import foundry.veil.api.client.render.VeilRenderBridge;
import foundry.veil.api.client.render.VeilRenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;

public class RenderLayers extends RenderLayer {
    private static final MinecraftClient client = MinecraftClient.getInstance();

    //PBR material identifiers
    private static final Identifier CARPET_COLOR = new Identifier(SPBRevamped.MOD_ID, "textures/shaders/carpet/carpet_color.png");
    private static final Identifier CARPET_NORMAL = new Identifier(SPBRevamped.MOD_ID, "textures/shaders/carpet/carpet_normal.png");

    private static final Identifier CEILING_TILE_NORMAL = new Identifier(SPBRevamped.MOD_ID, "textures/shaders/ceilingtile/ceiling_tile_normal.png");


    private static final RenderPhase.ShaderProgram LIGHT_SHADER = VeilRenderBridge.shaderState(new Identifier(SPBRevamped.MOD_ID, "light/fluorescent_light"));


    private static final RenderPhase.ShaderProgram CEILING_TILE_SHADER = VeilRenderBridge.shaderState(new Identifier(SPBRevamped.MOD_ID, "pbr/ceilingtile/ceilingtile"));
    private static final RenderPhase.ShaderProgram CHAIN_FENCE_SHADER = VeilRenderBridge.shaderState(new Identifier(SPBRevamped.MOD_ID, "pbr/chainfence/chainfence"));
    private static final RenderPhase.ShaderProgram BRICK_SHADER = VeilRenderBridge.shaderState(new Identifier(SPBRevamped.MOD_ID, "pbr/bricks/bricks"));
    private static final RenderPhase.ShaderProgram WOODEN_CRATE = VeilRenderBridge.shaderState(new Identifier(SPBRevamped.MOD_ID, "pbr/crate/crate"));
    private static final RenderPhase.ShaderProgram CONCRETE = VeilRenderBridge.shaderState(new Identifier(SPBRevamped.MOD_ID, "pbr/concrete/concrete"));
    private static final RenderPhase.ShaderProgram POOLROOMS_SKY_SHADER = VeilRenderBridge.shaderState(new Identifier(SPBRevamped.MOD_ID, "sky"));


    private static final Identifier shadowSolid = new Identifier(SPBRevamped.MOD_ID, "shadowmap/rendertype_solid");


    private static final Identifier normalCarpet = new Identifier(SPBRevamped.MOD_ID, "pbr/carpet/carpet");
    public static final RenderPhase.ShaderProgram CARPET_PROGRAM = new RenderPhase.ShaderProgram(RenderLayers::getCarpetProgram);

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

    private static final RenderLayer POOLROOMS_SKY = RenderLayer.of(
            "poolrooms_sky",
            VertexFormats.POSITION,
            VertexFormat.DrawMode.QUADS,
            256,
            false,
            false,
            RenderLayer.MultiPhaseParameters.builder()
                    .program(POOLROOMS_SKY_SHADER)
                    .build(true)
    );

    private static final RenderLayer CONCRETE_LAYER = RenderLayer.of(
            "concrete",
            VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL,
            VertexFormat.DrawMode.QUADS,
            2097152,
            false,
            false,
            RenderLayer.MultiPhaseParameters.builder()
                    .lightmap(ENABLE_LIGHTMAP)
                    .program(CONCRETE)
                    .texture(MIPMAP_BLOCK_ATLAS_TEXTURE)
                    .build(true)
    );

    private static final RenderLayer CEILING_TILE = RenderLayer.of(
            "ceiling_tile",
            VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL,
            VertexFormat.DrawMode.QUADS,
            2097152,
            false,
            false,
            RenderLayer.MultiPhaseParameters.builder()
                    .lightmap(ENABLE_LIGHTMAP)
                    .program(CEILING_TILE_SHADER)
                    .texture(
                            RenderPhase.Textures.create()
                                    .add(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, false, true)
                                    .add(CEILING_TILE_NORMAL, false, true)
                                    .build()
                    )
                    .build(true)
    );

    private static final RenderLayer CARPET = RenderLayer.of(
            "carpet",
            VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL,
            VertexFormat.DrawMode.QUADS,
            1536,
            false,
            false,
            RenderLayer.MultiPhaseParameters.builder()
                    .lightmap(ENABLE_LIGHTMAP)
                    .program(CARPET_PROGRAM)
                    .texture(
                            RenderPhase.Textures.create()
                                    .add(CARPET_COLOR, false, true)
                                    .add(CARPET_NORMAL, false, true)
                                    .build()
                    )
                    .build(true)
    );

    private static final RenderLayer CHAIN_FENCE = RenderLayer.of(
            "chain_fence",
            VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL,
            VertexFormat.DrawMode.QUADS,
            1536,
            false,
            false,
            RenderLayer.MultiPhaseParameters.builder()
                    .lightmap(ENABLE_LIGHTMAP)
                    .program(CHAIN_FENCE_SHADER)
                    .texture(MIPMAP_BLOCK_ATLAS_TEXTURE)
                    .build(true)
    );

    private static final RenderLayer BRICKS_LAYER = RenderLayer.of(
            "bricks",
            VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL,
            VertexFormat.DrawMode.QUADS,
            1536,
            false,
            false,
            RenderLayer.MultiPhaseParameters.builder()
                    .lightmap(ENABLE_LIGHTMAP)
                    .program(BRICK_SHADER)
                    .texture(MIPMAP_BLOCK_ATLAS_TEXTURE)
                    .build(true)
    );

    public static final RenderLayer WOODEN_CRATE_LAYER = RenderLayer.of(
            "crate",
            VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL,
            VertexFormat.DrawMode.QUADS,
            1536,
            false,
            false,
            RenderLayer.MultiPhaseParameters.builder()
                    .lightmap(ENABLE_LIGHTMAP)
                    .program(WOODEN_CRATE)
                    .texture(MIPMAP_BLOCK_ATLAS_TEXTURE)
                    .build(true)
    );

    private static net.minecraft.client.gl.ShaderProgram getCarpetProgram(){
        if(ShadowMapRenderer.isRenderingShadowMap()) {
            foundry.veil.api.client.render.shader.program.ShaderProgram shader = VeilRenderSystem.renderer().getShaderManager().getShader(shadowSolid);
            if (shader == null) {
                return null;
            }
            return shader.toShaderInstance();
        }
        foundry.veil.api.client.render.shader.program.ShaderProgram shader = VeilRenderSystem.renderer().getShaderManager().getShader(normalCarpet);
        if (shader == null) {
            return null;
        }

        return shader.toShaderInstance();
    }

    public static RenderLayer getConcreteLayer() {
        return CONCRETE_LAYER;
    }


    public static RenderLayer getPoolroomsSky() {
        return POOLROOMS_SKY;
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

    public static RenderLayer getBricksLayer() {
        return BRICKS_LAYER;
    }

    public static RenderLayer getWoodenCrateLayer() {
        return WOODEN_CRATE_LAYER;
    }

    public RenderLayers(String name, VertexFormat vertexFormat, VertexFormat.DrawMode drawMode, int expectedBufferSize, boolean hasCrumbling, boolean translucent, Runnable startAction, Runnable endAction) {
        super(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, startAction, endAction);
    }
}
