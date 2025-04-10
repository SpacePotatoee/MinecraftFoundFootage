package com.sp.mixin.grass;

import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.sp.mixininterfaces.RenderIndirectExtension;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.VertexFormat;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL43;
import org.lwjgl.opengl.GLDebugMessageCallback;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL15C.*;
import static org.lwjgl.opengl.GL40C.GL_PATCHES;
import static org.lwjgl.opengl.GL40C.glDrawElementsIndirect;

@Mixin(VertexBuffer.class)
public class VertexBufferMixin implements RenderIndirectExtension {

    @Shadow private int indexBufferId;
    @Shadow private VertexFormat.DrawMode drawMode;
    @Shadow private VertexFormat.IndexType indexType;
    @Shadow @Nullable private RenderSystem.@Nullable ShapeIndexBuffer sharedSequentialIndexBuffer;
    @Shadow private int indexCount;

    @Unique
    private int getDrawMode(int defaultMode) {
        ShaderProgram shader = VeilRenderSystem.getShader();
        if (shader != null && shader.hasTesselation()) {
            return GL_PATCHES;
        }
        return defaultMode;
    }

    @Override
    public void spb_revamped_1_20_1$drawIndirect() {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> this.drawIndirect());
        } else {
            this.drawIndirect();
        }
    }

    @Unique
    public void drawIndirect() {
//        if (this.sharedSequentialIndexBuffer != null) {
//            this.sharedSequentialIndexBuffer.bindAndGrow(this.indexCount);
//            glDrawElementsIndirect(this.getDrawMode(this.drawMode.glMode), this.sharedSequentialIndexBuffer.getIndexType().glType, 0);
//        } else {
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.indexBufferId);
            glDrawElementsIndirect(this.getDrawMode(this.drawMode.glMode), this.indexType.glType, 0);
//            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
//        }


        GL43.glDebugMessageCallback((source, type, id, severity, length, message, userParam) -> {
            System.err.println("GL DEBUG: " + GLDebugMessageCallback.getMessage(length, message));
        }, 0);

    }
}
