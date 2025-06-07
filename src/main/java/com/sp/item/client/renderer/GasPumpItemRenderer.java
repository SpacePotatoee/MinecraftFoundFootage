package com.sp.item.client.renderer;

import com.sp.item.client.model.GasPumpItemModel;
import com.sp.item.custom.GasPumpItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class GasPumpItemRenderer extends GeoItemRenderer<GasPumpItem> {
    public GasPumpItemRenderer() {
        super(new GasPumpItemModel());
    }
}