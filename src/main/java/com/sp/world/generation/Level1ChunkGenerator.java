package com.sp.world.generation;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Suppliers;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sp.SPBRevamped;
import com.sp.world.generation.maze_generator.Level1MazeGenerator;
import net.minecraft.SharedConstants;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.PerlinNoiseSampler;
import net.minecraft.util.math.noise.SimplexNoiseSampler;
import net.minecraft.util.math.random.CheckedRandom;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.math.random.RandomSeed;
import net.minecraft.world.*;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.BiomeSupplier;
import net.minecraft.world.chunk.BelowZeroRetrogen;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.HeightContext;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.StructureWeightSampler;
import net.minecraft.world.gen.carver.CarverContext;
import net.minecraft.world.gen.carver.CarvingMask;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.chunk.*;
import net.minecraft.world.gen.densityfunction.DensityFunction;
import net.minecraft.world.gen.densityfunction.DensityFunctionTypes;
import net.minecraft.world.gen.densityfunction.DensityFunctions;
import net.minecraft.world.gen.noise.NoiseConfig;
import net.minecraft.world.gen.noise.NoiseRouter;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Predicate;
import java.util.function.Supplier;

@SuppressWarnings("OptionalIsPresent")
public final class Level1ChunkGenerator extends ChunkGenerator {
    public static final Codec<Level1ChunkGenerator> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            BiomeSource.CODEC.fieldOf("biome_source").forGetter(generator -> generator.biomeSource),
                            ChunkGeneratorSettings.REGISTRY_CODEC.fieldOf("settings").forGetter(generator -> generator.settings)
                    )
                    .apply(instance, instance.stable(Level1ChunkGenerator::new))
    );
    private final RegistryEntry<ChunkGeneratorSettings> settings;
    Random random = Random.create();
    PerlinNoiseSampler noiseSampler = new PerlinNoiseSampler(random);

    public Level1ChunkGenerator(BiomeSource biomeSource, RegistryEntry<ChunkGeneratorSettings> settings) {
        super(biomeSource);
        this.settings = settings;
    }




    public void generateMaze(StructureWorldAccess world, Chunk chunk, StructureAccessor structureAccessor) {
        int x = chunk.getPos().getStartX();
        int z = chunk.getPos().getStartZ();
        int lights = random.nextBetween(1,6);

        BlockPos.Mutable mutable = new BlockPos.Mutable();
        MinecraftServer server = world.getServer();

        StructureTemplateManager structureTemplateManager = world.getServer().getStructureTemplateManager();
        Optional<StructureTemplate> optional;

        Identifier roomIdentifier;
        StructurePlacementData structurePlacementData = new StructurePlacementData();


        if((float) chunk.getPos().x == 0 && (float) chunk.getPos().z  == 0){
            roomIdentifier = new Identifier(SPBRevamped.MOD_ID, "level1/stairwell_1");
            structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.NONE).setIgnoreEntities(true);
            optional = structureTemplateManager.getTemplate(roomIdentifier);

            if(optional.isPresent()){
                optional.get().place(
                        world,
                        mutable.set(0,19,0),
                        mutable.set(0,19,0),
                        structurePlacementData, random, 2
                );
            }

            Level1MazeGenerator level1MazeGenerator = new Level1MazeGenerator(8, 10, 10, x, z, "level1");
            level1MazeGenerator.setup(world);
        } else if (((float)chunk.getPos().x) % SPBRevamped.finalMazeSize == 0 && ((float)chunk.getPos().z) % SPBRevamped.finalMazeSize == 0){
            world.setBlockState(mutable.set(x - 32, 17, z - 32), Blocks.GLOWSTONE.getDefaultState(), 16);
            double noise1 = noiseSampler.sample((x) * 0.002, 0, (z) * 0.002);
            if (server != null) {

                if(noise1 > 0){
                    roomIdentifier = new Identifier(SPBRevamped.MOD_ID, "level1/megaroom1");
                    structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.NONE).setIgnoreEntities(true);
                    optional = structureTemplateManager.getTemplate(roomIdentifier);

                    if (optional.isPresent()) {
                        optional.get().place(
                                world,
                                mutable.set(x - 32, 19, z - 32),
                                mutable.set(x - 32, 19, z - 32),
                                structurePlacementData, random, 2);
                        optional.get().place(
                                world,
                                mutable.set(x, 19, z - 32),
                                mutable.set(x, 19, z - 32),
                                structurePlacementData, random, 2);

                        roomIdentifier = new Identifier(SPBRevamped.MOD_ID, "level1/light" + lights);
                        structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.NONE).setIgnoreEntities(true);
                        optional = structureTemplateManager.getTemplate(roomIdentifier);

                        if (optional.isPresent()){
                        optional.get().place(
                                world,
                                mutable.set(x - 32, 19, z - 32),
                                mutable.set(x - 32, 19, z - 32),
                                structurePlacementData, random, 16);
                        optional.get().place(
                                world,
                                mutable.set(x, 19, z - 32),
                                mutable.set(x, 19, z - 32),
                                structurePlacementData, random, 16);
                        }


                    } else {
                        if(world.getBlockState(mutable.set(x, 19, z)) != Blocks.RED_WOOL.getDefaultState()) {
                            Level1MazeGenerator level1MazeGenerator = new Level1MazeGenerator(8, 10, 10, x, z, "level1");
                            level1MazeGenerator.setup(world);
                        }

                    }

                } else{

                    if(world.getBlockState(mutable.set(x, 19, z)) != Blocks.RED_WOOL.getDefaultState()) {
                        Level1MazeGenerator level1MazeGenerator = new Level1MazeGenerator(8, 10, 10, x, z, "level1");
                        level1MazeGenerator.setup(world);
                    }

                }
            }
        }

