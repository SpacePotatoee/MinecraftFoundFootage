package com.sp.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.sp.SPBRevamped;
import com.sp.compat.modmenu.ConfigStuff;
import com.sp.mixininterfaces.RenderIndirectExtension;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.VeilRenderer;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.*;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import org.joml.Vector4fc;
import org.lwjgl.opengl.GL43;

import java.nio.ByteBuffer;

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
    private final int indirectVbo;

    private int lastGrassCount;
    private int lastMeshResolution;
    private ByteBuffer cmd;

    public GrassRenderer() {
        this.vertexBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();

        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);


        this.createGrassModel(bufferBuilder);

        BufferBuilder.BuiltBuffer builtBuffer = bufferBuilder.end();

        this.vertexBuffer.bind();
        this.vertexBuffer.upload(builtBuffer);
        VertexBuffer.unbind();


        //Initialize Grass Positions buffer and Indirect buffer struct
        this.positionsVbo = glGenBuffers();
        this.indirectVbo = glGenBuffers();
        this.updateBuffers(true);
    }

    public void render() {

//        AdvancedFbo fbo = VeilRenderSystem.renderer().getFramebufferManager().getFramebuffer(VeilFramebuffers.OPAQUE);
//        if(fbo == null) return;
//        fbo.bind(true);

        //If there is a change in the grass count or resolution, update the buffers
        if(ConfigStuff.grassCount != this.lastGrassCount || ConfigStuff.meshResolution != this.lastMeshResolution) {
            if (this.vertexBuffer != null) {
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


        }

        //Update the Buffers
        this.updateBuffers(false);

        //Use a compute shader to get all visible grass positions (Frustum Culling)
        this.computeGrassPositions();



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
        //glDrawElementsIndirect needs the indirect fbo
        //REMEMBER the int struct goes HERE and not directly into the method like I thought before
        glBindBuffer(GL43.GL_DRAW_INDIRECT_BUFFER, this.indirectVbo);
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 0, this.positionsVbo);
        shader.bind();

        ((RenderIndirectExtension)this.vertexBuffer).spb_revamped_1_20_1$drawIndirect();

        ShaderProgram.unbind();
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 0, 0);
        glBindBuffer(GL43.GL_DRAW_INDIRECT_BUFFER, 0);
        VertexBuffer.unbind();

//        AdvancedFbo.unbind();
    }

    private void updateBuffers(boolean init){
        int currentGrassCount = ConfigStuff.grassCount;
        int currentMeshResolution = ConfigStuff.meshResolution;
        boolean countChange = currentGrassCount != this.lastGrassCount;
        boolean resolutionChange = currentMeshResolution != this.lastMeshResolution;

        if(countChange) {
            //Update positions buffer size
            glBindBuffer(GL_SHADER_STORAGE_BUFFER, this.positionsVbo);
            glBufferData(GL_SHADER_STORAGE_BUFFER, (long) 4 * ((long) currentGrassCount) * Float.BYTES, GL_DYNAMIC_DRAW);
            glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0);
        }

//        if(countChange || resolutionChange){
            //Update Indirect buffer instance count
            glBindBuffer(GL_DRAW_INDIRECT_BUFFER, this.indirectVbo);
            if(init) glBufferData(GL_DRAW_INDIRECT_BUFFER, (long) 20, GL_STATIC_DRAW);

            boolean unmap = false;
            if(this.cmd == null) {
                unmap = true;
                this.cmd = glMapBufferRange(
                        GL_DRAW_INDIRECT_BUFFER, 0, 20,
                        GL_MAP_WRITE_BIT | GL_MAP_INVALIDATE_BUFFER_BIT | GL_MAP_UNSYNCHRONIZED_BIT
                );
            }


            //Actual struct part
            /*
                uint count;
                uint primCount;
                uint firstIndex;
                uint baseVertex;
                uint baseInstance;
             */
            if(cmd != null) {
                this.cmd.clear();
                this.cmd.putInt(VeilRenderSystem.getIndexCount(this.vertexBuffer));
                this.cmd.putInt(0);
                this.cmd.putInt(0);
                this.cmd.putInt(0);
                this.cmd.putInt(0);

                this.cmd.flip();

                if(!init) glBufferSubData(GL_DRAW_INDIRECT_BUFFER, 0, cmd);
            }
            if(unmap) glUnmapBuffer(GL_DRAW_INDIRECT_BUFFER);
            glBindBuffer(GL_DRAW_INDIRECT_BUFFER, 0);
//        }

        if(countChange)this.lastGrassCount = currentGrassCount;
        if(resolutionChange)this.lastMeshResolution = currentMeshResolution;

    }

    private void computeGrassPositions() {
        ShaderProgram shader = VeilRenderSystem.setShader(computeShaderPath);
        if(shader == null) return;

        if(shader.isCompute()){
            glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 0, this.positionsVbo);
            glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 1, this.indirectVbo);

            shader.setInt("NumOfInstances", floor(sqrt(ConfigStuff.grassCount)));
            shader.setFloat("density", ConfigStuff.grassDensity);

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

            //Eight work groups
            int grass = floor(sqrt((float) ConfigStuff.grassCount) / 8);
            int x = Math.min(grass, VeilRenderSystem.maxComputeWorkGroupCountX());
            int y = Math.min(grass, VeilRenderSystem.maxComputeWorkGroupCountY());

            glDispatchCompute(x, y, 1);
            glMemoryBarrier(GL_ALL_BARRIER_BITS);


            ShaderProgram.unbind();
            glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 0, 0);
            glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 1, 0);
        }

        ShaderProgram.unbind();


    }

    private void createGrassModel(BufferBuilder bufferBuilder) {
        //Segmented Grass blades (1 is just a single triangle)
        int segments = ConfigStuff.meshResolution;
        float xStep = 0.1f/segments;

        for(int i = 0; i < segments; i++){
            bufferBuilder.vertex(0.6-xStep*(i),ConfigStuff.grassHeight/segments*i,0)      .next();
            bufferBuilder.vertex(0.4+xStep*(i),ConfigStuff.grassHeight/segments*i,0)      .next();
            bufferBuilder.vertex(0.6-xStep*(i+1),ConfigStuff.grassHeight/segments*(i+1),0).next();
            bufferBuilder.vertex(0.4+xStep*(i+1),ConfigStuff.grassHeight/segments*(i+1),0).next();


            bufferBuilder.vertex(0.4+xStep*(i),ConfigStuff.grassHeight/segments*i,0)      .next();
            bufferBuilder.vertex(0.6-xStep*(i),ConfigStuff.grassHeight/segments*i,0)      .next();
            bufferBuilder.vertex(0.4+xStep*(i+1),ConfigStuff.grassHeight/segments*(i+1),0).next();
            bufferBuilder.vertex(0.6-xStep*(i+1),ConfigStuff.grassHeight/segments*(i+1),0).next();
        }
    }

    public void close(){
        glDeleteBuffers(this.positionsVbo);
        glDeleteBuffers(this.indirectVbo);
        this.cmd = null;
    }

}