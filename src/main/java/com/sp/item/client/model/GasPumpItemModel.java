package com.sp.item.client.model;

import com.sp.SPBRevamped;
import com.sp.item.custom.GasPumpItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class GasPumpItemModel extends GeoModel<GasPumpItem> {
    @Override
    public Identifier getModelResource(GasPumpItem animatable) {
        return new Identifier(SPBRevamped.MOD_ID, "geo/blocks/staircase.geo.json");
    }

    @Override
    public Identifier getTextureResource(GasPumpItem animatable) {
        return new Identifier(SPBRevamped.MOD_ID, "textures/block/staircase.png");
    }

    @Override
    public Identifier getAnimationResource(GasPumpItem animatable) {
        return new Identifier(SPBRevamped.MOD_ID, "animations/staircase.animation.json");
    }
}