package com.sp.render;

import com.sp.SPBRevamped;
import foundry.veil.api.client.render.VeilRenderBridge;
import foundry.veil.api.client.render.VeilRenderSystem;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;

public class RenderLayers extends RenderLayer {

    public static final Identifier NORMAL_ATLAS_TEXTURE = new Identifier(SPBRevamped.MOD_ID, "textures/atlas/normal.png");
    public static final Identifier HEIGHT_ATLAS_TEXTURE = new Identifier(SPBRevamped.MOD_ID, "textures/atlas/height.png");

    private static final RenderPhase.ShaderProgram LIGHT_SHADER = VeilRenderBridge.shaderState(new Identifier(SPBRevamped.MOD_ID, "light/fluorescent_light"));
    private static final RenderPhase.ShaderProgram POOLROOMS_SKY_SHADER = VeilRenderBridge.shaderState(new Identifier(SPBRevamped.MOD_ID, "sky"));
    private static final RenderPhase.ShaderProgram PBR_SHADER = VeilRenderBridge.shaderState(new Identifier(SPBRevamped.MOD_ID, "pbr/pbr"));

    private static final RenderLayer PBR_LAYER = RenderLayer.of(
            "pbr",
            com.sp.render.VertexFormats.PBR,
            VertexFormat.DrawMode.QUADS,
            2097152,
            false,
            false,
            RenderLayer.MultiPhaseParameters.builder()
                    .lightmap(ENABLE_LIGHTMAP)
                    .program(PBR_SHADER)
                    .texture(RenderPhase.Textures.create()
                            .add(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, false, true)
                            .add(NORMAL_ATLAS_TEXTURE, false, true)
                            .add(HEIGHT_ATLAS_TEXTURE, false, true)
                            .add(HEIGHT_ATLAS_TEXTURE, false, true)
                            .build()
                    )
                    .build(true)
    );

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

    public static RenderLayer getPbrLayer() {
        return PBR_LAYER;
    }
    public static RenderLayer getPoolroomsSky() {
        return POOLROOMS_SKY;
    }

    public RenderLayers(String name, VertexFormat vertexFormat, VertexFormat.DrawMode drawMode, int expectedBufferSize, boolean hasCrumbling, boolean translucent, Runnable startAction, Runnable endAction) {
        super(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, startAction, endAction);
    }
}
