package com.sp.mixin.customatlas;

import com.sp.SPBRevamped;
import com.sp.render.RenderLayers;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * This method adds both the Normal texture atlas, and Height texture atlas for PBR materials.
 * It also changes the vanilla block atlas to remove height and normal textures
 */
@Mixin(BakedModelManager.class)
public class BakedModelManagerMixin {

    @Mutable
    @Shadow
    @Final
    private static Map<Identifier, Identifier> LAYERS_TO_LOADERS;



    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void addPBRAtlas(CallbackInfo ci){
        LAYERS_TO_LOADERS = new HashMap<>(LAYERS_TO_LOADERS);
        LAYERS_TO_LOADERS.put(
                RenderLayers.NORMAL_ATLAS_TEXTURE,
                new Identifier(SPBRevamped.MOD_ID, "normal")
        );
        LAYERS_TO_LOADERS.put(
                RenderLayers.HEIGHT_ATLAS_TEXTURE,
                new Identifier(SPBRevamped.MOD_ID, "height")
        );
        LAYERS_TO_LOADERS.put(
                SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE,
                new Identifier(SPBRevamped.MOD_ID, "blocks")
        );
    }

}
