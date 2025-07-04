package com.sp.world.generation.chunk_generator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sp.SPBRevamped;
import com.sp.init.ModBlocks;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.Blender;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.noise.NoiseConfig;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class Level324ChunkGenerator extends BackroomsChunkGenerator {
    public static final Codec<Level324ChunkGenerator> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            BiomeSource.CODEC.fieldOf("biome_source").forGetter(generator -> generator.biomeSource),
                            ChunkGeneratorSettings.REGISTRY_CODEC.fieldOf("settings").forGetter(generator -> generator.settings)
                    )
                    .apply(instance, instance.stable(Level324ChunkGenerator::new))
    );

    private final RegistryEntry<ChunkGeneratorSettings> settings;

    private final Random random;

    public Level324ChunkGenerator(BiomeSource biomeSource, RegistryEntry<ChunkGeneratorSettings> settings) {
        super(biomeSource, 10);
        this.settings = settings;
        this.random = Random.create();
    }

    @Override
    public void generate(StructureWorldAccess world, Chunk chunk) {
        int x = chunk.getPos().getStartX();
        int z = chunk.getPos().getStartZ();
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        StructureTemplateManager structureTemplateManager = world.getServer().getStructureTemplateManager();

        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                if (!((z + j > -1 && z + j < 27) && (x + i > 7 && x + i < 56))) {
                    if (i == 8 && j == 7) {
                        BlockPos placementPos = mutable.set(x + i, 4, z + j);

                        StructurePlacementData structurePlacementData = new StructurePlacementData();
                        structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.NONE).setIgnoreEntities(true);

                        Optional<StructureTemplate> optional = structureTemplateManager.getTemplate(new Identifier(SPBRevamped.MOD_ID, "level324/hanging_lamp" + (random.nextBetween(0, 5) == 0 ? "_on" : "_off")));

                        optional.ifPresent(structureTemplate -> structureTemplate.place(
                                world,
                                placementPos,
                                placementPos,
                                structurePlacementData, random, 2));
                    }

                    if ((chunk.getPos().getStartX() + i) % 1000 == 9) {
                        if ((chunk.getPos().getStartZ() + j) % 21 == 0) {
                            BlockPos placementPos = mutable.set(x + i, 65, z + j);
                            StructurePlacementData structurePlacementData = new StructurePlacementData();
                            structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.NONE).setIgnoreEntities(true);

                            Optional<StructureTemplate> optional = structureTemplateManager.getTemplate(new Identifier(SPBRevamped.MOD_ID, "inf_grass/utility_pole"));

                            optional.ifPresent(structureTemplate -> structureTemplate.place(
                                    world,
                                    placementPos,
                                    placementPos,
                                    structurePlacementData, random, 2));

                        }
                    }
                }

                if ((chunk.getPos().getStartX() + i) == 8) {
                    if ((chunk.getPos().getStartZ() + j) == 0) {

                        BlockPos placementPos = mutable.set(x + i, -2, z + j);
                        StructurePlacementData structurePlacementData = new StructurePlacementData();
                        structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.NONE).setIgnoreEntities(true);

                        Optional<StructureTemplate> optional = structureTemplateManager.getTemplate(new Identifier(SPBRevamped.MOD_ID, "level324/gas_station"));

                        optional.ifPresent(structureTemplate -> structureTemplate.place(
                                world,
                                placementPos,
                                placementPos,
                                structurePlacementData, random, 2));
                    }
                }
            }
        }
    }

    public CompletableFuture<Chunk> populateNoise(Executor executor, Blender blender, NoiseConfig noiseConfig, StructureAccessor structureAccessor, Chunk chunk) {
        int x = chunk.getPos().getStartX();
        int z = chunk.getPos().getStartZ();
        BlockPos.Mutable mutable = new BlockPos.Mutable();

        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                if (!((z + j > -1 && z + j < 27) && (x + i > 7 && x + i < 55))) {
                    chunk.setBlockState(mutable.set(i, 0, j), ModBlocks.CONCRETE_BLOCK_11.getDefaultState(), false);

                    chunk.setBlockState(mutable.set(i, 63, j), ModBlocks.CONCRETE_BLOCK_11.getDefaultState(), false);

                    if ((i + Math.abs(x * 16)) % 1000 < 8/* || (j + Math.abs(chunk.getPos().z * 16)) % 1000 < 8*/) {

                        chunk.setBlockState(mutable.set(i, 64, j), ModBlocks.ROAD.getDefaultState(), false);
                    } else {
                        chunk.setBlockState(mutable.set(i, 64, j), ModBlocks.RED_DIRT.getDefaultState(), false);
                    }
                }
            }
        }

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                for (int k = 1; k < 63; k++) {
                    chunk.setBlockState(mutable.set(i, k, j), ModBlocks.CONCRETE_BLOCK_11.getDefaultState(), false);
                }
            }

            for (int j = 14; j < 16; j++) {
                for (int k = 1; k < 63; k++) {
                    chunk.setBlockState(mutable.set(i, k, j), ModBlocks.CONCRETE_BLOCK_11.getDefaultState(), false);
                }
            }
        }

        for (int i = 14; i < 16; i++) {
            for (int j = 0; j < 2; j++) {
                for (int k = 1; k < 63; k++) {
                    chunk.setBlockState(mutable.set(i, k, j), ModBlocks.CONCRETE_BLOCK_11.getDefaultState(), false);
                }
            }

            for (int j = 14; j < 16; j++) {
                for (int k = 1; k < 63; k++) {
                    chunk.setBlockState(mutable.set(i, k, j), ModBlocks.CONCRETE_BLOCK_11.getDefaultState(), false);
                }
            }
        }


        return CompletableFuture.completedFuture(chunk);
    }

    @Override
    protected Codec<? extends ChunkGenerator> getCodec() {
        return CODEC;
    }
}
