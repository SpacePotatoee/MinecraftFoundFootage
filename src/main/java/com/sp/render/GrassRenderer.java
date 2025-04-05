package com.sp.render;

import com.sp.SPBRevamped;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.framebuffer.AdvancedFbo;
import foundry.veil.api.client.render.framebuffer.VeilFramebuffers;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;

public class GrassRenderer {
    VertexBuffer vertexBuffer;
    private static final Identifier shaderPath = new Identifier(SPBRevamped.MOD_ID, "grass/grass");


    public GrassRenderer() {
        this.vertexBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();

        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);


        this.crateGrass(bufferBuilder);

        this.vertexBuffer.bind();
        this.vertexBuffer.upload(bufferBuilder.end());
        VertexBuffer.unbind();
    }

    public void render() {
//        AdvancedFbo fbo = VeilRenderSystem.renderer().getFramebufferManager().getFramebuffer(VeilFramebuffers.OPAQUE);
//        if(fbo == null) return;
//        fbo.bind(true);

        ShaderProgram shader = VeilRenderSystem.setShader(shaderPath);
        if(shader == null) return;


        this.vertexBuffer.bind();
        shader.bind();

        VeilRenderSystem.drawInstanced(this.vertexBuffer, 1);

        ShaderProgram.unbind();
        VertexBuffer.unbind();
//        AdvancedFbo.unbind();
    }

    private void crateGrass(BufferBuilder bufferBuilder) {
        bufferBuilder.vertex(0,1,0).color(255,255,255,255);
        bufferBuilder.vertex(1,1,0).color(255,255,255,255);
        bufferBuilder.vertex(1,0,0).color(255,255,255,255);
        bufferBuilder.vertex(0,0,0).color(255,255,255,255);
    }

}