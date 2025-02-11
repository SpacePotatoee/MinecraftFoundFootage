package com.sp.mixin.customatlas;

import com.sp.SPBRevamped;
import com.sp.init.RenderLayers;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

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
