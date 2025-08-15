package com.sp.render.pbr;

import com.sp.render.RenderLayers;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.block.Block;

import java.util.HashMap;

public abstract class PbrRegistry {
    private static final HashMap<Block, PbrMaterial> PBR_MATERIALS = new HashMap<>();


    /**
     * Register PBR materials for blocks to get that realistic look.
     *
     * Call this in your client init method. You'll need to set up the textures properly:
     * - Put textures in textures/pbr/block/your_block_name/
     * - Need _color, _normal, and _height variants
     * - If you don't want height mapping, just use a white texture
     *
     * The block state and model files are needed too - just copy ours if you're not sure.
     *
     * Pro tip: Use the resource reload listener so you don't have to restart every time:
     * ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(...)
     *
     * This took forever to get working right with the shader pipeline, but now it's
     * pretty much plug-and-play.
     *
     * @param block The block to apply PBR to
     * @param material The PBR material settings
     */
    public static void registerPBR(Block block, PbrMaterial material) {
        PBR_MATERIALS.remove(block);
        PBR_MATERIALS.put(block, material);
        BlockRenderLayerMap.INSTANCE.putBlock(block, RenderLayers.getPbrLayer());
    }

    public static PbrMaterial getMaterial(Block block){
        return PBR_MATERIALS.get(block);
    }

    /**
     * New PBR Material record
     * @param enableHeight whether to enable parallax occlusion mapping (The ceiling tile and carpet blocks are the only ones with it disabled) <br>
     *                     Does improve performance when turned off.
     * @param depthMultiplier The multiplier for the height texture.
     *                        Anything under 0.2 will do nothing and anything over ~0.6 will completely delete your block.<br>
     *                        <i>So I would recommend something like 0.3 - 0.4</i>
     * @param zoom Value to scale the textures. A value of 1 will have the texture repeat every block (looks normal),
     *             a value of 2 will have the texture repeat every 2 blocks, and so on.
     * @param textureResolution The resolution of the PBR textures. <b>They must all be the same resolution and be square</b>.
     */
    public record PbrMaterial(boolean enableHeight, float depthMultiplier, float zoom, int textureResolution) {

    }
}
