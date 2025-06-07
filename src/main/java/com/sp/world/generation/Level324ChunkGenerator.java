package com.sp.world.generation;

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
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.noise.NoiseConfig;

import java.util.Optional;

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
        super(biomeSource);
        this.settings = settings;
        this.random = Random.create();
    }

    @Override
    public void generate(StructureWorldAccess world, Chunk chunk) {

    }

    @Override
    public void buildSurface(ChunkRegion region, StructureAccessor structures, NoiseConfig noiseConfig, Chunk chunk) {
        int x = chunk.getPos().getStartX();
        int z = chunk.getPos().getStartZ();
        StructureTemplateManager structureTemplateManager = region.getServer().getStructureTemplateManager();

        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                chunk.setBlockState(new BlockPos(i, 0, j), ModBlocks.CONCRETE_BLOCK_11.getDefaultState(), false);

                chunk.setBlockState(new BlockPos(i, 63, j), ModBlocks.CONCRETE_BLOCK_11.getDefaultState(), false);

                if (i == 8 && j == 7) {
                    BlockPos placementPos = new BlockPos(x + i, 4, z + j);

                    StructurePlacementData structurePlacementData = new StructurePlacementData();
                    structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.NONE).setIgnoreEntities(true);

                    Optional<StructureTemplate> optional = structureTemplateManager.getTemplate(new Identifier(SPBRevamped.MOD_ID, "level324/hanging_lamp" + (random.nextBetween(0, 5) == 0 ? "_on" : "_off")));

                    optional.ifPresent(structureTemplate -> structureTemplate.place(
                            region,
                            placementPos,
                            placementPos,
                            structurePlacementData, random, 2));
                }

                if ((i + Math.abs(x * 16)) % 1000 < 8/* || (j + Math.abs(chunk.getPos().z * 16)) % 1000 < 8*/) {

                    chunk.setBlockState(new BlockPos(i, 64, j), ModBlocks.ROAD.getDefaultState(), false);
                } else {
                    chunk.setBlockState(new BlockPos(i, 64, j), ModBlocks.RED_DIRT.getDefaultState(), false);
                }

                if ((chunk.getPos().getStartX() + i) % 1000 == 9) {
                    if ((chunk.getPos().getStartZ() + j) % 21 == 0) {
                        BlockPos placementPos = new BlockPos(x + i, 65, z + j);
                        StructurePlacementData structurePlacementData = new StructurePlacementData();
                        structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.NONE).setIgnoreEntities(true);

                        Optional<StructureTemplate> optional = structureTemplateManager.getTemplate(new Identifier(SPBRevamped.MOD_ID, "infgrass/utility_pole"));

                        optional.ifPresent(structureTemplate -> structureTemplate.place(
                                region,
                                placementPos,
                                placementPos,
                                structurePlacementData, random, 2));

                    }
                }
            }
        }

        for (int i = 2; i < 6; i++) {
            for (int j = 2; j < 6; j++) {
                for (int k = 1; k < 63; k++) {
                    chunk.setBlockState(new BlockPos(i, k, j), ModBlocks.CONCRETE_BLOCK_11.getDefaultState(), false);
                }
            }

            for (int j = 10; j < 14; j++) {
                for (int k = 1; k < 63; k++) {
                    chunk.setBlockState(new BlockPos(i, k, j), ModBlocks.CONCRETE_BLOCK_11.getDefaultState(), false);
                }
            }
        }

        for (int i = 10; i < 14; i++) {
            for (int j = 2; j < 6; j++) {
                for (int k = 1; k < 63; k++) {
                    chunk.setBlockState(new BlockPos(i, k, j), ModBlocks.CONCRETE_BLOCK_11.getDefaultState(), false);
                }
            }

            for (int j = 10; j < 14; j++) {
                for (int k = 1; k < 63; k++) {
                    chunk.setBlockState(new BlockPos(i, k, j), ModBlocks.CONCRETE_BLOCK_11.getDefaultState(), false);
                }
            }
        }
    }

    @Override
    protected Codec<? extends ChunkGenerator> getCodec() {
        return CODEC;
    }
}
