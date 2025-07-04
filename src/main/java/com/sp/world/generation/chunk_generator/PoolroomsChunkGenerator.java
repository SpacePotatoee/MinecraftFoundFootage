package com.sp.world.generation.chunk_generator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sp.SPBRevamped;
import com.sp.world.generation.maze_generator.PoolroomsMazeGenerator;
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

import java.util.ArrayList;
import java.util.List;
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

    private final List<String> mainMegaRoomList = List.of("16x16", "16x24", "16x32", "24x16", "24x24", "24x32", "32x16", "32x24", "32x32");

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
                PoolroomsMazeGenerator poolroomsMazeGenerator = new PoolroomsMazeGenerator(8, 10, 10, x, z, "poolrooms/sky");
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
                boolean shouldSky = noise > 0;

                this.generateMegaRooms(world, mutable, shouldSky, x - 32, z - 32);
                PoolroomsMazeGenerator poolroomsMazeGenerator = new PoolroomsMazeGenerator(8, 10, 10, x, z, "poolrooms/" + (shouldSky ? "sky" : "dark"));
                poolroomsMazeGenerator.setup(world, noise > 0, true, false);
            }
        }

        //Removes the ceiling for debugging
//        for(int i = 0; i < 16; i++){
//            for(int j = 0; j < 16; j++) {
//                world.setBlockState(mutable.set(x + i, 28, z + j), Blocks.AIR.getDefaultState(), 2);
//                world.setBlockState(mutable.set(x + i, 27, z + j), Blocks.AIR.getDefaultState(), 2);
//            }
//        }

    }

    private void generateMegaRooms(StructureWorldAccess world, BlockPos.Mutable mutable, boolean sky, int originX, int originY) {
        int size = 8;
        int rows = 10;
        int cols = 10;
        String levelDirectory = "poolrooms/sky";
        if (!sky) {
            levelDirectory = "poolrooms/dark";
        }

        Random random = Random.create();
        StructureTemplateManager structureTemplateManager = world.getServer().getStructureTemplateManager();
        Identifier roomIdentifier = null;

        //Initial Random Mega Room
        int w = random.nextBetween(1, 6);
        int p = random.nextBetween(1, 3);


        if (w == 1)
            roomIdentifier = new Identifier(SPBRevamped.MOD_ID, levelDirectory + "/megaroom_16x16_" + p);
        else if (w == 2)
            roomIdentifier = new Identifier(SPBRevamped.MOD_ID, levelDirectory + "/megaroom_16x24_" + p);
        else if (w == 3)
            roomIdentifier = new Identifier(SPBRevamped.MOD_ID, levelDirectory + "/megaroom_16x32_" + p);
        else if (w == 4)
            roomIdentifier = new Identifier(SPBRevamped.MOD_ID, levelDirectory + "/megaroom_24x24_" + p);
        else if (w == 5)
            roomIdentifier = new Identifier(SPBRevamped.MOD_ID, levelDirectory + "/megaroom_24x32_" + p);
        else if (w == 6)
            roomIdentifier = new Identifier(SPBRevamped.MOD_ID, levelDirectory + "/megaroom_32x32_" + p);

        StructurePlacementData structurePlacementData = new StructurePlacementData();
        structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.NONE).setIgnoreEntities(true);
        Optional<StructureTemplate> optional = structureTemplateManager.getTemplate(roomIdentifier);

        int directoryLength = levelDirectory.length();
        int roomWidth = Integer.parseInt(roomIdentifier.getPath().substring(directoryLength + 10, directoryLength + 12));
        int roomHeight = Integer.parseInt(roomIdentifier.getPath().substring(directoryLength + 13, directoryLength + 15));

        int randX = random.nextBetween(1, cols - (1 + (roomWidth / size)));
        int randY = random.nextBetween(1, rows - (1 + (roomHeight / size)));

        BlockPos structurePos = mutable.set(randX + ((size - 1) * randX) + originX, 18, randY + ((size - 1) * randY) + originY);

        if (optional.isPresent() &&
                world.getBlockState(mutable.set(structurePos.getX(), 18, structurePos.getZ())) != Blocks.PURPLE_WOOL.getDefaultState()
        ) {
            optional.get().place(world, structurePos, structurePos, structurePlacementData, random, 2);
        }

        List<String> megaRoomList = new ArrayList<>(mainMegaRoomList);

        //Fill area with more mega rooms randomly
        while (!megaRoomList.isEmpty()) {
            int ind = random.nextBetween(0, megaRoomList.size() - 1);
            String currentMegaRoom = megaRoomList.get(ind);
            int xx = Integer.parseInt(currentMegaRoom.substring(0, 2));
            int yy = Integer.parseInt(currentMegaRoom.substring(3, 5));
            p = random.nextBetween(1, 3);
            if (yy < xx) {
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.COUNTERCLOCKWISE_90).setIgnoreEntities(true);
                roomIdentifier = new Identifier(SPBRevamped.MOD_ID, levelDirectory + "/megaroom_" + yy + "x" + xx + "_" + p);
            } else {
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.NONE).setIgnoreEntities(true);
                roomIdentifier = new Identifier(SPBRevamped.MOD_ID, levelDirectory + "/megaroom_" + xx + "x" + yy + "_" + p);
            }
            roomWidth = xx;
            roomHeight = yy;


            boolean placed = false;
            for (int ay = 1; ay < rows - ((roomHeight / size)); ay++) {
                for (int ax = 1; ax < cols - ((roomWidth / size)); ax++) {
                    if (!placed) {
                        BlockPos StructurePos = mutable.set(ax + ((size - 1) * ax) + originX, 18, ay + ((size - 1) * ay) + originY);

                        boolean clear = true;
                        for (int ry = -1; ry <= roomHeight; ry++) {
                            for (int bx = -1; bx <= roomWidth; bx++) {
                                if (clear) {
                                    if (world.getBlockState(new BlockPos(StructurePos.getX() + bx, 18, StructurePos.getZ() + ry)) == Blocks.PURPLE_WOOL.getDefaultState()) {
                                        clear = false;
                                        break;
                                    }
                                }
                            }
                        }


                        if (clear) {
                            optional = structureTemplateManager.getTemplate(roomIdentifier);
                            if (optional.isPresent()) {
                                if (structurePlacementData.getRotation() == BlockRotation.COUNTERCLOCKWISE_90) {
                                    optional.get().place(world, new BlockPos(StructurePos.getX(), 18, StructurePos.getZ() + (roomHeight - 1)), new BlockPos(StructurePos.getX(), 19, StructurePos.getZ() + (roomWidth - 1)), structurePlacementData, random, 2);
                                } else {
                                    optional.get().place(world, StructurePos, StructurePos, structurePlacementData, random, 2);
                                }
                                placed = true;
                                break;
                            }
                        }
                    }
                }
            }
            megaRoomList.remove(ind);
        }

    }

    protected Codec<? extends ChunkGenerator> getCodec() {
        return CODEC;
    }
}

