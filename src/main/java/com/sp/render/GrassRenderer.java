package com.sp.render;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.systems.RenderSystem;
import com.sp.SPBRevamped;
import com.sp.compat.modmenu.ConfigStuff;
import foundry.veil.api.client.render.CullFrustum;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.VeilRenderer;
import foundry.veil.api.client.render.framebuffer.AdvancedFbo;
import foundry.veil.api.client.render.framebuffer.VeilFramebuffers;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.*;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import org.joml.Matrix3f;
import org.joml.Vector3f;
import org.joml.Vector4fc;

import static net.minecraft.client.render.VertexFormats.NORMAL_ELEMENT;
import static net.minecraft.client.render.VertexFormats.POSITION_ELEMENT;
import static net.minecraft.util.math.MathHelper.floor;
import static net.minecraft.util.math.MathHelper.sqrt;
import static org.lwjgl.opengl.GL11C.glDisable;
import static org.lwjgl.opengl.GL11C.glEnable;
import static org.lwjgl.opengl.GL30C.GL_CLIP_DISTANCE0;

public class GrassRenderer {
    VertexBuffer vertexBuffer;
    private static final Identifier shaderPath = new Identifier(SPBRevamped.MOD_ID, "grass/grass");
    private static final Identifier windTexture = new Identifier(SPBRevamped.MOD_ID, "textures/shaders/puddle_noise.png");

    public static final VertexFormat POSITION_NORMAL = new VertexFormat(
            ImmutableMap.<String, VertexFormatElement>builder()
                    .put("Position", POSITION_ELEMENT)
                    .put("Normal", NORMAL_ELEMENT)
                    .build()
    );

    public GrassRenderer() {
        this.vertexBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();

        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);


        this.crateGrass(bufferBuilder);

        this.vertexBuffer.bind();
        this.vertexBuffer.upload(bufferBuilder.end());
        VertexBuffer.unbind();
    }

    public void render() {
//        AdvancedFbo fbo = VeilRenderSystem.renderer().getFramebufferManager().getFramebuffer(VeilFramebuffers.OPAQUE);
//        if(fbo == null) return;
//        fbo.bind(true);

        if(this.vertexBuffer != null){
            this.vertexBuffer.close();
        }

        this.vertexBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();

        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);


        this.crateGrass(bufferBuilder);

        this.vertexBuffer.bind();
        this.vertexBuffer.upload(bufferBuilder.end());
        VertexBuffer.unbind();


        ShaderProgram shader = VeilRenderSystem.setShader(shaderPath);
        if(shader == null) return;

        shader.setFloat("GameTime", RenderSystem.getShaderGameTime());
        shader.setInt("NumOfInstances", floor(sqrt(ConfigStuff.grassCount)));
        shader.setFloat("grassHeight", ConfigStuff.grassHeight);
        shader.setFloat("density", ConfigStuff.grassDensity);


        RenderSystem.setShaderTexture(0, windTexture);
        shader.addSampler("WindNoise", RenderSystem.getShaderTexture(0));
        shader.applyShaderSamplers(0);
        this.vertexBuffer.bind();
        shader.bind();

        VeilRenderSystem.drawInstanced(this.vertexBuffer, ConfigStuff.grassCount);


        ShaderProgram.unbind();
        VertexBuffer.unbind();
//        AdvancedFbo.unbind();
    }

    private void crateGrass(BufferBuilder bufferBuilder) {

        bufferBuilder.vertex(0.5,ConfigStuff.grassHeight,0).normal(0,0,-1).next();
        bufferBuilder.vertex(0.5,ConfigStuff.grassHeight,0).normal(0,0,-1).next();
        bufferBuilder.vertex(0.6,0,0).normal(0,0,-1).next();
        bufferBuilder.vertex(0.4,0,0).normal(0,0,-1).next();

        bufferBuilder.vertex(0.4,0,0).normal(0,0,1).next();
        bufferBuilder.vertex(0.6,0,0).normal(0,0,1).next();
        bufferBuilder.vertex(0.5,ConfigStuff.grassHeight,0).normal(0,0,1).next();
        bufferBuilder.vertex(0.5,ConfigStuff.grassHeight,0).normal(0,0,1).next();
    }

}