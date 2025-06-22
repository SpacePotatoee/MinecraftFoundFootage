package com.sp.world.generation.chunk_generator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sp.SPBRevamped;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.noise.PerlinNoiseSampler;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;

import java.util.Optional;

public final class Level2ChunkGenerator extends BackroomsChunkGenerator {
    public static final Codec<Level2ChunkGenerator> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            BiomeSource.CODEC.fieldOf("biome_source").forGetter(generator -> generator.biomeSource),
                            ChunkGeneratorSettings.REGISTRY_CODEC.fieldOf("settings").forGetter(generator -> generator.settings)
                    )
                    .apply(instance, instance.stable(Level2ChunkGenerator::new))
    );
    private final RegistryEntry<ChunkGeneratorSettings> settings;
    Random random = Random.create();
    PerlinNoiseSampler noiseSampler = new PerlinNoiseSampler(random);

    public Level2ChunkGenerator(BiomeSource biomeSource, RegistryEntry<ChunkGeneratorSettings> settings) {
        super(biomeSource);
        this.settings = settings;
    }

    @Override
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



        if(chunk.getPos().x == 0 && chunk.getPos().z == 0 ){
            roomIdentifier = new Identifier(SPBRevamped.MOD_ID, "level2/stairwell2_2");
            optional = structureTemplateManager.getTemplate(roomIdentifier);

            optional.ifPresent(structureTemplate -> structureTemplate.place(
                    world,
                    mutable.set(x - 1, 19, z),
                    mutable.set(x - 1, 19, z),
                    structurePlacementData, random, 2));

        } else if (((float)chunk.getPos().x) == 0){
            double noise1 = noiseSampler.sample((x) * 0.02, 0, (z) * 0.02);
            if (server != null) {

                if (noise1 > 0.0) {
                    roomIdentifier = this.getRoom(false);
                }
                else{
                    roomIdentifier = this.getRoom(true);
                }

                optional = structureTemplateManager.getTemplate(roomIdentifier);

                optional.ifPresent(structureTemplate -> structureTemplate.place(
                        world,
                        mutable.set(x - 1, 19, z),
                        mutable.set(x - 1, 19, z),
                        structurePlacementData, random, 2));
            }
        }
    }


    public Identifier getRoom(boolean dark){
        Random random = Random.create();
        int roomNumber = random.nextBetween(1,18);
        Identifier identifier;

        if(dark){
            identifier = new Identifier(SPBRevamped.MOD_ID, "level2/dark_room" + roomNumber);
        }else{
            identifier = new Identifier(SPBRevamped.MOD_ID, "level2/room" + roomNumber);
        }
        return identifier;
    }


    protected Codec<? extends ChunkGenerator> getCodec() {
        return CODEC;
    }

}

