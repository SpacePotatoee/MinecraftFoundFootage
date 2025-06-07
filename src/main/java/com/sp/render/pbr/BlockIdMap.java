package com.sp.render.pbr;

import com.sp.SPBRevamped;
import com.sp.init.ModBlocks;
import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.registry.Registries;

import java.util.ArrayList;
import java.util.List;

/**
 * In case I need a certain block to be bound to a certain ID. Kinda like how iris does it.<br>
 * Also keeps me from making a new render layer every time I need to access only a specific block. <br>
 * Lets me easily access certain blocks when running the shaders.
 */
public class BlockIdMap {
    private static Object2IntMap<Block> BlockIDs = new Object2IntOpenHashMap<>();
    private static List<BlockIDCallback> BlockIDCallbacks = new ArrayList<>();
    public static boolean init = false;
    private static int numOfBlocks;

    public static void init() {
        BlockIDs.clear();
        init = true;

        //For every block, assign an ID
        for(Block block : Registries.BLOCK){
            RenderLayer renderLayer = RenderLayers.getBlockLayer(block.getDefaultState());
            if(renderLayer == RenderLayer.getSolid()){
                BlockIDs.put(block, 0);
            } else if(renderLayer == RenderLayer.getCutout()){
                BlockIDs.put(block, 1);
            } else if(renderLayer == RenderLayer.getCutoutMipped()){
                BlockIDs.put(block, 2);
            } else if(renderLayer == RenderLayer.getTranslucent()){
                BlockIDs.put(block, 3);
            }
            numOfBlocks++;
        }

        SPBRevamped.LOGGER.info("Loaded {} Default Block IDs", numOfBlocks);

        for (BlockIDCallback blockIDCallback : BlockIDCallbacks) {
            blockIDCallback.apply(BlockIDs);
        }
    }

    public static int getBlockID(Block block) {
        if (BlockIDs.isEmpty() || !init) {
            return -1;
        }

        return BlockIDs.getOrDefault(block, -1);
    }

    /**
     * Register a callback that will be called when the block IDs are initialized or updated.
     * Anything under 23 is already taken by either another modded block or a vanilla rendering layers <i>like the world border for example.</i>
     *
     * @param function The callback function to register.
     */
    public static void registerBlockID(BlockIDCallback function) {
        BlockIDCallbacks.add(function);
    }

    @FunctionalInterface
    public interface BlockIDCallback {
        void apply(Object2IntMap<Block> blockIDs);
    }
}
