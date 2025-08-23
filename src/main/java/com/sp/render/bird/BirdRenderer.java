package com.sp.render.bird;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.systems.RenderSystem;
import com.sp.SPBRevamped;
import com.sp.compat.modmenu.ConfigStuff;
import com.sp.mixininterfaces.RenderIndirectExtension;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.VeilRenderer;
import foundry.veil.api.client.render.framebuffer.AdvancedFbo;
import foundry.veil.api.client.render.framebuffer.VeilFramebuffers;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormatElement;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import org.joml.Vector4fc;
import org.lwjgl.opengl.GL43;

import java.nio.ByteBuffer;

import static net.minecraft.client.render.VertexFormats.NORMAL_ELEMENT;
import static net.minecraft.client.render.VertexFormats.POSITION_ELEMENT;
import static net.minecraft.util.math.MathHelper.floor;
import static org.lwjgl.opengl.GL15C.glBindBuffer;
import static org.lwjgl.opengl.GL15C.glGenBuffers;
import static org.lwjgl.opengl.GL42C.*;
import static org.lwjgl.opengl.GL43C.GL_SHADER_STORAGE_BUFFER;
import static org.lwjgl.opengl.GL43C.glDispatchCompute;

public class BirdRenderer {
    private static final Identifier shaderPath = new Identifier(SPBRevamped.MOD_ID, "bird/bird");
    public static final Identifier computeShaderPath = new Identifier(SPBRevamped.MOD_ID, "bird/compute/positions");

    private final int positionsVbo;
    private final int indirectVbo;

    private int lastBirdCount;
    private ByteBuffer cmd;

    VertexBuffer vertexBuffer;

    public static final VertexFormat POSITION_NORMAL = new VertexFormat(
            ImmutableMap.<String, VertexFormatElement>builder()
                    .put("Position", POSITION_ELEMENT)
                    .put("Color", NORMAL_ELEMENT)
                    .build()
    );
    private int lastFlockCount;

    public BirdRenderer() {
        this.lastBirdCount = ConfigStuff.birdQuality.getBirdCount();
        this.lastFlockCount = ConfigStuff.birdQuality.getFlockCount();

        this.vertexBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();

        bufferBuilder.begin(VertexFormat.DrawMode.TRIANGLES, POSITION_NORMAL);


        this.createGrassModel(bufferBuilder);

        BufferBuilder.BuiltBuffer builtBuffer = bufferBuilder.end();

        this.vertexBuffer.bind();
        this.vertexBuffer.upload(builtBuffer);
        VertexBuffer.unbind();


        //*Initialize Grass Positions buffer and Indirect buffer struct
        this.positionsVbo = glGenBuffers();
        this.indirectVbo = glGenBuffers();
        this.updateBuffers(true);
    }

    public void render() {
//        RenderSystem.disableDepthTest();
        AdvancedFbo fbo = VeilRenderSystem.renderer().getFramebufferManager().getFramebuffer(VeilFramebuffers.OPAQUE);
        if (fbo == null) return;
        fbo.bind(false);

        //*If there is a change in the grass count or resolution, update the buffers
        if (ConfigStuff.birdQuality.getBirdCount() != this.lastBirdCount) {
            if (this.vertexBuffer != null) {
                this.vertexBuffer.close();
            }

            this.vertexBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferBuilder = tessellator.getBuffer();

            bufferBuilder.begin(VertexFormat.DrawMode.TRIANGLES, POSITION_NORMAL);

            this.createGrassModel(bufferBuilder);

            this.vertexBuffer.bind();
            this.vertexBuffer.upload(bufferBuilder.end());
            VertexBuffer.unbind();


        }

        //*Update the Buffers
        this.updateBuffers(false);

        //*Use a compute shader to get all visible grass positions (Frustum Culling)
        this.computeBirdPositions();


        ShaderProgram shader = VeilRenderSystem.setShader(shaderPath);
        if (shader == null) return;

        shader.setFloat("GameTime", RenderSystem.getShaderGameTime());
        shader.setInt("NumOfInstances", ConfigStuff.birdQuality.getBirdCount());

//        int prevTexture = RenderSystem.getShaderTexture(0);
        shader.applyShaderSamplers(0);

        this.vertexBuffer.bind();
        //*glDrawElementsIndirect needs the indirect fbo
        //*REMEMBER the int struct goes HERE and not directly into the method like I thought before
        glBindBuffer(GL43.GL_DRAW_INDIRECT_BUFFER, this.indirectVbo);
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 0, this.positionsVbo);
        shader.bind();

