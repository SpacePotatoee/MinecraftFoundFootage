package com.sp.init;

import com.sp.SPBRevamped;
import com.sp.block.SprintBlockSoundGroup;
import com.sp.block.custom.*;
import com.sp.block.custom.CarpetBlock;
import com.sp.block.custom.WallBlock;
import com.sp.block.custom.pipes.*;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public class ModBlocks {

    public static final Block VoidBlock = registerBlock("void_block",
            new Block(FabricBlockSettings.copyOf(Blocks.STONE).hardness(-1f).solid().noBlockBreakParticles()));

    //////Level 1 Stuff//////
    public static final Block WallBlock = registerBlock("wall_block",
            new WallBlock(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS).hardness(-1f).solid().noBlockBreakParticles().sounds(SprintBlockSoundGroup.WALL)));

    public static final Block WallBlock2 = registerBlock("wall_block_2",
            new WallBlock(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS).hardness(-1f).solid().noBlockBreakParticles().sounds(SprintBlockSoundGroup.WALL)));


    public static final Block CarpetBlock = registerBlock("carpet_block",
            new CarpetBlock(FabricBlockSettings.copyOf(Blocks.WHITE_WOOL).hardness(-1f).solid().noBlockBreakParticles().sounds(SprintBlockSoundGroup.CARPET)));

    public static final Block CeilingTile = registerBlock("ceiling_tile",
            new Block(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS).hardness(-1f).solid().noBlockBreakParticles().sounds(SprintBlockSoundGroup.CEILING_TILE)));

    public static final Block GhostCeilingTile = registerBlock("ghost_ceiling_tile",
            new Block(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS).hardness(-1f).solid().noBlockBreakParticles().collidable(false)));


    public static final Block FluorescentLight = registerBlock("fluorescent_light",
            new FluorescentLightBlock(FabricBlockSettings.copyOf(Blocks.GLASS).hardness(-1f).noBlockBreakParticles()));


    public static final Block WallArrow1 = registerBlock("arrow1",
            new WallText(FabricBlockSettings.copyOf(Blocks.STONE).hardness(-1f).noBlockBreakParticles().nonOpaque().collidable(false).sounds(SprintBlockSoundGroup.SILENT)));

    public static final Block WallArrow2 = registerBlock("arrow2",
            new WallText(FabricBlockSettings.copyOf(Blocks.STONE).hardness(-1f).noBlockBreakParticles().nonOpaque().collidable(false).sounds(SprintBlockSoundGroup.SILENT)));

    public static final Block WallArrow3 = registerBlock("arrow3",
            new WallText(FabricBlockSettings.copyOf(Blocks.STONE).hardness(-1f).noBlockBreakParticles().nonOpaque().collidable(false).sounds(SprintBlockSoundGroup.SILENT)));

    public static final Block WallArrow4 = registerBlock("arrow4",
            new WallText(FabricBlockSettings.copyOf(Blocks.STONE).hardness(-1f).noBlockBreakParticles().nonOpaque().collidable(false).sounds(SprintBlockSoundGroup.SILENT)));


    public static final Block WallSmall1 = registerBlock("wall_small_1",
            new WallText(FabricBlockSettings.copyOf(Blocks.STONE).hardness(-1f).noBlockBreakParticles().nonOpaque().collidable(false).sounds(SprintBlockSoundGroup.SILENT)));

    public static final Block WallSmall2 = registerBlock("wall_small_2",
            new WallText(FabricBlockSettings.copyOf(Blocks.STONE).hardness(-1f).noBlockBreakParticles().nonOpaque().collidable(false).sounds(SprintBlockSoundGroup.SILENT)));

    public static final Block WallDrawingDoor = registerBlock("wall_drawing_door",
            new WallText(FabricBlockSettings.copyOf(Blocks.STONE).hardness(-1f).noBlockBreakParticles().nonOpaque().collidable(false).sounds(SprintBlockSoundGroup.SILENT)));

    public static final Block WallDrawingWindow = registerBlock("wall_drawing_window",
            new WallText(FabricBlockSettings.copyOf(Blocks.STONE).hardness(-1f).noBlockBreakParticles().nonOpaque().collidable(false).sounds(SprintBlockSoundGroup.SILENT)));

    public static final Block drawingMarker = registerBlock("drawing_marker",
            new DrawingMarker(FabricBlockSettings.copyOf(Blocks.STONE).hardness(-1f).noBlockBreakParticles()));


    public static final Block Rug1 = registerBlock("rug1",
            new Rug(FabricBlockSettings.copyOf(Blocks.WHITE_WOOL).hardness(-1f).noBlockBreakParticles().nonOpaque().collidable(false).sounds(SprintBlockSoundGroup.SILENT)));

    public static final Block Rug2 = registerBlock("rug2",
            new Rug(FabricBlockSettings.copyOf(Blocks.WHITE_WOOL).hardness(-1f).noBlockBreakParticles().nonOpaque().collidable(false).sounds(SprintBlockSoundGroup.SILENT)));

    public static final Block CHAINFENCE = registerBlock("chain_fence",
            new ChainFence(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).hardness(-1f).noBlockBreakParticles()));

    public static final Block NEWSTAIRS = registerBlock("new_stairs",
            new NewStairs(FabricBlockSettings.copyOf(Blocks.STONE).hardness(-1f).noBlockBreakParticles().nonOpaque().sounds(SprintBlockSoundGroup.CONCRETE)));

    public static final Block Bricks = registerBlock("bricks",
            new Block(FabricBlockSettings.copyOf(Blocks.STONE).hardness(-1f).noBlockBreakParticles()));



    //////Level 2 and 3 Stuff//////
    public static final Block ThinFluorescentLight = registerBlock("thin_fluorescent_light",
            new ThinFluorescentLightBlock(FabricBlockSettings.copyOf(Blocks.GLASS).hardness(-1f).noBlockBreakParticles()));

    public static final Block EmergencyLight = registerBlock("emergency_light",
            new EmergencyLightBlock(FabricBlockSettings.copyOf(Blocks.GLASS).hardness(-1f).noBlockBreakParticles()));

    public static final Block BottomTrim = registerBlock("bottom_trim",
            new BottomTrim(FabricBlockSettings.copyOf(Blocks.STONE).hardness(-1f).collidable(false).nonOpaque().noBlockBreakParticles().sounds(SprintBlockSoundGroup.SILENT)));

    public static final Block ConcreteBlock1 = registerBlock("concrete1",
            new Block(FabricBlockSettings.copyOf(Blocks.STONE).hardness(-1f).solid().noBlockBreakParticles()));

    public static final Block ConcreteBlock2 = registerBlock("concrete2",
            new Block(FabricBlockSettings.copyOf(Blocks.STONE).hardness(-1f).solid().noBlockBreakParticles()));

    public static final Block ConcreteBlock5 = registerBlock("concrete5",
            new Block(FabricBlockSettings.copyOf(Blocks.STONE).hardness(-1f).solid().noBlockBreakParticles()));

    public static final Block ConcreteBlock6 = registerBlock("concrete6",
            new Block(FabricBlockSettings.copyOf(Blocks.STONE).hardness(-1f).solid().noBlockBreakParticles().sounds(SprintBlockSoundGroup.CONCRETE)));

    public static final Block ConcreteBlock7 = registerBlock("concrete7",
            new Block(FabricBlockSettings.copyOf(Blocks.STONE).hardness(-1f).solid().noBlockBreakParticles()));

    public static final Block ConcreteBlock9 = registerBlock("concrete9",
            new Block(FabricBlockSettings.copyOf(Blocks.STONE).hardness(-1f).solid().noBlockBreakParticles()));

    public static final Block ConcreteBlock9Slab = registerBlock("concrete9slab",
            new SlabBlock(FabricBlockSettings.copyOf(Blocks.STONE).hardness(-1f).solid().noBlockBreakParticles()));

    public static final Block ConcreteBlock10 = registerBlock("concrete10",
            new Block(FabricBlockSettings.copyOf(Blocks.STONE).hardness(-1f).solid().noBlockBreakParticles()));

    public static final Block ConcreteBlock11 = registerBlock("concrete11",
            new Block(FabricBlockSettings.copyOf(Blocks.STONE).hardness(-1f).solid().noBlockBreakParticles()));

    public static final Block ConcreteBlock12 = registerBlock("concrete12",
            new Block(FabricBlockSettings.copyOf(Blocks.STONE).hardness(-1f).solid().noBlockBreakParticles()));


    public static final Block ThinPipe = registerBlock("thin_pipe",
            new ThinPipe(FabricBlockSettings.copyOf(Blocks.STONE).hardness(-1f).noBlockBreakParticles().nonOpaque().collidable(false).sounds(SprintBlockSoundGroup.SILENT)));

    public static final Block ThinPipeCorner = registerBlock("thin_pipe_corner",
            new ThinPipeCorner(FabricBlockSettings.copyOf(Blocks.STONE).hardness(-1f).noBlockBreakParticles().nonOpaque().collidable(false).sounds(SprintBlockSoundGroup.SILENT)));

    public static final Block Pipe = registerBlock("pipe",
            new Pipe(FabricBlockSettings.copyOf(Blocks.STONE).hardness(-1f).noBlockBreakParticles().nonOpaque().sounds(SprintBlockSoundGroup.SILENT)));

    public static final Block PipeMiddle = registerBlock("pipe_middle",
            new Pipe(FabricBlockSettings.copyOf(Blocks.STONE).hardness(-1f).noBlockBreakParticles().nonOpaque().sounds(SprintBlockSoundGroup.SILENT)));

    public static final Block PipeCorner = registerBlock("pipe_corner",
            new PipeCorner(FabricBlockSettings.copyOf(Blocks.STONE).hardness(-1f).noBlockBreakParticles().nonOpaque().sounds(SprintBlockSoundGroup.SILENT)));

    public static final Block BigPipe = registerBlock("big_pipe",
            new Pipe(FabricBlockSettings.copyOf(Blocks.STONE).hardness(-1f).noBlockBreakParticles().nonOpaque().sounds(SprintBlockSoundGroup.SILENT)));

    public static final Block BigPipeMiddle = registerBlock("big_pipe_middle",
            new Pipe(FabricBlockSettings.copyOf(Blocks.STONE).hardness(-1f).noBlockBreakParticles().nonOpaque().sounds(SprintBlockSoundGroup.SILENT)));

    public static final Block SmallPipeSet = registerBlock("small_pipe_set",
            new SmallPipeSet(FabricBlockSettings.copyOf(Blocks.STONE).hardness(-1f).noBlockBreakParticles().nonOpaque().collidable(false).sounds(SprintBlockSoundGroup.SILENT)));


    public static final Block WallText1 = registerBlock("wall_text_1",
            new WallText(FabricBlockSettings.copyOf(Blocks.STONE).hardness(-1f).noBlockBreakParticles().nonOpaque().collidable(false).sounds(SprintBlockSoundGroup.SILENT)));

    public static final Block WallText2 = registerBlock("wall_text_2",
            new WallText(FabricBlockSettings.copyOf(Blocks.STONE).hardness(-1f).noBlockBreakParticles().nonOpaque().collidable(false).sounds(SprintBlockSoundGroup.SILENT)));

    public static final Block WallText3 = registerBlock("wall_text_3",
            new WallText(FabricBlockSettings.copyOf(Blocks.STONE).hardness(-1f).noBlockBreakParticles().nonOpaque().collidable(false).sounds(SprintBlockSoundGroup.SILENT)));

    public static final Block WallText4 = registerBlock("wall_text_4",
            new WallText(FabricBlockSettings.copyOf(Blocks.STONE).hardness(-1f).noBlockBreakParticles().nonOpaque().collidable(false).sounds(SprintBlockSoundGroup.SILENT)));

    public static final Block WallText5 = registerBlock("wall_text_5",
            new WallText(FabricBlockSettings.copyOf(Blocks.STONE).hardness(-1f).noBlockBreakParticles().nonOpaque().collidable(false).sounds(SprintBlockSoundGroup.SILENT)));

    public static final Block WallText6 = registerBlock("wall_text_6",
            new WallText(FabricBlockSettings.copyOf(Blocks.STONE).hardness(-1f).noBlockBreakParticles().nonOpaque().collidable(false).sounds(SprintBlockSoundGroup.SILENT)));

    public static final Block WallText7 = registerBlock("wall_text_7",
            new WallText(FabricBlockSettings.copyOf(Blocks.STONE).hardness(-1f).noBlockBreakParticles().nonOpaque().collidable(false).sounds(SprintBlockSoundGroup.SILENT)));

    public static final Block WallText8 = registerBlock("wall_text_8",
            new WallText(FabricBlockSettings.copyOf(Blocks.STONE).hardness(-1f).noBlockBreakParticles().nonOpaque().collidable(false).sounds(SprintBlockSoundGroup.SILENT)));

    public static final Block WallText99 = registerBlock("wall_text_99",
            new WallText(FabricBlockSettings.copyOf(Blocks.STONE).hardness(-1f).noBlockBreakParticles().nonOpaque().collidable(false).sounds(SprintBlockSoundGroup.SILENT)));


    public static final Block WOODEN_CRATE = registerBlock("wooden_crate",
            new WoodenCrate(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS).hardness(-1f).solid().noBlockBreakParticles()));



    //////Poolrooms Stuff//////
    public static final Block PoolroomsSkyBlock = registerBlock("pool_sky",
            new GlassBlock(FabricBlockSettings.copyOf(Blocks.GLASS).hardness(-1f).noBlockBreakParticles().sounds(SprintBlockSoundGroup.SILENT)));

    public static final Block PoolTiles = registerBlock("pool_tiles",
            new Block(FabricBlockSettings.copyOf(Blocks.STONE).hardness(-1f).solid().noBlockBreakParticles().sounds(SprintBlockSoundGroup.CONCRETE)));

    public static final Block PoolTileWall = registerBlock("pool_tile_wall",
            new PoolTileWall(FabricBlockSettings.copyOf(Blocks.STONE).hardness(-1f).solid().noBlockBreakParticles()));

    public static final Block Window = registerBlock("window",
            new WindowBlock(FabricBlockSettings.copyOf(Blocks.GLASS).hardness(-1f).nonOpaque().noBlockBreakParticles()));

    public static final Block PoolTileSlope = registerBlock("slope",
            new PoolTileSlopeBlock(FabricBlockSettings.copyOf(Blocks.STONE).hardness(-1f).noBlockBreakParticles().nonOpaque().sounds(SprintBlockSoundGroup.CONCRETE)));


    public static final Block CEILINGLIGHT = registerBlock("ceiling_light",
            new CeilingLight(FabricBlockSettings.copyOf(Blocks.GLASS).hardness(-1f).noBlockBreakParticles()));



    public static final Block POWER_POLE_TOP = registerBlock("power_pole_top",
            new UtilityPole(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS).hardness(-1f).noBlockBreakParticles()));

    public static final Block POWER_POLE = registerBlock("power_pole",
            new UtilityPole(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS).hardness(-1f).noBlockBreakParticles()));

    public static final Block DIRT = registerBlock("dirt",
            new Block(FabricBlockSettings.copyOf(Blocks.GRASS_BLOCK).hardness(-1f).noBlockBreakParticles().sounds(SprintBlockSoundGroup.GRASS2)));


    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, new Identifier(SPBRevamped.MOD_ID, name), block);
    }
    private static Item registerBlockItem(String name, Block block) {
        return Registry.register(Registries.ITEM, new Identifier(SPBRevamped.MOD_ID, name),
                new BlockItem(block, new FabricItemSettings()));
    }

    public static void init() {

    }

}
