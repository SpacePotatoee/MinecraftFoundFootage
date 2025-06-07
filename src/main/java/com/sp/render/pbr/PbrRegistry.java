package com.sp.render.pbr;

import com.sp.render.RenderLayers;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.block.Block;

import java.util.HashMap;

public abstract class PbrRegistry {
    private static final HashMap<Block, PbrMaterial> PBR_MATERIALS = new HashMap<>();


    /**
     * Call this method in your client's {@code onInitializeClient()} method to render PBR textures on the specified block<br>
     * Also you need to have a block state file and model file registered for the block. <i>I suggest just copying our files.</i> <br>
     * Put all of your textures into {@code textures/pbr/block/<name of your block>} <br>
     * <br>
     * Then in that folder, you need the block's<br>
     * {@code Color Texture} with the name {@code <name of your block>_color} <br>
     * {@code Normal Texture} with the name {@code <name of your block>_normal}<br>
     * {@code Height Texture} with the name {@code <name of your block>_height}<br>
     * <br>
     * If you have height disabled, just use a completely white texture of the same size. <br>
     * Look at the ceiling tile or carpet blocks if you're confused.<br>
     * <br>
     * The rest <i> should </i> be handled automatically.
     * I also highly recommend calling this in a {@code ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener()}s reload method <br>
     * this will make it so that when you reload with F3+T, the PBR texture settings well be reloaded too <i>no need to constantly restart</i>.
     *
     * @param block The block to render the PBR textures on.
     * @param material The new PBR material parameters.
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
