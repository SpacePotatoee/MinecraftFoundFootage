package com.sp.entity.client.model;

import com.sp.SPBRevamped;
import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.SkinWalkerComponent;
import com.sp.entity.custom.SkinWalkerEntity;
import com.sp.entity.ik.model.GeckoLib.GeoModelAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class SkinWalkerModel extends GeoModel<SkinWalkerEntity> {
	private final Identifier SLIM_MODEL = new Identifier(SPBRevamped.MOD_ID, "geo/entity/skin_walker_slim.geo.json");
	private final Identifier DEFAULT_MODEL = new Identifier(SPBRevamped.MOD_ID, "geo/entity/skin_walker_default.geo.json");
	private final Identifier FINAL_MODEL = new Identifier(SPBRevamped.MOD_ID, "geo/entity/skin_walker_final_default.geo.json");

	private final Identifier PLACEHOLDER_TEXTURE = new Identifier(SPBRevamped.MOD_ID, "textures/entity/skinwalker/placeholder.png");

	private final Identifier ANIMATION = new Identifier(SPBRevamped.MOD_ID, "animations/entity/skinwalker.animation.json");

	@Override
	public void setCustomAnimations(SkinWalkerEntity animatable, long instanceId, AnimationState<SkinWalkerEntity> animationState) {
		super.setCustomAnimations(animatable, instanceId, animationState);
		SkinWalkerComponent component = InitializeComponents.SKIN_WALKER.get(animatable);

		if(component.isInTrueForm()) {
			animatable.tickComponentsClient(animatable, new GeoModelAccessor(this));
		}
	}

	@Override
	public Identifier getModelResource(SkinWalkerEntity animatable) {
		SkinWalkerComponent component = InitializeComponents.SKIN_WALKER.get(animatable);

		if(!component.isInTrueForm()) {
			MinecraftClient client = MinecraftClient.getInstance();
			if (client.world != null) {
				AbstractClientPlayerEntity player = (AbstractClientPlayerEntity) client.world.getPlayerByUuid(component.getTargetPlayerUUID());
				if (player != null) {
					if (player.getModel().equals("slim")) {
						return SLIM_MODEL;
					} else {
						return DEFAULT_MODEL;
					}
				}
			}
		}

		return FINAL_MODEL;
	}

	@Override
	public Identifier getTextureResource(SkinWalkerEntity animatable) {
		MinecraftClient client = MinecraftClient.getInstance();
		SkinWalkerComponent component = InitializeComponents.SKIN_WALKER.get(animatable);
		if(client.world != null){
			AbstractClientPlayerEntity player = (AbstractClientPlayerEntity) client.world.getPlayerByUuid(component.getTargetPlayerUUID());
			if(player != null) {
				return player.getSkinTexture();
			}
		}

		return PLACEHOLDER_TEXTURE;
	}

	@Override
	public Identifier getAnimationResource(SkinWalkerEntity animatable) {
		return ANIMATION;
	}

}