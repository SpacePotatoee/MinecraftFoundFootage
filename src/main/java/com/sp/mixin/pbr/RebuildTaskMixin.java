package com.sp.mixin.pbr;

import com.llamalad7.mixinextras.sugar.Local;
import com.sp.render.pbr.BlockIdMap;
import com.sp.mixininterfaces.BlockMaterial;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.chunk.BlockBufferBuilderStorage;
import net.minecraft.client.render.chunk.ChunkBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ChunkBuilder.BuiltChunk.RebuildTask.class)
public class RebuildTaskMixin {

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/block/BlockRenderManager;renderBlock(Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/BlockRenderView;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;ZLnet/minecraft/util/math/random/Random;)V"))
    private void setCurrentBlock(float cameraX, float cameraY, float cameraZ, BlockBufferBuilderStorage storage, CallbackInfoReturnable<ChunkBuilder.BuiltChunk.RebuildTask.RenderData> cir, @Local BufferBuilder bufferBuilder, @Local(ordinal = 0) BlockState blockState){
        if(bufferBuilder instanceof BlockMaterial){
            ((BlockMaterial) bufferBuilder).setCurrentBlock(BlockIdMap.getBlockID(blockState.getBlock()));
        }
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/block/BlockRenderManager;renderBlock(Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/BlockRenderView;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;ZLnet/minecraft/util/math/random/Random;)V", shift = At.Shift.AFTER))
    private void setCurrentBlock2(float cameraX, float cameraY, float cameraZ, BlockBufferBuilderStorage storage, CallbackInfoReturnable<ChunkBuilder.BuiltChunk.RebuildTask.RenderData> cir, @Local BufferBuilder bufferBuilder, @Local(ordinal = 0) BlockState blockState){
        if(bufferBuilder instanceof BlockMaterial){
            ((BlockMaterial) bufferBuilder).setCurrentBlock(-1);
        }
    }

}
