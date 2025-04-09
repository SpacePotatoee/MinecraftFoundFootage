package com.sp.mixin.pbr;

import com.sp.mixininterfaces.BlockMaterial;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.ByteBuffer;

@Mixin(BufferBuilder.class)
public abstract class BufferBuilderMixin implements BlockMaterial {
    @Shadow public abstract void nextElement();

    @Shadow private ByteBuffer buffer;
    @Shadow private int elementOffset;
    @Unique boolean isRenderingBlock;
    @Unique int currentBlock;
    @Unique VertexFormat currentFormat;

    @Override
    public void setCurrentBlock(int block) {
        this.currentBlock = block;
    }

    @ModifyVariable(method = "begin", at = @At("HEAD"), argsOnly = true)
    private VertexFormat setFormat(VertexFormat format){
        this.isRenderingBlock = false;
        currentFormat = format;


        if(format == VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL) {
            this.isRenderingBlock = true;
            this.currentFormat = com.sp.render.VertexFormats.BLOCKS;
            return com.sp.render.VertexFormats.BLOCKS;
        }

        return format;
    }


    @Inject(method = "next", at = @At("HEAD"))
    private void putBlockID(CallbackInfo ci){
        if(this.isRenderingBlock && currentFormat == com.sp.render.VertexFormats.BLOCKS) {
            this.buffer.putInt(this.elementOffset, this.currentBlock);
            this.nextElement();
        }
    }

}
