package com.sp.world.generation.chunk_generator;

import com.sp.compat.modmenu.ConfigStuff;
import com.sp.mixininterfaces.NewServerProperties;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.Blender;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.VerticalBlockSample;
import net.minecraft.world.gen.noise.NoiseConfig;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public abstract class BackroomsChunkGenerator extends ChunkGenerator {
    private final int placementRadius;

    public BackroomsChunkGenerator(BiomeSource biomeSource) {
        this(biomeSource, 1);
    }

    public BackroomsChunkGenerator(BiomeSource biomeSource, int placementRadius) {
        super(biomeSource);
        this.placementRadius = placementRadius;
    }

    public abstract void generate(StructureWorldAccess world, Chunk chunk);

    protected int getExitSpawnRadius(StructureWorldAccess world){
        if(world.getServer().isDedicated()) {
            return ((NewServerProperties) ((MinecraftDedicatedServer)world.getServer()).getProperties()).getExitSpawnRadius();
        } else {
            return ConfigStuff.exitSpawnRadius;
        }
    }

    /**
     * When doing chunk generation, you normally can only change the blockstates within the current chunk (placement radius of 1).
     * But you can increase that radius which is necessary since the maze generators need to place rooms a few chunks away.
     */
    public int getPlacementRadius() {
        return placementRadius;
    }


    /**
     * We Don't need to use any of these methods
     */
    @Override
    public CompletableFuture<Chunk> populateNoise(Executor executor, Blender blender, NoiseConfig noiseConfig, StructureAccessor structureAccessor, Chunk chunk) {
        return CompletableFuture.completedFuture(chunk);
    }

    @Override
    public int getSeaLevel() {
        return 0;
    }

    @Override
    public int getMinimumY() {
        return 0;
    }

    @Override
    public int getHeight(int x, int z, Heightmap.Type heightmap, HeightLimitView world, NoiseConfig noiseConfig) {
        return this.getWorldHeight();
    }

    @Override
    public VerticalBlockSample getColumnSample(int x, int z, HeightLimitView world, NoiseConfig noiseConfig) {
        BlockState[] states = new BlockState[world.getHeight()];

        for (int i = 0; i < states.length; i++) {
            states[i] = Blocks.AIR.getDefaultState();
        }

        return new VerticalBlockSample(0, states);
    }

    @Override
    public void getDebugHudText(List<String> text, NoiseConfig noiseConfig, BlockPos pos) {
    }

    @Override
    public int getWorldHeight() {
        return 384;
    }

    @Override
    public void carve(ChunkRegion chunkRegion, long seed, NoiseConfig noiseConfig, BiomeAccess biomeAccess, StructureAccessor structureAccessor, Chunk chunk, GenerationStep.Carver carverStep) {
    }

    @Override
    public void buildSurface(ChunkRegion region, StructureAccessor structures, NoiseConfig noiseConfig, Chunk chunk) {

    }
    @Override
    public void populateEntities(ChunkRegion region) {

    }
}
