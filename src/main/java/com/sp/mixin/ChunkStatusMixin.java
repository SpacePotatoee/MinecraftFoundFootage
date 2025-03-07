package com.sp.mixin;

import com.sp.world.generation.Level0ChunkGenerator;
import com.sp.world.generation.Level1ChunkGenerator;
import com.sp.world.generation.PoolroomsChunkGenerator;
import net.minecraft.server.world.ServerLightingProvider;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

@Mixin(ChunkStatus.class)
public abstract class ChunkStatusMixin {

    @Inject(method = "method_38284(Lnet/minecraft/world/chunk/ChunkStatus;Ljava/util/concurrent/Executor;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/world/gen/chunk/ChunkGenerator;Lnet/minecraft/structure/StructureTemplateManager;Lnet/minecraft/server/world/ServerLightingProvider;Ljava/util/function/Function;Ljava/util/List;Lnet/minecraft/world/chunk/Chunk;)Ljava/util/concurrent/CompletableFuture;", at = @At("HEAD"), cancellable = true)
    private static void runGenerationTask(ChunkStatus targetStatus, Executor executor, ServerWorld world, ChunkGenerator generator, StructureTemplateManager structureTemplateManager, ServerLightingProvider lightingProvider, Function fullChunkConverter, List chunks, Chunk chunk, CallbackInfoReturnable<CompletableFuture> cir) {

        if (generator instanceof Level1ChunkGenerator l1cg) {
            ChunkRegion chunkRegion = new ChunkRegion(world, chunks, targetStatus, 10);
            l1cg.generateMaze(chunkRegion, chunk);
        }

        if (generator instanceof Level0ChunkGenerator l0cg) {
            ChunkRegion chunkRegion = new ChunkRegion(world, chunks, targetStatus, 5);
            l0cg.generateMaze(chunkRegion, chunk);
        }

        if (generator instanceof PoolroomsChunkGenerator prcg) {
            ChunkRegion chunkRegion = new ChunkRegion(world, chunks, targetStatus, 10);
            prcg.generateMaze(chunkRegion, chunk);
        }

    }
}
