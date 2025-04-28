package com.sp.world.generation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sp.SPBRevamped;
import com.sp.world.generation.maze_generator.PoolroomsMazeGenerator;
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

public final class PoolroomsChunkGenerator extends BackroomsChunkGenerator {
    public static final Codec<PoolroomsChunkGenerator> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            BiomeSource.CODEC.fieldOf("biome_source").forGetter(generator -> generator.biomeSource),
                            ChunkGeneratorSettings.REGISTRY_CODEC.fieldOf("settings").forGetter(generator -> generator.settings)
                    )
                    .apply(instance, instance.stable(PoolroomsChunkGenerator::new))
    );
    private final RegistryEntry<ChunkGeneratorSettings> settings;
    Random random = Random.create();
    PerlinNoiseSampler noiseSampler = new PerlinNoiseSampler(random);

    public PoolroomsChunkGenerator(BiomeSource biomeSource, RegistryEntry<ChunkGeneratorSettings> settings) {
        super(biomeSource, 10);
        this.settings = settings;
    }

    public void generate(StructureWorldAccess world, Chunk chunk) {
        int x = chunk.getPos().getStartX();
        int z = chunk.getPos().getStartZ();

        BlockPos.Mutable mutable = new BlockPos.Mutable();
        MinecraftServer server = world.getServer();

        StructureTemplateManager structureTemplateManager = world.getServer().getStructureTemplateManager();
        Optional<StructureTemplate> optional;

        Identifier roomIdentifier;
        StructurePlacementData structurePlacementData = new StructurePlacementData();
        structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.NONE).setIgnoreEntities(true);



        if(chunk.getPos().x == 0 && chunk.getPos().z == 0){
            roomIdentifier = new Identifier(SPBRevamped.MOD_ID, "poolrooms/entrance");
            optional = structureTemplateManager.getTemplate(roomIdentifier);

            optional.ifPresent(structureTemplate -> structureTemplate.place(
                    world,
                    mutable.set(0, 18, 0),
                    mutable.set(0, 18, 0),
                    structurePlacementData,
                    random,
                    2
            ));

            if(server != null){
                PoolroomsMazeGenerator poolroomsMazeGenerator = new PoolroomsMazeGenerator(8, 10, 10, x, z, "poolrooms");
                poolroomsMazeGenerator.setup(world, true, false, false);
            }

        } else if (((float)chunk.getPos().x) % SPBRevamped.finalMazeSize == 0 && ((float)chunk.getPos().z) % SPBRevamped.finalMazeSize == 0) {

            if(!chunk.getPos().getBlockPos(0,20,0).isWithinDistance(new Vec3i(0,20,0), this.getExitSpawnRadius(world))){
                int exit = random.nextBetween(0,4);

                if(exit == 0){
                    roomIdentifier = new Identifier(SPBRevamped.MOD_ID, "poolrooms/poolrooms_exit");
                    optional = structureTemplateManager.getTemplate(roomIdentifier);

                    optional.ifPresent(structureTemplate -> structureTemplate.place(
                            world,
                            mutable.set(x - 24, 18, z - 24),
                            mutable.set(x - 24, 18, z - 24),
                            structurePlacementData,
                            random,
                            2
                    ));

                    roomIdentifier = new Identifier(SPBRevamped.MOD_ID, "poolrooms/poolrooms_exit2");
                    optional = structureTemplateManager.getTemplate(roomIdentifier);

                    optional.ifPresent(structureTemplate -> structureTemplate.place(
                            world,
                            mutable.set(x - 32, 39, z + 17),
                            mutable.set(x - 32, 39, z + 17),
                            structurePlacementData,
                            random,
                            2
                    ));
                }
            }


            if (server != null) {
                double noise = noiseSampler.sample((x) * 0.002, 0, (z) * 0.002);
                PoolroomsMazeGenerator poolroomsMazeGenerator = new PoolroomsMazeGenerator(8, 10, 10, x, z, "poolrooms");
                poolroomsMazeGenerator.setup(world, noise > 0, true, false);
            }
        }

    }

    protected Codec<? extends ChunkGenerator> getCodec() {
        return CODEC;
    }
}

