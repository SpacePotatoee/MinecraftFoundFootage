package com.sp.world.generation.chunk_generator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sp.SPBRevamped;
import com.sp.init.ModBlocks;
import com.sp.world.generation.maze_generator.Level0MazeGenerator;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.chunk.ChunkGenerator;

import java.util.Optional;

public class Level0ChunkGenerator extends BackroomsChunkGenerator {
    public static final Codec<Level0ChunkGenerator> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            BiomeSource.CODEC.fieldOf("biome_source").forGetter(generator -> generator.biomeSource)
                    ).apply(instance, instance.stable(Level0ChunkGenerator::new)));


    public Level0ChunkGenerator(BiomeSource biomeSource) {
        super(biomeSource, 5);
    }

    @Override
    protected Codec<? extends ChunkGenerator> getCodec() {
        return CODEC;
    }

    public void generate(StructureWorldAccess world, Chunk chunk) {
        int x = chunk.getPos().getStartX();
        int z = chunk.getPos().getStartZ();
        Random random = Random.create();

        BlockPos.Mutable mutable = new BlockPos.Mutable();
        MinecraftServer server = world.getServer();

        if (server != null) {
            StructureTemplateManager structureTemplateManager = world.getServer().getStructureTemplateManager();
            Optional<StructureTemplate> optional;

            int megaRooms = random.nextBetween(1, 2);

            Identifier roomIdentifier;
            StructurePlacementData structurePlacementData = new StructurePlacementData();

            //Spawn Point
            if((float) chunk.getPos().x == 0 && (float) chunk.getPos().z  == 0) {
                for(int i = 0; i < 16; i++) {
                    for(int j = 0; j < 16; j++){
                        if(i == 0 && j == 0){
                            world.setBlockState(mutable.set(i, 25, j), ModBlocks.GHOST_CEILING_TILE.getDefaultState(), 16);
                        } else {
                            world.setBlockState(mutable.set(i, 25, j), ModBlocks.CEILING_TILE.getDefaultState(), 16);
                        }
                    }
                }
                world.setBlockState(mutable.set(0, 25, 0), ModBlocks.GHOST_CEILING_TILE.getDefaultState(), 16);

                roomIdentifier = new Identifier(SPBRevamped.MOD_ID, "level0/megaroom1");
                structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.NONE).setIgnoreEntities(true);
                optional = structureTemplateManager.getTemplate(roomIdentifier);

                if (optional.isPresent()) {
                    optional.get().place(
                            world,
                            mutable.set(x - 32, 18, z - 32),
                            mutable.set(x - 32, 18, z - 32),
                            structurePlacementData, random, 2);
                    optional.get().place(
                            world,
                            mutable.set(x, 18, z - 32),
                            mutable.set(x, 18, z - 32),
                            structurePlacementData, random, 2);
                    optional.get().place(
                            world,
                            mutable.set(x - 32, 18, z),
                            mutable.set(x - 32, 18, z),
                            structurePlacementData, random, 2);
                    optional.get().place(
                            world,
                            mutable.set(x, 18, z),
                            mutable.set(x, 18, z),
                            structurePlacementData, random, 2);
                }
            } else if (((float) chunk.getPos().x) % SPBRevamped.finalMazeSize == 0 && ((float) chunk.getPos().z) % SPBRevamped.finalMazeSize == 0) {


                if(!chunk.getPos().getBlockPos(0,20,0).isWithinDistance(new Vec3i(0,20,0), this.getExitSpawnRadius(world))) {
                    if(megaRooms != 1){
                        roomIdentifier = new Identifier(SPBRevamped.MOD_ID, "level0/stairwell_0");
                        structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.NONE).setIgnoreEntities(true);
                        optional = structureTemplateManager.getTemplate(roomIdentifier);

                        if (optional.isPresent()) {
                            optional.get().place(
                                    world,
                                    mutable.set(x + 15,4,z + 15),
                                    mutable.set(x + 15,4,z + 15),
                                    structurePlacementData, random, 2
                            );
                        }

                    }
                }

                if (megaRooms == 1) {
                    if (!isNearMegaRooms(x, z, world)) {

                        megaRooms = random.nextBetween(1, 6);
                        roomIdentifier = new Identifier(SPBRevamped.MOD_ID, "level0/megaroom" + megaRooms);
                        structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.NONE).setIgnoreEntities(true);
                        optional = structureTemplateManager.getTemplate(roomIdentifier);

                        if (optional.isPresent()) {
                            if (megaRooms == 1 || megaRooms == 2) {
                                optional.get().place(
                                        world,
                                        mutable.set(x - 32, 18, z - 32),
                                        mutable.set(x - 32, 18, z - 32),
                                        structurePlacementData, random, 2);
                                optional.get().place(
                                        world,
                                        mutable.set(x, 18, z - 32),
                                        mutable.set(x, 18, z - 32),
                                        structurePlacementData, random, 2);
                                optional.get().place(
                                        world,
                                        mutable.set(x - 32, 18, z),
                                        mutable.set(x - 32, 18, z),
                                        structurePlacementData, random, 2);
                                optional.get().place(
                                        world,
                                        mutable.set(x, 18, z),
                                        mutable.set(x, 18, z),
                                        structurePlacementData, random, 2);
                            } else {
                                optional.get().place(
                                        world,
                                        mutable.set(x - 16, 18, z - 16),
                                        mutable.set(x - 16, 18, z - 16),
                                        structurePlacementData, random, 2);
                                Level0MazeGenerator level0MazeGenerator = new Level0MazeGenerator(16, 5, 5, x, z, "level0");
                                level0MazeGenerator.setup(world, false, false, false);
                            }
                        }
                    } else {

                        Level0MazeGenerator level0MazeGenerator = new Level0MazeGenerator(16, 5, 5, x, z, "level0");
                        level0MazeGenerator.setup(world, false, false, false);

                    }
                } else {

                    Level0MazeGenerator level0MazeGenerator = new Level0MazeGenerator(16, 5, 5, x, z, "level0");
                    level0MazeGenerator.setup(world, false, false, false);
                }
            }


            ////Code for 8 x 8 Roof////
            for(int i = 0; i < 2; i++) {
                for(int j = 0; j < 2; j++) {
                    roomIdentifier = this.getRoof();
                    structurePlacementData = this.randRotation();
                    optional = structureTemplateManager.getTemplate(roomIdentifier);

                    if (optional.isPresent()) {
                        if (world.getBlockState(mutable.set(x + 8 * i, 18, z + 8 * j)) != Blocks.CYAN_WOOL.getDefaultState() && world.getBlockState(mutable.set(x + 8 * i, 25, z + 8 * j)) == Blocks.AIR.getDefaultState() ){
                            if (structurePlacementData.getRotation() == BlockRotation.CLOCKWISE_90) {
                                optional.get().place(world, new BlockPos((x + 7) + 8 * i, 25, (z) + 8 * j), mutable.set((x + 7) + 8 * i, 25, (z) + 8 * j), structurePlacementData, random, 16);
                            } else {
                                optional.get().place(world, new BlockPos((x) + 8 * i, 25, (z) + 8 * j), mutable.set((x) + 8 * i, 25, (z) + 8 * j), structurePlacementData, random, 16);
                            }
                        }
                    }
                }
            }

        }

    }

    public boolean isNearMegaRooms(int x, int z,StructureWorldAccess world){
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        boolean near = false;

        for(int i = -80; i <= 80; i += 80){
            for(int j = -80; j <= 80; j += 80){
                BlockState blockState = world.getBlockState(mutable.set(x + i, 19, z + j));
                if (blockState == Blocks.RED_WOOL.getDefaultState()){
                    near = true;
                    break;
                }
            }
        }

        return near;
    }

    public Identifier getRoof(){
        Random random = Random.create();
        int roofNumber = random.nextBetween(1,5);

        if (roofNumber == 1){
            return new Identifier(SPBRevamped.MOD_ID, "level0/roof2");
        }
        else {
            return new Identifier(SPBRevamped.MOD_ID, "level0/roof1");
        }


    }

    public StructurePlacementData randRotation(){
        StructurePlacementData structurePlacementData = new StructurePlacementData();
        Random random = Random.create();
        int rot = random.nextBetween(1,2);

        if(rot == 1){
            structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.NONE).setIgnoreEntities(true);
        }else{
            structurePlacementData.setMirror(BlockMirror.NONE).setRotation(BlockRotation.CLOCKWISE_90).setIgnoreEntities(true);
        }
        return structurePlacementData;
    }
}
