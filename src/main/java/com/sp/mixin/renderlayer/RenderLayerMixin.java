package com.sp.mixin.renderlayer;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.sp.init.RenderLayers;
import net.minecraft.client.render.RenderLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.List;

@Mixin(RenderLayer.class)
public class RenderLayerMixin {

    @ModifyReturnValue(method = "getBlockLayers", at = @At("RETURN"))
    private static List<RenderLayer> addRenderLayer(List<RenderLayer> original){
        List<RenderLayer> list = new ArrayList<>(original);
        list.add(RenderLayers.getConcreteLayer());
        list.add(RenderLayers.getBricksLayer());
        list.add(RenderLayers.getChainFence());
        list.add(RenderLayers.getCeilingTile());
        list.add(RenderLayers.getCarpet());
        list.add(RenderLayers.getWoodenCrateLayer());
        list.add(RenderLayers.getPoolroomsSky());
        list.add(RenderLayers.getCeilingLightLayer());
        list.add(RenderLayers.getPoolTileLayer());
        return list;
    }

}
