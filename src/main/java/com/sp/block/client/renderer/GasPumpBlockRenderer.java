package com.sp.block.client.renderer;

import com.sp.block.client.models.GasPumpBlockModel;
import com.sp.block.entity.GasPumpBlockEntity;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class GasPumpBlockRenderer extends GeoBlockRenderer<GasPumpBlockEntity> {
    public GasPumpBlockRenderer(BlockEntityRendererFactory.Context context) {
        super(new GasPumpBlockModel());
    }
}