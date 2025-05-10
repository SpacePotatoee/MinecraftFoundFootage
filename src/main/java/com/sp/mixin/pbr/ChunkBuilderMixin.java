package com.sp.mixin.pbr;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.sp.render.RenderLayers;
import com.sp.render.VertexFormats;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.chunk.ChunkBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ChunkBuilder.BuiltChunk.RebuildTask.class)
public class ChunkBuilderMixin {

    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/chunk/ChunkBuilder$BuiltChunk;beginBufferBuilding(Lnet/minecraft/client/render/BufferBuilder;)V"))
    private void beginPBR(ChunkBuilder.BuiltChunk instance, BufferBuilder buffer, Operation<Void> original, @Local RenderLayer renderLayer){
        if(renderLayer == RenderLayers.getPbrLayer()){
            buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.PBR);
        } else {
            original.call(instance, buffer);
        }
    }

}
