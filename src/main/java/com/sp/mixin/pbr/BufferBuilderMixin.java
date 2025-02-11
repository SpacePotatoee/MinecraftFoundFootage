package com.sp.mixin.pbr;

import com.sp.util.mixinstuff.BlockMaterial;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BufferBuilder.class)
public abstract class BufferBuilderMixin implements BlockMaterial {
    @Shadow public abstract void putShort(int index, short value);
    @Shadow public abstract void nextElement();

    @Unique boolean isRenderingBlock;
    @Unique int currentBlock;

    @Override
    public void setCurrentBlock(int block) {
        this.currentBlock = block;
    }

    @Inject(method = "begin", at = @At("HEAD"))
    private void isRenderingBlocks(VertexFormat.DrawMode drawMode, VertexFormat format, CallbackInfo ci){
        isRenderingBlock = format == VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL;
    }

    @Inject(method = "next", at = @At("HEAD"))
    private void putBlockID(CallbackInfo ci){
        if(isRenderingBlock){
            this.putShort(0, (short) this.currentBlock);
            this.nextElement();
        }
    }

}
