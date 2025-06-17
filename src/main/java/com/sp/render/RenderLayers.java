package com.sp.render;

import com.sp.SPBRevamped;
import foundry.veil.api.client.render.VeilRenderBridge;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.util.function.Function;

public class RenderLayers extends RenderLayer {

    public static final Identifier NORMAL_ATLAS_TEXTURE = new Identifier(SPBRevamped.MOD_ID, "textures/atlas/normal.png");
    public static final Identifier HEIGHT_ATLAS_TEXTURE = new Identifier(SPBRevamped.MOD_ID, "textures/atlas/height.png");

    private static final RenderPhase.ShaderProgram LIGHT_SHADER = VeilRenderBridge.shaderState(new Identifier(SPBRevamped.MOD_ID, "light/fluorescent_light"));
    private static final RenderPhase.ShaderProgram DISTORTED_ENTITY_SHADER = VeilRenderBridge.shaderState(new Identifier(SPBRevamped.MOD_ID, "distorted_entity"));
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
                            .add(HEIGHT_ATLAS_TEXTURE, false, false)  // Sampler2 gets replaced with the lightmap texture for some reason
                            .add(HEIGHT_ATLAS_TEXTURE, false, true)  // Which is why I added it twice
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



    private static final Function<Identifier, RenderLayer> DISTORTED_ENTITY = Util.memoize((texture) -> {
        MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder()
                .program(DISTORTED_ENTITY_SHADER)
                .texture(new RenderPhase.Texture(texture, false, false))
                .transparency(RenderPhase.NO_TRANSPARENCY)
                .lightmap(ENABLE_LIGHTMAP)
                .overlay(ENABLE_OVERLAY_COLOR)
                .build(true);
        return of("distorted_entity", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS, 256, true, false, multiPhaseParameters);
    });

        /*RenderLayer.of(
            "distored_entity",
            VertexFormats.POSITION,
            VertexFormat.DrawMode.QUADS,
            256,
            false,
            false,
            RenderLayer.MultiPhaseParameters.builder()
                    .program(DISTORTED_ENTITY)
                    .build(true)
    );
    */

    public static RenderLayer getDistortedEntity(Identifier texture) {
        return DISTORTED_ENTITY.apply(texture);
    }

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
