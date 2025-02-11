package com.sp.render.pbr;

import com.sp.init.ModBlocks;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.block.Block;

/**
 * <p>In case I need a certain block to be binded to a certain ID. Kinda like how iris does it.</p>
 * Also keeps me from making a new render layer every time I need to access only a specific block
 */
public class BlockIdMap {
    private static Object2IntMap<Block> BlockIDs = null;

    public static void init(){
        BlockIDs = new Object2IntOpenHashMap<>();

        BlockIDs.put(ModBlocks.PoolTiles, 18);
        BlockIDs.put(ModBlocks.CEILINGLIGHT, 15);
    }

    public static int getBlockID(Block block) {
        if(BlockIDs == null){
            init();
        }

        return BlockIDs.getOrDefault(block, -1);
    }
}
