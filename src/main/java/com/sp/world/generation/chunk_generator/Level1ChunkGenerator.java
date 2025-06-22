package com.sp.world.generation.chunk_generator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sp.SPBRevamped;
import com.sp.world.generation.maze_generator.Level1MazeGenerator;
import net.minecraft.block.Blocks;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.noise.PerlinNoiseSampler;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;

import java.util.Optional;

@SuppressWarnings("OptionalIsPresent")
public final class Level1ChunkGenerator extends BackroomsChunkGenerator {
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
        super(biomeSource, 10);
        this.settings = settings;
    }




    public void generate(StructureWorldAccess world, Chunk chunk) {
        int x = chunk.getPos().getStartX();
        int z = chunk.getPos().getStartZ();
        int lights = random.nextBetween(1,6);
        int exit;

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
                        mutable.set(-1,19,-1),
                        mutable.set(-1,19,-1),
                        structurePlacementData, random, 2
                );
            }

            Level1MazeGenerator level1MazeGenerator = new Level1MazeGenerator(8, 10, 10, x, z, "level1");
            level1MazeGenerator.setup(world, false, false, false);
        } else if (((float)chunk.getPos().x) % SPBRevamped.finalMazeSize == 0 && ((float)chunk.getPos().z) % SPBRevamped.finalMazeSize == 0){
            double noise1 = noiseSampler.sample((x) * 0.002, 0, (z) * 0.002);
            if (server != null) {

                if(!chunk.getPos().getBlockPos(0,20,0).isWithinDistance(new Vec3i(0,20,0), this.getExitSpawnRadius(world))){
                    if(noise1 <= 0){
                        exit = random.nextBetween(1,1);
                        if(exit == 1){

                            roomIdentifier = new Identifier(SPBRevamped.MOD_ID, "level1/stairwell2_1");
                            structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.NONE).setIgnoreEntities(true);
                            optional = structureTemplateManager.getTemplate(roomIdentifier);

                            if (optional.isPresent()) {
                                optional.get().place(
                                        world,
                                        mutable.set(x + 16,11,z + 16),
                                        mutable.set(x + 16,11,z + 16),
                                        structurePlacementData, random, 2
                                );
                            }

                        }
                    }
                }

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
                            level1MazeGenerator.setup(world, false, false,true);
                        }

                    }

                } else{

                    if(world.getBlockState(mutable.set(x, 19, z)) != Blocks.RED_WOOL.getDefaultState()) {
                        Level1MazeGenerator level1MazeGenerator = new Level1MazeGenerator(8, 10, 10, x, z, "level1");
                        level1MazeGenerator.setup(world, false, false,true);
                    }

                }
            }
        }

        //Removes the ceiling for debugging
//        for(int i = 0; i < 16; i++){
//            for(int j = 0; j < 16; j++) {
//                world.setBlockState(mutable.set(x + i, 25, z + j), Blocks.AIR.getDefaultState(), 2);
//            }
//        }

    }

    protected Codec<? extends ChunkGenerator> getCodec() {
        return CODEC;
    }



}