//        for (int j = 0; j < 16; j++){
//            for (int i = 0; i < 16; i++){
//                world.setBlockState(mutable.set(x + j, 25, z + i), Blocks.AIR.getDefaultState(), 16);
//            }
//        }

    }

    protected Codec<? extends ChunkGenerator> getCodec() {
        return CODEC;
    }

    /* this method builds the shape of the terrain. it places stone everywhere, which will later be overwritten with grass, terracotta, snow, sand, etc
         by the buildSurface method. it also is responsible for putting the water in oceans. it returns a CompletableFuture-- you'll likely want this to be delegated to worker threads. */
    @Override
    public CompletableFuture<Chunk> populateNoise(Executor executor, Blender blender, NoiseConfig noiseConfig, StructureAccessor structureAccessor, Chunk chunk) {
        return CompletableFuture.completedFuture(chunk);
    }


    @Override
    public int getSeaLevel() {
        return 0;
    }

    /* the lowest value that blocks can be placed in the world. in a vanilla world, this is -64. */
    @Override
    public int getMinimumY() {
        return 0;
    }

    /* this method returns the height of the terrain at a given coordinate. it's used for structure generation */
    @Override
    public int getHeight(int x, int z, Heightmap.Type heightmap, HeightLimitView world, NoiseConfig noiseConfig) {
        return this.getWorldHeight();
    }

    /* this method returns a "core sample" of the world at a given coordinate. it's used for structure generation */
    @Override
    public VerticalBlockSample getColumnSample(int x, int z, HeightLimitView world, NoiseConfig noiseConfig) {
        BlockState[] states = new BlockState[world.getHeight()];

        for (int i = 0; i < states.length; i++) {
            states[i] = Blocks.AIR.getDefaultState();
        }

        return new VerticalBlockSample(0, states);
    }

    /* this method adds text to the f3 menu. for NoiseChunkGenerator, it's the NoiseRouter line */
    @Override
    public void getDebugHudText(List<String> text, NoiseConfig noiseConfig, BlockPos pos) {
    }

    /* the distance between the highest and lowest points in the world. in vanilla, this is 384 (64+325) */
    @Override
    public int getWorldHeight() {
        return 384;
    }




    /* the method that creates non-noise caves (i.e., all the caves we had before the caves and cliffs update) */
    @Override
    public void carve(ChunkRegion chunkRegion, long seed, NoiseConfig noiseConfig, BiomeAccess biomeAccess, StructureAccessor structureAccessor, Chunk chunk, GenerationStep.Carver carverStep) {
    }

    /* the method that places grass, dirt, and other things on top of the world, as well as handling the bedrock and deepslate layers,
    as well as a few other miscellaneous things. without this method, your world is just a blank stone (or whatever your default block is) canvas (plus any ores, etc) */
    @Override
    public void buildSurface(ChunkRegion region, StructureAccessor structures, NoiseConfig noiseConfig, Chunk chunk) {

    }

    /* this method spawns entities in the world */
    @Override
    public void populateEntities(ChunkRegion region) {

    }



}

