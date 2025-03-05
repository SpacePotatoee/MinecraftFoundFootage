package com.sp.mixin.customatlas;

import net.minecraft.client.render.model.BakedModelManager;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BakedModelManager.class)
public class BakedModelManagerMixin {

//    @Mutable
//    @Shadow @Final private static Map<Identifier, Identifier> LAYERS_TO_LOADERS;
//
//    @Inject(method = "<clinit>", at = @At("TAIL"))
//    private static void addPBRAtlas(CallbackInfo ci){
//        LAYERS_TO_LOADERS = new HashMap<>(LAYERS_TO_LOADERS);
//        LAYERS_TO_LOADERS.put(
//                RenderLayers.NORMAL_ATLAS,
//                new Identifier(SPBRevamped.MOD_ID, "normal")
//        );
//        LAYERS_TO_LOADERS.put(
//                RenderLayers.HEIGHT_ATLAS,
//                new Identifier(SPBRevamped.MOD_ID, "height")
//        );
//    }

}
