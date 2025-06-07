package com.sp.block.client.models;

import com.sp.SPBRevamped;
import com.sp.block.entity.GasPumpBlockEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class GasPumpBlockModel extends GeoModel<GasPumpBlockEntity> {
    @Override
    public Identifier getModelResource(GasPumpBlockEntity animatable) {
        return new Identifier(SPBRevamped.MOD_ID, "geo/block/gas_pump.geo.json");
    }

    @Override
    public Identifier getTextureResource(GasPumpBlockEntity animatable) {
        return new Identifier(SPBRevamped.MOD_ID, "textures/block/gas_pump.png");
    }

    @Override
    public Identifier getAnimationResource(GasPumpBlockEntity animatable) {
        return new Identifier(SPBRevamped.MOD_ID, "animations/gas_pump.animation.json");
    }
}