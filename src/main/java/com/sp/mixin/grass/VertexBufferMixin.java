package com.sp.mixin.grass;

import com.mojang.blaze3d.systems.RenderSystem;
import com.sp.mixininterfaces.RenderIndirectExtension;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.VertexFormat;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import static org.lwjgl.opengl.GL15C.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15C.glBindBuffer;
import static org.lwjgl.opengl.GL40C.GL_PATCHES;
import static org.lwjgl.opengl.GL40C.glDrawElementsIndirect;

/***
 * Thanks Veil lol
 */
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
        if (this.sharedSequentialIndexBuffer != null) {
            this.sharedSequentialIndexBuffer.bindAndGrow(this.indexCount);
            glDrawElementsIndirect(this.getDrawMode(this.drawMode.glMode), this.sharedSequentialIndexBuffer.getIndexType().glType, 0);
        } else {
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.indexBufferId);
            glDrawElementsIndirect(this.getDrawMode(this.drawMode.glMode), this.indexType.glType, 0);
        }

    }
}
