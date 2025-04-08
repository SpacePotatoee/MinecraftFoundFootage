package com.sp.render;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.systems.RenderSystem;
import com.sp.SPBRevamped;
import com.sp.compat.modmenu.ConfigStuff;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.shader.VeilShaders;
import foundry.veil.api.client.render.shader.definition.DynamicShaderBlock;
import foundry.veil.api.client.render.shader.definition.ShaderBlock;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.*;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import org.joml.Vector4f;

import java.nio.IntBuffer;

import static foundry.veil.api.client.render.framebuffer.FramebufferAttachmentDefinition.DataType.UNSIGNED_SHORT;
import static net.minecraft.client.render.VertexFormats.NORMAL_ELEMENT;
import static net.minecraft.client.render.VertexFormats.POSITION_ELEMENT;
import static net.minecraft.util.math.MathHelper.floor;
import static net.minecraft.util.math.MathHelper.sqrt;
import static org.lwjgl.opengl.GL15C.glBindBuffer;
import static org.lwjgl.opengl.GL15C.glGenBuffers;
import static org.lwjgl.opengl.GL42C.*;
import static org.lwjgl.opengl.GL43C.GL_SHADER_STORAGE_BUFFER;
import static org.lwjgl.opengl.GL43C.glDispatchCompute;

public class GrassRenderer {
    VertexBuffer vertexBuffer;
    private static final Identifier shaderPath = new Identifier(SPBRevamped.MOD_ID, "grass/grass");
    private static final Identifier windTexture = new Identifier(SPBRevamped.MOD_ID, "textures/shaders/puddle_noise.png");

    private static final Identifier computeShaderPath = new Identifier(SPBRevamped.MOD_ID, "grass/compute/positions");
    private final int positionsVbo;
    private final IntBuffer instanceBuffer;
    private final DynamicShaderBlock<?> grassPositions;

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

        this.positionsVbo = glGenBuffers();
        this.grassPositions = ShaderBlock.wrapper(GL_SHADER_STORAGE_BUFFER, this.positionsVbo);

//        this.instanceBuffer = IntBuffer.allocate(5);


        this.createGrassModel(bufferBuilder);

        this.vertexBuffer.bind();
        this.vertexBuffer.upload(bufferBuilder.end());

        int[] args = new int[5];
        args[0] = VeilRenderSystem.getIndexCount(this.vertexBuffer);
        args[1] = 0;
        args[2] = 0;
        args[3] = 0;
        args[4] = 0;
        this.instanceBuffer = IntBuffer.wrap(args);

        VertexBuffer.unbind();
    }

    private void getGrassPositions() {
//        ShaderProgram shader = VeilRenderSystem.setShader(computeShaderPath);
        ShaderProgram prevProgram = VeilRenderSystem.getShader();
        VeilRenderSystem.setShader(VeilShaders.LIGHT_INDIRECT_SPHERE);
        ShaderProgram shader = VeilRenderSystem.getShader();

        if(shader == null) return;

        if(shader.isCompute()){
            this.grassPositions.setSize((long) ConfigStuff.grassCount * Float.SIZE * 3);
//            VeilRenderSystem.bind("GrassPositions", this.grassPositions);

            glBindBuffer(GL_ATOMIC_COUNTER_BUFFER, this.positionsVbo);
            glBufferSubData(GL_ATOMIC_COUNTER_BUFFER, 0L, this.instanceBuffer);


            shader.bind();
            glDispatchCompute(Math.min(ConfigStuff.grassCount, VeilRenderSystem.maxComputeWorkGroupCountX()), 1, 1);
            glMemoryBarrier(GL_BUFFER_UPDATE_BARRIER_BIT);

            ShaderProgram.unbind();
            glBindBuffer(GL_ATOMIC_COUNTER_BUFFER, 0);
//            VeilRenderSystem.unbind(this.grassPositions);
        }

        VeilRenderSystem.setShader(prevProgram);


    }

    public void render() {
//        this.getGrassPositions();
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

        this.createGrassModel(bufferBuilder);

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

        glBindBuffer(GL_SHADER_STORAGE_BUFFER, this.positionsVbo);

        this.vertexBuffer.bind();
        shader.bind();


        glDrawElementsIndirect(GL_QUADS, GL_UNSIGNED_SHORT, this.instanceBuffer);
//        VeilRenderSystem.drawInstanced(this.vertexBuffer, ConfigStuff.grassCount);

        ShaderProgram.unbind();
        VertexBuffer.unbind();
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0);
//        AdvancedFbo.unbind();
    }

    private void createGrassModel(BufferBuilder bufferBuilder) {

//        bufferBuilder.vertex(0.5,ConfigStuff.grassHeight,0).normal(0,0,-1).next();
//        bufferBuilder.vertex(0.5,ConfigStuff.grassHeight,0).normal(0,0,-1).next();
//        bufferBuilder.vertex(0.6,0,0).normal(0,0,-1).next();
//        bufferBuilder.vertex(0.4,0,0).normal(0,0,-1).next();
//
//        bufferBuilder.vertex(0.4,0,0).normal(0,0,1).next();
//        bufferBuilder.vertex(0.6,0,0).normal(0,0,1).next();
//        bufferBuilder.vertex(0.5,ConfigStuff.grassHeight,0).normal(0,0,1).next();
//        bufferBuilder.vertex(0.5,ConfigStuff.grassHeight,0).normal(0,0,1).next();

        //Segmented Grass blades (Use 1 for just a single triangle)
        int segments = 1;
        float thickness = 0.1f;
        float xStep = thickness/segments;

        for(int i = 0; i < segments; i++){
            bufferBuilder.vertex(0.6-xStep*(i+1),ConfigStuff.grassHeight/segments*(i+1),0).next();
            bufferBuilder.vertex(0.4+xStep*(i+1),ConfigStuff.grassHeight/segments*(i+1),0).next();
            bufferBuilder.vertex(0.4+xStep*(i),ConfigStuff.grassHeight/segments*i,0)      .next();
            bufferBuilder.vertex(0.6-xStep*(i),ConfigStuff.grassHeight/segments*i,0)      .next();

            bufferBuilder.vertex(0.6-xStep*(i),ConfigStuff.grassHeight/segments*i,0)      .next();
            bufferBuilder.vertex(0.4+xStep*(i),ConfigStuff.grassHeight/segments*i,0)      .next();
            bufferBuilder.vertex(0.4+xStep*(i+1),ConfigStuff.grassHeight/segments*(i+1),0).next();
            bufferBuilder.vertex(0.6-xStep*(i+1),ConfigStuff.grassHeight/segments*(i+1),0).next();
        }
    }

}