package com.sp.entity.client;

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
	private final Identifier FINAL_MODEL = new Identifier(SPBRevamped.MOD_ID, "geo/entity/skin_walker_final.geo.json");

	private final Identifier PLACEHOLDER_TEXTURE = new Identifier(SPBRevamped.MOD_ID, "textures/entity/placeholder.png");
	private final Identifier PLACEHOLDER_TEXTURE2 = new Identifier(SPBRevamped.MOD_ID, "textures/entity/he_a_lil_blank.png");

	private final Identifier EMPTY_ANIMATION = new Identifier(SPBRevamped.MOD_ID, "animations/entity/empty.animation.json");

	@Override
	public void setCustomAnimations(SkinWalkerEntity animatable, long instanceId, AnimationState<SkinWalkerEntity> animationState) {
		super.setCustomAnimations(animatable, instanceId, animationState);
        animatable.tickComponentsClient(animatable, new GeoModelAccessor(this));
	}

	@Override
	public Identifier getModelResource(SkinWalkerEntity animatable) {
//		if(animatable.getWorld().isRaining())		{
//			return DEFAULT_MODEL;
//		} else {
//			return FINAL_MODEL;
//		}
		return FINAL_MODEL;
//		MinecraftClient client = MinecraftClient.getInstance();
//		SkinWalkerComponent component = InitializeComponents.SKIN_WALKER.get(animatable);
//
//		if(client.world != null){
//			AbstractClientPlayerEntity player = (AbstractClientPlayerEntity) client.world.getPlayerByUuid(component.getTargetPlayerUUID());
//			if(player != null) {
//				if (player.getModel().equals("slim")) {
//					return SLIM_MODEL;
//				} else {
//					return DEFAULT_MODEL;
//				}
//			}
//		}
//		return DEFAULT_MODEL;
	}

	@Override
	public Identifier getTextureResource(SkinWalkerEntity animatable) {
		/*
		MinecraftClient client = MinecraftClient.getInstance();
		SkinWalkerComponent component = InitializeComponents.SKIN_WALKER.get(animatable);
		if(client.world != null){
			AbstractClientPlayerEntity player = (AbstractClientPlayerEntity) client.world.getPlayerByUuid(component.getTargetPlayerUUID());
			if(player != null) {
				return player.getSkinTexture();
			}
		}

		 */
		return PLACEHOLDER_TEXTURE2;
	}

	@Override
	public Identifier getAnimationResource(SkinWalkerEntity animatable) {
		return EMPTY_ANIMATION;
	}

}