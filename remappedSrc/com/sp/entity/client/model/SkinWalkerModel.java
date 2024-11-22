package com.sp.entity.client.model;

import com.sp.SPBRevamped;
import com.sp.entity.custom.SkinWalkerEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class SkinWalkerModel extends GeoModel<SkinWalkerEntity> {

	@Override
	public Identifier getModelResource(SkinWalkerEntity animatable) {
		MinecraftClient client = MinecraftClient.getInstance();
		if(client.player != null){
			if(client.player.getModel().equals("slim")){
				return new Identifier(SPBRevamped.MOD_ID, "geo/entity/skin_walker_slim.geo.json");
			} else {
				return new Identifier(SPBRevamped.MOD_ID, "geo/entity/skin_walker_default.geo.json");
			}
		}
		return new Identifier(SPBRevamped.MOD_ID, "geo/entity/skin_walker_default.geo.json");
	}

	@Override
	public Identifier getTextureResource(SkinWalkerEntity animatable) {
		MinecraftClient client = MinecraftClient.getInstance();
		if(client.player != null){
			return client.player.getSkinTexture();
		}
		return new Identifier(SPBRevamped.MOD_ID, "textures/entity/placeholder.png");
	}

	@Override
	public Identifier getAnimationResource(SkinWalkerEntity animatable) {
		return new Identifier(SPBRevamped.MOD_ID, "animations/entity/empty.animation.json");
	}
}