        ((RenderIndirectExtension) this.vertexBuffer).spb_revamped_1_20_1$drawIndirect();

        ShaderProgram.unbind();
        shader.clearSamplers();

        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 0, 0);
        glBindBuffer(GL43.GL_DRAW_INDIRECT_BUFFER, 0);
        VertexBuffer.unbind();
//        RenderSystem.setShaderTexture(0, prevTexture);

        AdvancedFbo.unbind();
//        RenderSystem.enableDepthTest();
    }

    private void updateBuffers(boolean init) {
        int currentBirdCount = ConfigStuff.birdQuality.getBirdCount();
        int currentFlockCount = ConfigStuff.birdQuality.getFlockCount();
        boolean configChange = currentBirdCount != this.lastBirdCount || currentFlockCount != this.lastFlockCount;

        if (configChange || init) {
            //*Update positions buffer size
            glBindBuffer(GL_SHADER_STORAGE_BUFFER, this.positionsVbo);
            glBufferData(GL_SHADER_STORAGE_BUFFER, (long) 6 * ((long) currentBirdCount) * Float.BYTES, GL_DYNAMIC_DRAW);

            ByteBuffer initialData = glMapBufferRange(
                    GL_SHADER_STORAGE_BUFFER, 0, (long) 6 * ((long) currentBirdCount) * Float.BYTES,
                    GL_MAP_WRITE_BIT | GL_MAP_INVALIDATE_BUFFER_BIT
            );

            if (initialData != null) {
                Random random = Random.create();

                for (int i = 0; i < currentBirdCount; i++) {
                    initialData.putFloat((float) (FlockManager.getFlockCenter((i) % ConfigStuff.birdQuality.getFlockCount()).x + (random.nextFloat() * 10)));
                    initialData.putFloat((float) (FlockManager.getFlockCenter((i) % ConfigStuff.birdQuality.getFlockCount()).y + (random.nextFloat() * 10)));
                    initialData.putFloat((float) (FlockManager.getFlockCenter((i) % ConfigStuff.birdQuality.getFlockCount()).z + (random.nextFloat() * 10)));

                    initialData.putFloat(0);
                    initialData.putFloat(1);
                    initialData.putFloat(0);
                }

                initialData.flip();
                glUnmapBuffer(GL_SHADER_STORAGE_BUFFER);
            }

            glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0);
        }

        //*Update Indirect buffer instance count
        glBindBuffer(GL_DRAW_INDIRECT_BUFFER, this.indirectVbo);
        glBufferData(GL_DRAW_INDIRECT_BUFFER, 20, GL_STATIC_DRAW);


        this.cmd = glMapBufferRange(
                GL_DRAW_INDIRECT_BUFFER, 0, 20,
                GL_MAP_WRITE_BIT | GL_MAP_INVALIDATE_BUFFER_BIT | GL_MAP_UNSYNCHRONIZED_BIT
        );


        //*Actual struct part
        /*
         *uint count;
         *uint primCount;
         *uint firstIndex;
         *uint baseVertex;
         *uint baseInstance;
         */
        if (cmd != null) {
            this.cmd.clear();
            this.cmd.putInt(VeilRenderSystem.getIndexCount(this.vertexBuffer));
            this.cmd.putInt(0);
            this.cmd.putInt(0);
            this.cmd.putInt(0);
            this.cmd.putInt(0);

            this.cmd.flip();

        }
        glUnmapBuffer(GL_DRAW_INDIRECT_BUFFER);
        glBindBuffer(GL_DRAW_INDIRECT_BUFFER, 0);

        if (configChange) {
            this.lastBirdCount = currentBirdCount;
            this.lastFlockCount = currentFlockCount;
        }
    }

    private void computeBirdPositions() {
        ShaderProgram shader = VeilRenderSystem.setShader(computeShaderPath);
        ShaderProgram fragShader = VeilRenderSystem.setShader(shaderPath);
        if (shader == null) return;
        if (fragShader == null) return;
        int numOfInst = ConfigStuff.birdQuality.getBirdCount();

        if (shader.isCompute()) {
            glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 0, this.positionsVbo);
            glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 1, this.indirectVbo);

            shader.setInt("NumOfInstances", numOfInst);

            Vector4fc[] planes = VeilRenderer.getCullingFrustum().getPlanes();
            float[] values = new float[4 * planes.length];
            for (int i = 0; i < planes.length; i++) {
                Vector4fc plane = planes[i];
                values[i * 4] = plane.x();
                values[i * 4 + 1] = plane.y();
                values[i * 4 + 2] = plane.z();
                values[i * 4 + 3] = plane.w();
            }
            shader.setFloats("FrustumPlanes", values);

            shader.bind();

            //*Eight local groups
            int grass = floor(Math.sqrt((float) ConfigStuff.birdQuality.getBirdCount()) / 8);
            int x = Math.min(grass, VeilRenderSystem.maxComputeWorkGroupCountX());
            int y = Math.min(grass, VeilRenderSystem.maxComputeWorkGroupCountY());

            glDispatchCompute(x, y, 1);
            glMemoryBarrier(GL_ALL_BARRIER_BITS);


            ShaderProgram.unbind();
            glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 0, 0);
            glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 1, 0);
        }

        fragShader.setInt("NumOfInstances", numOfInst);

        ShaderProgram.unbind();
    }

    private void createGrassModel(BufferBuilder bufferBuilder) {
        // Front face
        bufferBuilder.vertex(0.000000f, 0.000000f, -1.000000f).normal(0.8402f, 0.2425f, -0.4851f).next(); // v1
        bufferBuilder.vertex(0.000000f, 2.000000f, 0.000000f).normal(0.8402f, 0.2425f, -0.4851f).next(); // v4
        bufferBuilder.vertex(0.866025f, 0.000000f, 0.500000f).normal(0.8402f, 0.2425f, -0.4851f).next(); // v2

        // Bottom face
        bufferBuilder.vertex(0.000000f, 0.000000f, -1.000000f).normal(0.0000f, -1.0000f, 0.0000f).next(); // v1
        bufferBuilder.vertex(0.866025f, 0.000000f, 0.500000f).normal(0.0000f, -1.0000f, 0.0000f).next(); // v2
        bufferBuilder.vertex(-0.866025f, 0.000000f, 0.500000f).normal(0.0000f, -1.0000f, 0.0000f).next(); // v3

        // Right face
        bufferBuilder.vertex(0.866025f, 0.000000f, 0.500000f).normal(0.0000f, 0.2425f, 0.9701f).next(); // v2
        bufferBuilder.vertex(0.000000f, 2.000000f, 0.000000f).normal(0.0000f, 0.2425f, 0.9701f).next(); // v4
        bufferBuilder.vertex(-0.866025f, 0.000000f, 0.500000f).normal(0.0000f, 0.2425f, 0.9701f).next(); // v3

        // Left face
        bufferBuilder.vertex(-0.866025f, 0.000000f, 0.500000f).normal(-0.8402f, 0.2425f, -0.4851f).next(); // v3
        bufferBuilder.vertex(0.000000f, 2.000000f, 0.000000f).normal(-0.8402f, 0.2425f, -0.4851f).next(); // v4
        bufferBuilder.vertex(0.000000f, 0.000000f, -1.000000f).normal(-0.8402f, 0.2425f, -0.4851f).next(); // v1
    }

    public void close() {
        glDeleteBuffers(this.positionsVbo);
        glDeleteBuffers(this.indirectVbo);
        glUnmapBuffer(GL_DRAW_INDIRECT_BUFFER);
        this.cmd.clear();
        this.cmd = null;
    }
}